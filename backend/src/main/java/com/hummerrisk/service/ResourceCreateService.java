package com.hummerrisk.service;

import com.google.gson.Gson;
import com.hummer.quartz.anno.QuartzScheduled;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtHistoryScanMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.resource.ResourceRequest;
import com.hummerrisk.dto.ResourceDTO;
import com.hummerrisk.i18n.Translator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @author harris
 */
@Service
public class ResourceCreateService {
    // 只有一个任务在处理，防止超配
    private static ConcurrentHashMap<String, String> processingGroupIdMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Integer> historyIdMap = new ConcurrentHashMap<>();
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
    private ExtHistoryScanMapper extHistoryScanMapper;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private NucleiService nucleiService;
    @Resource
    private ProwlerService prowlerService;
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
    @Resource
    private TaskMapper taskMapper;
    @Resource
    private TaskItemMapper taskItemMapper;
    @Resource
    private TaskItemResourceMapper taskItemResourceMapper;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private HistoryScanMapper historyScanMapper;
    @Resource
    private HistoryScanTaskMapper historyScanTaskMapper;
    @Resource
    private ServerMapper serverMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private PackageMapper packageMapper;

    @QuartzScheduled(cron = "${cron.expression.local}")
    public void handleTasks() throws Exception {
        //云资源检测、漏洞检测
        final CloudTaskExample cloudTaskExample = new CloudTaskExample();
        CloudTaskExample.Criteria criteria = cloudTaskExample.createCriteria();
        criteria.andStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
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
                        LogUtil.error(e);
                    } finally {
                        processingGroupIdMap.remove(cloudTaskToBeProceed.getId());
                    }
                });
            });
        }

        //软件包检测
        final PackageResultExample packageResultExample = new PackageResultExample();
        PackageResultExample.Criteria packageCriteria = packageResultExample.createCriteria();
        packageCriteria.andResultStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            packageCriteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        packageResultExample.setOrderByClause("create_time limit 10");
        List<PackageResultWithBLOBs> packageResults = packageResultMapper.selectByExampleWithBLOBs(packageResultExample);
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
        final ServerResultExample serverResultExample = new ServerResultExample();
        ServerResultExample.Criteria serverCriteria = serverResultExample.createCriteria();
        serverCriteria.andResultStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            serverCriteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        serverResultExample.setOrderByClause("create_time limit 10");
        List<ServerResult> serverResultList = serverResultMapper.selectByExample(serverResultExample);
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
        ImageResultExample.Criteria imageCriteria = imageResultExample.createCriteria();
        imageCriteria.andResultStatusEqualTo(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            imageCriteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
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

        //历史数据统计
        final HistoryScanExample historyScanExample = new HistoryScanExample();
        HistoryScanExample.Criteria historyScanCriteria = historyScanExample.createCriteria();
        historyScanCriteria.andStatusEqualTo(TaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(historyIdMap.keySet())) {
            historyScanCriteria.andIdNotIn(new ArrayList<>(historyIdMap.keySet()));
        }
        historyScanExample.setOrderByClause("create_time limit 10");
        List<HistoryScan> historyScans = historyScanMapper.selectByExample(historyScanExample);
        List<String> historyScanStatus = Arrays.asList(TaskConstants.TASK_STATUS.ERROR.name(), TaskConstants.TASK_STATUS.FINISHED.name(), TaskConstants.TASK_STATUS.WARNING.name());
        for (HistoryScan historyScan : historyScans) {
            final HistoryScan historyScanToBeProceed;
            try {
                historyScanToBeProceed = BeanUtils.copyBean(new HistoryScan(), historyScan);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            if (historyIdMap.get(historyScanToBeProceed.getId()) != null) {
                return;
            }
            historyIdMap.put(historyScanToBeProceed.getId(), historyScanToBeProceed.getId());
            HistoryScanTaskExample historyScanTaskExample = new HistoryScanTaskExample();
            HistoryScanTaskExample.Criteria historyScanTaskCriteria = historyScanTaskExample.createCriteria();
            historyScanTaskCriteria.andScanIdEqualTo(historyScan.getId());
            List<HistoryScanTask> historyScanTasks = historyScanTaskMapper.selectByExample(historyScanTaskExample);
            for (HistoryScanTask historyScanTask : historyScanTasks) {
                if (StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.cloudAccount.getType())) {
                    CloudTask cloudTask = cloudTaskMapper.selectByPrimaryKey(historyScanTask.getTaskId());
                    if (historyScanStatus.contains(cloudTask.getStatus())) {
                        historyScanTask.setStatus(cloudTask.getStatus());
                        historyScanTaskMapper.updateByPrimaryKey(historyScanTask);
                        historyService.updateScanTaskHistory(accountMapper.selectByPrimaryKey(historyScanTask.getAccountId()), historyScanTask);
                    }
                } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.vulnAccount.getType())) {
                    CloudTask cloudTask = cloudTaskMapper.selectByPrimaryKey(historyScanTask.getTaskId());
                    if (historyScanStatus.contains(cloudTask.getStatus())) {
                        historyScanTask.setStatus(cloudTask.getStatus());
                        historyScanTaskMapper.updateByPrimaryKey(historyScanTask);
                        historyService.updateScanTaskHistory(accountMapper.selectByPrimaryKey(historyScanTask.getAccountId()), historyScanTask);
                    }
                } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.serverAccount.getType())) {
                    ServerResult serverResult = serverResultMapper.selectByPrimaryKey(historyScanTask.getTaskId());
                    if (historyScanStatus.contains(serverResult.getResultStatus())) {
                        historyScanTask.setStatus(serverResult.getResultStatus());
                        historyScanTaskMapper.updateByPrimaryKey(historyScanTask);
                        historyService.updateScanTaskHistory(serverMapper.selectByPrimaryKey(historyScanTask.getAccountId()), historyScanTask);
                    }
                } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.imageAccount.getType())) {
                    ImageResult imageResult = imageResultMapper.selectByPrimaryKey(historyScanTask.getTaskId());
                    if (historyScanStatus.contains(imageResult.getResultStatus())) {
                        historyScanTask.setStatus(imageResult.getResultStatus());
                        historyScanTaskMapper.updateByPrimaryKey(historyScanTask);
                        historyService.updateScanTaskHistory(imageMapper.selectByPrimaryKey(historyScanTask.getAccountId()), historyScanTask);
                    }
                } else if(StringUtils.equalsIgnoreCase(historyScanTask.getAccountType(), TaskEnum.packageAccount.getType())) {
                    PackageResult packageResult = packageResultMapper.selectByPrimaryKey(historyScanTask.getTaskId());
                    if (historyScanStatus.contains(packageResult.getResultStatus())) {
                        historyScanTask.setStatus(packageResult.getResultStatus());
                        historyScanTaskMapper.updateByPrimaryKey(historyScanTask);
                        historyService.updateScanTaskHistory(packageMapper.selectByPrimaryKey(historyScanTask.getAccountId()), historyScanTask);
                    }
                }
            }
            historyScanTaskCriteria.andStatusIn(historyScanStatus);
            long count = historyScanTaskMapper.countByExample(historyScanTaskExample);
            if(historyScanTasks.size() == count) {
                historyScan.setStatus(TaskConstants.TASK_STATUS.FINISHED.name());
                historyScanMapper.updateByPrimaryKeySelective(historyScan);
                historyService.updateScanHistory(historyScan);
            }
            historyIdMap.remove(historyScanToBeProceed.getId());
        }

        //任务编排
        final TaskExample taskExample = new TaskExample();
        TaskExample.Criteria taskCriteria = taskExample.createCriteria();
        taskCriteria.andStatusEqualTo(TaskConstants.TASK_STATUS.APPROVED.toString());
        if (CollectionUtils.isNotEmpty(processingGroupIdMap.keySet())) {
            taskCriteria.andIdNotIn(new ArrayList<>(processingGroupIdMap.keySet()));
        }
        taskExample.setOrderByClause("create_time limit 10");
        List<Task> tasks = taskMapper.selectByExample(taskExample);
        List<String> status = Arrays.asList(TaskConstants.TASK_STATUS.ERROR.name(), TaskConstants.TASK_STATUS.FINISHED.name(), TaskConstants.TASK_STATUS.WARNING.name());
        for (Task task : tasks) {
            final Task taskToBeProceed;
            try {
                taskToBeProceed = BeanUtils.copyBean(new Task(), task);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            if (processingGroupIdMap.get(taskToBeProceed.getId()) != null) {
                return;
            }
            processingGroupIdMap.put(taskToBeProceed.getId(), taskToBeProceed.getId());
            TaskItemExample taskItemExample = new TaskItemExample();
            TaskItemExample.Criteria taskItemCriteria = taskItemExample.createCriteria();
            taskItemCriteria.andTaskIdEqualTo(task.getId());
            List<TaskItem> taskItems = taskItemMapper.selectByExample(taskItemExample);
            for (TaskItem taskItem : taskItems) {
                TaskItemResourceExample taskItemResourceExample = new TaskItemResourceExample();
                TaskItemResourceExample.Criteria resourceCriteria = taskItemResourceExample.createCriteria();
                resourceCriteria.andTaskItemIdEqualTo(taskItem.getId());
                long sum = taskItemResourceMapper.countByExample(taskItemResourceExample);
                long i = 0;
                List<TaskItemResource> taskItemResources = taskItemResourceMapper.selectByExample(taskItemResourceExample);
                for (TaskItemResource taskItemResource : taskItemResources) {
                    long n = 0;
                    if (StringUtils.equalsIgnoreCase(taskItemResource.getAccountType(), TaskEnum.cloudAccount.getType())) {
                        CloudTaskExample example = new CloudTaskExample();
                        example.createCriteria().andIdEqualTo(taskItemResource.getResourceId()).andStatusIn(status);
                        n = cloudTaskMapper.countByExample(example);
                        i = i + n;
                    } else if(StringUtils.equalsIgnoreCase(taskItemResource.getAccountType(), TaskEnum.vulnAccount.getType())) {
                        CloudTaskExample example = new CloudTaskExample();
                        example.createCriteria().andIdEqualTo(taskItemResource.getResourceId()).andStatusIn(status);
                        n = cloudTaskMapper.countByExample(example);
                        i = i + n;
                    } else if(StringUtils.equalsIgnoreCase(taskItemResource.getAccountType(), TaskEnum.serverAccount.getType())) {
                        ServerResultExample example = new ServerResultExample();
                        example.createCriteria().andIdEqualTo(taskItemResource.getResourceId()).andResultStatusIn(status);
                        n = serverResultMapper.countByExample(example);
                        i = i + n;
                    } else if(StringUtils.equalsIgnoreCase(taskItemResource.getAccountType(), TaskEnum.imageAccount.getType())) {
                        ImageResultExample example = new ImageResultExample();
                        example.createCriteria().andIdEqualTo(taskItemResource.getResourceId()).andResultStatusIn(status);
                        n = imageResultMapper.countByExample(example);
                        i = i + n;
                    } else if(StringUtils.equalsIgnoreCase(taskItemResource.getAccountType(), TaskEnum.packageAccount.getType())) {
                        PackageResultExample example = new PackageResultExample();
                        example.createCriteria().andIdEqualTo(taskItemResource.getResourceId()).andResultStatusIn(status);
                        n = packageResultMapper.countByExample(example);
                        i = i + n;
                    }
                    if (n > 0) {
                        taskService.saveTaskItemResourceLog(taskItemResource.getTaskItemId(), String.valueOf(taskItemResource.getId()), taskItemResource.getResourceId(), "i18n_end_task", "", true);
                    }
                }
                if (sum == i) {
                    taskItem.setStatus(TaskConstants.TASK_STATUS.FINISHED.name());
                    taskItemMapper.updateByPrimaryKeySelective(taskItem);
                }
            }
            taskItemCriteria.andStatusIn(status);
            long count = taskItemMapper.countByExample(taskItemExample);
            if(taskItems.size() == count) {
                task.setStatus(TaskConstants.TASK_STATUS.FINISHED.name());
                taskMapper.updateByPrimaryKeySelective(task);
            }
            processingGroupIdMap.remove(taskToBeProceed.getId());
        }


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

        } catch (Exception e) {
            orderService.updateTaskStatus(taskId, null, CloudTaskConstants.TASK_STATUS.ERROR.name());
            LogUtil.error("handleTask, taskId: " + taskId, e);
        }
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
        String operation = "i18n_create_resource";
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
                    orderService.saveTaskItemLog(taskItemId, "resourceType", "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY, true);
                Rule rule = ruleMapper.selectByPrimaryKey(taskItem.getRuleId());
                if (rule == null) {
                    orderService.saveTaskItemLog(taskItemId, taskItemId, "i18n_operation_ex" + ": " + operation, "i18n_ex_rule_not_exist", false);
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
                orderService.saveTaskItemLog(taskItemId, resourceType, "i18n_operation_end" + ": " + operation, "i18n_cloud_account" + ": " + resource.getPluginName() + "，"
                        + "i18n_region" + ": " + resource.getRegionName() + "，" + "i18n_rule_type" + ": " + resourceType + "，" + "i18n_resource_manage" + ": " + resource.getReturnSum() + "/" + resource.getResourcesSum(), true);
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
