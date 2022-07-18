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

    public CloudTask createTask(QuartzTaskDTO quartzTaskDTO, String status, String messageOrderId) throws Exception {
        CloudTask cloudTask = createTaskOrder(quartzTaskDTO, status, messageOrderId);
        String taskId = cloudTask.getId();

        String script = quartzTaskDTO.getScript();
        String groupName = "xss";
        JSONArray jsonArray = JSON.parseArray(quartzTaskDTO.getParameter());
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            groupName = jsonObject.getString("defaultValue");
        }

        this.deleteTaskItems(cloudTask.getId());
        List<String> resourceTypes = new ArrayList();
        resourceTypes.add(groupName);
        for (SelectTag selectTag : quartzTaskDTO.getSelectTags()) {
            for (String regionId : selectTag.getRegions()) {
                CloudTaskItemWithBLOBs taskItemWithBLOBs = new CloudTaskItemWithBLOBs();
                String uuid = UUIDUtil.newUUID();
                taskItemWithBLOBs.setId(uuid);
                taskItemWithBLOBs.setTaskId(cloudTask.getId());
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


                    taskItemWithBLOBs.setDetails(finalScript);
                    cloudTaskItemMapper.updateByPrimaryKeySelective(taskItemWithBLOBs);

                    cloudTask.setResourceTypes(resourceTypes.stream().collect(Collectors.toSet()).toString());
                    cloudTaskMapper.updateByPrimaryKeySelective(cloudTask);
                });
            }
        }
        //向首页活动添加操作信息
        OperationLogService.log(SessionUtils.getUser(), taskId, cloudTask.getTaskName(), ResourceTypeConstants.TASK.name(), ResourceOperation.CREATE, "创建检测任务");
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
        cloudTask.setApplyUser(SessionUtils.getUser().getId());
        cloudTask.setStatus(status);
        cloudTask.setScanType(ScanTypeConstants.xray.name());
        if (quartzTaskDTO.getCron() != null){
            cloudTask.setCron(quartzTaskDTO.getCron());
            cloudTask.setCronDesc(DescCornUtils.descCorn(quartzTaskDTO.getCron()));
        }

        CloudTaskExample example = new CloudTaskExample();
        CloudTaskExample.Criteria criteria = example.createCriteria();
        criteria.andAccountIdEqualTo(quartzTaskDTO.getAccountId()).andTaskNameEqualTo(quartzTaskDTO.getTaskName());
        List<CloudTask> queryCloudTasks = cloudTaskMapper.selectByExample(example);
        if (queryCloudTasks.size() > 0) {
            cloudTask.setId(queryCloudTasks.get(0).getId());
            cloudTask.setCreateTime(System.currentTimeMillis());
            cloudTaskMapper.updateByPrimaryKeySelective(cloudTask);
        } else {
            String taskId = IDGenerator.newBusinessId(CloudTaskConstants.TASK_ID_PREFIX, SessionUtils.getUser().getId());
            cloudTask.setId(taskId);
            cloudTask.setCreateTime(System.currentTimeMillis());
            cloudTaskMapper.insertSelective(cloudTask);
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

    public void createXrayResource(CloudTaskItemWithBLOBs taskItem, CloudTask cloudTask) throws Exception {
        LogUtil.info("createResource for taskItem: {}", toJSONString(taskItem));
        String operation = "i18n_create_resource";
        String resultStr = "";
        String fileName = cloudTask.getResourceTypes().replace("[", "").replace("]", "");
        try {
            CloudTaskItemResourceExample example = new CloudTaskItemResourceExample();
            example.createCriteria().andTaskIdEqualTo(cloudTask.getId()).andTaskItemIdEqualTo(taskItem.getId());
            List<CloudTaskItemResourceWithBLOBs> list = cloudTaskItemResourceMapper.selectByExampleWithBLOBs(example);
            if (list.isEmpty()) return;

            String dirPath = CloudTaskConstants.XRAY_RESULT_FILE_PATH + cloudTask.getId();
            AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(taskItem.getAccountId());
            Map<String, String> map = PlatformUtils.getAccount(accountWithBLOBs, taskItem.getRegionId(), proxyMapper.selectByPrimaryKey(accountWithBLOBs.getProxyId()));
            String command = PlatformUtils.fixedCommand(CommandEnum.xray.getCommand(), CommandEnum.run.getCommand(), dirPath, fileName, map);

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
                    orderService.saveTaskItemLog(taskItemId, "resourceType", "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY, true);
                Rule rule = ruleMapper.selectByPrimaryKey(taskItem.getRuleId());
                if (rule == null) {
                    orderService.saveTaskItemLog(taskItemId, taskItemId, "i18n_operation_ex" + ": " + operation, "i18n_ex_rule_not_exist", false);
                    throw new Exception("i18n_ex_rule_not_exist" + ":" + taskItem.getRuleId());
                }
                String xrayRun = command;
                String metadata = resultStr;
                String resources = ReadFileUtils.readToBufferByXray(dirPath + "/" + CloudTaskConstants.XRAY_RUN_RESULT_FILE, resultStr);

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
                ResourceWithBLOBs resource = saveResource(resourceWithBLOBs, taskItem, cloudTask, taskItemResource);
                LogUtil.info("The returned data is{}: " + new Gson().toJson(resource));
                XrayCredential xrayCredential = new Gson().fromJson(accountWithBLOBs.getCredential(), XrayCredential.class);
                orderService.saveTaskItemLog(taskItemId, resourceType, "i18n_operation_end" + ": " + operation, "i18n_vuln" + ": " + resource.getPluginName() + "，"
                        + "i18n_domain" + ": " + xrayCredential.getTargetAddress() + "，" + "i18n_rule_type" + ": " + resourceType + "，" + "i18n_resource_manage" + ": " + resource.getReturnSum() + "/" + resource.getResourcesSum(), true);
                //执行完删除返回目录文件，以便于下一次操作覆盖
                String deleteResourceDir = "rm -rf " + dirPath;
                CommandUtils.commonExecCmdWithResult(deleteResourceDir, dirPath);
            }

        } catch (Exception e) {
            orderService.saveTaskItemLog(taskItem.getId(), taskItem.getId(), "i18n_operation_ex" + ": " + operation, e.getMessage(), false);
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

    private void insertTaskItemResource(CloudTaskItemResourceWithBLOBs taskItemResource) {
        if (taskItemResource.getId() != null) {
            cloudTaskItemResourceMapper.updateByPrimaryKeySelective(taskItemResource);
        } else {
            cloudTaskItemResourceMapper.insertSelective(taskItemResource);
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
