package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.code.CodeRuleRequest;
import com.hummerrisk.dto.CodeRuleDTO;

import java.util.List;

public interface ExtCodeRuleMapper {

    List<CodeRuleDTO> ruleList(CodeRuleRequest request);

}
