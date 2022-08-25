package com.hummerrisk.service;

import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtCodeMapper;
import com.hummerrisk.base.mapper.ext.ExtCodeResultMapper;
import com.hummerrisk.base.mapper.ext.ExtCodeRuleMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.utils.BeanUtils;
import com.hummerrisk.commons.utils.SessionUtils;
import com.hummerrisk.commons.utils.UUIDUtil;
import com.hummerrisk.controller.request.code.CodeRequest;
import com.hummerrisk.controller.request.code.CodeResultRequest;
import com.hummerrisk.controller.request.code.CodeRuleRequest;
import com.hummerrisk.dto.CodeDTO;
import com.hummerrisk.dto.CodeResultDTO;
import com.hummerrisk.dto.CodeResultWithBLOBsDTO;
import com.hummerrisk.dto.CodeRuleDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    private HistoryService historyService;


    public List<CodeDTO> codeList(CodeRequest request) {
        return extCodeMapper.codeList(request);
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

    public List<CodeResultItem> resultItemList(CodeResultItem codeResultItem) {
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

}
