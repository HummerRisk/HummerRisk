package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtCloudTaskMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.dto.QuartzTaskDTO;
import com.hummerrisk.i18n.Translator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @author harris
 */
@Service
public class ProwlerService {

    @Resource @Lazy
    private CloudTaskMapper cloudTaskMapper;
    @Resource @Lazy
    private CloudTaskItemMapper cloudTaskItemMapper;
    @Resource @Lazy
    private CloudTaskItemLogMapper cloudTaskItemLogMapper;
    @Resource @Lazy
    private CommonThreadPool commonThreadPool;
    @Resource @Lazy
    private CloudTaskItemResourceMapper cloudTaskItemResourceMapper;
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
    private ExtCloudTaskMapper extCloudTaskMapper;
    @Resource @Lazy
    private HistoryService historyService;
    @Resource @Lazy
    private HistoryCloudTaskMapper historyCloudTaskMapper;

    public CloudTask createTask(QuartzTaskDTO quartzTaskDTO, String status, String messageOrderId) throws Exception {
        CloudTask cloudTask = createTaskOrder(quartzTaskDTO, status, messageOrderId);
        String taskId = cloudTask.getId();

        String script = quartzTaskDTO.getScript();
        JSONArray jsonArray = JSON.parseArray(quartzTaskDTO.getParameter());
        String groupName = "group1";
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            groupName = jsonObject.getString("defaultValue");
        }

