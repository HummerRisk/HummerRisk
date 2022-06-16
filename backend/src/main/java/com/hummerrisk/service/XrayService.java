package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtTaskMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.dto.QuartzTaskDTO;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.proxy.xray.XrayCredential;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @author harris
 */
@Service
public class XrayService {

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
        String groupName = "xss";
        JSONArray jsonArray = JSON.parseArray(quartzTaskDTO.getParameter());
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            groupName = jsonObject.getString("defaultValue");
        }

        this.deleteTaskItems(task.getId());
        List<String> resourceTypes = new ArrayList();
        resourceTypes.add(groupName);
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
                final String finalDirName = groupName;
                commonThreadPool.addTask(() -> {
                    String resourceType = finalDirName;

                    TaskItemResourceWithBLOBs taskItemResource = new TaskItemResourceWithBLOBs();
                    taskItemResource.setTaskId(taskId);
                    taskItemResource.setTaskItemId(taskItemWithBLOBs.getId());
                    taskItemResource.setDirName(finalDirName);
                    taskItemResource.setResourceType(resourceType);
                    taskItemResource.setResourceName(finalDirName);

                    //包含actions
                    taskItemResource.setResourceCommandAction(finalScript);

                    //不包含actions
                    taskItemResource.setResourceCommand(finalScript);
                    taskItemResourceMapper.insertSelective(taskItemResource);


                    taskItemWithBLOBs.setDetails(finalScript);
                    taskItemMapper.updateByPrimaryKeySelective(taskItemWithBLOBs);

                    task.setResourceTypes(resourceTypes.stream().collect(Collectors.toSet()).toString());
                    taskMapper.updateByPrimaryKeySelective(task);
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
        task.setScanType(ScanTypeConstants.xray.name());
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

    public void createXrayResource(TaskItemWithBLOBs taskItem, Task task) throws Exception {
        LogUtil.info("createResource for taskItem: {}", toJSONString(taskItem));
        String operation = Translator.get("i18n_create_resource");
        String resultStr = "";
        String fileName = task.getResourceTypes().replace("[", "").replace("]", "");
        try {
            TaskItemResourceExample example = new TaskItemResourceExample();
            example.createCriteria().andTaskIdEqualTo(task.getId()).andTaskItemIdEqualTo(taskItem.getId());
            List<TaskItemResourceWithBLOBs> list = taskItemResourceMapper.selectByExampleWithBLOBs(example);
            if (list.isEmpty()) return;

            String dirPath = TaskConstants.XRAY_RESULT_FILE_PATH + task.getId();
            AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(taskItem.getAccountId());
            Map<String, String> map = PlatformUtils.getAccount(accountWithBLOBs, taskItem.getRegionId(), proxyMapper.selectByPrimaryKey(accountWithBLOBs.getProxyId()));
            String command = PlatformUtils.fixedCommand(CommandEnum.xray.getCommand(), CommandEnum.run.getCommand(), dirPath, fileName, map);

            LogUtil.info(task.getId() + " {}[command]: " + command);
            resultStr = CommandUtils.commonExecCmdWithResult(command, dirPath);
            if (LogUtil.getLogger().isDebugEnabled()) {
                LogUtil.getLogger().debug("resource created: {}", resultStr);
            }

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
                String xrayRun = command;
                String metadata = resultStr;
                String resources = ReadFileUtils.readToBufferByXray(dirPath + "/" + TaskConstants.XRAY_RUN_RESULT_FILE, resultStr);

                ResourceWithBLOBs resourceWithBLOBs = new ResourceWithBLOBs();
                if (taskItemResource.getResourceId() != null) {
                    resourceWithBLOBs = resourceMapper.selectByPrimaryKey(taskItemResource.getResourceId());
                }
                resourceWithBLOBs.setCustodianRunLog(xrayRun);
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
                XrayCredential xrayCredential = new Gson().fromJson(accountWithBLOBs.getCredential(), XrayCredential.class);
                orderService.saveTaskItemLog(taskItemId, resourceType, Translator.get("i18n_operation_end") + ": " + operation, Translator.get("i18n_vuln") + ": " + resource.getPluginName() + "，"
                        + Translator.get("i18n_domain") + ": " + xrayCredential.getTargetAddress() + "，" + Translator.get("i18n_rule_type") + ": " + resourceType + "，" + Translator.get("i18n_resource_manage") + ": " + resource.getReturnSum() + "/" + resource.getResourcesSum(), true);
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

            long failNum = resourceWithBLOBs.getMetadata()!=null?appearNumber(resourceWithBLOBs.getMetadata(), "[ERRO]"):0;
            long infoNum = resourceWithBLOBs.getMetadata()!=null?appearNumber(resourceWithBLOBs.getMetadata(), "[INFO]"):0;
            long warnNum = resourceWithBLOBs.getMetadata()!=null?appearNumber(resourceWithBLOBs.getMetadata(), "[WARN]"):0;

            resourceWithBLOBs.setResourcesSum(failNum + infoNum + warnNum);
            resourceWithBLOBs.setReturnSum(failNum);

            AccountWithBLOBs account = accountMapper.selectByPrimaryKey(resourceWithBLOBs.getAccountId());
            resourceWithBLOBs = updateResourceSum(resourceWithBLOBs, account);
            XrayCredential xrayCredential = new Gson().fromJson(account.getCredential(), XrayCredential.class);
            InputStreamReader read = new InputStreamReader(new ByteArrayInputStream(resourceWithBLOBs.getMetadata().getBytes()));
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                if(lineTxt.contains("[ERRO]")){
                    SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
                    sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
                    Date date = new Date();// 获取当前时间
                    String fid = UUIDUtil.newUUID();
                    String json = "{\n" +
                            "  \"id\": " + "\"" + fid + "\"" + ",\n" +
                            "  \"ScanTime\": " + "\"" + sdf.format(date) + "\"" + ",\n" +
                            "  \"Domain\": " + "\"" + xrayCredential.getTargetAddress() + "\"" + ",\n" +
                            "  \"Result\": " + "\"" + lineTxt.replace("\"", "'") + "\"" + "\n" +
                            "}";
                    //资源详情
                    saveResourceItem(resourceWithBLOBs, json, fid);
                }
            }
            bufferedReader.close();
            read.close();

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

    private void saveResourceItem(ResourceWithBLOBs resourceWithBLOBs, String json, String fid) {
        ResourceItem resourceItem = new ResourceItem();
        try{
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
            resourceItem.setResource(json);

            resourceItem.setId(fid);
            resourceItem.setCreateTime(System.currentTimeMillis());
            resourceItemMapper.insertSelective(resourceItem);
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            throw e;
        }
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
            LogUtil.error("[{}] Generate updateResourceSum xray.yml file，and xray run failed:{}", resourceWithBLOBs.getId(), e.getMessage());
            throw e;
        }
        return resourceWithBLOBs;
    }

    private void insertTaskItemResource(TaskItemResourceWithBLOBs taskItemResource) {
        if (taskItemResource.getId() != null) {
            taskItemResourceMapper.updateByPrimaryKeySelective(taskItemResource);
        } else {
            taskItemResourceMapper.insertSelective(taskItemResource);
        }
    }

    /**
     * 获取指定字符串出现的次数
     *
     * @param srcText 源字符串
     * @param findText 要查找的字符串
     * @return
     */
    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }
}
