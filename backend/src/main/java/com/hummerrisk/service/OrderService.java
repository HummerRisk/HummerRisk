package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.quartz.service.QuartzManageService;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtResourceMapper;
import com.hummerrisk.base.mapper.ext.ExtTaskMapper;
import com.hummerrisk.commons.constants.ResourceOperation;
import com.hummerrisk.commons.constants.ResourceTypeConstants;
import com.hummerrisk.commons.constants.ScanTypeConstants;
import com.hummerrisk.commons.constants.TaskConstants;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.dto.QuartzTaskDTO;
import com.hummerrisk.dto.TaskCopyDTO;
import com.hummerrisk.dto.TaskDTO;
import com.hummerrisk.dto.TaskItemLogDTO;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.commons.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author harris
 */
@Service
public class OrderService {

    @Resource @Lazy
    private TaskMapper taskMapper;
    @Resource @Lazy
    private TaskItemMapper taskItemMapper;
    @Resource @Lazy
    private ExtTaskMapper extTaskMapper;
    @Resource @Lazy
    private TaskItemLogMapper taskItemLogMapper;
    @Resource @Lazy
    private CommonThreadPool commonThreadPool;
    @Resource @Lazy
    private TaskItemResourceMapper taskItemResourceMapper;
    @Resource @Lazy
    private ResourceMapper resourceMapper;
    @Resource @Lazy
    private AccountMapper accountMapper;
    @Resource @Lazy
    private QuartzManageService quartzManageService;
    @Resource @Lazy
    private RuleMapper ruleMapper;
    @Resource @Lazy
    private ResourceRuleMapper resourceRuleMapper;
    @Resource @Lazy
    private RuleTagMappingMapper ruleTagMappingMapper;
    @Resource @Lazy
    private ExtResourceMapper extResourceMapper;
    @Resource @Lazy
    private ScanHistoryMapper scanHistoryMapper;
    @Resource @Lazy
    private ScanTaskHistoryMapper scanTaskHistoryMapper;
    @Resource @Lazy
    private NoticeService noticeService;
    @Resource @Lazy
    private CloudAccountQuartzTaskMapper quartzTaskMapper;
    @Resource @Lazy
    private CloudAccountQuartzTaskRelationMapper quartzTaskRelationMapper;
    @Resource @Lazy
    private CloudAccountQuartzTaskRelaLogMapper quartzTaskRelaLogMapper;

    public Task createTask(QuartzTaskDTO quartzTaskDTO, String status, String messageOrderId) throws Exception {
        Task task = createTaskOrder(quartzTaskDTO, status, messageOrderId);
        String taskId = task.getId();

        String script = quartzTaskDTO.getScript();
        JSONArray jsonArray = JSON.parseArray(quartzTaskDTO.getParameter());
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            String key = "${{" + jsonObject.getString("key") + "}}";
            if (script.contains(key)) {
                script = script.replace(key, jsonObject.getString("defaultValue"));
            }
        }

