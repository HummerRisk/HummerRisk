package com.hummer.cloud.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hummer.cloud.mapper.*;
import com.hummer.cloud.oss.constants.OSSConstants;
import com.hummer.common.core.constant.CloudTaskConstants;
import com.hummer.common.core.constant.CommandEnum;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.resource.ResourceRequest;
import com.hummer.common.core.dto.ResourceDTO;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.*;
import com.hummer.system.api.ISystemProviderService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.alibaba.fastjson2.JSON.toJSONString;

/**
 * @author harris
 */
@Service
public class ResourceCreateService {
    // 只有一个任务在处理，防止超配
    private static ConcurrentHashMap<String, String> processingGroupIdMap = new ConcurrentHashMap<>();
    @Autowired
    private CloudTaskMapper cloudTaskMapper;
    @Autowired
    private OssMapper ossMapper;
    @Autowired
    private OssService ossService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CloudTaskItemMapper cloudTaskItemMapper;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private CloudTaskItemResourceMapper cloudTaskItemResourceMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private ProxyMapper proxyMapper;
    @Autowired
    private ProwlerService prowlerService;
    @Autowired
    private CloudResourceSyncMapper cloudResourceSyncMapper;
    @Autowired
    private CloudResourceSyncItemMapper cloudResourceSyncItemMapper;
    @Autowired
    private CloudResourceRelaMapper cloudResourceRelaMapper;
    @Autowired
    private CloudResourceRelaLinkMapper cloudResourceRelaLinkMapper;
    @Autowired
    private CommonThreadPool commonThreadPool;
    @Autowired
    private K8sCreateService k8sCreateService;
    @DubboReference
    private ISystemProviderService systemProviderService;

    @Autowired
    @Qualifier("loadBalanced")
    private RestTemplate restTemplate;

    //云资源检测
    @XxlJob("cloudTasksJobHandler")
    public void cloudTasksJobHandler() throws Exception {
        //云规则检测
        final CloudTaskExample cloudTaskExample = new CloudTaskExample();
        CloudTaskExample.Criteria criteria = cloudTaskExample.createCriteria();
        criteria.andStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString()).andPluginIdNotIn(PlatformUtils.getK8sPlugin());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            criteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        cloudTaskExample.setOrderByClause("create_time limit 10");
        List<CloudTask> cloudTaskList = cloudTaskMapper.selectByExample(cloudTaskExample);
        if (CollectionUtils.isNotEmpty(cloudTaskList)) {
            cloudTaskList.forEach(task -> {
                LogUtil.info("handling cloudTask: {}", toJSONString(task));
                final CloudTask cloudTaskToBeProceed;
                try {
                    cloudTaskToBeProceed = BeanUtils.copyBean(new CloudTask(), task);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(cloudTaskToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(cloudTaskToBeProceed.getId(), cloudTaskToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        handleTask(cloudTaskToBeProceed);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.error(e.getMessage());
                    } finally {
                        processingGroupIdMap.remove(cloudTaskToBeProceed.getId());
                    }
                });
            });
        }

