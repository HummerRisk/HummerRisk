package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hummer.quartz.service.QuartzManageService;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtCloudTaskMapper;
import com.hummerrisk.base.mapper.ext.ExtHistoryScanMapper;
import com.hummerrisk.base.mapper.ext.ExtResourceMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.excel.ExcelExportRequest;
import com.hummerrisk.controller.request.resource.ResourceRequest;
import com.hummerrisk.controller.request.rule.RuleGroupRequest;
import com.hummerrisk.dto.*;
import com.hummerrisk.i18n.Translator;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.parseArray;
import static com.alibaba.fastjson.JSON.parseObject;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceService {
    @Resource @Lazy
    private ExtResourceMapper extResourceMapper;
    @Resource @Lazy
    private ResourceMapper resourceMapper;
    @Resource @Lazy
    private ResourceRuleMapper resourceRuleMapper;
    @Resource @Lazy
    private CloudTaskItemMapper cloudTaskItemMapper;
    @Resource @Lazy
    private CloudTaskItemResourceMapper cloudTaskItemResourceMapper;
    @Resource @Lazy
    private CloudTaskMapper cloudTaskMapper;
    @Resource @Lazy
    private AccountMapper accountMapper;
    @Resource @Lazy
    private ExtCloudTaskMapper extCloudTaskMapper;
    @Resource @Lazy
    private CloudTaskItemLogMapper cloudTaskItemLogMapper;
    @Resource @Lazy
    private CloudTaskService cloudTaskService;
    @Resource @Lazy
    private OrderService orderService;
    @Resource @Lazy
    private RuleMapper ruleMapper;
    @Resource @Lazy
    private ResourceItemMapper resourceItemMapper;
    @Resource @Lazy
    private HistoryScanMapper historyScanMapper;
    @Resource @Lazy
    private ExtHistoryScanMapper extHistoryScanMapper;
    @Resource @Lazy
    private HistoryScanTaskMapper historyScanTaskMapper;
    @Resource @Lazy
    private ProxyMapper proxyMapper;
    @Resource @Lazy
    private CloudAccountQuartzTaskMapper quartzTaskMapper;
    @Resource @Lazy
    private CloudAccountQuartzTaskRelationMapper quartzTaskRelationMapper;
    @Resource @Lazy
    private CloudAccountQuartzTaskRelaLogMapper quartzTaskRelaLogMapper;
    @Resource @Lazy
    private QuartzManageService quartzManageService;
    @Resource
    private HistoryService historyService;


    public SourceDTO source (String accountId) {
        return extResourceMapper.source(accountId);
    }

    public SourceDTO vulnSource (String accountId) {
        return extResourceMapper.vulnSource(accountId);
    }

    public List<ResourceDTO> search(ResourceRequest request) {
        List<ResourceDTO> resourceDTOListTmp = new ArrayList<>();
        try {
            resourceDTOListTmp = getComplianceResult(request);
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return resourceDTOListTmp;
    }

    public List<ExportDTO> searchExportData(ResourceRequest request, List<String> accountIds) {
        return extResourceMapper.searchExportData(request, accountIds);
    }

    public List<ExportDTO> searchGroupExportData(ResourceRequest request, String groupId, String accountId) {
        return extResourceMapper.searchGroupExportData(request, groupId, accountId);
    }

    public List<ReportDTO> reportList(ResourceRequest request) {
        return extResourceMapper.reportList(request);
    }

    private List<ResourceDTO> getComplianceResult(ResourceRequest resourceRequest) {
        return extResourceMapper.getComplianceResult(resourceRequest);
    }

    public ResourceWithBLOBs saveResource(ResourceWithBLOBs resourceWithBLOBs, CloudTaskItemWithBLOBs taskItem, CloudTask cloudTask, CloudTaskItemResourceWithBLOBs taskItemResource) {
        try {
            //保存创建的资源
            long now = System.currentTimeMillis();
            resourceWithBLOBs.setCreateTime(now);
            resourceWithBLOBs.setUpdateTime(now);
            JSONArray jsonArray = parseArray(resourceWithBLOBs.getResources());
            resourceWithBLOBs.setReturnSum((long) jsonArray.size());
            //执行去除filter的yaml，取到总数
            resourceWithBLOBs = updateResourceSum(resourceWithBLOBs);

            for (Object obj : jsonArray) {
                //资源详情
                saveResourceItem(resourceWithBLOBs, parseObject(obj.toString()));
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
            HRException.throwException(e.getMessage());
        }

        return resourceWithBLOBs;
    }

    private void saveResourceItem(ResourceWithBLOBs resourceWithBLOBs, JSONObject jsonObject) throws Exception {
        ResourceItem resourceItem = new ResourceItem();
        try{
            String fid = jsonObject.getString("hummerId") != null ? jsonObject.getString("hummerId") : jsonObject.getString("id");
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
            example.createCriteria().andHummerIdEqualTo(fid).andResourceIdEqualTo(resourceWithBLOBs.getId());
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
            throw e;
        }
    }

    private ResourceWithBLOBs updateResourceSum(ResourceWithBLOBs resourceWithBLOBs) throws Exception {
        try {
            resourceWithBLOBs = calculateTotal(resourceWithBLOBs);
            AccountWithBLOBs account = accountMapper.selectByPrimaryKey(resourceWithBLOBs.getAccountId());
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
            LogUtil.error("[{}] Generate updateResourceSum policy.yml file，and custodian run failed:{}", resourceWithBLOBs.getId(), e.getMessage());
            throw e;
        }
        return resourceWithBLOBs;
    }

    public ResourceWithBLOBs calculateTotal(ResourceWithBLOBs resourceWithBLOBs) {
        String dirPath;
        try {
            String uuid = resourceWithBLOBs.getId() != null ? resourceWithBLOBs.getId() : UUIDUtil.newUUID();
            String resultFile = ResourceConstants.QUERY_ALL_RESOURCE.replace("{resource_name}", resourceWithBLOBs.getDirName());
            resultFile = resultFile.replace("{resource_type}", resourceWithBLOBs.getResourceType());
            dirPath = CommandUtils.saveAsFile(resultFile, CloudTaskConstants.RESULT_FILE_PATH_PREFIX + uuid, "policy.yml", true);
            AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(resourceWithBLOBs.getAccountId());
            Map<String, String> map = PlatformUtils.getAccount(accountWithBLOBs, resourceWithBLOBs.getRegionId(), proxyMapper.selectByPrimaryKey(accountWithBLOBs.getProxyId()));
            String command = PlatformUtils.fixedCommand(CommandEnum.custodian.getCommand(), CommandEnum.run.getCommand(), dirPath, "policy.yml", map);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, dirPath);
            String resources = "[]";
            if(PlatformUtils.isUserForbidden(resultStr)){
                resultStr = Translator.get("i18n_create_resource_region_failed");
            }else{
                resources = ReadFileUtils.readJsonFile(dirPath + "/" + resourceWithBLOBs.getDirName() + "/", CloudTaskConstants.RESOURCES_RESULT_FILE);
            }
            if (LogUtil.getLogger().isDebugEnabled()) {
                LogUtil.getLogger().debug("resource created: {}", resultStr);
            }
            JSONArray jsonArray = parseArray(resources);
            if ((long) jsonArray.size() < resourceWithBLOBs.getReturnSum()) {
                resourceWithBLOBs.setResourcesSum(resourceWithBLOBs.getReturnSum());
            } else {
                resourceWithBLOBs.setResourcesSum((long) jsonArray.size());
            }
            //执行完删除返回目录文件，以便于下一次操作覆盖
            String deleteResourceDir = "rm -rf " + dirPath;
            CommandUtils.commonExecCmdWithResult(deleteResourceDir, dirPath);
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return resourceWithBLOBs;
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

    public ResourceDetailDTO getResource(String id) throws Exception {
        ResourceDetailDTO dto = new ResourceDetailDTO();
        ResourceWithBLOBs resource = resourceMapper.selectByPrimaryKey(id);
        resource.setMetadata(toJSONString(resource.getMetadata()));
        resource.setResources(toJSONString2(resource.getResources()));

        BeanUtils.copyBean(dto, resource);
        CloudTaskItemResourceExample example = new CloudTaskItemResourceExample();
        example.createCriteria().andResourceIdEqualTo(id);
        List<CloudTaskItemResource> cloudTaskItemResources = cloudTaskItemResourceMapper.selectByExample(example);

        CloudTaskItemWithBLOBs taskItemWithBLOBs = cloudTaskItemMapper.selectByPrimaryKey(cloudTaskItemResources.get(0).getTaskItemId());
        CloudTask cloudTask = cloudTaskMapper.selectByPrimaryKey(taskItemWithBLOBs.getTaskId());
        CloudTaskDTO cloudTaskDTO = new CloudTaskDTO();
        BeanUtils.copyBean(cloudTaskDTO, cloudTask);
        dto.setCloudTaskDTO(cloudTaskDTO);
        dto.setCustomData(taskItemWithBLOBs.getCustomData());

        return dto;
    }

    public String toJSONString(String jsonString) {
        String res = JSON.parse(jsonString).toString();
        JSONObject object = parseObject(res);
        return JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
    }

    public String toJSONString2(String jsonString) {
        String res = JSON.parse(jsonString).toString();
        JSONArray jsonArray = parseArray(res);
        return JSON.toJSONString(jsonArray, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
    }

    public List<String> getResourceIdsByTaskItemId(String taskItemId) {
        CloudTaskItemResourceExample cloudTaskItemResourceExample = new CloudTaskItemResourceExample();
        cloudTaskItemResourceExample.createCriteria().andTaskItemIdEqualTo(taskItemId);
        return cloudTaskItemResourceMapper.selectByExample(cloudTaskItemResourceExample)
                .stream()
                .map(CloudTaskItemResource::getResourceId)
                .collect(Collectors.toList());
    }

    /**
     * 执行actions(修复动作)
     */
    public ResourceWithBLOBs operatingResource(String id, String operating) {
        ResourceWithBLOBs resourceWithBLOBs = resourceMapper.selectByPrimaryKey(id);
        try {
            operatingDetail(resourceWithBLOBs, operating);
            resourceWithBLOBs = operatingDetail(resourceWithBLOBs, "restart");//执行完actions 操作后获取的数据并未更新
            if (resourceWithBLOBs.getReturnSum() > 0) {
                resourceWithBLOBs.setResourceStatus(ResourceConstants.RESOURCE_STATUS.AlreadyFixed.name());
            } else {
                resourceWithBLOBs.setResourceStatus(ResourceConstants.RESOURCE_STATUS.NotNeedFix.name());
            }
            resourceMapper.updateByPrimaryKeySelective(resourceWithBLOBs);
            cloudTaskService.syncTaskSum();//资源（检测结果）数量变化的话要更新任务表的数据
        } catch (Exception e) {
            resourceWithBLOBs.setResourceStatus(ResourceConstants.RESOURCE_STATUS.Error.name());
            resourceMapper.updateByPrimaryKeySelective(resourceWithBLOBs);
            HRException.throwException(e.getMessage());
        }
        return resourceWithBLOBs;
    }

    public ResourceWithBLOBs operatingDetail(ResourceWithBLOBs resourceWithBLOBs, String operating) {
        try {
            String operation = "";
            String finalScript = "";
            if (StringUtils.equals(operating, "fix")) {
                operation = "i18n_bug_fix";
                finalScript = resourceWithBLOBs.getResourceCommandAction();
            } else if (StringUtils.equals(operating, "restart")) {
                operation = "i18n_more_resource";
                finalScript = resourceWithBLOBs.getResourceCommand();
            }

            AccountExample example = new AccountExample();
            example.createCriteria().andPluginNameEqualTo(resourceWithBLOBs.getPluginName()).andStatusEqualTo("VALID");
            String region = resourceWithBLOBs.getRegionId();

            CloudTaskItemResourceExample cloudTaskItemResourceExample = new CloudTaskItemResourceExample();
            cloudTaskItemResourceExample.createCriteria().andResourceIdEqualTo(resourceWithBLOBs.getId());
            CloudTaskItemResource cloudTaskItemResource = cloudTaskItemResourceMapper.selectByExample(cloudTaskItemResourceExample).get(0);
            CloudTaskItemWithBLOBs taskItem = cloudTaskItemMapper.selectByPrimaryKey(cloudTaskItemResource.getTaskItemId());
            AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(resourceWithBLOBs.getAccountId());
            Map<String, String> map = PlatformUtils.getAccount(accountWithBLOBs, region, proxyMapper.selectByPrimaryKey(accountWithBLOBs.getProxyId()));

            orderService.saveTaskItemLog(taskItem.getId(), resourceWithBLOBs.getId(), "i18n_operation_begin" + ": " + operation, StringUtils.EMPTY, true, CloudTaskConstants.HISTORY_TYPE.Cloud.name());

            CloudTask cloudTask = cloudTaskMapper.selectByPrimaryKey(taskItem.getTaskId());
            switch (cloudTask.getScanType()) {
                case "custodian":
                    createCustodianResource(finalScript, resourceWithBLOBs, map, taskItem, cloudTaskItemResource, operation);
                    break;
                case "nuclei":
                    createNucleiResource(resourceWithBLOBs, taskItem, operation);
                    break;
                case "xray":
                    createXrayResource(resourceWithBLOBs, taskItem, operation);
                    break;
                case "tsunami":
                    createTsunamiResource(resourceWithBLOBs, taskItem, operation);
                    break;
                case "prowler":
                    createProwlerResource(resourceWithBLOBs, taskItem, cloudTask, operation);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: scantype");
            }

        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return resourceWithBLOBs;
    }

    private void createCustodianResource (String finalScript, ResourceWithBLOBs resourceWithBLOBs, Map<String, String> map,
                                          CloudTaskItemWithBLOBs taskItem, CloudTaskItemResource cloudTaskItemResource, String operation) {
        try {
            String dirPath = CommandUtils.saveAsFile(finalScript, CloudTaskConstants.RESULT_FILE_PATH_PREFIX + resourceWithBLOBs.getId(), "policy.yml", true);
            String command = PlatformUtils.fixedCommand(CommandEnum.custodian.getCommand(), CommandEnum.run.getCommand(), dirPath, "policy.yml", map);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, dirPath);
            if (!resultStr.isEmpty() && !resultStr.contains("INFO")) {
                HRException.throwException(Translator.get("i18n_compliance_rule_error") + ": " + resultStr);
            }
            CommandUtils.commonExecCmdWithResult(command, dirPath);//第一次执行action会修复资源，但是会返回修复之前的数据回来。所以此处再执行一次
            String custodianRun = ReadFileUtils.readJsonFile(dirPath + "/" + cloudTaskItemResource.getDirName() + "/", CloudTaskConstants.CUSTODIAN_RUN_RESULT_FILE);
            String metadata = ReadFileUtils.readJsonFile(dirPath + "/" + cloudTaskItemResource.getDirName() + "/", CloudTaskConstants.METADATA_RESULT_FILE);
            String resources = ReadFileUtils.readJsonFile(dirPath + "/" + cloudTaskItemResource.getDirName() + "/", CloudTaskConstants.RESOURCES_RESULT_FILE);

            resourceWithBLOBs.setCustodianRunLog(custodianRun);
            resourceWithBLOBs.setMetadata(metadata);
            resourceWithBLOBs.setResources(resources);

            JSONArray jsonArray = parseArray(resourceWithBLOBs.getResources());
            resourceWithBLOBs.setReturnSum((long) jsonArray.size());
            resourceWithBLOBs = calculateTotal(resourceWithBLOBs);

            orderService.saveTaskItemLog(taskItem.getId(), resourceWithBLOBs.getId(), "i18n_operation_end" + ": " + operation, "i18n_cloud_account" + ": " + resourceWithBLOBs.getPluginName() + "，"
                    + "i18n_region" + ": " + resourceWithBLOBs.getRegionName() + "，" + "i18n_rule_type" + ": " + resourceWithBLOBs.getResourceType() + "，" + "i18n_resource_manage" + ": "
                    + resourceWithBLOBs.getResourceName() + "，" + "i18n_resource_manage" + ": " + resourceWithBLOBs.getReturnSum() + "/" + resourceWithBLOBs.getResourcesSum(),
                    true, CloudTaskConstants.HISTORY_TYPE.Cloud.name());
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
    }

    private void createNucleiResource (ResourceWithBLOBs resourceWithBLOBs, CloudTaskItemWithBLOBs taskItem, String operation) {
        try {
            String dirPath = CloudTaskConstants.RESULT_FILE_PATH_PREFIX + taskItem.getTaskId() + "/" + taskItem.getRegionId();
            AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(taskItem.getAccountId());
            Map<String, String> map = PlatformUtils.getAccount(accountWithBLOBs, taskItem.getRegionId(), proxyMapper.selectByPrimaryKey(accountWithBLOBs.getProxyId()));
            String command = PlatformUtils.fixedCommand(CommandEnum.nuclei.getCommand(), CommandEnum.run.getCommand(), dirPath, "nuclei.yml", map);
            if(taskItem.getDetails().contains("workflows:")) {
                command = command.replace("-t", "-w");
            }
            LogUtil.info(taskItem.getTaskId() + " {}[command]: " + command);
            CommandUtils.saveAsFile(taskItem.getDetails(), dirPath, "nuclei.yml", true);//重启服务后容器内文件在/tmp目录下会丢失
            String resultStr = CommandUtils.commonExecCmdWithResultByNuclei(command, dirPath);

            String nucleiRun = resultStr;
            String metadata = resultStr;
            String resources = ReadFileUtils.readToBuffer(dirPath + "/" + CloudTaskConstants.NUCLEI_RUN_RESULT_FILE);

            resourceWithBLOBs.setCustodianRunLog(nucleiRun);
            resourceWithBLOBs.setMetadata(metadata);
            resourceWithBLOBs.setResources(resources);

            resourceWithBLOBs.setResourcesSum((long) 1);
            if (StringUtils.isNotEmpty(resourceWithBLOBs.getResources())) {
                resourceWithBLOBs.setReturnSum((long) 1);
            } else {
                resourceWithBLOBs.setReturnSum((long) 0);
            }

            orderService.saveTaskItemLog(taskItem.getId(), resourceWithBLOBs.getId(), "i18n_operation_end" + ": " + operation, "i18n_cloud_account" + ": " + resourceWithBLOBs.getPluginName() + "，"
                    + "i18n_region" + ": " + resourceWithBLOBs.getRegionName() + "，" + "i18n_rule_type" + ": " + resourceWithBLOBs.getResourceType() + "，" + "i18n_resource_manage" + ": "
                    + resourceWithBLOBs.getResourceName() + "，" + "i18n_resource_manage" + ": " + resourceWithBLOBs.getReturnSum() + "/" + resourceWithBLOBs.getResourcesSum(),
                    true, CloudTaskConstants.HISTORY_TYPE.Cloud.name());
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
    }

    private void createXrayResource (ResourceWithBLOBs resourceWithBLOBs, CloudTaskItemWithBLOBs taskItem, String operation) {
        try {
            CloudTask cloudTask = cloudTaskMapper.selectByPrimaryKey(taskItem.getTaskId());
            String fileName = cloudTask.getResourceTypes().replace("[", "").replace("]", "");
            String dirPath = CloudTaskConstants.RESULT_FILE_PATH_PREFIX + taskItem.getTaskId() + "/" + taskItem.getRegionId();
            AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(taskItem.getAccountId());
            Map<String, String> map = PlatformUtils.getAccount(accountWithBLOBs, taskItem.getRegionId(), proxyMapper.selectByPrimaryKey(accountWithBLOBs.getProxyId()));
            String command = PlatformUtils.fixedCommand(CommandEnum.xray.getCommand(), CommandEnum.run.getCommand(), dirPath, fileName, map);

            LogUtil.info(taskItem.getTaskId() + " {}[command]: " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, dirPath);

            String xrayRun = command;
            String metadata = resultStr;
            String resources = ReadFileUtils.readToBufferByXray(dirPath + "/" + CloudTaskConstants.XRAY_RUN_RESULT_FILE, resultStr);

            resourceWithBLOBs.setCustodianRunLog(xrayRun);
            resourceWithBLOBs.setMetadata(metadata);
            resourceWithBLOBs.setResources(resources);

            resourceWithBLOBs.setResourcesSum((long) 1);
            if (StringUtils.isNotEmpty(resourceWithBLOBs.getResources())) {
                resourceWithBLOBs.setReturnSum((long) 1);
            } else {
                resourceWithBLOBs.setReturnSum((long) 0);
            }

            orderService.saveTaskItemLog(taskItem.getId(), resourceWithBLOBs.getId(), "i18n_operation_end" + ": " + operation, "i18n_cloud_account" + ": " + resourceWithBLOBs.getPluginName() + "，"
                    + "i18n_region" + ": " + resourceWithBLOBs.getRegionName() + "，" + "i18n_rule_type" + ": " + resourceWithBLOBs.getResourceType() + "，" + "i18n_resource_manage" + ": "
                    + resourceWithBLOBs.getResourceName() + "，" + "i18n_resource_manage" + ": " + resourceWithBLOBs.getReturnSum() + "/" + resourceWithBLOBs.getResourcesSum(),
                    true, CloudTaskConstants.HISTORY_TYPE.Cloud.name());
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
    }

    private void createTsunamiResource (ResourceWithBLOBs resourceWithBLOBs, CloudTaskItemWithBLOBs taskItem, String operation) {
        try {

            orderService.saveTaskItemLog(taskItem.getId(), resourceWithBLOBs.getId(), "i18n_operation_end" + ": " + operation, "i18n_cloud_account" + ": " + resourceWithBLOBs.getPluginName() + "，"
                    + "i18n_region" + ": " + resourceWithBLOBs.getRegionName() + "，" + "i18n_rule_type" + ": " + resourceWithBLOBs.getResourceType() + "，" + "i18n_resource_manage" + ": "
                    + resourceWithBLOBs.getResourceName() + "，" + "i18n_resource_manage" + ": " + resourceWithBLOBs.getReturnSum() + "/" + resourceWithBLOBs.getResourcesSum(),
                    true, CloudTaskConstants.HISTORY_TYPE.Cloud.name());
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
    }

    private void createProwlerResource (ResourceWithBLOBs resourceWithBLOBs,
                                        CloudTaskItemWithBLOBs taskItem, CloudTask cloudTask, String operation) {
        try {
            String dirPath = CloudTaskConstants.PROWLER_RESULT_FILE_PATH;
            String fileName = cloudTask.getResourceTypes().replace("[", "").replace("]", "");
            AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(taskItem.getAccountId());
            Map<String, String> map = PlatformUtils.getAccount(accountWithBLOBs, taskItem.getRegionId(), proxyMapper.selectByPrimaryKey(accountWithBLOBs.getProxyId()));
            String command = PlatformUtils.fixedCommand(CommandEnum.prowler.getCommand(), CommandEnum.run.getCommand(), dirPath, fileName, map);
            LogUtil.info(taskItem.getTaskId() + " {}[command]: " + command);
            CommandUtils.commonExecCmdWithResult(command, dirPath);

            String prowlerRun = ReadFileUtils.readToBuffer(dirPath + "/" + CloudTaskConstants.PROWLER_RUN_RESULT_FILE);
            String metadata = taskItem.getCustomData();
            String resources = ReadFileUtils.readToBuffer(dirPath + "/" + CloudTaskConstants.PROWLER_RUN_RESULT_FILE);

            resourceWithBLOBs.setCustodianRunLog(prowlerRun);
            resourceWithBLOBs.setMetadata(metadata);
            resourceWithBLOBs.setResources(resources);

            long passNum = resourceWithBLOBs.getResources()!=null?appearNumber(resourceWithBLOBs.getResources(), "PASS!"):0;
            long failNum = resourceWithBLOBs.getResources()!=null?appearNumber(resourceWithBLOBs.getResources(), "FAIL!"):0;
            long infoNum = resourceWithBLOBs.getResources()!=null?appearNumber(resourceWithBLOBs.getResources(), "INFO!"):0;
            long warnNum = resourceWithBLOBs.getResources()!=null?appearNumber(resourceWithBLOBs.getResources(), "WARN!"):0;

            resourceWithBLOBs.setResourcesSum(passNum + failNum + infoNum + warnNum);
            resourceWithBLOBs.setReturnSum(failNum);

            orderService.saveTaskItemLog(taskItem.getId(), resourceWithBLOBs.getId(), "i18n_operation_end" + ": " + operation, "i18n_cloud_account" + ": " + resourceWithBLOBs.getPluginName() + "，"
                    + "i18n_region" + ": " + resourceWithBLOBs.getRegionName() + "，" + "i18n_rule_type" + ": " + resourceWithBLOBs.getResourceType() + "，" + "i18n_resource_manage" + ": "
                    + resourceWithBLOBs.getResourceName() + "，" + "i18n_resource_manage" + ": " + resourceWithBLOBs.getReturnSum() + "/" + resourceWithBLOBs.getResourcesSum(),
                    true, CloudTaskConstants.HISTORY_TYPE.Cloud.name());
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
    }


    public List<ResourceLogDTOCloud> getResourceLog(String resourceId) {
        List<ResourceLogDTOCloud> result = new ArrayList<>();
        try {
            CloudTaskItemResourceExample cloudTaskItemResourceExample = new CloudTaskItemResourceExample();
            cloudTaskItemResourceExample.createCriteria().andResourceIdEqualTo(resourceId);
            CloudTaskItemResource cloudTaskItemResource = cloudTaskItemResourceMapper.selectByExample(cloudTaskItemResourceExample).get(0);
            CloudTaskItemWithBLOBs taskItem = cloudTaskItemMapper.selectByPrimaryKey(cloudTaskItemResource.getTaskItemId());
            taskItem.setDetails(null);
            taskItem.setCustomData(null);
            ResourceLogDTOCloud resourceLogDTO = new ResourceLogDTOCloud();
            resourceLogDTO.setCloudTaskItem(taskItem);
            Rule rule = ruleMapper.selectByPrimaryKey(taskItem.getRuleId());
            rule.setScript(null);//没有用到暂时置空，以防止翻译总报错warn
            resourceLogDTO.setRule(rule);
            CloudTaskItemLogExample cloudTaskItemLogExample = new CloudTaskItemLogExample();
            cloudTaskItemLogExample.createCriteria().andTaskItemIdEqualTo(taskItem.getId()).andResourceIdEqualTo(resourceId);
            cloudTaskItemLogExample.setOrderByClause("create_time");
            resourceLogDTO.setCloudTaskItemLogList(cloudTaskItemLogMapper.selectByExampleWithBLOBs(cloudTaskItemLogExample));
            resourceLogDTO.setResource(resourceMapper.selectByPrimaryKey(resourceId));
            result.add(resourceLogDTO);
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }

        return result;
    }

    /**
     * 导出excel
     */
    @SuppressWarnings(value={"unchecked","deprecation", "serial"})
    public byte[] export(ExcelExportRequest request) throws Exception {
        try{
            Map<String, Object> params = request.getParams();
            ResourceRequest resourceRequest = new ResourceRequest();
            if (MapUtils.isNotEmpty(params)) {
                org.apache.commons.beanutils.BeanUtils.populate(resourceRequest, params);
            }
            List<ExcelExportRequest.Column> columns = request.getColumns();
            List<ExportDTO> exportDTOs = searchExportData(resourceRequest, request.getAccountIds());
            List<List<Object>> data = exportDTOs.stream().map(resource -> {
                return new ArrayList<Object>() {{
                    columns.forEach(column -> {
                        try {
                            switch (column.getKey()) {
                                case "auditName":
                                    add(resource.getFirstLevel() + "-" + resource.getSecondLevel());
                                    break;
                                case "basicRequirements":
                                    add(resource.getProject());
                                    break;
                                case "severity":
                                    add(resource.getSeverity());
                                    break;
                                case "hummerId":
                                    add(resource.getHummerId());
                                    break;
                                case "resourceName":
                                    add(resource.getResourceName());
                                    break;
                                case "resourceType":
                                    add(resource.getResourceType());
                                    break;
                                case "regionId":
                                    add(resource.getRegionId());
                                    break;
                                case "ruleName":
                                    add(resource.getRuleName());
                                    break;
                                case "ruleDescription":
                                    add(resource.getRuleDescription());
                                    break;
                                case "regionName":
                                    add(resource.getRegionName());
                                    break;
                                case "improvement":
                                    add(resource.getImprovement());
                                    break;
                                case "project":
                                    add(resource.getProject());
                                    break;
                                default:
                                    add(MethodUtils.invokeMethod(resource, "get" + StringUtils.capitalize(ExcelExportUtils.underlineToCamelCase(column.getKey()))));
                                    break;
                            }
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            LogUtil.error("export resource excel error: ", ExceptionUtils.getStackTrace(e));
                        }
                    });
                }};
            }).collect(Collectors.toList());
            OperationLogService.log(SessionUtils.getUser(), request.getAccountIds().get(0), "RESOURCE", ResourceTypeConstants.RESOURCE.name(), ResourceOperation.EXPORT, "i18n_export_report");
            return ExcelExportUtils.exportExcelData(Translator.get("i18n_scan_resource"), request.getColumns().stream().map(ExcelExportRequest.Column::getValue).collect(Collectors.toList()), data);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 导出excel
     */
    @SuppressWarnings(value={"unchecked","deprecation", "serial"})
    public byte[] exportGroupReport(ExcelExportRequest request) throws Exception {
        try{
            Map<String, Object> params = request.getParams();
            ResourceRequest resourceRequest = new ResourceRequest();
            if (MapUtils.isNotEmpty(params)) {
                org.apache.commons.beanutils.BeanUtils.populate(resourceRequest, params);
            }
            List<ExcelExportRequest.Column> columns = request.getColumns();
            List<ExportDTO> exportDTOs = searchGroupExportData(resourceRequest, request.getGroupId(), request.getAccountId());
            List<List<Object>> data = exportDTOs.stream().map(resource -> {
                return new ArrayList<Object>() {{
                    columns.forEach(column -> {
                        try {
                            switch (column.getKey()) {
                                case "auditName":
                                    add(resource.getFirstLevel() + "-" + resource.getSecondLevel());
                                    break;
                                case "basicRequirements":
                                    add(resource.getProject());
                                    break;
                                case "severity":
                                    add(resource.getSeverity());
                                    break;
                                case "hummerId":
                                    add(resource.getHummerId());
                                    break;
                                case "resourceName":
                                    add(resource.getResourceName());
                                    break;
                                case "resourceType":
                                    add(resource.getResourceType());
                                    break;
                                case "regionId":
                                    add(resource.getRegionId());
                                    break;
                                case "ruleName":
                                    add(resource.getRuleName());
                                    break;
                                case "ruleDescription":
                                    add(resource.getRuleDescription());
                                    break;
                                case "regionName":
                                    add(resource.getRegionName());
                                    break;
                                case "improvement":
                                    add(resource.getImprovement());
                                    break;
                                case "project":
                                    add(resource.getProject());
                                    break;
                                default:
                                    add(MethodUtils.invokeMethod(resource, "get" + StringUtils.capitalize(ExcelExportUtils.underlineToCamelCase(column.getKey()))));
                                    break;
                            }
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            LogUtil.error("export resource excel error: ", ExceptionUtils.getStackTrace(e));
                        }
                    });
                }};
            }).collect(Collectors.toList());
            OperationLogService.log(SessionUtils.getUser(), request.getAccountId(), "RESOURCE", ResourceTypeConstants.RESOURCE.name(), ResourceOperation.EXPORT, "i18n_export_report");
            return ExcelExportUtils.exportExcelData(Translator.get("i18n_scan_resource"), request.getColumns().stream().map(ExcelExportRequest.Column::getValue).collect(Collectors.toList()), data);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, rollbackFor = {RuntimeException.class, Exception.class})
    public void deleteResourceByAccountId (String accountId) throws Exception {
        CloudTaskExample example = new CloudTaskExample();
        example.createCriteria().andAccountIdEqualTo(accountId);
        List<CloudTask> cloudTasks = cloudTaskMapper.selectByExample(example);
        cloudTasks.forEach(task -> {
            cloudTaskService.deleteManualTask(task.getId());
            HistoryScanTaskExample historyScanTaskExample = new HistoryScanTaskExample();
            historyScanTaskExample.createCriteria().andTaskIdEqualTo(task.getId());
            historyScanTaskMapper.deleteByExample(historyScanTaskExample);
        });
        long current = System.currentTimeMillis();
        long zero = current/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset();//当天00点

        HistoryScanExample historyScanExample = new HistoryScanExample();
        historyScanExample.createCriteria().andAccountIdEqualTo(accountId).andCreateTimeEqualTo(zero);
        List<HistoryScan> list = historyScanMapper.selectByExample(historyScanExample);

        CloudAccountQuartzTaskRelationExample quartzTaskRelationExample = new CloudAccountQuartzTaskRelationExample();
        quartzTaskRelationExample.createCriteria().andSourceIdEqualTo(accountId);
        List<CloudAccountQuartzTaskRelation> quartzTaskRelationList = quartzTaskRelationMapper.selectByExample(quartzTaskRelationExample);
        for (CloudAccountQuartzTaskRelation quartzTaskRelation : quartzTaskRelationList) {
            CloudAccountQuartzTaskRelaLogExample quartzTaskRelaLogExample = new CloudAccountQuartzTaskRelaLogExample();
            quartzTaskRelaLogExample.createCriteria().andQuartzTaskRelaIdEqualTo(quartzTaskRelation.getId());
            quartzTaskRelaLogMapper.deleteByExample(quartzTaskRelaLogExample);
            quartzTaskRelationMapper.deleteByPrimaryKey(quartzTaskRelation.getId());

            CloudAccountQuartzTask quartzTask = quartzTaskMapper.selectByPrimaryKey(quartzTaskRelation.getQuartzTaskId());
            String triggerId = quartzTask.getTriggerId();
            Trigger trigger = quartzManageService.getTrigger(new TriggerKey(triggerId));
            quartzManageService.deleteJob(trigger.getJobKey());

            quartzTaskMapper.deleteByPrimaryKey(quartzTaskRelation.getQuartzTaskId());
        }

        OperationLogService.log(SessionUtils.getUser(), accountId, "RESOURCE", ResourceTypeConstants.RESOURCE.name(), ResourceOperation.DELETE, "i18n_delete_scan_resource");

    }

    public Map<String, String> reportIso (String accountId, String groupId) {
        return extResourceMapper.reportIso(accountId, groupId);
    }

    public List<Map<String, String>> groups (Map<String, Object> params) {
        return extResourceMapper.groups(params);
    }

    public ResourceWithBLOBs resource(CloudTaskItem cloudTaskItem) {
        ResourceWithBLOBs resource = extResourceMapper.resource(cloudTaskItem);
        try{
            if (PlatformUtils.isSupportCloudAccount(resource.getPluginId())) {
                resource.setMetadata(toJSONString(resource.getMetadata()));
                resource.setResources(toJSONString2(resource.getResources()));
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return resource;
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
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    public List<Map<String, Object>> regionData(Map<String, Object> map) {
        return extResourceMapper.regionData(map);
    }

    public List<Map<String, Object>> severityData(Map<String, Object> map) {
        return extResourceMapper.severityData(map);
    }

    public List<Map<String, Object>> resourceTypeData(Map<String, Object> map) {
        return extResourceMapper.resourceTypeData(map);
    }

    public List<Map<String, Object>> ruleData(Map<String, Object> map) {
        return extResourceMapper.ruleData(map);
    }

    public List<RuleInspectionReport> regulation(String ruleId) {
        return extResourceMapper.regulation(ruleId);
    }

    public List<RuleGroupDTO> ruleGroupList(RuleGroupRequest request) {
        return extResourceMapper.ruleGroupList(request);
    }

    public List<ResourceDTO> resourceList(ResourceRequest resourceRequest) {
        return extResourceMapper.getComplianceResult(resourceRequest);
    }
}
