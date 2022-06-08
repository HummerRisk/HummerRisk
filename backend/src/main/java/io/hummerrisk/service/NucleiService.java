package io.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import io.hummerrisk.base.domain.*;
import io.hummerrisk.base.mapper.*;
import io.hummerrisk.base.mapper.ext.ExtTaskMapper;
import io.hummerrisk.commons.constants.*;
import io.hummerrisk.commons.exception.HRException;
import io.hummerrisk.commons.utils.*;
import io.hummerrisk.dto.QuartzTaskDTO;
import io.hummerrisk.i18n.Translator;
import io.hummerrisk.proxy.nuclei.NucleiCredential;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @author harris
 */
@Service
public class NucleiService {

    @Resource @Lazy
    private TaskMapper taskMapper;
    @Resource @Lazy
    private TaskItemMapper taskItemMapper;
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
    private ResourceRuleMapper resourceRuleMapper;
    @Resource @Lazy
    private NoticeService noticeService;
    @Resource @Lazy
    private ProxyMapper proxyMapper;
    @Resource @Lazy
    private OrderService orderService;
    @Resource @Lazy
    private RuleMapper ruleMapper;
    @Resource @Lazy
    private ResourceItemMapper resourceItemMapper;
    @Resource @Lazy
    private ExtTaskMapper extTaskMapper;

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
                        LogUtil.info(" ::: Generate nuclei.yml file start ::: ");
                        dirPath = CommandUtils.saveAsFile(finalScript, TaskConstants.RESULT_FILE_PATH_PREFIX + taskId + "/" + regionId, "nuclei.yml");
                        LogUtil.info(" ::: Generate nuclei.yml file end ::: " + dirPath);
                    } catch (Exception e) {
                        LogUtil.error("[{}] Generate nuclei.yml file，and nuclei run failed:{}", taskId + "/" + regionId, e.getMessage());
                    }
                    Yaml yaml = new Yaml();
                    Map map = null;
                    try {
                        map = (Map) yaml.load(new FileInputStream(dirPath + "/nuclei.yml"));
                    } catch (FileNotFoundException e) {
                        LogUtil.error(e.getMessage());
                    }
                    if (map != null) {
                        String dirName = "nuclei";
                        String resourceType = "nuclei";

                        TaskItemResourceWithBLOBs taskItemResource = new TaskItemResourceWithBLOBs();
                        taskItemResource.setTaskId(taskId);
                        taskItemResource.setTaskItemId(taskItemWithBLOBs.getId());
                        taskItemResource.setDirName(dirName);
                        taskItemResource.setResourceType(resourceType);
                        taskItemResource.setResourceName(dirName);

                        //包含actions
                        taskItemResource.setResourceCommandAction(yaml.dump(map));

                        //不包含actions
                        taskItemResource.setResourceCommand(yaml.dump(map));
                        taskItemResourceMapper.insertSelective(taskItemResource);

                        resourceTypes.add(resourceType);

                        sc = yaml.dump(map);
                        taskItemWithBLOBs.setDetails(sc);
                        taskItemMapper.updateByPrimaryKeySelective(taskItemWithBLOBs);

                        task.setResourceTypes(resourceTypes.stream().collect(Collectors.toSet()).toString());
                        taskMapper.updateByPrimaryKeySelective(task);
                    }
                });
            }
        }
        //向首页活动添加操作信息
        OperationLogService.log(SessionUtils.getUser(), taskId, task.getTaskName(), ResourceTypeConstants.TASK.name(), ResourceOperation.CREATE, "创建扫描任务");
        return task;
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
        task.setApplyUser(SessionUtils.getUser().getId());
        task.setStatus(status);
        task.setScanType(ScanTypeConstants.nuclei.name());
        if (quartzTaskDTO.getCron() != null){
            task.setCron(quartzTaskDTO.getCron());
            task.setCronDesc(DescCornUtils.descCorn(quartzTaskDTO.getCron()));
        }

        TaskExample example = new TaskExample();
        TaskExample.Criteria criteria = example.createCriteria();
        criteria.andAccountIdEqualTo(quartzTaskDTO.getAccountId()).andTaskNameEqualTo(quartzTaskDTO.getTaskName());
        List<Task> queryTasks = taskMapper.selectByExample(example);
        if (queryTasks.size() > 0) {
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

    public void createNucleiResource(TaskItemWithBLOBs taskItem, Task task) throws Exception {
        LogUtil.info("createResource for taskItem: {}", toJSONString(taskItem));
        String operation = Translator.get("i18n_create_resource");
        String resultStr = "", fileName = "nuclei.yml";
        try {
            TaskItemResourceExample example = new TaskItemResourceExample();
            example.createCriteria().andTaskIdEqualTo(task.getId()).andTaskItemIdEqualTo(taskItem.getId());
            List<TaskItemResourceWithBLOBs> list = taskItemResourceMapper.selectByExampleWithBLOBs(example);
            if (list.isEmpty()) return;

            String dirPath = TaskConstants.RESULT_FILE_PATH_PREFIX + task.getId() + "/" + taskItem.getRegionId();
            AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(taskItem.getAccountId());
            Map<String, String> map = PlatformUtils.getAccount(accountWithBLOBs, taskItem.getRegionId(), proxyMapper.selectByPrimaryKey(accountWithBLOBs.getProxyId()));
            String command = PlatformUtils.fixedCommand(CommandEnum.nuclei.getCommand(), CommandEnum.run.getCommand(), dirPath, fileName, map);
            if(taskItem.getDetails().contains("workflows:")) {
                command = command.replace("-t", "-w");
            }
            LogUtil.info(task.getId() + " {}[command]: " + command);
            CommandUtils.saveAsFile(taskItem.getDetails(), dirPath, fileName);//重启服务后容器内文件在/tmp目录下会丢失
            resultStr = CommandUtils.commonExecCmdWithResultByNuclei(command, dirPath);
            if (LogUtil.getLogger().isDebugEnabled()) {
                LogUtil.getLogger().debug("resource created: {}", resultStr);
            }
            if (resultStr.contains("ERR"))
                throw new Exception(Translator.get("i18n_create_resource_failed") + ": " + resultStr);


            for (TaskItemResourceWithBLOBs taskItemResource : list) {

                String resourceType = taskItemResource.getResourceType();
                String resourceName = taskItemResource.getResourceName();
                String taskItemId = taskItem.getId();
                if (StringUtils.equals(task.getType(), TaskConstants.TaskType.manual.name()))
                    orderService.saveTaskItemLog(taskItemId, "resourceType", Translator.get("i18n_operation_begin") + ": " + operation, StringUtils.EMPTY, true);
                Rule rule = ruleMapper.selectByPrimaryKey(taskItem.getRuleId());
                if (rule == null) {
                    orderService.saveTaskItemLog(taskItemId, taskItemId, Translator.get("i18n_operation_ex") + ": " + operation, Translator.get("i18n_ex_rule_not_exist"), false);
                    throw new Exception(Translator.get("i18n_ex_rule_not_exist") + ":" + taskItem.getRuleId());
                }
                String nucleiRun = resultStr;
                String metadata = resultStr;
                String resources = ReadFileUtils.readToBuffer(dirPath + "/" + TaskConstants.NUCLEI_RUN_RESULT_FILE);

                ResourceWithBLOBs resourceWithBLOBs = new ResourceWithBLOBs();
                if (taskItemResource.getResourceId() != null) {
                    resourceWithBLOBs = resourceMapper.selectByPrimaryKey(taskItemResource.getResourceId());
                }
                resourceWithBLOBs.setCustodianRunLog(nucleiRun);
                resourceWithBLOBs.setMetadata(metadata);
                resourceWithBLOBs.setResources(resources);
                resourceWithBLOBs.setResourceName(resourceName);
                resourceWithBLOBs.setDirName(taskItemResource.getDirName());
                resourceWithBLOBs.setResourceType(resourceType);
                resourceWithBLOBs.setAccountId(taskItem.getAccountId());
                resourceWithBLOBs.setSeverity(taskItem.getSeverity());
                resourceWithBLOBs.setRegionId(taskItem.getRegionId());
                resourceWithBLOBs.setRegionName(taskItem.getRegionName());
                resourceWithBLOBs.setResourceCommand(taskItemResource.getResourceCommand());
                resourceWithBLOBs.setResourceCommandAction(taskItemResource.getResourceCommandAction());
                ResourceWithBLOBs resource = saveResource(resourceWithBLOBs, taskItem, task, taskItemResource);
                LogUtil.info("The returned data is{}: " + new Gson().toJson(resource));
                NucleiCredential nucleiCredential = new Gson().fromJson(accountWithBLOBs.getCredential(), NucleiCredential.class);
                orderService.saveTaskItemLog(taskItemId, resourceType, Translator.get("i18n_operation_end") + ": " + operation, Translator.get("i18n_vuln") + ": " + resource.getPluginName() + "，"
                        + Translator.get("i18n_domain") + ": " + nucleiCredential.getTargetAddress() + "，" + Translator.get("i18n_rule_type") + ": " + resourceType + "，" + Translator.get("i18n_resource_manage") + ": " + resource.getReturnSum() + "/" + resource.getResourcesSum(), true);
                //执行完删除返回目录文件，以便于下一次操作覆盖
                String deleteResourceDir = "rm -rf " + dirPath;
                CommandUtils.commonExecCmdWithResult(deleteResourceDir, dirPath);
            }

        } catch (Exception e) {
            orderService.saveTaskItemLog(taskItem.getId(), taskItem.getId(), Translator.get("i18n_operation_ex") + ": " + operation, e.getMessage(), false);
            LogUtil.error("createResource, taskItemId: " + taskItem.getId() + ", resultStr:" + resultStr, ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    public ResourceWithBLOBs saveResource(ResourceWithBLOBs resourceWithBLOBs, TaskItemWithBLOBs taskItem, Task task, TaskItemResourceWithBLOBs taskItemResource) {
        try {
            //保存创建的资源
            long now = System.currentTimeMillis();
            resourceWithBLOBs.setCreateTime(now);
            resourceWithBLOBs.setUpdateTime(now);
            resourceWithBLOBs.setResourcesSum((long) 1);
            if (StringUtils.isNotEmpty(resourceWithBLOBs.getResources())) {
                resourceWithBLOBs.setReturnSum((long) 1);
            } else {
                resourceWithBLOBs.setReturnSum((long) 0);
            }

            AccountWithBLOBs account = accountMapper.selectByPrimaryKey(resourceWithBLOBs.getAccountId());
            resourceWithBLOBs = updateResourceSum(resourceWithBLOBs, account);
            NucleiCredential nucleiCredential = new Gson().fromJson(account.getCredential(), NucleiCredential.class);
            SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
            Date date = new Date();// 获取当前时间
            String json = "{\n" +
                    "  \"id\": " + "\"" + UUIDUtil.newUUID() + "\"" + ",\n" +
                    "  \"CreatedTime\": " + "\"" + sdf.format(date) + "\"" + ",\n" +
                    "  \"Domain\": " + "\"" + nucleiCredential.getTargetAddress() + "\"" + ",\n" +
                    "  \"Result\": " + "\"" + resourceWithBLOBs.getResources() + "\"" + "\n" +
                    "}";
            //资源详情
            saveResourceItem(resourceWithBLOBs, parseObject(json));


            //资源、规则、申请人关联表
            ResourceRule resourceRule = new ResourceRule();
            resourceRule.setResourceId(resourceWithBLOBs.getId());
            resourceRule.setRuleId(taskItem.getRuleId());
            resourceRule.setApplyUser(task.getApplyUser());
            if (resourceRuleMapper.selectByPrimaryKey(resourceWithBLOBs.getId()) != null) {
                resourceRuleMapper.updateByPrimaryKeySelective(resourceRule);
            } else {
                resourceRuleMapper.insertSelective(resourceRule);
            }

            //任务条目和资源关联表
            taskItemResource.setResourceId(resourceWithBLOBs.getId());
            insertTaskItemResource(taskItemResource);

            //计算sum资源总数与扫描的资源数到task
            int resourceSum = extTaskMapper.getResourceSum(task.getId());
            int returnSum = extTaskMapper.getReturnSum(task.getId());
            task.setResourcesSum((long) resourceSum);
            task.setReturnSum((long) returnSum);
            taskMapper.updateByPrimaryKeySelective(task);
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            HRException.throwException(e.getMessage());
        }

        return resourceWithBLOBs;
    }

    private ResourceWithBLOBs updateResourceSum(ResourceWithBLOBs resourceWithBLOBs, AccountWithBLOBs account) {
        try {
            resourceWithBLOBs.setPluginIcon(account.getPluginIcon());
            resourceWithBLOBs.setPluginName(account.getPluginName());
            resourceWithBLOBs.setPluginId(account.getPluginId());
            if (resourceWithBLOBs.getReturnSum() > 0) {
                resourceWithBLOBs.setResourceStatus(ResourceConstants.RESOURCE_STATUS.NotFixed.name());
            } else {
                resourceWithBLOBs.setResourceStatus(ResourceConstants.RESOURCE_STATUS.NotNeedFix.name());
            }

            if (resourceWithBLOBs.getId() != null) {
                resourceMapper.updateByPrimaryKeySelective(resourceWithBLOBs);
            } else {
                resourceWithBLOBs.setId(UUIDUtil.newUUID());
                resourceMapper.insertSelective(resourceWithBLOBs);
            }
        } catch (Exception e) {
            LogUtil.error("[{}] Generate updateResourceSum nuclei.yml file，and nuclei run failed:{}", resourceWithBLOBs.getId(), e.getMessage());
            throw e;
        }
        return resourceWithBLOBs;
    }

    private void saveResourceItem(ResourceWithBLOBs resourceWithBLOBs, JSONObject jsonObject) {
        ResourceItem resourceItem = new ResourceItem();
        try{
            String fid = UUIDUtil.newUUID();
            resourceItem.setAccountId(resourceWithBLOBs.getAccountId());
            resourceItem.setUpdateTime(System.currentTimeMillis());
            resourceItem.setPluginIcon(resourceWithBLOBs.getPluginIcon());
            resourceItem.setPluginId(resourceWithBLOBs.getPluginId());
            resourceItem.setPluginName(resourceWithBLOBs.getPluginName());
            resourceItem.setRegionId(resourceWithBLOBs.getRegionId());
            resourceItem.setRegionName(resourceWithBLOBs.getRegionName());
            resourceItem.setResourceId(resourceWithBLOBs.getId());
            resourceItem.setSeverity(resourceWithBLOBs.getSeverity());
            resourceItem.setResourceType(resourceWithBLOBs.getResourceType());
            resourceItem.setHummerId(fid);
            resourceItem.setResource(jsonObject.toJSONString());

            ResourceItemExample example = new ResourceItemExample();
            example.createCriteria().andResourceIdEqualTo(resourceWithBLOBs.getId());
            List<ResourceItem> resourceItems = resourceItemMapper.selectByExampleWithBLOBs(example);
            if (!resourceItems.isEmpty()) {
                resourceItem.setId(resourceItems.get(0).getId());
                resourceItemMapper.updateByPrimaryKeySelective(resourceItem);
            } else {
                resourceItem.setId(UUIDUtil.newUUID());
                resourceItem.setCreateTime(System.currentTimeMillis());
                resourceItemMapper.insertSelective(resourceItem);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            throw e;
        }
    }

    private void insertTaskItemResource(TaskItemResourceWithBLOBs taskItemResource) {
        if (taskItemResource.getId() != null) {
            taskItemResourceMapper.updateByPrimaryKeySelective(taskItemResource);
        } else {
            taskItemResourceMapper.insertSelective(taskItemResource);
        }
    }
}
