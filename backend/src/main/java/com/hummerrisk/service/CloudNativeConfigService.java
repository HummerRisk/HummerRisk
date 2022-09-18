package com.hummerrisk.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtCloudNativeConfigMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudNativeConfigResultMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.config.ConfigRequest;
import com.hummerrisk.controller.request.config.ConfigResultRequest;
import com.hummerrisk.dto.CloudNativeConfigDTO;
import com.hummerrisk.dto.CloudNativeConfigResultDTO;
import com.hummerrisk.dto.MetricChartDTO;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.service.impl.ExecEngineFactoryImp;
import com.hummerrisk.service.impl.IProvider;
import io.kubernetes.client.openapi.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CloudNativeConfigService {

    @Resource
    private ExtCloudNativeConfigMapper extCloudNativeConfigMapper;
    @Resource
    private CloudNativeConfigMapper cloudNativeConfigMapper;
    @Resource
    private CloudNativeConfigResultMapper cloudNativeConfigResultMapper;
    @Resource
    private CloudNativeConfigResultItemMapper cloudNativeConfigResultItemMapper;
    @Resource
    private CloudNativeConfigResultLogMapper cloudNativeConfigResultLogMapper;
    @Resource
    private ExtCloudNativeConfigResultMapper extCloudNativeConfigResultMapper;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private HistoryService historyService;
    @Resource
    private NoticeService noticeService;
    @Resource
    private CloudNativeConfigRuleMapper cloudNativeConfigRuleMapper;
    @Resource
    private ExecEngineFactoryImp execEngineFactoryImp;

    public List<CloudNativeConfigDTO> getCloudNativeConfigList(ConfigRequest request) {
        return extCloudNativeConfigMapper.getCloudNativeConfigList(request);
    }

    public boolean validate(List<String> ids) {
        ids.forEach(id -> {
            try {
                boolean validate = validate(id);
                if(!validate) throw new HRException(Translator.get("failed_cloud_native"));
            } catch (Exception e) {
                LogUtil.error(e.getMessage());
                throw new HRException(e.getMessage());
            }
        });
        return true;
    }

    public boolean validate(String id) throws IOException, ApiException {
        CloudNativeConfig cloudNativeConfig = cloudNativeConfigMapper.selectByPrimaryKey(id);
        //检验账号的有效性
        boolean valid = validateAccount(cloudNativeConfig);
        if (valid) {
            cloudNativeConfig.setStatus(CloudAccountConstants.Status.VALID.name());
        } else {
            cloudNativeConfig.setStatus(CloudAccountConstants.Status.INVALID.name());
        }
        cloudNativeConfigMapper.updateByPrimaryKeySelective(cloudNativeConfig);
        return valid;
    }

    private boolean validateAccount(CloudNativeConfig cloudNativeConfig) {
        try {
            Proxy proxy = new Proxy();//代理在校验yaml格式时暂时没用到
            if (cloudNativeConfig.getProxyId() != null) proxy = proxyMapper.selectByPrimaryKey(cloudNativeConfig.getProxyId());
            return YamlUtil.validateYaml(cloudNativeConfig.getConfigYaml()) || YamlUtil.validateJson(cloudNativeConfig.getConfigYaml());
        } catch (Exception e) {
            LogUtil.error(String.format("HRException in verifying cloud native, cloud native: [%s], error information:%s", cloudNativeConfig.getName(), e.getMessage()), e);
            return false;
        }
    }

    public CloudNativeConfig addCloudNativeConfig(CloudNativeConfig request) {
        try{
            //部署配置名称不能重复
            CloudNativeConfigExample cloudNativeConfigExample = new CloudNativeConfigExample();
            cloudNativeConfigExample.createCriteria().andNameEqualTo(request.getName());
            List<CloudNativeConfig> cloudNativeConfigs = cloudNativeConfigMapper.selectByExampleWithBLOBs(cloudNativeConfigExample);
            if (!CollectionUtils.isEmpty(cloudNativeConfigs)) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_config_name_duplicate"));
            }

            CloudNativeConfig account = new CloudNativeConfig();

            BeanUtils.copyBean(account, request);
            account.setCreateTime(System.currentTimeMillis());
            account.setUpdateTime(System.currentTimeMillis());
            account.setCreator(Objects.requireNonNull(SessionUtils.getUser()).getId());
            account.setId(UUIDUtil.newUUID());
            //检验账号的有效性
            boolean valid = validateAccount(account);
            if (valid) {
                account.setStatus(CloudAccountConstants.Status.VALID.name());
            } else {
                account.setStatus(CloudAccountConstants.Status.INVALID.name());
            }
            cloudNativeConfigMapper.insertSelective(account);
            OperationLogService.log(SessionUtils.getUser(), account.getId(), account.getName(), ResourceTypeConstants.CLOUD_NATIVE_CONFIG.name(), ResourceOperation.CREATE, "i18n_create_cloud_native_config");
            return account;
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return null;
    }

    public CloudNativeConfig editCloudNativeConfig(CloudNativeConfig request) throws Exception {
        try {
            //部署配置名称不能重复
            CloudNativeConfigExample cloudNativeConfigExample = new CloudNativeConfigExample();
            cloudNativeConfigExample.createCriteria().andNameEqualTo(request.getName()).andIdNotEqualTo(request.getId());
            List<CloudNativeConfig> cloudNativeConfigs = cloudNativeConfigMapper.selectByExampleWithBLOBs(cloudNativeConfigExample);
            if (!CollectionUtils.isEmpty(cloudNativeConfigs)) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_config_name_duplicate"));
            }

            if (cloudNativeConfigMapper.selectByPrimaryKey(request.getId()) == null) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_config_no_exist_id"));
            }

            CloudNativeConfig account = new CloudNativeConfig();

            BeanUtils.copyBean(account, request);
            account.setUpdateTime(System.currentTimeMillis());
            //检验账号的有效性
            boolean valid = validateAccount(account);
            if (valid) {
                account.setStatus(CloudAccountConstants.Status.VALID.name());
            } else {
                account.setStatus(CloudAccountConstants.Status.INVALID.name());
            }
            cloudNativeConfigMapper.updateByPrimaryKeySelective(account);
            account = cloudNativeConfigMapper.selectByPrimaryKey(account.getId());
            //检验账号已更新状态
            OperationLogService.log(SessionUtils.getUser(), account.getId(), account.getName(), ResourceTypeConstants.CLOUD_NATIVE_CONFIG.name(), ResourceOperation.UPDATE, "i18n_update_cloud_native_config");
            return account;

        } catch (HRException | ClientException e) {
            HRException.throwException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    public void delete(String accountId) {
        CloudNativeConfig cloudNativeConfig = cloudNativeConfigMapper.selectByPrimaryKey(accountId);
        cloudNativeConfigMapper.deleteByPrimaryKey(accountId);
        OperationLogService.log(SessionUtils.getUser(), accountId, cloudNativeConfig.getName(), ResourceTypeConstants.CLOUD_NATIVE_CONFIG.name(), ResourceOperation.DELETE, "i18n_delete_cloud_native_config");
    }

    public String uploadYaml(MultipartFile file) throws Exception {
        String yaml = upload(file);
        return yaml;
    }

    public static final String upload(MultipartFile file) throws IOException
    {
        try
        {
            return FileUploadUtils.uploadFileToString(file);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    public void scan(String id) throws Exception {
        CloudNativeConfig cloudNativeConfig = cloudNativeConfigMapper.selectByPrimaryKey(id);
        Integer scanId = historyService.insertScanHistory(cloudNativeConfig);
        if(StringUtils.equalsIgnoreCase(cloudNativeConfig.getStatus(), CloudAccountConstants.Status.VALID.name())) {
            List<CloudNativeConfigRule> ruleList = cloudNativeConfigRuleMapper.selectByExample(null);
            CloudNativeConfigResult result = new CloudNativeConfigResult();

            deleteResultByCloudNativeConfigId(id);

            for(CloudNativeConfigRule rule : ruleList) {
                BeanUtils.copyBean(result, cloudNativeConfig);
                result.setId(UUIDUtil.newUUID());
                result.setConfigId(id);
                result.setApplyUser(SessionUtils.getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
                result.setUserName(SessionUtils.getUser().getName());
                result.setRuleId(rule.getId());
                result.setRuleName(rule.getName());
                result.setRuleDesc(rule.getDescription());
                result.setSeverity(rule.getSeverity());
                cloudNativeConfigResultMapper.insertSelective(result);

                saveCloudNativeConfigResultLog(result.getId(), "i18n_start_k8s_result_config", "", true);
                OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.CLOUD_NATIVE_CONFIG.name(), ResourceOperation.CREATE, "i18n_start_k8s_result_config");

                historyService.insertScanTaskHistory(result, scanId, cloudNativeConfig.getId(), TaskEnum.configAccount.getType());

                historyService.insertHistoryCloudNativeConfigResult(BeanUtils.copyBean(new HistoryCloudNativeConfigResult(), result));
            }
        }
    }

    public String reScan(String id) throws Exception {
        CloudNativeConfigResult result = cloudNativeConfigResultMapper.selectByPrimaryKey(id);

        result.setUpdateTime(System.currentTimeMillis());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        result.setUserName(SessionUtils.getUser().getName());
        cloudNativeConfigResultMapper.updateByPrimaryKeySelective(result);

        reScanDeleteCloudNativeConfigResult(id);

        saveCloudNativeConfigResultLog(result.getId(), "i18n_restart_k8s_result_config", "", true);

        OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.CLOUD_NATIVE_CONFIG.name(), ResourceOperation.CREATE, "i18n_restart_k8s_result_config");

        historyService.updateHistoryCloudNativeConfigResult(BeanUtils.copyBean(new HistoryCloudNativeConfigResult(), result));

        return result.getId();
    }

    public void deleteResultByCloudNativeConfigId(String id) throws Exception {
        CloudNativeConfigResultExample example = new CloudNativeConfigResultExample();
        example.createCriteria().andConfigIdEqualTo(id);
        List<CloudNativeConfigResult> list = cloudNativeConfigResultMapper.selectByExample(example);

        for (CloudNativeConfigResult result : list) {
            CloudNativeConfigResultItemExample cloudNativeConfigResultItemExample = new CloudNativeConfigResultItemExample();
            cloudNativeConfigResultItemExample.createCriteria().andResultIdEqualTo(result.getId());
            cloudNativeConfigResultItemMapper.deleteByExample(cloudNativeConfigResultItemExample);
            CloudNativeConfigResultLogExample logExample = new CloudNativeConfigResultLogExample();
            logExample.createCriteria().andResultIdEqualTo(result.getId());
            cloudNativeConfigResultLogMapper.deleteByExample(logExample);
        }
        cloudNativeConfigResultMapper.deleteByExample(example);
    }

    void saveCloudNativeConfigResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        CloudNativeConfigResultLog cloudNativeConfigResultLog = new CloudNativeConfigResultLog();
        String operator = "system";
        try {
            if (SessionUtils.getUser() != null) {
                operator = SessionUtils.getUser().getId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        cloudNativeConfigResultLog.setOperator(operator);
        cloudNativeConfigResultLog.setResultId(resultId);
        cloudNativeConfigResultLog.setCreateTime(System.currentTimeMillis());
        cloudNativeConfigResultLog.setOperation(operation);
        cloudNativeConfigResultLog.setOutput(output);
        cloudNativeConfigResultLog.setResult(result);
        cloudNativeConfigResultLogMapper.insertSelective(cloudNativeConfigResultLog);

        historyService.insertHistoryCloudNativeConfigResultLog(BeanUtils.copyBean(new HistoryCloudNativeConfigResultLog(), cloudNativeConfigResultLog));
    }

    public void reScanDeleteCloudNativeConfigResult(String id) throws Exception {
        CloudNativeConfigResultItemExample cloudNativeConfigResultItemExample = new CloudNativeConfigResultItemExample();
        cloudNativeConfigResultItemExample.createCriteria().andResultIdEqualTo(id);
        cloudNativeConfigResultItemMapper.deleteByExample(cloudNativeConfigResultItemExample);
    }

    public void createScan (CloudNativeConfigResult result) throws Exception {
        try {
            CloudNativeConfig cloudNativeConfig = cloudNativeConfigMapper.selectByPrimaryKey(result.getConfigId());
            String trivyJson = "";

            execute(cloudNativeConfig);

            trivyJson = ReadFileUtils.readToBuffer(TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON);

            result.setResultJson(trivyJson);
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());

            long count = saveCloudNativeConfigResultItem(result);
            result.setReturnSum(count);
            cloudNativeConfigResultMapper.updateByPrimaryKeySelective(result);

            noticeService.createCloudNativeConfigMessageOrder(result);
            saveCloudNativeConfigResultLog(result.getId(), "i18n_end_k8s_result_config", "", true);

            historyService.updateHistoryImageTask(BeanUtils.copyBean(new HistoryImageTaskWithBLOBs(), result));
        } catch (Exception e) {
            LogUtil.error("create K8sConfigResult: " + e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            cloudNativeConfigResultMapper.updateByPrimaryKeySelective(result);
            historyService.updateHistoryImageTask(BeanUtils.copyBean(new HistoryImageTaskWithBLOBs(), result));
            saveCloudNativeConfigResultLog(result.getId(), "i18n_operation_ex" + ": " + StringUtils.substring(e.getMessage(), 0, 900) + "...", e.getMessage(), false);
        }
    }

    public String execute(CloudNativeConfig cloudNativeConfig) throws Exception {
        Proxy proxy = new Proxy();
        if (cloudNativeConfig.getProxyId()!=null) {
            proxy = proxyMapper.selectByPrimaryKey(cloudNativeConfig.getProxyId());
        }
        IProvider cp = execEngineFactoryImp.getProvider("configProvider");
        return (String) execEngineFactoryImp.executeMethod(cp, "execute", cloudNativeConfig, proxy);
    }

    long saveCloudNativeConfigResultItem(CloudNativeConfigResult result) throws Exception {

        //插入trivyJsons
        JSONObject jsonG = JSONObject.parseObject(result.getResultJson());
        JSONArray trivyJsons = JSONArray.parseArray(jsonG.getString("Results"));
        int i = 0;
        if(trivyJsons != null) {
            for (Object obj : trivyJsons) {
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray misconfigurations = JSONArray.parseArray(jsonObject.getString("Misconfigurations"));
                for (Object o : misconfigurations) {
                    JSONObject resultObject = (JSONObject) o;
                    CloudNativeConfigResultItemWithBLOBs cloudNativeConfigResultItem = new CloudNativeConfigResultItemWithBLOBs();
                    cloudNativeConfigResultItem.setId(UUIDUtil.newUUID());
                    cloudNativeConfigResultItem.setResultId(result.getId());
                    cloudNativeConfigResultItem.setType(resultObject.getString("Type"));
                    cloudNativeConfigResultItem.setItemId(resultObject.getString("ID"));
                    cloudNativeConfigResultItem.setTitle(resultObject.getString("Title"));
                    cloudNativeConfigResultItem.setDescription(resultObject.getString("Description"));
                    cloudNativeConfigResultItem.setMessage(resultObject.getString("Message"));
                    cloudNativeConfigResultItem.setNamespace(resultObject.getString("Namespace"));
                    cloudNativeConfigResultItem.setLayer(resultObject.getString("Layer"));
                    cloudNativeConfigResultItem.setQuery(resultObject.getString("Query"));
                    cloudNativeConfigResultItem.setPrimaryUrl(resultObject.getString("PrimaryURL"));
                    cloudNativeConfigResultItem.setResolution(resultObject.getString("Resolution"));
                    cloudNativeConfigResultItem.setSeverity(resultObject.getString("Severity"));
                    cloudNativeConfigResultItem.setStatus(resultObject.getString("Status"));
                    cloudNativeConfigResultItem.setCausemetaData(resultObject.getString("CauseMetadata"));
                    cloudNativeConfigResultItem.setReferences(resultObject.getString("References"));
                    cloudNativeConfigResultItem.setLayer(resultObject.getString("Layer"));
                    cloudNativeConfigResultItemMapper.insertSelective(cloudNativeConfigResultItem);
                    i++;
                }

            }
        }

        return i;
    }

    public List<CloudNativeConfigResultDTO> resultList(ConfigResultRequest request) {
        List<CloudNativeConfigResultDTO> list = extCloudNativeConfigResultMapper.resultList(request);
        return list;
    }

    public List<CloudNativeConfigResultItem> resultItemList(ConfigResultRequest resourceRequest) {
        CloudNativeConfigResultItemExample example = new CloudNativeConfigResultItemExample();
        if(resourceRequest.getName()!=null) {
            example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId()).andTitleLike(resourceRequest.getName());
        } else {
            example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId());
        }
        return cloudNativeConfigResultItemMapper.selectByExample(example);
    }

    public CloudNativeConfigResult getCloudNativeConfigResult(String resultId) {
        CloudNativeConfigResult cloudNativeConfigResult = cloudNativeConfigResultMapper.selectByPrimaryKey(resultId);
        return cloudNativeConfigResult;
    }

    public List<CloudNativeConfigResultLog> getCloudNativeConfigResultLog(String resultId) {
        CloudNativeConfigResultLogExample example = new CloudNativeConfigResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return cloudNativeConfigResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public void deleteCloudNativeConfigResult(String id) throws Exception {

        CloudNativeConfigResultLogExample logExample = new CloudNativeConfigResultLogExample();
        logExample.createCriteria().andResultIdEqualTo(id);
        cloudNativeConfigResultLogMapper.deleteByExample(logExample);

        cloudNativeConfigResultMapper.deleteByPrimaryKey(id);
    }

    public MetricChartDTO metricChart (String resultId) {
        return extCloudNativeConfigResultMapper.metricChart(resultId);
    }
}
