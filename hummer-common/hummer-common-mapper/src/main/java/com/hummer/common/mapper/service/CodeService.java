package com.hummer.common.mapper.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.common.core.constant.*;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.code.CodeRequest;
import com.hummer.common.core.domain.request.code.CodeResultItemRequest;
import com.hummer.common.core.domain.request.code.CodeResultRequest;
import com.hummer.common.core.domain.request.code.CodeRuleRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.mapper.mapper.*;
import com.hummer.common.mapper.mapper.ext.ExtCodeMapper;
import com.hummer.common.mapper.mapper.ext.ExtCodeResultItemMapper;
import com.hummer.common.mapper.mapper.ext.ExtCodeResultMapper;
import com.hummer.common.mapper.mapper.ext.ExtCodeRuleMapper;
import com.hummer.common.core.proxy.code.CodeCredential;
import com.hummer.common.core.proxy.code.CodeCredentialRequest;
import com.hummer.common.core.utils.*;
import io.kubernetes.client.openapi.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CodeService {

    @Resource
    private ExtCodeMapper extCodeMapper;
    @Resource
    private CodeMapper codeMapper;
    @Resource
    private ExtCodeRuleMapper extCodeRuleMapper;
    @Resource
    private CodeRuleMapper codeRuleMapper;
    @Resource
    private ExtCodeResultMapper extCodeResultMapper;
    @Resource
    private CodeResultMapper codeResultMapper;
    @Resource
    private CodeResultLogMapper codeResultLogMapper;
    @Resource
    private RuleTagMapper ruleTagMapper;
    @Resource
    private RuleTagMappingMapper ruleTagMappingMapper;
    @Resource
    private CodeResultItemMapper codeResultItemMapper;
    @Resource
    private ExtCodeResultItemMapper extCodeResultItemMapper;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private HistoryService historyService;
    @Resource
    private NoticeService noticeService;
    @Resource
    private HistoryCodeResultMapper historyCodeResultMapper;
    @Resource
    private SystemParameterService systemParameterService;


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
        code.setCreator(SessionUtils.getUserId());
        code.setCreateTime(System.currentTimeMillis());
        code.setUpdateTime(System.currentTimeMillis());
        code.setStatus("VALID");

        OperationLogService.log(SessionUtils.getUser(), code.getId(), code.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.CREATE, "i18n_create_code");
        codeMapper.insertSelective(code);
        return code;
    }

    public Code updateCode(Code code) throws Exception {
        code.setUpdateTime(System.currentTimeMillis());
        code.setStatus("VALID");

        OperationLogService.log(SessionUtils.getUser(), code.getId(), code.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.UPDATE, "i18n_update_code");
        codeMapper.updateByPrimaryKeySelective(code);
        return code;
    }

    public void deleteCode(String id) throws Exception {
        codeMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.CODE.name(), ResourceOperation.DELETE, "i18n_delete_code");
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
        OperationLogService.log(SessionUtils.getUser(), record.getId(), record.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.CREATE, "i18n_create_code_rule");
        return codeRuleMapper.insertSelective(record);
    }

    public void saveRuleTagMapping(String ruleId, String tagKey) {
        deleteRuleTag(null, ruleId);
        if (StringUtils.isNotEmpty(tagKey)) {
            RuleTagMapping sfRulesTagMapping = new RuleTagMapping();
            sfRulesTagMapping.setRuleId(ruleId);
            sfRulesTagMapping.setTagKey(tagKey);
            ruleTagMappingMapper.insert(sfRulesTagMapping);
        }
    }

    public void deleteRuleTag(String tagkey, String ruleId) {
        if (StringUtils.isNotBlank(tagkey)) {
            ruleTagMapper.deleteByPrimaryKey(tagkey);
            RuleTagExample ruleTagExample = new RuleTagExample();
            ruleTagExample.createCriteria().andTagKeyEqualTo(tagkey);
            ruleTagMapper.deleteByExample(ruleTagExample);
        }
        if (StringUtils.isNotBlank(ruleId)) {
            RuleTagMappingExample ruleTagMappingExample = new RuleTagMappingExample();
            ruleTagMappingExample.createCriteria().andRuleIdEqualTo(ruleId);
            ruleTagMappingMapper.deleteByExample(ruleTagMappingExample);
        }
    }

    public int updateCodeRule(CodeRuleRequest request) throws Exception {
        CodeRule record = new CodeRule();
        BeanUtils.copyBean(record, request);
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        OperationLogService.log(SessionUtils.getUser(), record.getId(), record.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.UPDATE, "i18n_update_code_rule");
        return codeRuleMapper.updateByPrimaryKeySelective(record);
    }

    public void deleteCodeRule(String id) throws Exception {
        deleteRuleTag(null, id);
        codeRuleMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.CODE.name(), ResourceOperation.DELETE, "i18n_delete_code_rule");
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
        codeResultMapper.deleteByPrimaryKey(id);
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
        Integer scanId = historyService.insertScanHistory(code);
        if(StringUtils.equalsIgnoreCase(code.getStatus(), CloudAccountConstants.Status.VALID.name())) {
            List<CodeRuleDTO> ruleList = ruleList(null);
            CodeResult result = new CodeResult();

            deleteResultByCodeId(id);

            for(CodeRuleDTO dto : ruleList) {
                BeanUtils.copyBean(result, code);
                result.setId(UUIDUtil.newUUID());
                result.setCodeId(id);
                result.setApplyUser(SessionUtils.getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setRuleId(dto.getId());
                result.setRuleName(dto.getName());
                result.setRuleDesc(dto.getDescription());
                result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(dto.getSeverity());
                result.setUserName(SessionUtils.getUser().getName());
                codeResultMapper.insertSelective(result);

                saveCodeResultLog(result.getId(), "i18n_start_code_result", "", true);
                OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.SCAN, "i18n_start_code_result");

                historyService.insertScanTaskHistory(result, scanId, code.getId(), TaskEnum.codeAccount.getType());

                historyService.insertHistoryCodeResult(BeanUtils.copyBean(new HistoryCodeResult(), result));
            }
        }
    }

    public String reScan(String id) throws Exception {
        CodeResult result = codeResultMapper.selectByPrimaryKey(id);

        result.setUpdateTime(System.currentTimeMillis());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        result.setUserName(SessionUtils.getUser().getName());
        codeResultMapper.updateByPrimaryKeySelective(result);

        reScanDeleteCodeResult(id);

        saveCodeResultLog(result.getId(), "i18n_restart_code_result", "", true);

        OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.CODE.name(), ResourceOperation.RESCAN, "i18n_restart_code_result");

        historyService.updateHistoryCodeResult(BeanUtils.copyBean(new HistoryCodeResult(), result));

        return result.getId();
    }

    public void reScanDeleteCodeResult(String id) throws Exception {

        CodeResultItemExample example = new CodeResultItemExample();
        example.createCriteria().andResultIdEqualTo(id);
        codeResultItemMapper.deleteByExample(example);

    }

    public void deleteResultByCodeId(String id) throws Exception {
        CodeResultExample example = new CodeResultExample();
        example.createCriteria().andCodeIdEqualTo(id);
        codeResultMapper.deleteByExample(example);
    }

    public void saveCodeResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        CodeResultLogWithBLOBs codeResultLog = new CodeResultLogWithBLOBs();
        String operator = "system";
        try {
            if (SessionUtils.getUser() != null) {
                operator = SessionUtils.getUser().getId();
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

            noticeService.createCodeMessageOrder(result);
            saveCodeResultLog(result.getId(), "i18n_end_code_result", "", true);

            historyService.updateHistoryCodeResult(BeanUtils.copyBean(new HistoryCodeResult(), result));
        } catch (Exception e) {
            LogUtil.error("create CodeResult: " + e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            codeResultMapper.updateByPrimaryKeySelective(result);
            historyService.updateHistoryCodeResult(BeanUtils.copyBean(new HistoryCodeResult(), result));
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
            if (codeCredential != null && codeCredential.getToken() != null) {
                if (StringUtils.equals(code.getPluginIcon(), CodeConstants.GITHUB_TOKEN)) {
                    token = "export GITHUB_TOKEN=" + codeCredential.getToken() + "\n";
                } else if (StringUtils.equals(code.getPluginIcon(), CodeConstants.GITLAB_TOKEN)) {
                    token = "export GITLAB_TOKEN=" + codeCredential.getToken() + "\n";
                }
            }
            if (codeCredential != null && codeCredential.getBranch() != null) {
                branch = TrivyConstants.BRANCH + codeCredential.getBranch();
            } else if (codeCredential != null && codeCredential.getTag() != null) {
                branch = TrivyConstants.TAG + codeCredential.getTag();
            } else if (codeCredential != null && codeCredential.getCommit() != null) {
                branch = TrivyConstants.COMMIT + codeCredential.getCommit();
            }
            ScanSetting scanSetting = (ScanSetting) obj[2];
            String str = "";
            if(scanSetting.getSkipDbUpdate() != null && StringUtils.equalsIgnoreCase(scanSetting.getSkipDbUpdate(), "true")) {
                str = str + TrivyConstants.SKIP_DB_UPDATE + TrivyConstants.SKIP_JAVA_DB_UPDATE;
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
        String BASE_CREDENTIAL_DIC = "support/credential/";
        String JSON_EXTENSION = ".json";
        String pluginId = "hummer-code-plugin";
        try {
            return ReadFileUtils.readConfigFile(BASE_CREDENTIAL_DIC, pluginId, JSON_EXTENSION);
        } catch (Exception e) {
            LogUtil.error("Error getting credential parameters: " + pluginId, e);
            HRException.throwException(Translator.get("i18n_ex_plugin_get"));
        }
        return Translator.get("i18n_ex_plugin_get");
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
        HistoryCodeResult codeResult = historyCodeResultMapper.selectByPrimaryKey(map.get("id").toString());
        String str = codeResult.getReturnJson();
        return str;
    }

    public List<HistoryCodeResultDTO> history(Map<String, Object> params) {
        List<HistoryCodeResultDTO> historyList = extCodeResultMapper.history(params);
        return historyList;
    }

    public void deleteHistoryCodeResult(String id) throws Exception {
        historyCodeResultMapper.deleteByPrimaryKey(id);
    }

    public List<CodeResultItemWithBLOBs> historyResultItemList(CodeResultItem codeResultItem) {
        CodeResultItemExample example = new CodeResultItemExample();
        example.createCriteria().andResultIdEqualTo(codeResultItem.getResultId());
        example.setOrderByClause("FIELD(`severity`, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'UNKNOWN')");
        return codeResultItemMapper.selectByExampleWithBLOBs(example);
    }

}