        this.deleteTaskItems(taskId);
        List<String> resourceTypes = new ArrayList();
        resourceTypes.add(groupName);
        for (SelectTag selectTag : quartzTaskDTO.getSelectTags()) {
            for (String regionId : selectTag.getRegions()) {
                CloudTaskItemWithBLOBs taskItemWithBLOBs = new CloudTaskItemWithBLOBs();
                String uuid = UUIDUtil.newUUID();
                taskItemWithBLOBs.setId(uuid);
                taskItemWithBLOBs.setTaskId(taskId);
                taskItemWithBLOBs.setRuleId(quartzTaskDTO.getId());
                taskItemWithBLOBs.setCustomData(script);
                taskItemWithBLOBs.setStatus(CloudTaskConstants.TASK_STATUS.UNCHECKED.name());
                taskItemWithBLOBs.setSeverity(quartzTaskDTO.getSeverity());
                taskItemWithBLOBs.setCreateTime(cloudTask.getCreateTime());
                taskItemWithBLOBs.setAccountId(selectTag.getAccountId());
                AccountWithBLOBs account = accountMapper.selectByPrimaryKey(selectTag.getAccountId());
                taskItemWithBLOBs.setAccountUrl(account.getPluginIcon());
                taskItemWithBLOBs.setAccountLabel(account.getName());
                taskItemWithBLOBs.setRegionId(regionId);
                taskItemWithBLOBs.setRegionName(PlatformUtils.tranforRegionId2RegionName(regionId, cloudTask.getPluginId()));
                taskItemWithBLOBs.setTags(cloudTask.getRuleTags());
                cloudTaskItemMapper.insertSelective(taskItemWithBLOBs);

                historyService.insertHistoryCloudTaskItem(BeanUtils.copyBean(new HistoryCloudTaskItemWithBLOBs(), taskItemWithBLOBs));//插入历史数据

                final String finalScript = script;
                final String finalDirName = groupName;
                commonThreadPool.addTask(() -> {
                    String resourceType = finalDirName;

                    CloudTaskItemResourceWithBLOBs taskItemResource = new CloudTaskItemResourceWithBLOBs();
                    taskItemResource.setTaskId(taskId);
                    taskItemResource.setTaskItemId(taskItemWithBLOBs.getId());
                    taskItemResource.setDirName(finalDirName);
                    taskItemResource.setResourceType(resourceType);
                    taskItemResource.setResourceName(finalDirName);

                    //包含actions
                    taskItemResource.setResourceCommandAction(finalScript);

                    //不包含actions
                    taskItemResource.setResourceCommand(finalScript);
                    cloudTaskItemResourceMapper.insertSelective(taskItemResource);

                    try {
                        HistoryCloudTaskResourceWithBLOBs historyCloudTaskResourceWithBLOBs = new HistoryCloudTaskResourceWithBLOBs();
                        BeanUtils.copyBean(historyCloudTaskResourceWithBLOBs, taskItemResource);
                        historyService.insertHistoryCloudTaskResource(historyCloudTaskResourceWithBLOBs);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    taskItemWithBLOBs.setDetails(finalScript);
                    cloudTaskItemMapper.updateByPrimaryKeySelective(taskItemWithBLOBs);

                    try {
                        historyService.updateHistoryCloudTaskItem(BeanUtils.copyBean(new HistoryCloudTaskItemWithBLOBs(), taskItemWithBLOBs));//插入历史数据
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    cloudTask.setResourceTypes(new HashSet<>(resourceTypes).toString());
                    cloudTaskMapper.updateByPrimaryKeySelective(cloudTask);

                    try {
                        historyService.updateHistoryCloudTask(BeanUtils.copyBean(new HistoryCloudTask(), cloudTask));//插入历史数据
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        //向首页活动添加操作信息
        OperationLogService.log(SessionUtils.getUser(), taskId, cloudTask.getTaskName(), ResourceTypeConstants.TASK.name(), ResourceOperation.CREATE, "i18n_create_scan_task");
        return cloudTask;
    }

    private CloudTask createTaskOrder(QuartzTaskDTO quartzTaskDTO, String status, String messageOrderId) throws Exception {
        CloudTask cloudTask = new CloudTask();
        cloudTask.setTaskName(quartzTaskDTO.getTaskName() != null ?quartzTaskDTO.getTaskName():quartzTaskDTO.getName());
        cloudTask.setRuleId(quartzTaskDTO.getId());
        cloudTask.setSeverity(quartzTaskDTO.getSeverity());
        cloudTask.setType(quartzTaskDTO.getType());
        cloudTask.setPluginId(quartzTaskDTO.getPluginId());
        cloudTask.setPluginIcon(quartzTaskDTO.getPluginIcon());
        cloudTask.setPluginName(quartzTaskDTO.getPluginName());
        cloudTask.setRuleTags(quartzTaskDTO.getTags().toString());
        cloudTask.setDescription(quartzTaskDTO.getDescription());
        cloudTask.setAccountId(quartzTaskDTO.getAccountId());
        cloudTask.setApplyUser(Objects.requireNonNull(SessionUtils.getUser()).getId());
        cloudTask.setStatus(status);
        cloudTask.setScanType(ScanTypeConstants.prowler.name());
        if (quartzTaskDTO.getCron() != null){
            cloudTask.setCron(quartzTaskDTO.getCron());
            cloudTask.setCronDesc(DescCornUtils.descCorn(quartzTaskDTO.getCron()));
        }

        CloudTaskExample example = new CloudTaskExample();
        CloudTaskExample.Criteria criteria = example.createCriteria();
        criteria.andAccountIdEqualTo(quartzTaskDTO.getAccountId()).andTaskNameEqualTo(quartzTaskDTO.getTaskName());
        List<CloudTask> queryCloudTasks = cloudTaskMapper.selectByExample(example);
        if (!queryCloudTasks.isEmpty()) {
            cloudTask.setId(queryCloudTasks.get(0).getId());
            cloudTask.setCreateTime(System.currentTimeMillis());
            cloudTaskMapper.updateByPrimaryKeySelective(cloudTask);

            HistoryCloudTask historyCloudTask = historyCloudTaskMapper.selectByPrimaryKey(queryCloudTasks.get(0).getId());
            if (historyCloudTask != null) {
                historyService.updateHistoryCloudTask(BeanUtils.copyBean(new HistoryCloudTask(), cloudTask));//插入历史数据
            } else {
                historyService.insertHistoryCloudTask(BeanUtils.copyBean(new HistoryCloudTask(), cloudTask));//插入历史数据
            }

        } else {
            String taskId = IDGenerator.newBusinessId(CloudTaskConstants.TASK_ID_PREFIX, SessionUtils.getUser().getId());
            cloudTask.setId(taskId);
            cloudTask.setCreateTime(System.currentTimeMillis());
            cloudTaskMapper.insertSelective(cloudTask);

            historyService.insertHistoryCloudTask(BeanUtils.copyBean(new HistoryCloudTask(), cloudTask));//插入历史数据
        }

        if (StringUtils.isNotEmpty(messageOrderId)) {
            noticeService.createMessageOrderItem(messageOrderId, cloudTask);
        }

        return cloudTask;
    }

    private void deleteTaskItems (String taskId) {
        CloudTaskItemExample cloudTaskItemExample = new CloudTaskItemExample();
        cloudTaskItemExample.createCriteria().andTaskIdEqualTo(taskId);
        List<CloudTaskItem> cloudTaskItems = cloudTaskItemMapper.selectByExample(cloudTaskItemExample);

        for (CloudTaskItem cloudTaskItem : cloudTaskItems) {
            CloudTaskItemLogExample cloudTaskItemLogExample = new CloudTaskItemLogExample();
            cloudTaskItemLogExample.createCriteria().andTaskItemIdEqualTo(cloudTaskItem.getId());
            cloudTaskItemLogMapper.deleteByExample(cloudTaskItemLogExample);

            CloudTaskItemResourceExample cloudTaskItemResourceExample = new CloudTaskItemResourceExample();
            cloudTaskItemResourceExample.createCriteria().andTaskItemIdEqualTo(cloudTaskItem.getId());
            List<CloudTaskItemResource> cloudTaskItemResources = cloudTaskItemResourceMapper.selectByExample(cloudTaskItemResourceExample);
            for (CloudTaskItemResource cloudTaskItemResource : cloudTaskItemResources) {
                resourceMapper.deleteByPrimaryKey(cloudTaskItemResource.getResourceId());
                resourceRuleMapper.deleteByPrimaryKey(cloudTaskItemResource.getResourceId());
            }
            cloudTaskItemResourceMapper.deleteByExample(cloudTaskItemResourceExample);
        }
        cloudTaskItemMapper.deleteByExample(cloudTaskItemExample);
    }

    public void createProwlerResource(CloudTaskItemWithBLOBs taskItem, CloudTask cloudTask) throws Exception {
        LogUtil.info("createResource for taskItem: {}", toJSONString(taskItem));
        String operation = "i18n_create_resource";
        String resultStr = "";
        String fileName = cloudTask.getResourceTypes().replace("[", "").replace("]", "");
        try {
            CloudTaskItemResourceExample example = new CloudTaskItemResourceExample();
            example.createCriteria().andTaskIdEqualTo(cloudTask.getId()).andTaskItemIdEqualTo(taskItem.getId());
            List<CloudTaskItemResourceWithBLOBs> list = cloudTaskItemResourceMapper.selectByExampleWithBLOBs(example);
            if (list.isEmpty()) return;

            String dirPath = CloudTaskConstants.PROWLER_RESULT_FILE_PATH;
            AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(taskItem.getAccountId());
            Map<String, String> map = PlatformUtils.getAccount(accountWithBLOBs, taskItem.getRegionId(), proxyMapper.selectByPrimaryKey(accountWithBLOBs.getProxyId()));
            String command = PlatformUtils.fixedCommand(CommandEnum.prowler.getCommand(), CommandEnum.run.getCommand(), dirPath, fileName, map);
            LogUtil.info(cloudTask.getId() + " {}[command]: " + command);
            resultStr = CommandUtils.commonExecCmdWithResult(command, dirPath);
            if (LogUtil.getLogger().isDebugEnabled()) {
                LogUtil.getLogger().debug("resource created: {}", resultStr);
            }

            for (CloudTaskItemResourceWithBLOBs taskItemResource : list) {

                String resourceType = taskItemResource.getResourceType();
                String resourceName = taskItemResource.getResourceName();
                String taskItemId = taskItem.getId();
                if (StringUtils.equals(cloudTask.getType(), CloudTaskConstants.TaskType.manual.name()))
                    orderService.saveTaskItemLog(taskItemId, taskItemResource.getResourceId()!=null?taskItemResource.getResourceId():"", "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY, true, CloudTaskConstants.HISTORY_TYPE.Cloud.name());
                Rule rule = ruleMapper.selectByPrimaryKey(taskItem.getRuleId());
                if (rule == null) {
                    orderService.saveTaskItemLog(taskItemId, taskItemResource.getResourceId()!=null?taskItemResource.getResourceId():"", "i18n_operation_ex" + ": " + operation, "i18n_ex_rule_not_exist", false, CloudTaskConstants.HISTORY_TYPE.Cloud.name());
                    throw new Exception(Translator.get("i18n_ex_rule_not_exist") + ":" + taskItem.getRuleId());
                }
                String prowlerRun = ReadFileUtils.readToBuffer(dirPath + "/" + CloudTaskConstants.PROWLER_RUN_RESULT_FILE);
                String metadata = taskItem.getCustomData();
                String resources = ReadFileUtils.readToBuffer(dirPath + "/" + CloudTaskConstants.PROWLER_RUN_RESULT_FILE);

                ResourceWithBLOBs resourceWithBLOBs = new ResourceWithBLOBs();
                if (taskItemResource.getResourceId() != null) {
                    resourceWithBLOBs = resourceMapper.selectByPrimaryKey(taskItemResource.getResourceId());
                }
                resourceWithBLOBs.setCustodianRunLog(prowlerRun);
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
                ResourceWithBLOBs resource = saveResource(resourceWithBLOBs, taskItem, cloudTask, taskItemResource);
                LogUtil.info("The returned data is{}: " + new Gson().toJson(resource));
                orderService.saveTaskItemLog(taskItemId, resource.getId(), "i18n_operation_end" + ": " + operation, "i18n_cloud_account" + ": " + resource.getPluginName() + "，"
                        + "i18n_region" + ": " + resource.getRegionName() + "，" + "i18n_rule_type" + ": " + resourceType + "，" + "i18n_resource_manage" + ": " + resource.getReturnSum() + "/" + resource.getResourcesSum(),
                        true, CloudTaskConstants.HISTORY_TYPE.Cloud.name());
            }

        } catch (Exception e) {
            orderService.saveTaskItemLog(taskItem.getId(), "", "i18n_operation_ex" + ": " + operation, e.getMessage(), false, CloudTaskConstants.HISTORY_TYPE.Cloud.name());
            LogUtil.error("createResource, taskItemId: " + taskItem.getId() + ", resultStr:" + resultStr, ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    public ResourceWithBLOBs saveResource(ResourceWithBLOBs resourceWithBLOBs, CloudTaskItemWithBLOBs taskItem, CloudTask cloudTask, CloudTaskItemResourceWithBLOBs taskItemResource) {
        try {
            //保存创建的资源
            long now = System.currentTimeMillis();
            resourceWithBLOBs.setCreateTime(now);
            resourceWithBLOBs.setUpdateTime(now);

            long passNum = resourceWithBLOBs.getResources()!=null?appearNumber(resourceWithBLOBs.getResources(), "PASS!"):0;
            long failNum = resourceWithBLOBs.getResources()!=null?appearNumber(resourceWithBLOBs.getResources(), "FAIL!"):0;
            long infoNum = resourceWithBLOBs.getResources()!=null?appearNumber(resourceWithBLOBs.getResources(), "INFO!"):0;
            long warnNum = resourceWithBLOBs.getResources()!=null?appearNumber(resourceWithBLOBs.getResources(), "WARN!"):0;
            resourceWithBLOBs.setResourcesSum(passNum + failNum + infoNum + warnNum);
            resourceWithBLOBs.setReturnSum(failNum);

            AccountWithBLOBs account = accountMapper.selectByPrimaryKey(resourceWithBLOBs.getAccountId());
            resourceWithBLOBs = updateResourceSum(resourceWithBLOBs, account, taskItemResource);
            try {
                File file = new File(CloudTaskConstants.PROWLER_RESULT_FILE_PATH + "/" + CloudTaskConstants.PROWLER_RUN_RESULT_FILE);
                if (file.isFile() && file.exists()) { // 判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                            new FileInputStream(file), "UTF-8");// 考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while ((lineTxt = bufferedReader.readLine()) != null) {
                        if(lineTxt.contains("FAIL!")){
                            SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
                            sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
                            Date date = new Date();// 获取当前时间
                            String fid = UUIDUtil.newUUID();
                            lineTxt = lineTxt.replaceAll("", "").replaceAll("\u001B", "");
                            String json = "{\n" +
                                    "  \"id\": " + "\"" + fid + "\"" + ",\n" +
                                    "  \"ScanTime\": " + "\"" + sdf.format(date) + "\"" + ",\n" +
                                    "  \"RegionName\": " + "\"" + resourceWithBLOBs.getRegionName() + "\"" + ",\n" +
                                    "  \"Result\": " + "\"" + lineTxt + "\"" + "\n" +
                                    "}";
                            //资源详情
                            saveResourceItem(resourceWithBLOBs, json, fid);
                        }
                    }
                    bufferedReader.close();
                    read.close();

                } else {
                    LogUtil.error(Translator.get("i18n_not_found_file"));
                    throw new Exception(Translator.get("i18n_not_found_file"));
                }
            } catch (Exception error) {
                LogUtil.error(error.getMessage(), Translator.get("i18n_read_file_error"));
                throw new Exception(error.getMessage());
            }

            //资源、规则、申请人关联表
            ResourceRule resourceRule = new ResourceRule();
            resourceRule.setResourceId(resourceWithBLOBs.getId());
            resourceRule.setRuleId(taskItem.getRuleId());
            resourceRule.setApplyUser(cloudTask.getApplyUser());
            if (resourceRuleMapper.selectByPrimaryKey(resourceWithBLOBs.getId()) != null) {
                resourceRuleMapper.updateByPrimaryKeySelective(resourceRule);
            } else {
                resourceRuleMapper.insertSelective(resourceRule);
            }

            //任务条目和资源关联表
            taskItemResource.setResourceId(resourceWithBLOBs.getId());
            insertTaskItemResource(taskItemResource);

            //计算sum资源总数与检测的资源数到task
            int resourceSum = extCloudTaskMapper.getResourceSum(cloudTask.getId());
            int returnSum = extCloudTaskMapper.getReturnSum(cloudTask.getId());
            cloudTask.setResourcesSum((long) resourceSum);
            cloudTask.setReturnSum((long) returnSum);
            cloudTaskMapper.updateByPrimaryKeySelective(cloudTask);
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            HRException.throwException(e.getMessage());
        }

        return resourceWithBLOBs;
    }

    private ResourceWithBLOBs updateResourceSum(ResourceWithBLOBs resourceWithBLOBs, AccountWithBLOBs account, CloudTaskItemResource cloudTaskItemResource) throws Exception {
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
            LogUtil.error(resourceWithBLOBs.getId(), e.getMessage());
            throw e;
        }
        return resourceWithBLOBs;
    }

    private void saveResourceItem(ResourceWithBLOBs resourceWithBLOBs, String json, String fid) throws Exception {
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

    private void insertTaskItemResource(CloudTaskItemResourceWithBLOBs taskItemResource) throws Exception {
        if (taskItemResource.getId() != null) {
            cloudTaskItemResourceMapper.updateByPrimaryKeySelective(taskItemResource);

            historyService.updateHistoryCloudTaskResource(BeanUtils.copyBean(new HistoryCloudTaskResourceWithBLOBs(), taskItemResource));
        } else {
            cloudTaskItemResourceMapper.insertSelective(taskItemResource);

            historyService.insertHistoryCloudTaskResource(BeanUtils.copyBean(new HistoryCloudTaskResourceWithBLOBs(), taskItemResource));
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
