package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.CodeRuleDTO;
import com.hummer.common.mapper.domain.request.code.CodeRuleRequest;

import java.util.List;

public interface ExtCodeRuleMapper {

    List<CodeRuleDTO> ruleList(CodeRuleRequest request);

}
