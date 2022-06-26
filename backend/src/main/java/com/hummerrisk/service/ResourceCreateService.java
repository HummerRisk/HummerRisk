package com.hummerrisk.service;

import com.google.gson.Gson;
import com.hummer.quartz.anno.QuartzScheduled;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtScanHistoryMapper;
import com.hummerrisk.commons.constants.CloudAccountConstants;
import com.hummerrisk.commons.constants.CloudTaskConstants;
import com.hummerrisk.commons.constants.CommandEnum;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.resource.ResourceRequest;
import com.hummerrisk.dto.ResourceDTO;
import com.hummerrisk.i18n.Translator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @author harris
 */
@Service
public class ResourceCreateService {
    // 只有一个任务在处理，防止超配
    private static ConcurrentHashMap<String, String> processingGroupIdMap = new ConcurrentHashMap<>();
    @Resource
    private CloudTaskMapper cloudTaskMapper;
    @Resource
    private CommonThreadPool commonThreadPool;
    @Resource
    private CloudTaskItemMapper cloudTaskItemMapper;
    @Resource
    private ResourceService resourceService;
    @Resource
    private RuleMapper ruleMapper;
    @Resource
    private CloudTaskItemResourceMapper cloudTaskItemResourceMapper;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private ResourceMapper resourceMapper;
    @Resource
    private OrderService orderService;
    @Resource
    private ExtScanHistoryMapper extScanHistoryMapper;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private NucleiService nucleiService;
    @Resource
    private ProwlerService prowlerService;
    @Resource
    private ScanTaskHistoryMapper scanTaskHistoryMapper;
    @Resource
    private XrayService xrayService;
    @Resource
    private PackageService packageService;
    @Resource
    private PackageResultMapper packageResultMapper;
    @Resource
    private ServerService serverService;
    @Resource
    private ServerResultMapper serverResultMapper;
    @Resource
    private ImageService imageService;
    @Resource
    private ImageResultMapper imageResultMapper;

