package com.hummer.cloud.mapper.ext;


import com.hummer.common.core.domain.RuleTag;
import com.hummer.common.core.domain.request.rule.CreateRuleRequest;
import com.hummer.common.core.dto.RuleDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtRuleMapper {

    List<RuleTag> getTagsOfRule(@Param("ruleId") String ruleId);

    List<RuleDTO> cloudList(CreateRuleRequest Rule);

    List<RuleDTO> k8sList(CreateRuleRequest Rule);

    RuleDTO selectByPrimaryKey(@Param("ruleId") String ruleId, @Param("accountId") String accountId);
}
