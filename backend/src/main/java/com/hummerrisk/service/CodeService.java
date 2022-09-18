package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtCodeMapper;
import com.hummerrisk.base.mapper.ext.ExtCodeResultMapper;
import com.hummerrisk.base.mapper.ext.ExtCodeRuleMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.code.CodeRequest;
import com.hummerrisk.controller.request.code.CodeResultRequest;
import com.hummerrisk.controller.request.code.CodeRuleRequest;
import com.hummerrisk.dto.CodeDTO;
import com.hummerrisk.dto.CodeResultDTO;
import com.hummerrisk.dto.CodeResultWithBLOBsDTO;
import com.hummerrisk.dto.CodeRuleDTO;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.service.impl.ExecEngineFactoryImp;
import com.hummerrisk.service.impl.IProvider;
import io.kubernetes.client.openapi.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

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
    private ProxyMapper proxyMapper;
    @Resource
    private HistoryService historyService;
    @Resource
    private NoticeService noticeService;
    @Resource
    private ExecEngineFactoryImp execEngineFactoryImp;


    public List<CodeDTO> codeList(CodeRequest request) {
        return extCodeMapper.codeList(request);
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

    public List<CodeResultWithBLOBsDTO> resultListWithBLOBs(CodeResultRequest request) {
        List<CodeResultWithBLOBsDTO> list = extCodeResultMapper.resultListWithBLOBs(request);
        return list;
    }

    public List<CodeResultDTO> resultList(CodeResultRequest request) {
        List<CodeResultDTO> list = extCodeResultMapper.resultList(request);
        return list;
    }

    public CodeResult getCodeResult(String resultId) {
        CodeResult codeResult = codeResultMapper.selectByPrimaryKey(resultId);
        return codeResult;
    }

    public List<CodeResultLog> getCodeResultLog(String resultId) {
        CodeResultLogExample example = new CodeResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return codeResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public void deleteCodeResult(String id) throws Exception {
        CodeResultLogExample logExample = new CodeResultLogExample();
        logExample.createCriteria().andResultIdEqualTo(id);
        codeResultLogMapper.deleteByExample(logExample);
        codeResultMapper.deleteByPrimaryKey(id);
    }

    public List<CodeResultItemWithBLOBs> resultItemList(CodeResultItem codeResultItem) {
        CodeResultItemExample example = new CodeResultItemExample();
        example.createCriteria().andResultIdEqualTo(codeResultItem.getResultId());
        return codeResultItemMapper.selectByExampleWithBLOBs(example);
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
        List<CodeResult> list = codeResultMapper.selectByExample(example);

        for (CodeResult result : list) {
            CodeResultLogExample logExample = new CodeResultLogExample();
            logExample.createCriteria().andResultIdEqualTo(result.getId());
            codeResultLogMapper.deleteByExample(logExample);

        }
        codeResultMapper.deleteByExample(example);
    }

    void saveCodeResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        CodeResultLog codeResultLog = new CodeResultLog();
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

        historyService.insertHistoryCodeResultLog(BeanUtils.copyBean(new HistoryCodeResultLog(), codeResultLog));
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

            execute(code);
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
            saveCodeResultLog(result.getId(), "i18n_operation_ex" + ": " + StringUtils.substring(e.getMessage(), 0, 900) + "...", e.getMessage(), false);
        }
    }

    public String execute(Code code) throws Exception {
        Proxy proxy = new Proxy();
        if (code.getProxyId()!=null) {
            proxy = proxyMapper.selectByPrimaryKey(code.getProxyId());
        }
        IProvider cp = execEngineFactoryImp.getProvider("codeProvider");
        return (String) execEngineFactoryImp.executeMethod(cp, "execute", code, proxy);
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

}
