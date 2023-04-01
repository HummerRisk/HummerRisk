package com.hummer.k8s.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.*;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.code.CodeRequest;
import com.hummer.common.core.domain.request.code.CodeResultItemRequest;
import com.hummer.common.core.domain.request.code.CodeResultRequest;
import com.hummer.common.core.domain.request.code.CodeRuleRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.proxy.code.CodeCredential;
import com.hummer.common.core.proxy.code.CodeCredentialRequest;
import com.hummer.common.core.utils.*;
import com.hummer.common.security.service.TokenService;
import com.hummer.k8s.mapper.*;
import com.hummer.k8s.mapper.ext.ExtCodeMapper;
import com.hummer.k8s.mapper.ext.ExtCodeResultItemMapper;
import com.hummer.k8s.mapper.ext.ExtCodeResultMapper;
import com.hummer.k8s.mapper.ext.ExtCodeRuleMapper;
import com.hummer.system.api.IOperationLogService;
import com.hummer.system.api.ISystemProviderService;
import io.kubernetes.client.openapi.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CodeService {

    @Autowired
    private ExtCodeMapper extCodeMapper;
    @Autowired
    private CodeMapper codeMapper;
    @Autowired
    private ExtCodeRuleMapper extCodeRuleMapper;
    @Autowired
    private CodeRuleMapper codeRuleMapper;
    @Autowired
    private ExtCodeResultMapper extCodeResultMapper;
    @Autowired
    private CodeResultMapper codeResultMapper;
    @Autowired
    private CodeResultLogMapper codeResultLogMapper;
    @Autowired
    private CodeResultItemMapper codeResultItemMapper;
    @Autowired
    private ExtCodeResultItemMapper extCodeResultItemMapper;
    @Autowired
    private ProxyMapper proxyMapper;
    @Autowired
    private TokenService tokenService;
    @DubboReference
    private ISystemProviderService systemProviderService;
    @DubboReference
    private IOperationLogService operationLogService;
    @DubboReference
    private ICloudProviderService cloudProviderService;

    public List<CodeDTO> codeList(CodeRequest request) {
        return extCodeMapper.codeList(request);
    }

    public List<Code> allList() {
        return codeMapper.selectByExample(null);
    }

    public List<Code> allBindList(String sbomVersionId) {
        CodeExample example = new CodeExample();
        example.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        return codeMapper.selectByExample(example);
    }

    public List<Code> unBindList() {
        return codeMapper.selectByExample(null);
    }

    public Code addCode(Code code) throws Exception {
        String id = UUIDUtil.newUUID();
        code.setId(id);
        code.setCreator(tokenService.getLoginUser().getUserId());
        code.setCreateTime(System.currentTimeMillis());
        code.setUpdateTime(System.currentTimeMillis());
        code.setStatus("VALID");

        operationLogService.log(tokenService.getLoginUser(), code.getId(), code.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.CREATE, "i18n_create_code");
        codeMapper.insertSelective(code);
        return code;
    }

    public Code updateCode(Code code) throws Exception {
        code.setUpdateTime(System.currentTimeMillis());
        code.setStatus("VALID");

        operationLogService.log(tokenService.getLoginUser(), code.getId(), code.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.UPDATE, "i18n_update_code");
        codeMapper.updateByPrimaryKeySelective(code);
        return code;
    }

    public void deleteCode(String id) throws Exception {
        Code code = codeMapper.selectByPrimaryKey(id);
        codeMapper.deleteByPrimaryKey(id);
        deleteResultByCodeId(id);
        operationLogService.log(tokenService.getLoginUser(), id, code.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.DELETE, "i18n_delete_code");
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
        Code code = codeMapper.selectByPrimaryKey(id);
        //检验账号的有效性
        boolean valid = true;
        if (valid) {
            code.setStatus(CloudAccountConstants.Status.VALID.name());
        } else {
            code.setStatus(CloudAccountConstants.Status.INVALID.name());
        }
        codeMapper.updateByPrimaryKeySelective(code);
        return valid;
    }

    public List<CodeRuleDTO> ruleList(CodeRuleRequest request) {
        return extCodeRuleMapper.ruleList(request);
    }

    public int addCodeRule(CodeRuleRequest request) throws Exception {
        CodeRule record = new CodeRule();
        BeanUtils.copyBean(record, request);
        record.setId(UUIDUtil.newUUID());
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        operationLogService.log(tokenService.getLoginUser(), record.getId(), record.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.CREATE, "i18n_create_code_rule");
        return codeRuleMapper.insertSelective(record);
    }

    public void saveRuleTagMapping(String ruleId, String tagKey) {
        deleteRuleTag(null, ruleId);
        if (StringUtils.isNotEmpty(tagKey)) {
            RuleTagMapping sfRulesTagMapping = new RuleTagMapping();
            sfRulesTagMapping.setRuleId(ruleId);
            sfRulesTagMapping.setTagKey(tagKey);
            cloudProviderService.insertRuleTagMappings(sfRulesTagMapping);
        }
    }

    public void deleteRuleTag(String tagkey, String ruleId) {
        if (StringUtils.isNotBlank(tagkey)) {
            cloudProviderService.deleteRuleTag(tagkey);
        }
        if (StringUtils.isNotBlank(ruleId)) {
            RuleTagMappingExample ruleTagMappingExample = new RuleTagMappingExample();
            ruleTagMappingExample.createCriteria().andRuleIdEqualTo(ruleId);
            cloudProviderService.deleteRuleTagMappingByExample(ruleTagMappingExample);
        }
    }

    public int updateCodeRule(CodeRuleRequest request) throws Exception {
        CodeRule record = new CodeRule();
        BeanUtils.copyBean(record, request);
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        operationLogService.log(tokenService.getLoginUser(), record.getId(), record.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.UPDATE, "i18n_update_code_rule");
        return codeRuleMapper.updateByPrimaryKeySelective(record);
    }

    public void deleteCodeRule(String id) throws Exception {
        deleteRuleTag(null, id);
        codeRuleMapper.deleteByPrimaryKey(id);
        operationLogService.log(tokenService.getLoginUser(), id, id, ResourceTypeConstants.CODE.name(), ResourceOperation.DELETE, "i18n_delete_code_rule");
    }

    public int changeStatus(CodeRule rule) throws Exception {
        return codeRuleMapper.updateByPrimaryKeySelective(rule);
    }

    public List<CodeResultDTO> resultListWithBLOBs(CodeResultRequest request) {
        List<CodeResultDTO> list = extCodeResultMapper.resultListWithBLOBs(request);
        return list;
    }

    public List<CodeResultDTO> resultList(CodeResultRequest request) {
        List<CodeResultDTO> list = extCodeResultMapper.resultList(request);
        return list;
    }

    public CodeResultDTO getCodeResult(String resultId) {
        CodeResultDTO codeResult = extCodeResultMapper.getCodeResult(resultId);
        return codeResult;
    }

    public List<CodeResultLogWithBLOBs> getCodeResultLog(String resultId) {
        CodeResultLogExample example = new CodeResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return codeResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public void deleteCodeResult(String id) throws Exception {
        CodeResultLogExample logExample = new CodeResultLogExample();
        logExample.createCriteria().andResultIdEqualTo(id);
        codeResultLogMapper.deleteByExample(logExample);

        CodeResultItemExample itemExample = new CodeResultItemExample();
        itemExample.createCriteria().andResultIdEqualTo(id);
        codeResultItemMapper.deleteByExample(itemExample);

        systemProviderService.deleteHistoryCodeResult(id);
        codeResultMapper.deleteByPrimaryKey(id);
        operationLogService.log(tokenService.getLoginUser(), id, id, ResourceTypeConstants.CODE.name(), ResourceOperation.DELETE, "i18n_delete_code_result");
    }

    public List<CodeResultItemWithBLOBs> resultItemList(CodeResultItem codeResultItem) {
        CodeResultItemExample example = new CodeResultItemExample();
        if(codeResultItem.getPkgName()!=null && !StringUtils.isBlank(codeResultItem.getPkgName())) {
            example.createCriteria().andResultIdEqualTo(codeResultItem.getResultId()).andPkgNameLike("%" + codeResultItem.getPkgName() + "%");
        } else {
            example.createCriteria().andResultIdEqualTo(codeResultItem.getResultId());
        }
        example.setOrderByClause("FIELD(`severity`, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'UNKNOWN')");
        return codeResultItemMapper.selectByExampleWithBLOBs(example);
    }

    public List<CodeResultItemWithBLOBs> resultItemListBySearch(CodeResultItemRequest request) {
        return extCodeResultItemMapper.resultItemListBySearch(request);
    }

    public void scan(String id) throws Exception {
        Code code = codeMapper.selectByPrimaryKey(id);
        Integer scanId = systemProviderService.insertScanHistory(code);
        if(StringUtils.equalsIgnoreCase(code.getStatus(), CloudAccountConstants.Status.VALID.name())) {
            List<CodeRuleDTO> ruleList = ruleList(null);
            CodeResult result = new CodeResult();

            deleteRescanResultByCodeId(id);

            for(CodeRuleDTO dto : ruleList) {
                BeanUtils.copyBean(result, code);
                result.setId(UUIDUtil.newUUID());
                result.setCodeId(id);
                result.setApplyUser(tokenService.getLoginUser().getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setRuleId(dto.getId());
                result.setRuleName(dto.getName());
                result.setRuleDesc(dto.getDescription());
                result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(dto.getSeverity());
                result.setUserName(tokenService.getLoginUser().getUserName());
                codeResultMapper.insertSelective(result);

                saveCodeResultLog(result.getId(), "i18n_start_code_result", "", true);
                operationLogService.log(tokenService.getLoginUser(), result.getId(), result.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.SCAN, "i18n_start_code_result");

                systemProviderService.insertScanTaskHistory(result, scanId, code.getId(), TaskEnum.codeAccount.getType());

                systemProviderService.insertHistoryCodeResult(BeanUtils.copyBean(new HistoryCodeResult(), result));
            }
        }
    }

    public String reScan(String id) throws Exception {
        CodeResult result = codeResultMapper.selectByPrimaryKey(id);

        result.setUpdateTime(System.currentTimeMillis());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        result.setUserName(tokenService.getLoginUser().getUserName());
        codeResultMapper.updateByPrimaryKeySelective(result);

        reScanDeleteCodeResult(id);

        saveCodeResultLog(result.getId(), "i18n_restart_code_result", "", true);

        operationLogService.log(tokenService.getLoginUser(), result.getId(), result.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.RESCAN, "i18n_restart_code_result");

        systemProviderService.updateHistoryCodeResult(BeanUtils.copyBean(new HistoryCodeResult(), result));

        return result.getId();
    }

    public void reScanDeleteCodeResult(String id) throws Exception {

        CodeResultItemExample example = new CodeResultItemExample();
        example.createCriteria().andResultIdEqualTo(id);
        codeResultItemMapper.deleteByExample(example);

    }

    public void deleteRescanResultByCodeId(String id) throws Exception {
        CodeResultExample example = new CodeResultExample();
        example.createCriteria().andCodeIdEqualTo(id);
        codeResultMapper.deleteByExample(example);
    }

    public void deleteResultByCodeId(String id) throws Exception {
        CodeResultExample example = new CodeResultExample();
        example.createCriteria().andCodeIdEqualTo(id);
        List<CodeResult> list = codeResultMapper.selectByExample(example);

        for (CodeResult codeResult : list) {
            CodeResultLogExample logExample = new CodeResultLogExample();
            logExample.createCriteria().andResultIdEqualTo(codeResult.getId());
            codeResultLogMapper.deleteByExample(logExample);

            CodeResultItemExample itemExample = new CodeResultItemExample();
            itemExample.createCriteria().andResultIdEqualTo(codeResult.getId());
            codeResultItemMapper.deleteByExample(itemExample);

            systemProviderService.deleteHistoryCodeResult(codeResult.getId());
        }
        codeResultMapper.deleteByExample(example);
        operationLogService.log(tokenService.getLoginUser(), id, id, ResourceTypeConstants.CODE.name(), ResourceOperation.DELETE, "i18n_delete_code_result");
    }

    public void saveCodeResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        CodeResultLogWithBLOBs codeResultLog = new CodeResultLogWithBLOBs();
        String operator = "system";
        try {
            if (tokenService.getLoginUser() != null) {
                operator = tokenService.getLoginUser().getUserId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        codeResultLog.setOperator(operator);
        codeResultLog.setResultId(resultId);
        codeResultLog.setCreateTime(System.currentTimeMillis());
        codeResultLog.setOperation(operation);
        codeResultLog.setOutput(output);
        codeResultLog.setResult(result);
        codeResultLogMapper.insertSelective(codeResultLog);

    }

    public void createScan (CodeResult result) throws Exception {
        try {
            CodeRuleRequest request = new CodeRuleRequest();
            request.setId(result.getRuleId());
            CodeRuleDTO dto = ruleList(request).get(0);
            Code code = codeMapper.selectByPrimaryKey(result.getCodeId());
            String script = dto.getScript();
            JSONArray jsonArray = JSON.parseArray(dto.getParameter());
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                String key = "${{" + jsonObject.getString("key") + "}}";
                if (script.contains(key)) {
                    script = script.replace(key, jsonObject.getString("defaultValue"));
                }
            }
            String returnJson = "";

            String command = execute(code).getCommand();

            result.setCommand(command);
            returnJson = ReadFileUtils.readToBuffer(TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON);
            result.setUpdateTime(System.currentTimeMillis());
            result.setReturnJson(returnJson);
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());

            long count = saveResultItem(result);
            result.setReturnSum(count);
            codeResultMapper.updateByPrimaryKeySelective(result);

            systemProviderService.createCodeMessageOrder(result);
            saveCodeResultLog(result.getId(), "i18n_end_code_result", "", true);

            systemProviderService.updateHistoryCodeResult(BeanUtils.copyBean(new HistoryCodeResult(), result));
        } catch (Exception e) {
            LogUtil.error("create CodeResult: " + e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            codeResultMapper.updateByPrimaryKeySelective(result);
            systemProviderService.updateHistoryCodeResult(BeanUtils.copyBean(new HistoryCodeResult(), result));
            saveCodeResultLog(result.getId(), "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false);
        }
    }

    public ResultDTO execute(Code code) throws Exception {
        try {
            Proxy proxy = new Proxy();
            if (code.getProxyId()!=null) {
                proxy = proxyMapper.selectByPrimaryKey(code.getProxyId());
            }
            ScanSetting scanSetting = new ScanSetting();
            String skipDbUpdate = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.SkipDbUpdate.getKey());
            String securityChecks = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.SecurityChecks.getKey());
            String ignoreUnfixed = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.IgnoreUnfixed.getKey());
            String offlineScan = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.OfflineScan.getKey());
            String severity = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.Severity.getKey());
            scanSetting.setSkipDbUpdate(skipDbUpdate);
            scanSetting.setSecurityChecks(securityChecks);
            scanSetting.setIgnoreUnfixed(ignoreUnfixed);
            scanSetting.setOfflineScan(offlineScan);
            scanSetting.setSeverity(severity);
            ResultDTO resultDTO = execute(code, proxy, scanSetting);
            if (resultDTO.getResultStr().contains("ERROR") || resultDTO.getResultStr().contains("error")) {
                throw new Exception(resultDTO.getResultStr());
            }
            return resultDTO;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ResultDTO execute(Object... obj) throws Exception {
        Code code = (Code) obj[0];
        try {
            String _proxy = "";
            if (code.getProxyId() != null) {
                Proxy proxy = (Proxy) obj[1];
                _proxy = ProxyUtil.isProxy(proxy);
            }
            CodeCredentialRequest codeRequest = new CodeCredentialRequest();
            codeRequest.setCredential(code.getCredential());
            CodeCredential codeCredential = codeRequest.getCodeClient();
            String token = "", branch = "";
            if (codeCredential != null && StringUtils.isNotEmpty(codeCredential.getToken())) {
                if (StringUtils.equals(code.getPluginIcon(), CodeConstants.GITHUB_TOKEN)) {
                    token = "GITHUB_TOKEN='" + codeCredential.getToken() + "';";
                } else if (StringUtils.equals(code.getPluginIcon(), CodeConstants.GITLAB_TOKEN)) {
                    token = "GITLAB_TOKEN='" + codeCredential.getToken() + "';";
                }
            }
            if (codeCredential != null && StringUtils.isNotEmpty(codeCredential.getBranch())) {
                branch = TrivyConstants.BRANCH + codeCredential.getBranch();
            } else if (codeCredential != null && StringUtils.isNotEmpty(codeCredential.getTag())) {
                branch = TrivyConstants.TAG + codeCredential.getTag();
            } else if (codeCredential != null && StringUtils.isNotEmpty(codeCredential.getCommit())) {
                branch = TrivyConstants.COMMIT + codeCredential.getCommit();
            }
            ScanSetting scanSetting = (ScanSetting) obj[2];
            String str = "";
            if(StringUtils.isNotEmpty(scanSetting.getSkipDbUpdate()) && StringUtils.equalsIgnoreCase(scanSetting.getSkipDbUpdate(), "true")) {
                str = str + TrivyConstants.SKIP_DB_UPDATE + TrivyConstants.SKIP_JAVA_DB_UPDATE;
            }
            if(StringUtils.isNotEmpty(scanSetting.getIgnoreUnfixed()) && StringUtils.equalsIgnoreCase(scanSetting.getIgnoreUnfixed(), "true")) {
                str = str + TrivyConstants.UNFIXED;
            }
            if(StringUtils.isNotEmpty(scanSetting.getSecurityChecks())) {
                str = str + TrivyConstants.SECURITY_CHECKS + scanSetting.getSecurityChecks();
            } else {
                str = str + TrivyConstants.SECURITY_CHECKS_DEFAULT;
            }
            if(StringUtils.isNotEmpty(scanSetting.getOfflineScan()) && StringUtils.equalsIgnoreCase(scanSetting.getOfflineScan(), "true")) {
                str = str + TrivyConstants.OFFLINE_SCAN;
            }
            CommandUtils.commonExecCmdWithResult(TrivyConstants.TRIVY_RM + TrivyConstants.TRIVY_JSON, TrivyConstants.DEFAULT_BASE_DIR);
            String command = _proxy + token + TrivyConstants.TRIVY_REPO + str + branch + " " + codeCredential.getUrl() + TrivyConstants.TRIVY_TYPE + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON + TrivyConstants.TRIVY_SERVER;
            LogUtil.info(code.getId() + " {code scan}[command]: " + code.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, TrivyConstants.DEFAULT_BASE_DIR);
            ResultDTO dto = new ResultDTO();
            dto.setCommand(command);
            dto.setResultStr(resultStr);
            return dto;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    long saveResultItem(CodeResult result) throws Exception {

        int i = 0;
        //插入returnJson
        JSONObject jsonG = JSONObject.parseObject(result.getReturnJson());
        if (jsonG != null) {
            JSONArray returnJson = JSONArray.parseArray(jsonG.getString("Results"));
            if(returnJson != null) {
                for (Object obj : returnJson) {
                    JSONObject jsonObject = (JSONObject) obj;
                    JSONArray vulnerabilities = JSONArray.parseArray(jsonObject.getString("Vulnerabilities"));
                    if(vulnerabilities == null) continue;
                    for (Object o : vulnerabilities) {
                        JSONObject resultObject = (JSONObject) o;
                        CodeResultItemWithBLOBs codeResultItem = new CodeResultItemWithBLOBs();
                        codeResultItem.setId(UUIDUtil.newUUID());
                        codeResultItem.setResultId(result.getId());
                        codeResultItem.setVulnerabilityId(resultObject.getString("VulnerabilityID"));
                        codeResultItem.setPkgName(resultObject.getString("PkgName"));
                        codeResultItem.setInstalledVersion(resultObject.getString("InstalledVersion"));
                        codeResultItem.setFixedVersion(resultObject.getString("FixedVersion"));
                        codeResultItem.setLayer(resultObject.getString("Layer"));
                        codeResultItem.setSeveritySource(resultObject.getString("SeveritySource"));
                        codeResultItem.setPrimaryUrl(resultObject.getString("PrimaryURL"));
                        codeResultItem.setDataSource(resultObject.getString("DataSource"));
                        codeResultItem.setTitle(resultObject.getString("Title"));
                        codeResultItem.setDescription(resultObject.getString("Description"));
                        codeResultItem.setSeverity(resultObject.getString("Severity"));
                        codeResultItem.setCweIds(resultObject.getString("CweIDs"));
                        codeResultItem.setCvss(resultObject.getString("CVSS"));
                        codeResultItem.setReferences(resultObject.getString("References"));
                        codeResultItem.setPublishedDate(resultObject.getString("PublishedDate"));
                        codeResultItem.setLastModifiedDate(resultObject.getString("LastModifiedDate"));
                        codeResultItemMapper.insertSelective(codeResultItem);
                        i++;
                    }

                }
            }
        }

        return i;

    }

    public String getCredential() {
        return systemProviderService.getCodeCredential();
    }

    public Map<String, Object> topInfo(Map<String, Object> params) {
        return extCodeMapper.topInfo(params);
    }

    public List<Map<String, Object>> projectChart() {
        return extCodeMapper.projectChart();
    }

    public List<Map<String, Object>> severityChart() {
        return extCodeMapper.severityChart();
    }

    public String download(Map<String, Object> map) {
        HistoryCodeResult codeResult = systemProviderService.codeResult(map.get("id").toString());
        String str = codeResult.getReturnJson();
        return str;
    }

    public List<HistoryCodeResultDTO> history(Map<String, Object> params) {
        List<HistoryCodeResultDTO> historyList = systemProviderService.codeHistory(params);
        return historyList;
    }

    public List<CodeResultItemWithBLOBs> historyResultItemList(CodeResultItem codeResultItem) {
        CodeResultItemExample example = new CodeResultItemExample();
        example.createCriteria().andResultIdEqualTo(codeResultItem.getResultId());
        example.setOrderByClause("FIELD(`severity`, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'UNKNOWN')");
        return codeResultItemMapper.selectByExampleWithBLOBs(example);
    }

    public void deleteHistoryCodeResult(String id) throws Exception {
        systemProviderService.deleteHistoryCodeResult(id);
    }

}
