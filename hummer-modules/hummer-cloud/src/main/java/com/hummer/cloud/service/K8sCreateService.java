package com.hummer.cloud.service;

import com.google.gson.Gson;
import com.hummer.cloud.mapper.*;
import com.hummer.common.core.constant.CloudTaskConstants;
import com.hummer.common.core.constant.CommandEnum;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.*;
import com.hummer.k8s.api.IK8sProviderService;
import com.hummer.system.api.ISystemProviderService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson2.JSON.toJSONString;

/**
 * @author harris
 */
@Service
public class K8sCreateService {
    @Autowired
    private CloudTaskMapper cloudTaskMapper;
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
    @DubboReference
    private ISystemProviderService systemProviderService;
    @DubboReference
    private IK8sProviderService k8sProviderService;

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
            CloudNative cloudNative = k8sProviderService.cloudNative(taskItem.getAccountId());
            Map<String, String> map = PlatformUtils.getK8sAccount(cloudNative, taskItem.getRegionId(), proxyMapper.selectByPrimaryKey(cloudNative.getProxyId()));
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
                orderService.saveTaskItemLog(taskItemId, resource.getId(), "i18n_operation_end" + ": " + operation, "i18n_k8s_account" + ": " + resource.getPluginName() + "，"
                                + "i18n_rule_type" + ": " + resourceType + "，" + "i18n_resource_manage" + ": " + resource.getReturnSum() + "/" + resource.getResourcesSum(),
                        true, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);

                //执行完删除返回目录文件，以便于下一次操作覆盖
                String deleteResourceDir = "rm -rf " + dirPath;
                CommandUtils.commonExecCmdWithResult(deleteResourceDir, dirPath);
            }

        } catch (Exception e) {
            e.printStackTrace();
            orderService.saveTaskItemLog(taskItem.getId(), "", "i18n_operation_ex" + ": " + operation, e.getMessage(), false, CloudTaskConstants.HISTORY_TYPE.Cloud.name(), null);
            LogUtil.error("createResource, taskItemId: " + taskItem.getId() + ", resultStr:" + resultStr, ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

}
