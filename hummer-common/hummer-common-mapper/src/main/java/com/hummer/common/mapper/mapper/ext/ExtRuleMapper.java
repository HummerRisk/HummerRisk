package com.hummer.common.mapper.mapper.ext;


import com.hummer.common.mapper.dto.RuleDTO;
import com.hummer.common.mapper.domain.RuleTag;
import com.hummer.common.mapper.domain.request.rule.CreateRuleRequest;
import feign.Param;

import java.util.List;

public interface ExtRuleMapper {

    List<RuleTag> getTagsOfRule(@Param("ruleId") String ruleId);

    List<RuleDTO> cloudList(CreateRuleRequest Rule);

    List<RuleDTO> vulnList(CreateRuleRequest Rule);

    RuleDTO selectByPrimaryKey(@Param("ruleId") String ruleId, @Param("accountId") String accountId);
}