    @QuartzScheduled(cron = "${cron.expression.local}")
    public void handleTasks() {
        //云资源检测、漏洞检测
        final CloudTaskExample taskExample = new CloudTaskExample();
        CloudTaskExample.Criteria criteria = taskExample.createCriteria();
        criteria.andStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            criteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        taskExample.setOrderByClause("create_time limit 10");
        List<CloudTask> cloudTaskList = cloudTaskMapper.selectByExample(taskExample);
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
                        LogUtil.error(e);
                    } finally {
                        processingGroupIdMap.remove(cloudTaskToBeProceed.getId());
                    }
                });
            });
        } else {
            AccountExample accountExample = new AccountExample();
            accountExample.createCriteria().andStatusEqualTo(CloudAccountConstants.Status.VALID.name());
            List<AccountWithBLOBs> accountList = accountMapper.selectByExampleWithBLOBs(accountExample);
            accountList.forEach(account -> orderService.insertScanHistory(account));
        }

        //软件包检测
        final PackageResultExample packageExample = new PackageResultExample();
        PackageResultExample.Criteria pc = packageExample.createCriteria();
        pc.andResultStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            pc.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        packageExample.setOrderByClause("create_time limit 10");
        List<PackageResultWithBLOBs> packageResults = packageResultMapper.selectByExampleWithBLOBs(packageExample);
        if (CollectionUtils.isNotEmpty(packageResults)) {
            packageResults.forEach(packageResult -> {
                final PackageResultWithBLOBs packageToBeProceed;
                try {
                    packageToBeProceed = BeanUtils.copyBean(new PackageResultWithBLOBs(), packageResult);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(packageToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(packageToBeProceed.getId(), packageToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        packageService.createScan(packageToBeProceed);
                    } catch (Exception e) {
                        LogUtil.error(e);
                    } finally {
                        processingGroupIdMap.remove(packageToBeProceed.getId());
                    }
                });
            });
        }

        //虚拟机检测
        final ServerResultExample serverExample = new ServerResultExample();
        ServerResultExample.Criteria s = serverExample.createCriteria();
        s.andResultStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            pc.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        serverExample.setOrderByClause("create_time limit 10");
        List<ServerResult> serverResultList = serverResultMapper.selectByExample(serverExample);
        if (CollectionUtils.isNotEmpty(serverResultList)) {
            serverResultList.forEach(serverResult -> {
                final ServerResult serverToBeProceed;
                try {
                    serverToBeProceed = BeanUtils.copyBean(new ServerResult(), serverResult);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(serverToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(serverToBeProceed.getId(), serverToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        serverService.createScan(serverToBeProceed);
                    } catch (Exception e) {
                        LogUtil.error(e);
                    } finally {
                        processingGroupIdMap.remove(serverToBeProceed.getId());
                    }
                });
            });
        }

        //镜像检测
        final ImageResultExample imageResultExample = new ImageResultExample();
        ImageResultExample.Criteria ic = imageResultExample.createCriteria();
        ic.andResultStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            pc.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        imageResultExample.setOrderByClause("create_time limit 10");
        List<ImageResultWithBLOBs> imageResults = imageResultMapper.selectByExampleWithBLOBs(imageResultExample);
        if (CollectionUtils.isNotEmpty(imageResults)) {
            imageResults.forEach(imageResult -> {
                final ImageResultWithBLOBs imageToBeProceed;
                try {
                    imageToBeProceed = BeanUtils.copyBean(new ImageResultWithBLOBs(), imageResult);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                if (processingGroupIdMap.get(imageToBeProceed.getId()) != null) {
                    return;
                }
                processingGroupIdMap.put(imageToBeProceed.getId(), imageToBeProceed.getId());
                commonThreadPool.addTask(() -> {
                    try {
                        imageService.createScan(imageToBeProceed);
                    } catch (Exception e) {
                        LogUtil.error(e);
                    } finally {
                        processingGroupIdMap.remove(imageToBeProceed.getId());
                    }
                });
            });
        }

        //网络检测

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
            String taskStatus;
            if (StringUtils.equalsIgnoreCase(cloudTask.getType(), CloudTaskConstants.TaskType.quartz.name())) {
                taskStatus = CloudTaskConstants.TASK_STATUS.RUNNING.toString();
            } else {
                taskStatus = CloudTaskConstants.TASK_STATUS.FINISHED.toString();
            }
            if (successCount != taskItemWithBLOBs.size()) {
                taskStatus = CloudTaskConstants.TASK_STATUS.WARNING.toString();
            }
            orderService.updateTaskStatus(taskId, null, taskStatus);

            if (PlatformUtils.isSupportCloudAccount(cloudTask.getPluginId())) {
                ScanTaskHistoryExample example = new ScanTaskHistoryExample();
                ScanTaskHistoryExample.Criteria criteria = example.createCriteria();
                criteria.andTaskIdEqualTo(cloudTask.getId());
                example.setOrderByClause("id desc");
                ScanTaskHistory scanTaskHistory = scanTaskHistoryMapper.selectByExampleWithBLOBs(example).get(0);

                criteria.andScanIdEqualTo(scanTaskHistory.getScanId()).andIdEqualTo(scanTaskHistory.getId());
                orderService.updateTaskHistory(cloudTask, example);
            }
        } catch (Exception e) {
            orderService.updateTaskStatus(taskId, null, CloudTaskConstants.TASK_STATUS.ERROR.name());
            LogUtil.error("handleTask, taskId: " + taskId, e);
        }
        ScanTaskHistoryExample example = new ScanTaskHistoryExample();
        example.createCriteria().andTaskIdEqualTo(cloudTask.getId()).andScanIdEqualTo(extScanHistoryMapper.getScanId(cloudTask.getAccountId()));
        orderService.updateTaskHistory(cloudTask, example);
    }

    private boolean handleTaskItem(CloudTaskItemWithBLOBs taskItem, CloudTask cloudTask) {
        orderService.updateTaskItemStatus(taskItem.getId(), CloudTaskConstants.TASK_STATUS.PROCESSING);
        try {
            for (int i = 0; i < taskItem.getCount(); i++) {
                createResource(taskItem, cloudTask);
            }
            orderService.updateTaskItemStatus(taskItem.getId(), CloudTaskConstants.TASK_STATUS.FINISHED);
            return true;
        } catch (Exception e) {
            orderService.updateTaskItemStatus(taskItem.getId(), CloudTaskConstants.TASK_STATUS.ERROR);
            LogUtil.error("handleTaskItem, taskItemId: " + taskItem.getId(), e);
            return false;
        }
    }

    private void createResource(CloudTaskItemWithBLOBs taskItem, CloudTask cloudTask) throws Exception {
        switch (cloudTask.getScanType()) {
            case "custodian":
                createCustodianResource(taskItem, cloudTask);
                break;
            case "nuclei":
                nucleiService.createNucleiResource(taskItem, cloudTask);
                break;
            case "xray":
                xrayService.createXrayResource(taskItem, cloudTask);
                break;
            case "tsunami":
                break;
            case "prowler":
                prowlerService.createProwlerResource(taskItem, cloudTask);
                break;
            default:
                throw new IllegalStateException("Unexpected value: scantype");
        }
    }

    private void createCustodianResource(CloudTaskItemWithBLOBs taskItem, CloudTask cloudTask) throws Exception {
        LogUtil.info("createResource for taskItem: {}", toJSONString(taskItem));
        String operation = Translator.get("i18n_create_resource");
        String resultStr = "", fileName = "policy.yml";
        try {
            CloudTaskItemResourceExample example = new CloudTaskItemResourceExample();
            example.createCriteria().andTaskIdEqualTo(cloudTask.getId()).andTaskItemIdEqualTo(taskItem.getId());
            List<CloudTaskItemResourceWithBLOBs> list = cloudTaskItemResourceMapper.selectByExampleWithBLOBs(example);
            if (list.isEmpty()) return;

            String dirPath = CloudTaskConstants.RESULT_FILE_PATH_PREFIX + cloudTask.getId() + "/" + taskItem.getRegionId();
            AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(taskItem.getAccountId());
            Map<String, String> map = PlatformUtils.getAccount(accountWithBLOBs, taskItem.getRegionId(), proxyMapper.selectByPrimaryKey(accountWithBLOBs.getProxyId()));
            String command = PlatformUtils.fixedCommand(CommandEnum.custodian.getCommand(), CommandEnum.run.getCommand(), dirPath, fileName, map);
            LogUtil.info(cloudTask.getId() + " {}[command]: " + command);
            CommandUtils.saveAsFile(taskItem.getDetails(), dirPath, fileName);//重启服务后容器内文件在/tmp目录下会丢失
            resultStr = CommandUtils.commonExecCmdWithResult(command, dirPath);
            if (LogUtil.getLogger().isDebugEnabled()) {
                LogUtil.getLogger().debug("resource created: {}", resultStr);
            }
            if (resultStr.contains("ERROR"))
                throw new Exception(Translator.get("i18n_create_resource_failed") + ": " + resultStr);


            for (CloudTaskItemResourceWithBLOBs taskItemResource : list) {

                String resourceType = taskItemResource.getResourceType();
                String resourceName = taskItemResource.getResourceName();
                String taskItemId = taskItem.getId();
                if (StringUtils.equals(cloudTask.getType(), CloudTaskConstants.TaskType.manual.name()))
                    orderService.saveTaskItemLog(taskItemId, "resourceType", Translator.get("i18n_operation_begin") + ": " + operation, StringUtils.EMPTY, true);
                Rule rule = ruleMapper.selectByPrimaryKey(taskItem.getRuleId());
                if (rule == null) {
                    orderService.saveTaskItemLog(taskItemId, taskItemId, Translator.get("i18n_operation_ex") + ": " + operation, Translator.get("i18n_ex_rule_not_exist"), false);
                    throw new Exception(Translator.get("i18n_ex_rule_not_exist") + ":" + taskItem.getRuleId());
                }
                String custodianRun = ReadFileUtils.readToBuffer(dirPath + "/" + taskItemResource.getDirName() + "/" + CloudTaskConstants.CUSTODIAN_RUN_RESULT_FILE);
                String metadata = ReadFileUtils.readJsonFile(dirPath + "/" + taskItemResource.getDirName() + "/", CloudTaskConstants.METADATA_RESULT_FILE);
                String resources = ReadFileUtils.readJsonFile(dirPath + "/" + taskItemResource.getDirName() + "/", CloudTaskConstants.RESOURCES_RESULT_FILE);

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
                orderService.saveTaskItemLog(taskItemId, resourceType, Translator.get("i18n_operation_end") + ": " + operation, Translator.get("i18n_cloud_account") + ": " + resource.getPluginName() + "，"
                        + Translator.get("i18n_region") + ": " + resource.getRegionName() + "，" + Translator.get("i18n_rule_type") + ": " + resourceType + "，" + Translator.get("i18n_resource_manage") + ": " + resource.getReturnSum() + "/" + resource.getResourcesSum(), true);
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