        this.deleteTaskItems(task.getId());
        List<String> resourceTypes = new ArrayList();
        for (SelectTag selectTag : quartzTaskDTO.getSelectTags()) {
            for (String regionId : selectTag.getRegions()) {
                TaskItemWithBLOBs taskItemWithBLOBs = new TaskItemWithBLOBs();
                String uuid = UUIDUtil.newUUID();
                taskItemWithBLOBs.setId(uuid);
                taskItemWithBLOBs.setTaskId(task.getId());
                taskItemWithBLOBs.setRuleId(quartzTaskDTO.getId());
                taskItemWithBLOBs.setCustomData(script);
                taskItemWithBLOBs.setStatus(TaskConstants.TASK_STATUS.UNCHECKED.name());
                taskItemWithBLOBs.setSeverity(quartzTaskDTO.getSeverity());
                taskItemWithBLOBs.setCreateTime(task.getCreateTime());
                taskItemWithBLOBs.setAccountId(selectTag.getAccountId());
                AccountWithBLOBs account = accountMapper.selectByPrimaryKey(selectTag.getAccountId());
                taskItemWithBLOBs.setAccountUrl(account.getPluginIcon());
                taskItemWithBLOBs.setAccountLabel(account.getName());
                taskItemWithBLOBs.setRegionId(regionId);
                taskItemWithBLOBs.setRegionName(PlatformUtils.tranforRegionId2RegionName(regionId, task.getPluginId()));
                taskItemWithBLOBs.setTags(task.getRuleTags());
                taskItemMapper.insertSelective(taskItemWithBLOBs);

                final String finalScript = script;
                commonThreadPool.addTask(() -> {
                    String sc = "";
                    String dirPath = "";
                    try {
                        LogUtil.info(" ::: Generate policy.yml file start ::: ");
                        dirPath = CommandUtils.saveAsFile(finalScript, TaskConstants.RESULT_FILE_PATH_PREFIX + taskId + "/" + regionId, "policy.yml");
                        LogUtil.info(" ::: Generate policy.yml file end ::: " + dirPath);
                    } catch (Exception e) {
                        LogUtil.error("[{}] Generate policy.yml file，and custodian run failed:{}", taskId + "/" + regionId, e.getMessage());
                    }
                    Yaml yaml = new Yaml();
                    Map map = null;
                    try {
                        map = (Map) yaml.load(new FileInputStream(dirPath + "/policy.yml"));
                    } catch (FileNotFoundException e) {
                        LogUtil.error(e.getMessage());
                    }
                    if (map != null) {
                        List<Map> list = (List) map.get("policies");
                        for (Map m : list) {
                            String dirName = m.get("name").toString();
                            String resourceType = m.get("resource").toString();

                            if (!PlatformUtils.checkAvailableRegion(account.getPluginId(), resourceType, regionId)) {
                                continue;
                            }

                            TaskItemResourceWithBLOBs taskItemResource = new TaskItemResourceWithBLOBs();
                            taskItemResource.setTaskId(taskId);
                            taskItemResource.setTaskItemId(taskItemWithBLOBs.getId());
                            taskItemResource.setDirName(dirName);
                            taskItemResource.setResourceType(resourceType);
                            taskItemResource.setResourceName(dirName);

                            //包含actions
                            Map<String, Object> paramMap = new HashMap<>();
                            paramMap.put("policies", Collections.singletonList(m));
                            taskItemResource.setResourceCommandAction(yaml.dump(paramMap));

                            //不包含actions
                            m.remove("actions");
                            paramMap.put("policies", Collections.singletonList(m));
                            taskItemResource.setResourceCommand(yaml.dump(paramMap));
                            taskItemResourceMapper.insertSelective(taskItemResource);

                            resourceTypes.add(resourceType);
                        }

                        map.put("policies", list);
                        sc = yaml.dump(map);
                        taskItemWithBLOBs.setDetails(sc);
                        taskItemMapper.updateByPrimaryKeySelective(taskItemWithBLOBs);

                        task.setResourceTypes(new HashSet<>(resourceTypes).toString());
                        taskMapper.updateByPrimaryKeySelective(task);
                    }
                });
            }
        }
        //向首页活动添加操作信息
        OperationLogService.log(SessionUtils.getUser(), taskId, task.getTaskName(), ResourceTypeConstants.TASK.name(), ResourceOperation.CREATE, "创建检测任务");
        return task;
    }

    private void deleteTaskItems (String taskId) {
        TaskItemExample taskItemExample = new TaskItemExample();
        taskItemExample.createCriteria().andTaskIdEqualTo(taskId);
        List<TaskItem> taskItems = taskItemMapper.selectByExample(taskItemExample);

        for (TaskItem taskItem : taskItems) {
            TaskItemLogExample taskItemLogExample = new TaskItemLogExample();
            taskItemLogExample.createCriteria().andTaskItemIdEqualTo(taskItem.getId());
            taskItemLogMapper.deleteByExample(taskItemLogExample);

            TaskItemResourceExample taskItemResourceExample = new TaskItemResourceExample();
            taskItemResourceExample.createCriteria().andTaskItemIdEqualTo(taskItem.getId());
            List<TaskItemResource> taskItemResources = taskItemResourceMapper.selectByExample(taskItemResourceExample);
            for (TaskItemResource taskItemResource : taskItemResources) {
                resourceMapper.deleteByPrimaryKey(taskItemResource.getResourceId());
                resourceRuleMapper.deleteByPrimaryKey(taskItemResource.getResourceId());
            }
            taskItemResourceMapper.deleteByExample(taskItemResourceExample);
        }
        taskItemMapper.deleteByExample(taskItemExample);
    }

    private Task createTaskOrder(QuartzTaskDTO quartzTaskDTO, String status, String messageOrderId) throws Exception {
        Task task = new Task();
        task.setTaskName(quartzTaskDTO.getTaskName() != null ?quartzTaskDTO.getTaskName():quartzTaskDTO.getName());
        task.setRuleId(quartzTaskDTO.getId());
        task.setSeverity(quartzTaskDTO.getSeverity());
        task.setType(quartzTaskDTO.getType());
        task.setPluginId(quartzTaskDTO.getPluginId());
        task.setPluginIcon(quartzTaskDTO.getPluginIcon());
        task.setPluginName(quartzTaskDTO.getPluginName());
        task.setRuleTags(quartzTaskDTO.getTags().toString());
        task.setDescription(quartzTaskDTO.getDescription());
        task.setAccountId(quartzTaskDTO.getAccountId());
        task.setApplyUser(Objects.requireNonNull(SessionUtils.getUser()).getId());
        task.setStatus(status);
        task.setScanType(ScanTypeConstants.custodian.name());
        if (quartzTaskDTO.getCron() != null){
            task.setCron(quartzTaskDTO.getCron());
            task.setCronDesc(DescCornUtils.descCorn(quartzTaskDTO.getCron()));
        }

        TaskExample example = new TaskExample();
        TaskExample.Criteria criteria = example.createCriteria();
        criteria.andAccountIdEqualTo(quartzTaskDTO.getAccountId()).andTaskNameEqualTo(quartzTaskDTO.getTaskName());
        List<Task> queryTasks = taskMapper.selectByExample(example);
        if (!queryTasks.isEmpty()) {
            task.setId(queryTasks.get(0).getId());
            task.setCreateTime(System.currentTimeMillis());
            taskMapper.updateByPrimaryKeySelective(task);
        } else {
            String taskId = IDGenerator.newBusinessId(TaskConstants.TASK_ID_PREFIX, SessionUtils.getUser().getId());
            task.setId(taskId);
            task.setCreateTime(System.currentTimeMillis());
            taskMapper.insertSelective(task);
        }

        if (StringUtils.isNotEmpty(messageOrderId)) {
            noticeService.createMessageOrderItem(messageOrderId, task);
        }

        return task;
    }

    public TaskDTO getTaskDetail(String taskId) {
        return extTaskMapper.getTaskDetail(taskId);
    }

    public TaskDTO getTaskExtendInfo(String taskId) {
        return extTaskMapper.getTaskExtendInfo(taskId);
    }

    void saveTaskItemLog(String taskItemId, String resourcePrimaryKey, String operation, String output, boolean success) {
        TaskItemLog taskItemLog = new TaskItemLog();
        String operator = "system";
        try {
            if (SessionUtils.getUser() != null) {
                operator = SessionUtils.getUser().getId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        taskItemLog.setOperator(operator);
        taskItemLog.setCreateTime(System.currentTimeMillis());
        taskItemLog.setTaskItemId(StringUtils.isBlank(taskItemId) ? StringUtils.EMPTY : taskItemId);
        taskItemLog.setResourceId(resourcePrimaryKey);
        taskItemLog.setOperation(operation);
        taskItemLog.setOutput(output);
        taskItemLog.setResult(success);
        taskItemLogMapper.insert(taskItemLog);
    }

    int updateTaskStatus(String taskId, String oldStatus, String newStatus) {
        TaskExample example = new TaskExample();
        TaskExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(taskId);
        if (StringUtils.isNotBlank(oldStatus)) {
            criteria.andStatusEqualTo(oldStatus);
        }
        Task task = new Task();
        task.setStatus(newStatus);
        return taskMapper.updateByExampleSelective(task, example);
    }

    void updateTaskItemStatus(String taskItemId, TaskConstants.TASK_STATUS status) {
        TaskItemWithBLOBs taskItem = new TaskItemWithBLOBs();
        taskItem.setId(taskItemId);
        taskItem.setStatus(status.name());
        taskItemMapper.updateByPrimaryKeySelective(taskItem);
    }

    public void updateTaskStatus(String id, String status) {
        Task task = new Task();
        task.setId(id);
        task.setStatus(status);
        taskMapper.updateByPrimaryKeySelective(task);
    }

    public List<Task> listAll() {
        return taskMapper.selectByExample(null);
    }

    public List<TaskItemLogDTO> getQuartzLogByTask(String taskId) {
        List<TaskItemLogDTO> result = new ArrayList<>();
        try {
            TaskItemExample taskItemExample = new TaskItemExample();
            taskItemExample.createCriteria().andTaskIdEqualTo(taskId);
            List<TaskItemWithBLOBs> taskItems = taskItemMapper.selectByExampleWithBLOBs(taskItemExample);
            for (TaskItemWithBLOBs taskItem : taskItems) {
                TaskItemLogDTO taskItemLogDTO = new TaskItemLogDTO();
                taskItem.setDetails(null);
                taskItem.setCustomData(null);
                taskItemLogDTO.setTaskItem(taskItem);
                Rule rule = ruleMapper.selectByPrimaryKey(taskItem.getRuleId());
                rule.setScript(null);//没有用到暂时置空，以防止翻译总报错warn
                taskItemLogDTO.setRule(rule);
                result.add(taskItemLogDTO);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }

        return result;
    }

    public TaskItemWithBLOBs taskItemWithBLOBs(String taskItemId) {
        return taskItemMapper.selectByPrimaryKey(taskItemId);
    }

    public List<TaskItemLog> getQuartzLogByTaskItemId(TaskItemWithBLOBs taskItemWithBLOBs) {
        TaskItemLogExample taskItemLogExample = new TaskItemLogExample();
        taskItemLogExample.createCriteria().andTaskItemIdEqualTo(taskItemWithBLOBs.getId());
        taskItemLogExample.setOrderByClause("create_time desc");
        return taskItemLogMapper.selectByExampleWithBLOBs(taskItemLogExample);
    }

    public List<CloudAccountQuartzTaskRelaLog> getQuartzLogsById(String qzTaskId) {
        CloudAccountQuartzTaskRelaLogExample example = new CloudAccountQuartzTaskRelaLogExample();
        example.createCriteria().andQuartzTaskIdEqualTo(qzTaskId);
        example.setOrderByClause("create_time desc");
        return quartzTaskRelaLogMapper.selectByExampleWithBLOBs(example);
    }

    public List<TaskItemLogDTO> getTaskItemLogByTaskId(String taskId) {
        List<TaskItemLogDTO> result = new ArrayList<>();
        try {
            TaskItemExample taskItemExample = new TaskItemExample();
            taskItemExample.createCriteria().andTaskIdEqualTo(taskId);
            List<TaskItem> taskItems = taskItemMapper.selectByExample(taskItemExample);
            for (TaskItem taskItem : taskItems) {
                result.addAll(getTaskItemLogByTaskItemId(taskItem.getId()));
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }

        return result;
    }

    private List<TaskItemLogDTO> getTaskItemLogByTaskItemId(String taskItemId) {
        List<TaskItemLogDTO> result = new ArrayList<>();
        try {
            TaskItemWithBLOBs taskItem = taskItemMapper.selectByPrimaryKey(taskItemId);
            if (taskItem != null) {
                TaskItemLogDTO taskItemLogDTO = new TaskItemLogDTO();
                taskItem.setDetails(null);
                taskItem.setCustomData(null);
                taskItemLogDTO.setTaskItem(taskItem);
                Rule rule = ruleMapper.selectByPrimaryKey(taskItem.getRuleId());
                rule.setScript(null);//没有用到暂时置空，以防止翻译总报错warn
                taskItemLogDTO.setRule(rule);
                TaskItemLogExample taskItemLogExample = new TaskItemLogExample();
                taskItemLogExample.createCriteria().andTaskItemIdEqualTo(taskItem.getId());
                taskItemLogExample.setOrderByClause("create_time");
                taskItemLogDTO.setTaskItemLogList(taskItemLogMapper.selectByExampleWithBLOBs(taskItemLogExample));
                result.add(taskItemLogDTO);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return result;
    }

    public TaskCopyDTO copy(String taskId) {
        TaskCopyDTO taskCopyDTO = new TaskCopyDTO();
        TaskItemExample example = new TaskItemExample();
        example.createCriteria().andTaskIdEqualTo(taskId);
        List<TaskItem> taskItemList = taskItemMapper.selectByExample(example);
        taskCopyDTO.setTaskItemList(taskItemList);
        taskCopyDTO.setRule(ruleMapper.selectByPrimaryKey(taskItemList.get(0).getRuleId()));
        RuleTagMappingExample ruleTagMappingExample = new RuleTagMappingExample();
        ruleTagMappingExample.createCriteria().andRuleIdEqualTo(taskItemList.get(0).getRuleId());
        List<RuleTagMapping> ruleTagMappings = ruleTagMappingMapper.selectByExample(ruleTagMappingExample);
        taskCopyDTO.setRuleTagMappingList(ruleTagMappings.stream().map(RuleTagMapping::getTagKey).collect(Collectors.toList()));
        Set<String> set = new HashSet<>();
        List<SelectTag> selectTagList = new LinkedList<>();
        taskItemList.stream().forEach(item -> {
            set.add(item.getAccountId());
        });
        Objects.requireNonNull(set).forEach(str -> {
            SelectTag selectTag = new SelectTag();
            selectTag.setAccountId(str);
            List<String> regions = new LinkedList<>();
            taskItemList.forEach(taskItem -> {
                if (StringUtils.equals(taskItem.getAccountId(), str)) {
                    regions.add(taskItem.getRegionId());
                }
            });
            selectTag.setRegions(regions);
            selectTagList.add(selectTag);
        });
        taskCopyDTO.setSelectTags(selectTagList);
        return taskCopyDTO;
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public void retry(String taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            HRException.throwException(Translator.get("i18n_ex_task_not_found") + taskId);
        }
        try {
            TaskItemExample taskItemExample = new TaskItemExample();
            taskItemExample.createCriteria().andTaskIdEqualTo(task.getId());
            List<TaskItemWithBLOBs> items = taskItemMapper.selectByExampleWithBLOBs(taskItemExample);
            for (TaskItemWithBLOBs item : items) {
                if ((StringUtils.equals(task.getType(), TaskConstants.Type.CREATE.toString()))) {
                    HashSet<String> resourceIdSet2BeReleased = new HashSet<>();

                    //先找到异常的资源
                    if (item.getStatus().equals(TaskConstants.TASK_STATUS.ERROR.name())) {
                        TaskItemLogExample taskItemLogExample = new TaskItemLogExample();
                        taskItemLogExample.createCriteria().andTaskItemIdEqualTo(item.getId()).andResourceIdIsNotNull().andResultEqualTo(false);
                        List<TaskItemLog> taskItemLogs = taskItemLogMapper.selectByExample(taskItemLogExample);
                        resourceIdSet2BeReleased.addAll(taskItemLogs.stream().map(TaskItemLog::getResourceId).collect(Collectors.toList()));
                    }

                    //释放资源
                    for (String resourceId : resourceIdSet2BeReleased) {
                        if (StringUtils.isBlank(resourceId)) {
                            continue;
                        }
                        String newTaskItemId = item.getId() + "-retry";
                        //删除task_item_resource表相关记录，否则任务重试完成后重试之前的资源还会和任务有关联
                        TaskItemResourceExample taskItemResourceExample = new TaskItemResourceExample();
                        taskItemResourceExample.createCriteria().andTaskIdEqualTo(task.getId()).andTaskItemIdEqualTo(item.getId());
                        taskItemResourceMapper.deleteByExample(taskItemResourceExample);
                        //清除日志关联
                        TaskItemLogExample taskItemLogExample = new TaskItemLogExample();
                        taskItemLogExample.createCriteria().andTaskItemIdEqualTo(item.getId()).andResourceIdEqualTo(resourceId);
                        TaskItemLog taskItemLog = new TaskItemLog();
                        taskItemLog.setTaskItemId(newTaskItemId);
                        taskItemLogMapper.updateByExampleSelective(taskItemLog, taskItemLogExample);
                    }

                    if (item.getCount() > Optional.ofNullable(this.getResourceByTaskItemId(item.getId())).orElse(new ArrayList<>()).size()) {
                        saveTaskItemLog(item.getId(), null, Translator.get("i18n_retry_create_resource"), "", true);
                        item.setStatus(TaskConstants.TASK_STATUS.UNCHECKED.name());
                    } else {
                        item.setStatus(TaskConstants.TASK_STATUS.FINISHED.name());
                    }
                } else {
                    if (item.getStatus().equals(TaskConstants.TASK_STATUS.ERROR.name())) {
                        item.setStatus(TaskConstants.TASK_STATUS.UNCHECKED.name());
                    }
                }
                //查询规则，如果规则有变动，按最新变动的规则执行
                /*    Rule rule = ruleMapper.selectByPrimaryKey(item.getRuleId());
                    String script = rule.getScript();
                    JSONArray jsonArray = JSON.parseArray(rule.getParameter());
                    for (Object o : jsonArray) {
                        JSONObject jsonObject = (JSONObject) o;
                        String key = "${{" + jsonObject.getString("key") + "}}";
                        if (script.contains(key)) {
                            script = script.replace(key, jsonObject.getString("defaultValue"));
                        }
                    }
                    if (!StringUtils.equals(script, item.getCustomData())) {
                        List<String> resourceTypes = new ArrayList();
                        final String finalScript = script;
                        commonThreadPool.addTask(() -> {
                            String sc = "";
                            String dirPath = "";
                            try {
                                dirPath = CommandUtils.saveAsFile(finalScript, TaskConstants.RESULT_FILE_PATH_PREFIX + taskId + "/" + item.getRegion(), "policy.yml");
                            } catch (Exception e) {
                                LogUtil.error("[{}] Generate policy.yml file，and custodian run failed:{}", taskId + "/" + item.getRegion(), e.getMessage());
                            }
                            Yaml yaml = new Yaml();
                            Map map = null;
                            try {
                                map = (Map) yaml.load(new FileInputStream(dirPath + "/policy.yml"));
                            } catch (FileNotFoundException e) {
                                LogUtil.error(e.getMessage());
                            }
                            if (map != null) {
                                List<Map> list = (List) map.get("policies");
                                for (Map m : list) {
                                    String dirName = m.get("name").toString();
                                    String resourceType = m.get("resource").toString();
                                    TaskItemResourceWithBLOBs taskItemResource = new TaskItemResourceWithBLOBs();
                                    taskItemResource.setTaskId(taskId);
                                    taskItemResource.setTaskItemId(item.getId());
                                    taskItemResource.setDirName(dirName);
                                    taskItemResource.setResourceType(resourceType);
                                    taskItemResource.setResourceName(dirName);

                                    //包含actions
                                    Map<String, Object> paramMap = new HashMap<>();
                                    paramMap.put("policies", Arrays.asList(m));
                                    taskItemResource.setResourceCommandAction(yaml.dump(paramMap));

                                    //不包含actions
                                    m.remove("actions");
                                    paramMap.put("policies", Arrays.asList(m));
                                    taskItemResource.setResourceCommand(yaml.dump(paramMap));
                                    taskItemResourceMapper.updateByPrimaryKeySelective(taskItemResource);

                                    resourceTypes.add(resourceType);
                                }
                                map.put("policies", list);
                                sc = yaml.dump(map);
                                item.setCustomData(sc);

                            }
                        });
                    }*/

                taskItemMapper.updateByPrimaryKey(item);
            }

            updateTaskStatus(task.getId(), task.getStatus(), TaskConstants.TASK_STATUS.APPROVED.name());
        } catch (Exception e) {
            LogUtil.error("Failed to retry, TaskId: " + task.getId(), e);
            throw e;
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public void getCronDesc(String taskId) throws Exception {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            HRException.throwException(Translator.get("i18n_ex_task_not_found") + taskId);
        }
        String cronDesc = DescCornUtils.descCorn(task.getCron());
        task.setCronDesc(cronDesc);
        taskMapper.updateByPrimaryKeySelective(task);
    }

    public List<ResourceWithBLOBs> getResourceByTaskItemId(String taskItemId) {
        List<ResourceWithBLOBs> list = new LinkedList<>();
        TaskItemResourceExample example = new TaskItemResourceExample();
        example.createCriteria().andTaskItemIdEqualTo(taskItemId);
        List<TaskItemResource> taskItemResources = taskItemResourceMapper.selectByExample(example);
        if (CollectionUtils.size(taskItemResources) != 1) {
            return null;
        }
        for (TaskItemResource itemResource : taskItemResources) {
            ResourceWithBLOBs resourceWithBLOBs = resourceMapper.selectByPrimaryKey(itemResource.getResourceId());
            list.add(resourceWithBLOBs);
        }
        return list;
    }

    /**
     * 高：中：低 = 5 ： 3 ： 2
     * @param account
     * @return
     */
    public Integer calculateScore (Account account, Task task) {
        Double highResultPercent = Double.valueOf(extResourceMapper.resultPercent(account.getId(), "HighRisk", task==null?null:task.getId())!=null?extResourceMapper.resultPercent(account.getId(), "HighRisk", task==null?null:task.getId()):"0.0");
        Double mediumlResultPercent = Double.valueOf(extResourceMapper.resultPercent(account.getId(), "MediumRisk", task==null?null:task.getId())!=null?extResourceMapper.resultPercent(account.getId(), "MediumRisk", task==null?null:task.getId()): "0.0");
        Double lowResultPercent = Double.valueOf(extResourceMapper.resultPercent(account.getId(), "LowRisk", task==null?null:task.getId())!=null?extResourceMapper.resultPercent(account.getId(), "LowRisk", task==null?null:task.getId()):"0.0");

        TaskExample example = new TaskExample();
        TaskExample.Criteria criteria = example.createCriteria();
        criteria.andAccountIdEqualTo(account.getId()).andSeverityEqualTo("HighRisk");
        long high = taskMapper.countByExample(example);
        criteria.andSeverityEqualTo("MediumRisk");
        long mediuml = taskMapper.countByExample(example);
        criteria.andSeverityEqualTo("LowRisk");
        long low = taskMapper.countByExample(example);

        long sum = 5 * high + 3 * mediuml + 2 * low;
        return 100 - (int) Math.ceil(highResultPercent * (5 * high / (sum == 0 ? 1 : sum) ) * 100 + mediumlResultPercent * (3 * mediuml / (sum == 0 ? 1 : sum) ) * 100 + lowResultPercent * (2 * low / (sum == 0 ? 1 : sum) ) * 100);
    }

    public Integer insertScanHistory (Account account) {
        long current = System.currentTimeMillis();
        long zero = current/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset();//当天00点

        ScanHistoryExample example = new ScanHistoryExample();
        example.createCriteria().andAccountIdEqualTo(account.getId()).andCreateTimeEqualTo(zero);
        List<ScanHistory> list = scanHistoryMapper.selectByExample(example);
        ScanHistory history = new ScanHistory();
        if (!list.isEmpty()) {
            int id = list.get(0).getId();
            if (list.size() > 1) {
                list.stream().filter(item -> !StringUtils.equals(item.getId().toString(), String.valueOf(id))).forEach(item -> scanHistoryMapper.deleteByPrimaryKey(item.getId()));
            }
            ScanTaskHistoryExample scanTaskHistoryExample = new ScanTaskHistoryExample();
            scanTaskHistoryExample.createCriteria().andIdEqualTo(id);
            List<ScanTaskHistory> scanTaskHistories = scanTaskHistoryMapper.selectByExampleWithBLOBs(scanTaskHistoryExample);
            JSONArray jsonArray = new JSONArray();
            scanTaskHistories.forEach(item ->{
                if(item.getOutput()!=null) jsonArray.addAll(JSON.parseArray(item.getOutput()));
            });
            history.setOutput(jsonArray.toJSONString());
            history.setId(id);
            history.setResourcesSum(Long.valueOf(extResourceMapper.sumResourcesSum(account.getId())));
            history.setReturnSum(Long.valueOf(extResourceMapper.sumReturnSum(account.getId())));
            history.setScanScore(calculateScore(accountMapper.selectByPrimaryKey(account.getId()), null));
            scanHistoryMapper.updateByPrimaryKeySelective(history);
            return id;
        }

        history.setOperator("System");
        history.setAccountId(account.getId());
        history.setCreateTime(zero);
        return scanHistoryMapper.insertSelective(history);
    }

    public void insertTaskHistory (Task task, Integer scanId) throws Exception {
        ScanTaskHistoryExample example = new ScanTaskHistoryExample();
        example.createCriteria().andTaskIdEqualTo(task.getId()).andScanIdEqualTo(scanId);
        List<ScanTaskHistory> list = scanTaskHistoryMapper.selectByExample(example);
        if (list.size() > 0) {
            updateTaskHistory(task, example);
        } else {
            ScanTaskHistory history = new ScanTaskHistory();
            history.setScanId(scanId);
            history.setTaskId(task.getId());
            history.setOperation("新增历史合规检测");
            scanTaskHistoryMapper.insertSelective(history);
        }
    }

    public void updateTaskHistory (Task task, ScanTaskHistoryExample example) throws Exception {
        try{
            TaskItemResourceExample taskItemResourceExample = new TaskItemResourceExample();
            taskItemResourceExample.createCriteria().andTaskIdEqualTo(task.getId());
            List<TaskItemResourceWithBLOBs> taskItemResources = taskItemResourceMapper.selectByExampleWithBLOBs(taskItemResourceExample);
            JSONArray jsonArray = new JSONArray();
            taskItemResources.forEach(item ->{
                ResourceWithBLOBs resource = resourceMapper.selectByPrimaryKey(item.getResourceId());
                if(resource!=null) {
                    if (!resource.getResources().isEmpty()) {
                        String json = resource.getResources();
                        boolean isJson = isJson(json);
                        if (isJson) {
                            jsonArray.addAll(JSON.parseArray(json));
                        } else {
                            jsonArray.addAll(JSON.parseArray("[{'key':'" + json + "'}]"));
                        }
                    } else {
                        jsonArray.addAll(JSON.parseArray("[]"));
                    }
                }
            });
            ScanTaskHistory history = new ScanTaskHistory();
            history.setResourcesSum(task.getResourcesSum()!=null?task.getResourcesSum():0);
            history.setReturnSum(task.getReturnSum()!=null?task.getReturnSum():0);
            history.setScanScore(calculateScore(accountMapper.selectByPrimaryKey(task.getAccountId()), task));
            history.setOperation("修改历史合规检测");
            history.setOutput(jsonArray.toJSONString());
            scanTaskHistoryMapper.updateByExampleSelective(history, example);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    private boolean isJson(String str){
        try {
            JSONObject.parseObject(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void createQuartzTask(String quartzTaskId) {

        try {
            CloudAccountQuartzTask quartzTask = quartzTaskMapper.selectByPrimaryKey(quartzTaskId);
            if (quartzTask != null) {
                quartzTask.setStatus(TaskConstants.TASK_STATUS.RUNNING.name());
                Trigger trigger = quartzManageService.getTrigger(new TriggerKey(quartzTask.getTriggerId()));
                quartzTask.setLastFireTime(trigger.getNextFireTime().getTime());
                if (trigger.getPreviousFireTime() != null)
                    quartzTask.setPrevFireTime(trigger.getPreviousFireTime().getTime());
                quartzTaskMapper.updateByPrimaryKeySelective(quartzTask);

                CloudAccountQuartzTaskRelationExample example = new CloudAccountQuartzTaskRelationExample();
                example.createCriteria().andQuartzTaskIdEqualTo(quartzTaskId);
                List<CloudAccountQuartzTaskRelation> list = quartzTaskRelationMapper.selectByExampleWithBLOBs(example);
                for (CloudAccountQuartzTaskRelation quartzTaskRelation : list) {
                    JSONArray jsonArray = JSON.parseArray(quartzTaskRelation.getTaskIds());
                    for (Object o : jsonArray) {
                        Task task = taskMapper.selectByPrimaryKey(o.toString());
                        task.setStatus(TaskConstants.TASK_STATUS.APPROVED.name());
                        task.setLastFireTime(quartzTask.getLastFireTime());
                        task.setPrevFireTime(quartzTask.getPrevFireTime());
                        taskMapper.updateByPrimaryKeySelective(task);
                    }
                    CloudAccountQuartzTaskRelaLog quartzTaskRelaLog = new CloudAccountQuartzTaskRelaLog();
                    quartzTaskRelaLog.setCreateTime(System.currentTimeMillis());
                    quartzTaskRelaLog.setQuartzTaskId(quartzTaskId);
                    quartzTaskRelaLog.setQuartzTaskRelaId(quartzTaskRelation.getId());
                    quartzTaskRelaLog.setTaskIds(quartzTaskRelation.getTaskIds());
                    quartzTaskRelaLog.setSourceId(quartzTaskRelation.getSourceId());
                    quartzTaskRelaLog.setQzType(quartzTaskRelation.getQzType());
                    quartzTaskRelaLog.setOperator("System");
                    quartzTaskRelaLog.setOperation("执行定时任务");
                    quartzTaskRelaLogMapper.insertSelective(quartzTaskRelaLog);
                }

            }
        } catch (Exception e) {
            LogUtil.error(quartzTaskId + "{createQuartzTask}: " + e.getMessage());
            HRException.throwException(quartzTaskId + "{createQuartzTask}: " + e);
        }
    }
}