        //K8s 规则检测
        final CloudTaskExample cloudTaskExample2 = new CloudTaskExample();
        CloudTaskExample.Criteria criteria2 = cloudTaskExample2.createCriteria();
        criteria2.andStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString()).andPluginIdIn(PlatformUtils.getK8sPlugin());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            criteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        cloudTaskExample2.setOrderByClause("create_time limit 10");
        List<CloudTask> cloudTaskList2 = cloudTaskMapper.selectByExample(cloudTaskExample2);
        if (CollectionUtils.isNotEmpty(cloudTaskList2)) {
            cloudTaskList2.forEach(task2 -> {
                LogUtil.info("handling k8sRuleTask: {}", toJSONString(task2));
                final CloudTask cloudTaskToBeProceed2;
                try {
                    cloudTaskToBeProceed2 = BeanUtils.copyBean(new CloudTask(), task2);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(cloudTaskToBeProceed2.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(cloudTaskToBeProceed2.getId(), cloudTaskToBeProceed2.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        k8sCreateService.handleTask(cloudTaskToBeProceed2);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.error(e.getMessage());
                    } finally {
                        processingGroupIdMap.remove(cloudTaskToBeProceed2.getId());
                    }
                });
            });
        }

    }

    //对象存储
    @XxlJob("ossTasksJobHandler")
    public void ossTasksJobHandler() throws Exception {
        //对象存储
        final OssExample ossExample = new OssExample();
        OssExample.Criteria ossCriteria = ossExample.createCriteria();
        ossCriteria.andSyncStatusEqualTo(OSSConstants.SYNC_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            ossCriteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        ossExample.setOrderByClause("create_time limit 10");
        List<Oss> ossList = ossMapper.selectByExample(ossExample);
        if (CollectionUtils.isNotEmpty(ossList)) {
            ossList.forEach(oss -> {
                final Oss ossToBeProceed;
                try {
                    ossToBeProceed = BeanUtils.copyBean(new Oss(), oss);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(ossToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(ossToBeProceed.getId(), ossToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        ossService.syncBatch(ossToBeProceed.getId(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.error(e.getMessage());
                    } finally {
                        processingGroupIdMap.remove(ossToBeProceed.getId());
                    }
                });
            });
        }
    }

    //云资源同步
    @XxlJob("syncResourceTasksJobHandler")
    public void syncResourceTasksJobHandler() throws Exception {
        //云资源同步
        CloudResourceSyncExample cloudResourceSyncExample = new CloudResourceSyncExample();
        List<String> statusList = new ArrayList<>();
        statusList.add(CloudTaskConstants.TASK_STATUS.APPROVED.name());
        statusList.add(CloudTaskConstants.TASK_STATUS.RUNNING.name());
        cloudResourceSyncExample.createCriteria().andStatusIn(statusList);
        List<CloudResourceSync> cloudResourceSyncs = cloudResourceSyncMapper.selectByExample(cloudResourceSyncExample);
        cloudResourceSyncs.forEach(cloudResourceSync -> {
            String id = cloudResourceSync.getId();
            CloudResourceSyncItemExample cloudResourceSyncItemExample = new CloudResourceSyncItemExample();
            cloudResourceSyncItemExample.createCriteria().andSyncIdEqualTo(id);
            List<CloudResourceSyncItem> cloudResourceSyncItems = cloudResourceSyncItemMapper.selectByExample(cloudResourceSyncItemExample);
            int errorCount = 0;
            int successCount = 0;
            int runningCount = 0;
            long resourceSum = 0;
            for (CloudResourceSyncItem cloudResourceSyncItem : cloudResourceSyncItems) {
                resourceSum += cloudResourceSyncItem.getCount()==null?0:cloudResourceSyncItem.getCount();
                if(CloudTaskConstants.TASK_STATUS.APPROVED.name().equals(cloudResourceSyncItem.getStatus())
                        ||CloudTaskConstants.TASK_STATUS.RUNNING.name().equals(cloudResourceSyncItem.getStatus())
                        ||CloudTaskConstants.TASK_STATUS.UNCHECKED.name().equals(cloudResourceSyncItem.getStatus())) {
                    runningCount++;
                }else if (CloudTaskConstants.TASK_STATUS.ERROR.name().equals(cloudResourceSyncItem.getStatus())){
                    errorCount++;
                } else if (CloudTaskConstants.TASK_STATUS.FINISHED.name().equals(cloudResourceSyncItem.getStatus())) {
                    successCount++;
                }
            }
            String syncStatus = CloudTaskConstants.TASK_STATUS.RUNNING.name();
            if(cloudResourceSyncItems.size() == 0){
                syncStatus =  CloudTaskConstants.TASK_STATUS.FINISHED.name();
            } else if (runningCount == 0 && errorCount>0 && successCount > 0){
                syncStatus = CloudTaskConstants.TASK_STATUS.WARNING.name();
            } else if (runningCount == 0 && errorCount > 0) {
                syncStatus = CloudTaskConstants.TASK_STATUS.ERROR.name();
            }else if (runningCount == 0){
                syncStatus =  CloudTaskConstants.TASK_STATUS.FINISHED.name();
            }
            CloudResourceSync cloudResourceSync1 = new CloudResourceSync();
            cloudResourceSync1.setId(cloudResourceSync.getId());
            cloudResourceSync1.setStatus(syncStatus);
            cloudResourceSync1.setResourcesSum(resourceSum);
            cloudResourceSyncMapper.updateByPrimaryKeySelective(cloudResourceSync1);
        });
    }

    public void handleTask(CloudTask cloudTask) throws Exception {
        String taskId = cloudTask.getId();
        int i = orderService.updateTaskStatus(taskId, CloudTaskConstants.TASK_STATUS.APPROVED.toString(), CloudTaskConstants.TASK_STATUS.PROCESSING.toString());
        if (i == 0) {
            return;
        }
        try {
            CloudTaskItemExample cloudTaskItemExample = new CloudTaskItemExample();
            cloudTaskItemExample.createCriteria().andTaskIdEqualTo(taskId);
            List<CloudTaskItemWithBLOBs> taskItemWithBLOBs = cloudTaskItemMapper.selectByExampleWithBLOBs(cloudTaskItemExample);
            int successCount = 0;
            for (CloudTaskItemWithBLOBs taskItem : taskItemWithBLOBs) {
                if (LogUtil.getLogger().isDebugEnabled()) {
                    LogUtil.getLogger().debug("handling taskItem: {}", toJSONString(taskItem));
                }
                if (handleTaskItem(BeanUtils.copyBean(new CloudTaskItemWithBLOBs(), taskItem), cloudTask)) {
                    successCount++;
                }
            }
            if (!taskItemWithBLOBs.isEmpty() && successCount == 0)
                throw new Exception("Faild to handle all taskItems, taskId: " + cloudTask.getId());
            String taskStatus = CloudTaskConstants.TASK_STATUS.FINISHED.toString();
            if (successCount != taskItemWithBLOBs.size()) {
                taskStatus = CloudTaskConstants.TASK_STATUS.WARNING.toString();
            }
            orderService.updateTaskStatus(taskId, null, taskStatus);

            //更新历史数据状态
            HistoryCloudTask historyCloudTask = BeanUtils.copyBean(new HistoryCloudTask(), cloudTask);
            historyCloudTask.setStatus(taskStatus);
            systemProviderService.updateHistoryCloudTask(historyCloudTask);

        } catch (Exception e) {
            e.printStackTrace();
            orderService.updateTaskStatus(taskId, null, CloudTaskConstants.TASK_STATUS.ERROR.name());

            //更新历史数据状态
            HistoryCloudTask historyCloudTask = BeanUtils.copyBean(new HistoryCloudTask(), cloudTask);
            historyCloudTask.setStatus(CloudTaskConstants.TASK_STATUS.ERROR.name());
            systemProviderService.updateHistoryCloudTask(historyCloudTask);

            LogUtil.error("handleTask, taskId: " + taskId, e);
        }
    }

    private boolean handleTaskItem(CloudTaskItemWithBLOBs taskItem, CloudTask cloudTask) throws Exception {
        orderService.updateTaskItemStatus(taskItem.getId(), CloudTaskConstants.TASK_STATUS.PROCESSING);
        try {
            for (int i = 0; i < taskItem.getCount(); i++) {
                createResource(taskItem, cloudTask);
            }
            orderService.updateTaskItemStatus(taskItem.getId(), CloudTaskConstants.TASK_STATUS.FINISHED);

            //更新历史数据状态
            HistoryCloudTaskItemWithBLOBs historyCloudTaskItemWithBLOBs = BeanUtils.copyBean(new HistoryCloudTaskItemWithBLOBs(), taskItem);
            historyCloudTaskItemWithBLOBs.setStatus(CloudTaskConstants.TASK_STATUS.FINISHED.name());
            systemProviderService.updateHistoryCloudTaskItem(historyCloudTaskItemWithBLOBs);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            orderService.updateTaskItemStatus(taskItem.getId(), CloudTaskConstants.TASK_STATUS.ERROR);

            //更新历史数据状态
            HistoryCloudTaskItemWithBLOBs historyCloudTaskItemWithBLOBs = BeanUtils.copyBean(new HistoryCloudTaskItemWithBLOBs(), taskItem);
            historyCloudTaskItemWithBLOBs.setStatus(CloudTaskConstants.TASK_STATUS.ERROR.name());
            systemProviderService.updateHistoryCloudTaskItem(historyCloudTaskItemWithBLOBs);

            LogUtil.error("handleTaskItem, taskItemId: " + taskItem.getId(), e);
            return false;
        }
    }

    private void createResource(CloudTaskItemWithBLOBs taskItem, CloudTask cloudTask) throws Exception {
        switch (cloudTask.getScanType()) {
            case "custodian":
                if (systemProviderService.license()) {
                    createScannerResource(taskItem, cloudTask);
                    break;
                }
                createCustodianResource(taskItem, cloudTask);
                break;
            case "prowler":
                prowlerService.createProwlerResource(taskItem, cloudTask, null);//云账号检测
                break;
            default:
                throw new IllegalStateException("Unexpected value: scantype");
        }
    }

    //社区版调用
    private void createCustodianResource(CloudTaskItemWithBLOBs taskItem, CloudTask cloudTask) throws Exception {
        LogUtil.info("createResource for taskItem: {}", toJSONString(taskItem));
        String operation = "i18n_create_resource";
        String resultStr = "", fileName = "policy.yml";
        boolean readResource = true;
        try {
            CloudTaskItemResourceExample example = new CloudTaskItemResourceExample();
            example.createCriteria().andTaskIdEqualTo(cloudTask.getId()).andTaskItemIdEqualTo(taskItem.getId());
            List<CloudTaskItemResourceWithBLOBs> list = cloudTaskItemResourceMapper.selectByExampleWithBLOBs(example);
            if (list.isEmpty()) return;

            String dirPath = CloudTaskConstants.RESULT_FILE_PATH_PREFIX + cloudTask.getId() + "/" + taskItem.getRegionId();
            AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(taskItem.getAccountId());
            Map<String, String> map = PlatformUtils.getAccount(accountWithBLOBs, taskItem.getRegionId(), proxyMapper.selectByPrimaryKey(accountWithBLOBs.getProxyId()));
            String command = PlatformUtils.fixedCommand(CommandEnum.custodian.getCommand(), CommandEnum.run.getCommand(), dirPath, fileName, map);
            LogUtil.warn(cloudTask.getId() + " {custodian}[command]: " + command);
            taskItem.setCommand(command);
            cloudTaskItemMapper.updateByPrimaryKeyWithBLOBs(taskItem);
            CommandUtils.saveAsFile(taskItem.getDetails(), dirPath, fileName, false);//重启服务后容器内文件在/tmp目录下会丢失
            resultStr = CommandUtils.commonExecCmdWithResult(command, dirPath);
            if (LogUtil.getLogger().isDebugEnabled()) {
                LogUtil.getLogger().debug("resource created: {}", resultStr);
            }
            if(PlatformUtils.isUserForbidden(resultStr)){
                resultStr = Translator.get("i18n_create_resource_region_failed");
                readResource = false;
            }
            if (resultStr.contains("ERROR"))
                HRException.throwException(Translator.get("i18n_create_resource_failed") + ": " + resultStr);


            for (CloudTaskItemResourceWithBLOBs taskItemResource : list) {

                String resourceType = taskItemResource.getResourceType();
                String resourceName = taskItemResource.getResourceName();
                String taskItemId = taskItem.getId();
                if (StringUtils.equals(cloudTask.getType(), CloudTaskConstants.TaskType.manual.name()))
                    orderService.saveTaskItemLog(taskItemId, taskItemResource.getResourceId()!=null?taskItemResource.getResourceId():"", "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY,
                            true, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);
                Rule rule = ruleMapper.selectByPrimaryKey(taskItem.getRuleId());
                if (rule == null) {
                    orderService.saveTaskItemLog(taskItemId, taskItemResource.getResourceId()!=null?taskItemResource.getResourceId():"", "i18n_operation_ex" + ": " + operation, "i18n_ex_rule_not_exist",
                            false, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);
                    HRException.throwException(Translator.get("i18n_ex_rule_not_exist") + ":" + taskItem.getRuleId());
                }
                String custodianRun = ReadFileUtils.readToBuffer(dirPath + "/" + taskItemResource.getDirName() + "/" + CloudTaskConstants.CUSTODIAN_RUN_RESULT_FILE);
                String metadata = ReadFileUtils.readJsonFile(dirPath + "/" + taskItemResource.getDirName() + "/", CloudTaskConstants.METADATA_RESULT_FILE);
                String resources = "[]";
                if(readResource){
                    resources = ReadFileUtils.readJsonFile(dirPath + "/" + taskItemResource.getDirName() + "/", CloudTaskConstants.RESOURCES_RESULT_FILE);
                }
                ResourceWithBLOBs resourceWithBLOBs = new ResourceWithBLOBs();
                if (taskItemResource.getResourceId() != null) {
                    resourceWithBLOBs = resourceMapper.selectByPrimaryKey(taskItemResource.getResourceId());
                }
                resourceWithBLOBs.setCustodianRunLog(custodianRun);
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
                ResourceWithBLOBs resource = resourceService.saveResource(resourceWithBLOBs, taskItem, cloudTask, taskItemResource);
                LogUtil.info("The returned data is{}: " + new Gson().toJson(resource));
                orderService.saveTaskItemLog(taskItemId, resource.getId(), "i18n_operation_end" + ": " + operation, "i18n_cloud_account" + ": " + resource.getPluginName() + "，"
                                + "i18n_region" + ": " + resource.getRegionName() + "，" + "i18n_rule_type" + ": " + resourceType + "，" + "i18n_resource_manage" + ": " + resource.getReturnSum() + "/" + resource.getResourcesSum(),
                        true, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);

                //执行完删除返回目录文件，以便于下一次操作覆盖
                String deleteResourceDir = "rm -rf " + dirPath;
                CommandUtils.commonExecCmdWithResult(deleteResourceDir, dirPath);
            }

        } catch (Exception e) {
            orderService.saveTaskItemLog(taskItem.getId(), "", "i18n_operation_ex" + ": " + operation, e.getMessage(), false, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);
            LogUtil.error("createResource, taskItemId: " + taskItem.getId() + ", resultStr:" + resultStr, ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }


    //企业版调用
    private void createScannerResource(CloudTaskItemWithBLOBs taskItem, CloudTask cloudTask) throws Exception {
        LogUtil.info("createResource for taskItem: {}", toJSONString(taskItem));
        String operation = "i18n_create_resource";
        String resultStr = "";
        boolean readResource = true;
        try {
            CloudTaskItemResourceExample example = new CloudTaskItemResourceExample();
            example.createCriteria().andTaskIdEqualTo(cloudTask.getId()).andTaskItemIdEqualTo(taskItem.getId());
            List<CloudTaskItemResourceWithBLOBs> list = cloudTaskItemResourceMapper.selectByExampleWithBLOBs(example);
            if (list.isEmpty()) return;

            AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(taskItem.getAccountId());
            Map<String, String> map = PlatformUtils.getAccount(accountWithBLOBs, taskItem.getRegionId(), proxyMapper.selectByPrimaryKey(accountWithBLOBs.getProxyId()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            JSONObject jsonObject = PlatformUtils.fixedScanner(taskItem.getDetails(), map, cloudTask.getPluginId());
            LogUtil.warn(cloudTask.getId() + " {scanner}[api body]: " + jsonObject.toJSONString());

            HttpEntity<?> httpEntity = new HttpEntity<>(jsonObject, headers);
            String result = restTemplate.postForObject("http://hummer-scaner/run",httpEntity,String.class);
            JSONObject resultJson = JSONObject.parseObject(result);
            String resultCode = resultJson.getString("code").toString();
            String resultMsg = resultJson.getString("msg").toString();
            if (!StringUtils.equals(resultCode, "200")) {
                HRException.throwException(Translator.get("i18n_create_resource_failed") + ": " + resultMsg);
            }

            resultStr = resultJson.getString("data").toString();

            taskItem.setCommand("api scanner");
            cloudTaskItemMapper.updateByPrimaryKeyWithBLOBs(taskItem);
            if(PlatformUtils.isUserForbidden(resultStr)){
                resultStr = Translator.get("i18n_create_resource_region_failed");
                readResource = false;
            }

            for (CloudTaskItemResourceWithBLOBs taskItemResource : list) {

                String resourceType = taskItemResource.getResourceType();
                String resourceName = taskItemResource.getResourceName();
                String taskItemId = taskItem.getId();
                if (StringUtils.equals(cloudTask.getType(), CloudTaskConstants.TaskType.manual.name()))
                    orderService.saveTaskItemLog(taskItemId, taskItemResource.getResourceId()!=null?taskItemResource.getResourceId():"", "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY,
                            true, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);
                Rule rule = ruleMapper.selectByPrimaryKey(taskItem.getRuleId());
                if (rule == null) {
                    orderService.saveTaskItemLog(taskItemId, taskItemResource.getResourceId()!=null?taskItemResource.getResourceId():"", "i18n_operation_ex" + ": " + operation, "i18n_ex_rule_not_exist",
                            false, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);
                    HRException.throwException(Translator.get("i18n_ex_rule_not_exist") + ":" + taskItem.getRuleId());
                }
                String custodianRun = jsonObject.toJSONString();
                String metadata = jsonObject.toJSONString();
                String resources = "[]";
                if(readResource){
                    resources = resultStr;
                }
                ResourceWithBLOBs resourceWithBLOBs = new ResourceWithBLOBs();
                if (taskItemResource.getResourceId() != null) {
                    resourceWithBLOBs = resourceMapper.selectByPrimaryKey(taskItemResource.getResourceId());
                }
                resourceWithBLOBs.setCustodianRunLog(custodianRun);
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
                ResourceWithBLOBs resource = resourceService.saveResource(resourceWithBLOBs, taskItem, cloudTask, taskItemResource);
                LogUtil.info("The returned data is{}: " + new Gson().toJson(resource));
                orderService.saveTaskItemLog(taskItemId, resource.getId(), "i18n_operation_end" + ": " + operation, "i18n_cloud_account" + ": " + resource.getPluginName() + "，"
                                + "i18n_region" + ": " + resource.getRegionName() + "，" + "i18n_rule_type" + ": " + resourceType + "，" + "i18n_resource_manage" + ": " + resource.getReturnSum() + "/" + resource.getResourcesSum(),
                        true, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);

            }

        } catch (Exception e) {
            orderService.saveTaskItemLog(taskItem.getId(), "", "i18n_operation_ex" + ": " + operation, e.getMessage(), false, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);
            LogUtil.error("createResource, taskItemId: " + taskItem.getId() + ", resultStr:" + resultStr, ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    public Map<String, Object> getParameters(String taskId) {
        Map<String, Object> map = new HashMap<>();
        CloudTask cloudTask = cloudTaskMapper.selectByPrimaryKey(taskId);
        map.put("TASK_DESCRIPTION", cloudTask.getDescription());
        ResourceRequest resourceRequest = new ResourceRequest();
        resourceRequest.setTaskId(taskId);
        List<ResourceDTO> list = resourceService.search(resourceRequest);
        if (!CollectionUtils.isEmpty(list)) {
            map.put("RESOURCES", list);
        }
        return map;
    }


    public void dealWithResourceRelation (CloudResourceItem cloudResourceItem) throws Exception  {
        try{
            String pluginId = cloudResourceItem.getPluginId();
            if (StringUtils.equals(pluginId, "hummer-aws-plugin")) {
                dealAws(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-azure-plugin")) {
                dealAzure(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-aliyun-plugin")) {
                dealAliyun(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-huawei-plugin")) {
                dealHuawei(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-qcloud-plugin")) {
                dealQcloud(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-vsphere-plugin")) {
                dealVsphere(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-openstack-plugin")) {
                dealOpenstack(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-gcp-plugin")) {
                dealGcp(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-huoshan-plugin")) {
                dealHuoshan(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-baidu-plugin")) {
                dealBaidu(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-qiniu-plugin")) {
                dealQiniu(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-qingcloud-plugin")) {
                dealQingcloud(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-ucloud-plugin")) {
                dealUcloud(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-jdcloud-plugin")) {
                dealJdcloud(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-ksyun-plugin")) {
                dealKsyun(cloudResourceItem);
            }

        } catch (Exception e) {
            throw e;
        }
    }

    public void dealAws (CloudResourceItem cloudResourceItem) throws Exception  {
        String json = cloudResourceItem.getResource();
        if(StringUtils.isEmpty(json)) return;

        String resourceType = cloudResourceItem.getResourceType();
        String accountId = cloudResourceItem.getAccountId();
        String regionId = cloudResourceItem.getRegionId();
        String hummerId = cloudResourceItem.getHummerId();
        JSONObject jsonObject = JSONObject.parseObject(json);

        switch (resourceType) {
            case "aws.ec2":
                String PublicIpAddress = jsonObject.getString("PublicIpAddress");
                String Internet = UUIDUtil.newUUID();
                Long x = 100L, y = 100L;
                if (StringUtils.isEmpty(PublicIpAddress)) {
                    CloudResourceRela cloudResourceRela = new CloudResourceRela();

                    cloudResourceRela.setId(Internet);
                    cloudResourceRela.setResourceItemId(cloudResourceItem.getId());
                    cloudResourceRela.setName("No Internet");
                    cloudResourceRela.setPluginId(cloudResourceItem.getPluginId());
                    cloudResourceRela.setAccountId(accountId);
                    cloudResourceRela.setRegionId(regionId);
                    cloudResourceRela.setResourceType(resourceType);
                    cloudResourceRela.setHummerId(hummerId);
                    cloudResourceRela.setCreateTime(System.currentTimeMillis());
                    cloudResourceRela.setxAxis(x);//100
                    cloudResourceRela.setyAxis(y);//100

                    cloudResourceRelaMapper.insertSelective(cloudResourceRela);
                } else {
                    CloudResourceRela cloudResourceRela = new CloudResourceRela();

                    cloudResourceRela.setId(Internet);
                    cloudResourceRela.setResourceItemId(cloudResourceItem.getId());
                    cloudResourceRela.setName("Internet");
                    cloudResourceRela.setPluginId(cloudResourceItem.getPluginId());
                    cloudResourceRela.setAccountId(accountId);
                    cloudResourceRela.setRegionId(regionId);
                    cloudResourceRela.setResourceType(resourceType);
                    cloudResourceRela.setHummerId(hummerId);
                    cloudResourceRela.setxAxis(x);//100
                    cloudResourceRela.setyAxis(y);//100

                    insertCloudResourceRela(cloudResourceRela);
                }

                JSONArray networkInterfaces = JSONArray.parseArray(!StringUtils.isEmpty(jsonObject.getString("NetworkInterfaces"))?jsonObject.getString("NetworkInterfaces"):"[]");

                for (Object obj : networkInterfaces) {
                    JSONObject jsonObj = JSONObject.parseObject(obj.toString());
                    String SubnetId = jsonObj.getString("SubnetId");
                    String VpcId = jsonObj.getString("VpcId");
                    JSONObject Association = JSONObject.parseObject(!StringUtils.isEmpty(jsonObj.getString("Association"))?jsonObj.getString("Association"):"{}");
                    String PublicIp = Association.getString("PublicIp");
                    JSONArray Groups = JSONArray.parseArray(!StringUtils.isEmpty(jsonObj.getString("Groups"))?jsonObj.getString("Groups"):"[]");

                    String SubnetRelaId = UUIDUtil.newUUID();
                    String VpcRelaId = UUIDUtil.newUUID();
                    String PublicRelaIp = UUIDUtil.newUUID();

                    if (!StringUtils.isEmpty(SubnetId)) {
                        CloudResourceRela cloudResourceRela = new CloudResourceRela();

                        cloudResourceRela.setId(SubnetRelaId);
                        cloudResourceRela.setResourceItemId(cloudResourceItem.getId());
                        cloudResourceRela.setName(SubnetId);
                        cloudResourceRela.setPluginId(cloudResourceItem.getPluginId());
                        cloudResourceRela.setAccountId(accountId);
                        cloudResourceRela.setRegionId(regionId);
                        cloudResourceRela.setResourceType(resourceType);
                        cloudResourceRela.setHummerId(SubnetId);
                        cloudResourceRela.setxAxis(x);//100
                        cloudResourceRela.setyAxis(y + 100L);//200
                        cloudResourceRela = insertCloudResourceRela(cloudResourceRela);

                        CloudResourceRelaLink cloudResourceRelaLink = new CloudResourceRelaLink();
                        cloudResourceRelaLink.setResourceItemId(cloudResourceItem.getId());
                        cloudResourceRelaLink.setSource(Internet);
                        cloudResourceRelaLink.setTarget(cloudResourceRela.getId());
                        insertCloudResourceRelaLink(cloudResourceRelaLink);

                        y = y + 100L;

                        if (!StringUtils.isEmpty(VpcId)) {
                            CloudResourceRela cloudResourceRela2 = new CloudResourceRela();

                            cloudResourceRela2.setId(VpcRelaId);
                            cloudResourceRela2.setResourceItemId(cloudResourceItem.getId());
                            cloudResourceRela2.setName(SubnetId);
                            cloudResourceRela2.setPluginId(cloudResourceItem.getPluginId());
                            cloudResourceRela2.setAccountId(accountId);
                            cloudResourceRela2.setRegionId(regionId);
                            cloudResourceRela2.setResourceType(resourceType);
                            cloudResourceRela2.setHummerId(SubnetId);
                            cloudResourceRela2.setxAxis(x + 100L);//200
                            cloudResourceRela2.setyAxis(y);//200
                            cloudResourceRela2 = insertCloudResourceRela(cloudResourceRela2);

                            CloudResourceRelaLink cloudResourceRelaLink2 = new CloudResourceRelaLink();
                            cloudResourceRelaLink2.setResourceItemId(cloudResourceItem.getId());
                            cloudResourceRelaLink2.setSource(SubnetRelaId);
                            cloudResourceRelaLink2.setTarget(cloudResourceRela2.getId());
                            insertCloudResourceRelaLink(cloudResourceRelaLink2);

                            x = x + 100L;

                            if (!StringUtils.isEmpty(PublicIp)) {
                                CloudResourceRela cloudResourceRela3 = new CloudResourceRela();

                                cloudResourceRela3.setId(PublicRelaIp);
                                cloudResourceRela3.setResourceItemId(cloudResourceItem.getId());
                                cloudResourceRela3.setName(PublicIp);
                                cloudResourceRela3.setPluginId(cloudResourceItem.getPluginId());
                                cloudResourceRela3.setAccountId(accountId);
                                cloudResourceRela3.setRegionId(regionId);
                                cloudResourceRela3.setResourceType(resourceType);
                                cloudResourceRela3.setHummerId(PublicIp);
                                cloudResourceRela3.setxAxis(x + 100L);//300
                                cloudResourceRela3.setyAxis(y);//200
                                cloudResourceRela3 = insertCloudResourceRela(cloudResourceRela3);

                                CloudResourceRelaLink cloudResourceRelaLink3 = new CloudResourceRelaLink();
                                cloudResourceRelaLink3.setResourceItemId(cloudResourceItem.getId());
                                cloudResourceRelaLink3.setSource(VpcRelaId);
                                cloudResourceRelaLink3.setTarget(cloudResourceRela3.getId());
                                insertCloudResourceRelaLink(cloudResourceRelaLink3);

                                x = x + 200L;
                                y = y - 100L;

                                String EcsRelaId = UUIDUtil.newUUID();

                                CloudResourceRela cloudResourceRela5 = new CloudResourceRela();
                                cloudResourceRela5.setId(EcsRelaId);
                                cloudResourceRela5.setResourceItemId(cloudResourceItem.getId());
                                cloudResourceRela5.setName(cloudResourceItem.getHummerName());
                                cloudResourceRela5.setPluginId(cloudResourceItem.getPluginId());
                                cloudResourceRela5.setAccountId(accountId);
                                cloudResourceRela5.setRegionId(regionId);
                                cloudResourceRela5.setResourceType(resourceType);
                                cloudResourceRela5.setHummerId(cloudResourceItem.getHummerId());
                                cloudResourceRela5.setxAxis(x);//400
                                cloudResourceRela5.setyAxis(y + 100L);//200
                                cloudResourceRela5 = insertCloudResourceRela(cloudResourceRela5);

                                if (Groups.size() > 0) {

                                    for (Object o : Groups) {
                                        JSONObject jsonO = JSONObject.parseObject(o.toString());
                                        String GroupId = jsonO.getString("GroupId");
                                        String GroupName = jsonO.getString("GroupName");
                                        String GroupRelaId = UUIDUtil.newUUID();

                                        CloudResourceRela cloudResourceRela4 = new CloudResourceRela();

                                        cloudResourceRela4.setId(GroupRelaId);
                                        cloudResourceRela4.setResourceItemId(cloudResourceItem.getId());
                                        cloudResourceRela4.setName(GroupName);
                                        cloudResourceRela4.setPluginId(cloudResourceItem.getPluginId());
                                        cloudResourceRela4.setAccountId(accountId);
                                        cloudResourceRela4.setRegionId(regionId);
                                        cloudResourceRela4.setResourceType(resourceType);
                                        cloudResourceRela4.setHummerId(GroupId);
                                        cloudResourceRela4.setxAxis(x);//400
                                        cloudResourceRela4.setyAxis(y + 100L);//200
                                        cloudResourceRela4 = insertCloudResourceRela(cloudResourceRela4);

                                        CloudResourceRelaLink cloudResourceRelaLink4 = new CloudResourceRelaLink();
                                        cloudResourceRelaLink4.setResourceItemId(cloudResourceItem.getId());
                                        cloudResourceRelaLink4.setSource(PublicRelaIp);
                                        cloudResourceRelaLink4.setTarget(cloudResourceRela4.getId());
                                        insertCloudResourceRelaLink(cloudResourceRelaLink4);

                                        CloudResourceRelaLink cloudResourceRelaLink5 = new CloudResourceRelaLink();
                                        cloudResourceRelaLink5.setResourceItemId(cloudResourceItem.getId());
                                        cloudResourceRelaLink5.setSource(cloudResourceRela4.getId());
                                        cloudResourceRelaLink5.setTarget(cloudResourceRela5.getId());
                                        insertCloudResourceRelaLink(cloudResourceRelaLink5);
                                    }

                                }

                            } else {

                            }

                        } else {

                        }

                    } else {

                    }

                }
                break;
            case "aws.ebs":
                break;
            case "aws.elb":
                break;
            case "aws.network-addr":
                break;
            case "aws.rds":
                break;
            case "aws.s3":
                break;
            case "aws.security-group":
                break;
            default:
                break;
        }





    }

    public void dealAzure (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealAliyun (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealHuawei (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealQcloud (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealVsphere (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealOpenstack (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealGcp (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealHuoshan (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealBaidu (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealQiniu (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealQingcloud (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealUcloud (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealJdcloud (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealKsyun (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public CloudResourceRela insertCloudResourceRela (CloudResourceRela cloudResourceRela) throws Exception  {
        cloudResourceRela.setCreateTime(System.currentTimeMillis());
        CloudResourceRelaExample example = new CloudResourceRelaExample();
        example.createCriteria().andResourceItemIdEqualTo(cloudResourceRela.getResourceItemId()).andHummerIdEqualTo(cloudResourceRela.getHummerId()).andNameEqualTo(cloudResourceRela.getName());
        List<CloudResourceRela> list = cloudResourceRelaMapper.selectByExample(example);
        if(list.size() == 0) {
            cloudResourceRelaMapper.insertSelective(cloudResourceRela);
            cloudResourceRela = list.get(0);
        }
        return cloudResourceRela;
    }

    public void insertCloudResourceRelaLink (CloudResourceRelaLink cloudResourceRelaLink) throws Exception  {
        cloudResourceRelaLink.setCreateTime(System.currentTimeMillis());
        cloudResourceRelaLinkMapper.insertSelective(cloudResourceRelaLink);
    }

}
