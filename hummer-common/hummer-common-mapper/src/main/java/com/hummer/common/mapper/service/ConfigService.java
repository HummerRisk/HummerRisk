package com.hummer.common.mapper.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.hummer.common.core.constant.*;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.config.ConfigRequest;
import com.hummer.common.core.domain.request.config.ConfigResultItemRequest;
import com.hummer.common.core.domain.request.config.ConfigResultRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.mapper.mapper.*;
import com.hummer.common.mapper.mapper.ext.ExtCloudNativeConfigMapper;
import com.hummer.common.mapper.mapper.ext.ExtCloudNativeConfigResultMapper;
import com.hummer.common.mapper.mapper.ext.ExtConfigResultItemMapper;
import com.hummer.common.core.utils.*;
import io.kubernetes.client.openapi.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ConfigService {

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
    private HistoryCloudNativeConfigResultMapper historyCloudNativeConfigResultMapper;
    @Resource
    private SystemParameterService systemParameterService;
    @Resource
    private ExtConfigResultItemMapper extConfigResultItemMapper;

    public List<CloudNativeConfigDTO> getCloudNativeConfigList(ConfigRequest request) {
        return extCloudNativeConfigMapper.getCloudNativeConfigList(request);
    }

    public boolean validate(List<String> ids) {
        ids.forEach(id -> {
            try {
                boolean validate = validate(id);
                if(!validate) throw new HRException(Translator.get("failed_cloud_native"));
            } catch (Exception e) {
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

                saveCloudNativeConfigResultLog(result.getId(), "i18n_start_config_result", "", true);
                OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.CLOUD_NATIVE_CONFIG.name(), ResourceOperation.CREATE, "i18n_start_config_result");

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

        saveCloudNativeConfigResultLog(result.getId(), "i18n_restart_config_result", "", true);

        OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.CLOUD_NATIVE_CONFIG.name(), ResourceOperation.CREATE, "i18n_restart_config_result");

        historyService.updateHistoryCloudNativeConfigResult(BeanUtils.copyBean(new HistoryCloudNativeConfigResult(), result));

        return result.getId();
    }

    public void deleteResultByCloudNativeConfigId(String id) throws Exception {
        CloudNativeConfigResultExample example = new CloudNativeConfigResultExample();
        example.createCriteria().andConfigIdEqualTo(id);
        cloudNativeConfigResultMapper.deleteByExample(example);
    }

    public void saveCloudNativeConfigResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        CloudNativeConfigResultLogWithBLOBs cloudNativeConfigResultLog = new CloudNativeConfigResultLogWithBLOBs();
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

    }

    public void reScanDeleteCloudNativeConfigResult(String id) throws Exception {
        CloudNativeConfigResultItemExample cloudNativeConfigResultItemExample = new CloudNativeConfigResultItemExample();
        cloudNativeConfigResultItemExample.createCriteria().andResultIdEqualTo(id);
        cloudNativeConfigResultItemMapper.deleteByExample(cloudNativeConfigResultItemExample);
    }

    public void createScan (CloudNativeConfigResult result) throws Exception {
        try {
            CloudNativeConfig cloudNativeConfig = cloudNativeConfigMapper.selectByPrimaryKey(result.getConfigId());
            String resultJson = "";

            String command = execute(cloudNativeConfig).getCommand();
            result.setCommand(command);
            resultJson = ReadFileUtils.readToBuffer(TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON);

            result.setResultJson(resultJson);
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());

            long count = saveCloudNativeConfigResultItem(result);
            result.setReturnSum(count);
            cloudNativeConfigResultMapper.updateByPrimaryKeySelective(result);

            noticeService.createCloudNativeConfigMessageOrder(result);
            saveCloudNativeConfigResultLog(result.getId(), "i18n_end_config_result", "", true);

            historyService.updateHistoryCloudNativeConfigResult(BeanUtils.copyBean(new HistoryCloudNativeConfigResult(), result));
        } catch (Exception e) {
            LogUtil.error("create K8sConfigResult: " + e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            cloudNativeConfigResultMapper.updateByPrimaryKeySelective(result);
            historyService.updateHistoryCloudNativeConfigResult(BeanUtils.copyBean(new HistoryCloudNativeConfigResult(), result));
            saveCloudNativeConfigResultLog(result.getId(), "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false);
        }
    }

    public ResultDTO execute(CloudNativeConfig cloudNativeConfig) throws Exception {
        try {
            Proxy proxy = new Proxy();
            if (cloudNativeConfig.getProxyId()!=null) {
                proxy= proxyMapper.selectByPrimaryKey(cloudNativeConfig.getProxyId());
            }
            ScanSetting scanSetting = new ScanSetting();
            String skipDbUpdate = systemParameterService.getValue(ParamConstants.SCAN.SkipDbUpdate.getKey());
            String securityChecks = systemParameterService.getValue(ParamConstants.SCAN.SecurityChecks.getKey());
            String ignoreUnfixed = systemParameterService.getValue(ParamConstants.SCAN.IgnoreUnfixed.getKey());
            String offlineScan = systemParameterService.getValue(ParamConstants.SCAN.OfflineScan.getKey());
            String severity = systemParameterService.getValue(ParamConstants.SCAN.Severity.getKey());
            scanSetting.setSkipDbUpdate(skipDbUpdate);
            scanSetting.setSecurityChecks(securityChecks);
            scanSetting.setIgnoreUnfixed(ignoreUnfixed);
            scanSetting.setOfflineScan(offlineScan);
            scanSetting.setSeverity(severity);
            ResultDTO resultDTO = execute(cloudNativeConfig, proxy, scanSetting);
            if (resultDTO.getResultStr().contains("ERROR") || resultDTO.getResultStr().contains("error")) {
                throw new Exception(resultDTO.getResultStr());
            }
            return resultDTO;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public ResultDTO execute(Object... obj) throws Exception {
        CloudNativeConfig cloudNativeConfig = (CloudNativeConfig) obj[0];
        try {
            String _proxy = "";
            if (cloudNativeConfig.getProxyId() != null) {
                Proxy proxy = (Proxy) obj[1];
                _proxy = ProxyUtil.isProxy(proxy);
            }
            ScanSetting scanSetting = (ScanSetting) obj[2];
            String str = "";
            if(scanSetting.getSkipDbUpdate() != null && StringUtils.equalsIgnoreCase(scanSetting.getSkipDbUpdate(), "true")) {
                str = str + TrivyConstants.SKIP_DB_UPDATE;
            }
            if(scanSetting.getIgnoreUnfixed() != null && StringUtils.equalsIgnoreCase(scanSetting.getIgnoreUnfixed(), "true")) {
                str = str + TrivyConstants.UNFIXED;
            }
            if(scanSetting.getSecurityChecks() != null) {
                str = str + TrivyConstants.SECURITY_CHECKS + scanSetting.getSecurityChecks();
            } else {
                str = str + TrivyConstants.SECURITY_CHECKS_DEFAULT;
            }
            if(scanSetting.getOfflineScan() != null && StringUtils.equalsIgnoreCase(scanSetting.getOfflineScan(), "true")) {
                str = str + TrivyConstants.OFFLINE_SCAN;
            }
            CommandUtils.saveAsFile(cloudNativeConfig.getConfigYaml(), TrivyConstants.DEFAULT_BASE_DIR, "trivy.yaml", false);
            CommandUtils.commonExecCmdWithResult(TrivyConstants.TRIVY_RM + TrivyConstants.TRIVY_JSON, TrivyConstants.DEFAULT_BASE_DIR);
            String command = _proxy + TrivyConstants.TRIVY_CONFIG + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_YAML + TrivyConstants.TRIVY_TYPE + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON;
            LogUtil.info(cloudNativeConfig.getId() + " {k8sConfig}[command]: " + cloudNativeConfig.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, TrivyConstants.DEFAULT_BASE_DIR);

            ResultDTO dto = new ResultDTO();
            dto.setCommand(command);
            dto.setResultStr(resultStr);
            return dto;
        } catch (Exception e) {
            throw e;
        }
    }

    long saveCloudNativeConfigResultItem(CloudNativeConfigResult result) throws Exception {

        //插入resultJsons
        JSONObject jsonG = JSONObject.parseObject(result.getResultJson());
        JSONArray resultJsons = JSONArray.parseArray(jsonG.getString("Results"));
        int i = 0;
        if(resultJsons != null) {
            for (Object obj : resultJsons) {
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
        if(resourceRequest.getName()!=null && !StringUtils.isBlank(resourceRequest.getName())) {
            example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId()).andTitleLike("%" + resourceRequest.getName() + "%");
        } else {
            example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId());
        }
        example.setOrderByClause("FIELD(`severity`, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'UNKNOWN')");
        return cloudNativeConfigResultItemMapper.selectByExample(example);
    }

    public List<CloudNativeConfigResultItem> resultItemListBySearch(ConfigResultItemRequest request) {
        return extConfigResultItemMapper.resultItemListBySearch(request);
    }

    public CloudNativeConfigResultDTO getCloudNativeConfigResult(String resultId) {
        CloudNativeConfigResultDTO cloudNativeConfigResult = extCloudNativeConfigResultMapper.getCloudNativeConfigResult(resultId);
        return cloudNativeConfigResult;
    }

    public List<CloudNativeConfigResultLogWithBLOBs> getCloudNativeConfigResultLog(String resultId) {
        CloudNativeConfigResultLogExample example = new CloudNativeConfigResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return cloudNativeConfigResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public void deleteCloudNativeConfigResult(String id) throws Exception {
        cloudNativeConfigResultMapper.deleteByPrimaryKey(id);
    }

    public MetricChartDTO metricChart (String resultId) {
        return extCloudNativeConfigResultMapper.metricChart(resultId);
    }

    public String download(Map<String, Object> map) {
        HistoryCloudNativeConfigResult historyCloudNativeConfigResult = historyCloudNativeConfigResultMapper.selectByPrimaryKey(map.get("id").toString());
        String str = historyCloudNativeConfigResult.getResultJson();
        return str;
    }

    public Map<String, Object> topInfo(Map<String, Object> params) {
        return extCloudNativeConfigMapper.topInfo(params);
    }

    public List<Map<String, Object>> configChart() {
        return extCloudNativeConfigMapper.configChart();
    }

    public List<Map<String, Object>> severityChart() {
        return extCloudNativeConfigMapper.severityChart();
    }

    public List<CloudNativeConfig> allList() {
        return cloudNativeConfigMapper.selectByExample(null);
    }

    public List<HistoryCloudNativeConfigResultDTO> history(Map<String, Object> params) {
        List<HistoryCloudNativeConfigResultDTO> historyList = extCloudNativeConfigResultMapper.history(params);
        return historyList;
    }

    public List<CloudNativeConfigResultItemWithBLOBs> historyResultItemList(CloudNativeConfigResultItem item) {
        CloudNativeConfigResultItemExample example = new CloudNativeConfigResultItemExample();
        example.createCriteria().andResultIdEqualTo(item.getResultId());
        example.setOrderByClause("FIELD(`severity`, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'UNKNOWN')");
        return cloudNativeConfigResultItemMapper.selectByExampleWithBLOBs(example);
    }

    public void deleteHistoryConfigResult(String id) throws Exception {
        CloudNativeConfigResultLogExample logExample = new CloudNativeConfigResultLogExample();
        logExample.createCriteria().andResultIdEqualTo(id);
        cloudNativeConfigResultLogMapper.deleteByExample(logExample);
        historyCloudNativeConfigResultMapper.deleteByPrimaryKey(id);
    }
}
