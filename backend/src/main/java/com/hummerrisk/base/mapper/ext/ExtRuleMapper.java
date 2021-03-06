package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.RuleTag;
import com.hummerrisk.controller.request.rule.CreateRuleRequest;
import com.hummerrisk.dto.RuleDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtRuleMapper {

    List<RuleTag> getTagsOfRule(@Param("ruleId") String ruleId);

    List<RuleDTO> cloudList(CreateRuleRequest Rule);

    List<RuleDTO> vulnList(CreateRuleRequest Rule);

    RuleDTO selectByPrimaryKey(@Param("ruleId") String ruleId, @Param("accountId") String accountId);
}
