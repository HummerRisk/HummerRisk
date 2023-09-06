package com.hummer.cloud.service;

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
    private CommonThreadPool commonThreadPool;
    @Autowired
    private K8sCreateService k8sCreateService;
    @Autowired
    private CloudProjectMapper cloudProjectMapper;
    @Autowired
    private CloudGroupMapper cloudGroupMapper;
    @DubboReference
    private ISystemProviderService systemProviderService;

    @Autowired
    private RestTemplate restTemplate;

    //云资源检测
    @XxlJob("cloudTasksJobHandler")
    public void cloudTasksJobHandler() throws Exception {
        //云账号（规则组）检测
        final CloudProjectExample cloudProjectExample = new CloudProjectExample();
        CloudProjectExample.Criteria criteria = cloudProjectExample.createCriteria();
        criteria.andStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            criteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        cloudProjectExample.setOrderByClause("create_time limit 10");
        List<CloudProject> cloudProjectList = cloudProjectMapper.selectByExample(cloudProjectExample);

        if (CollectionUtils.isNotEmpty(cloudProjectList)) {
            cloudProjectList.forEach(project -> {
                LogUtil.info("handling cloudProject: {}", toJSONString(project));
                final CloudProject cloudProjectToBeProceed;
                try {
                    cloudProjectToBeProceed = BeanUtils.copyBean(new CloudProject(), project);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(cloudProjectToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(cloudProjectToBeProceed.getId(), cloudProjectToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        handleProject(cloudProjectToBeProceed);
                    } catch (Exception e) {
                        LogUtil.error(e.getMessage());
                    } finally {
                        processingGroupIdMap.remove(cloudProjectToBeProceed.getId());
                    }
                });
            });
        }

        //K8s 规则检测
        final CloudTaskExample cloudTaskExample2 = new CloudTaskExample();
        CloudTaskExample.Criteria criteria2 = cloudTaskExample2.createCriteria();
        criteria2.andStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString()).andPluginIdIn(PlatformUtils.getK8sPlugin());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            criteria2.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
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
                resourceSum += cloudResourceSyncItem.getCount() == null ? 0 : cloudResourceSyncItem.getCount();
                if (CloudTaskConstants.TASK_STATUS.APPROVED.name().equals(cloudResourceSyncItem.getStatus())
                        || CloudTaskConstants.TASK_STATUS.RUNNING.name().equals(cloudResourceSyncItem.getStatus())
                        || CloudTaskConstants.TASK_STATUS.UNCHECKED.name().equals(cloudResourceSyncItem.getStatus())) {
                    runningCount++;
                } else if (CloudTaskConstants.TASK_STATUS.ERROR.name().equals(cloudResourceSyncItem.getStatus())) {
                    errorCount++;
                } else if (CloudTaskConstants.TASK_STATUS.FINISHED.name().equals(cloudResourceSyncItem.getStatus())) {
                    successCount++;
                }
            }
            String syncStatus = CloudTaskConstants.TASK_STATUS.RUNNING.name();
            if (cloudResourceSyncItems.size() == 0) {
                syncStatus = CloudTaskConstants.TASK_STATUS.FINISHED.name();
            } else if (runningCount == 0 && errorCount > 0 && successCount > 0) {
                syncStatus = CloudTaskConstants.TASK_STATUS.WARNING.name();
            } else if (runningCount == 0 && errorCount > 0) {
                syncStatus = CloudTaskConstants.TASK_STATUS.ERROR.name();
            } else if (runningCount == 0) {
                syncStatus = CloudTaskConstants.TASK_STATUS.FINISHED.name();
            }
            CloudResourceSync cloudResourceSync1 = new CloudResourceSync();
            cloudResourceSync1.setId(cloudResourceSync.getId());
            cloudResourceSync1.setStatus(syncStatus);
            cloudResourceSync1.setResourcesSum(resourceSum);
            cloudResourceSyncMapper.updateByPrimaryKeySelective(cloudResourceSync1);
        });
    }

    public void handleProject(CloudProject cloudProject) throws Exception {
        String projectId = cloudProject.getId();
        try {
            CloudGroupExample cloudGroupExample = new CloudGroupExample();
            cloudGroupExample.createCriteria().andProjectIdEqualTo(projectId);
            List<CloudGroup> cloudGroupList = cloudGroupMapper.selectByExample(cloudGroupExample);
            int successCount = 0;
            for (CloudGroup cloudGroup : cloudGroupList) {
                if (LogUtil.getLogger().isDebugEnabled()) {
                    LogUtil.getLogger().debug("handling cloudGroup: {}", toJSONString(cloudGroup));
                }
                if (handleGroup(BeanUtils.copyBean(new CloudGroup(), cloudGroup), cloudProject)) {
                    successCount++;
                }
            }
            if (!cloudGroupList.isEmpty() && successCount == 0)
                throw new Exception("Faild to handle all cloudGroups, projectId: " + projectId);
            String status = CloudTaskConstants.TASK_STATUS.FINISHED.toString();
            if (successCount != cloudGroupList.size()) {
                status = CloudTaskConstants.TASK_STATUS.WARNING.toString();
            }
            orderService.updateProjectStatus(projectId, null, status);

        } catch (Exception e) {
            orderService.updateProjectStatus(projectId, null, CloudTaskConstants.TASK_STATUS.ERROR.name());
            LogUtil.error("handleProject, projectId: " + projectId, e);
        }
    }

    private boolean handleGroup(CloudGroup cloudGroup, CloudProject cloudProject) throws Exception {
        try {
            CloudTaskExample example = new CloudTaskExample();
            example.createCriteria().andProjectIdEqualTo(cloudProject.getId()).andGroupIdEqualTo(cloudGroup.getId());
            List<CloudTask> cloudTaskList = cloudTaskMapper.selectByExample(example);
            int successCount = 0;
            for (CloudTask cloudTask : cloudTaskList) {
                if (handleTask(BeanUtils.copyBean(new CloudTask(), cloudTask))) {
                    successCount++;
                }
            }
            if (!cloudTaskList.isEmpty() && successCount == 0)
                throw new Exception("Faild to handle all cloudTask, groupId: " + cloudGroup.getId());
            String status = CloudTaskConstants.TASK_STATUS.FINISHED.toString();
            if (successCount != cloudTaskList.size()) {
                status = CloudTaskConstants.TASK_STATUS.WARNING.toString();
            }
            orderService.updateGroupStatus(cloudGroup.getId(), status);

            return true;
        } catch (Exception e) {
            orderService.updateGroupStatus(cloudGroup.getId(), CloudTaskConstants.TASK_STATUS.ERROR.name());

            LogUtil.error("handleGroup, groupId: " + cloudGroup.getId(), e);
            return false;
        }
    }

    public boolean handleTask(CloudTask cloudTask) throws Exception {
        String taskId = cloudTask.getId();
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

            return true;
        } catch (Exception e) {
            orderService.updateTaskStatus(taskId, null, CloudTaskConstants.TASK_STATUS.ERROR.name());

            //更新历史数据状态
            HistoryCloudTask historyCloudTask = BeanUtils.copyBean(new HistoryCloudTask(), cloudTask);
            historyCloudTask.setStatus(CloudTaskConstants.TASK_STATUS.ERROR.name());
            systemProviderService.updateHistoryCloudTask(historyCloudTask);

            LogUtil.error("handleTask, taskId: " + taskId, e);
            return false;
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
            if (PlatformUtils.isUserForbidden(resultStr)) {
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
                    orderService.saveTaskItemLog(taskItemId, taskItemResource.getResourceId() != null ? taskItemResource.getResourceId() : "", "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY,
                            true, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);
                Rule rule = ruleMapper.selectByPrimaryKey(taskItem.getRuleId());
                if (rule == null) {
                    orderService.saveTaskItemLog(taskItemId, taskItemResource.getResourceId() != null ? taskItemResource.getResourceId() : "", "i18n_operation_ex" + ": " + operation, "i18n_ex_rule_not_exist",
                            false, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);
                    HRException.throwException(Translator.get("i18n_ex_rule_not_exist") + ":" + taskItem.getRuleId());
                }
                String custodianRun = ReadFileUtils.readToBuffer(dirPath + "/" + taskItemResource.getDirName() + "/" + CloudTaskConstants.CUSTODIAN_RUN_RESULT_FILE);
                String metadata = ReadFileUtils.readJsonFile(dirPath + "/" + taskItemResource.getDirName() + "/", CloudTaskConstants.METADATA_RESULT_FILE);
                String resources = "[]";
                if (readResource) {
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
            JSONObject jsonObject = PlatformUtils.fixedScanner(taskItem.getDetails(), map, accountWithBLOBs.getPluginId());
            LogUtil.warn(cloudTask.getId() + " {cloud createScannerResource}[api body]: " + jsonObject.toJSONString());

            HttpEntity<?> httpEntity = new HttpEntity<>(jsonObject, headers);
            String result = restTemplate.postForObject("http://10.2.5.2:8011/run", httpEntity, String.class);
            LogUtil.info(cloudTask.getId() + " {cloud createScannerResource}[api result]: " + result);
            JSONObject resultJson = JSONObject.parseObject(result);
            String resultCode = resultJson != null ? resultJson.getString("code") : "";
            String resultMsg = resultJson != null ? resultJson.getString("msg") : "";
            if (!StringUtils.equals(resultCode, "200")) {
                HRException.throwException(Translator.get("i18n_create_resource_failed") + ": " + resultMsg);
            }

            resultStr = resultJson != null ? resultJson.getString("data") : "[]";

            taskItem.setCommand("api scanner");
            cloudTaskItemMapper.updateByPrimaryKeyWithBLOBs(taskItem);
            if (PlatformUtils.isUserForbidden(resultStr)) {
                resultStr = Translator.get("i18n_create_resource_region_failed");
                readResource = false;
            }

            for (CloudTaskItemResourceWithBLOBs taskItemResource : list) {

                String resourceType = taskItemResource.getResourceType();
                String resourceName = taskItemResource.getResourceName();
                String taskItemId = taskItem.getId();
                if (StringUtils.equals(cloudTask.getType(), CloudTaskConstants.TaskType.manual.name()))
                    orderService.saveTaskItemLog(taskItemId, taskItemResource.getResourceId() != null ? taskItemResource.getResourceId() : "", "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY,
                            true, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);
                Rule rule = ruleMapper.selectByPrimaryKey(taskItem.getRuleId());
                if (rule == null) {
                    orderService.saveTaskItemLog(taskItemId, taskItemResource.getResourceId() != null ? taskItemResource.getResourceId() : "", "i18n_operation_ex" + ": " + operation, "i18n_ex_rule_not_exist",
                            false, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);
                    HRException.throwException(Translator.get("i18n_ex_rule_not_exist") + ":" + taskItem.getRuleId());
                }
                String custodianRun = jsonObject.toJSONString();
                String metadata = jsonObject.toJSONString();
                String resources = "[]";
                if (readResource) {
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

}
