package com.hummer.common.core.mapper.ext;

import com.hummer.common.core.domain.request.rule.RuleGroupRequest;
import com.hummer.common.core.dto.RuleDTO;
import com.hummer.common.core.dto.RuleGroupDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtRuleGroupMapper {

    List<RuleGroupDTO> list(@Param("request") RuleGroupRequest request);

    List<RuleGroupDTO> allCloudRuleGroups(@Param("request") RuleGroupRequest request);

    List<RuleGroupDTO> allVulnRuleGroups(@Param("request") RuleGroupRequest request);

    List<RuleDTO> getRules(@Param("accountId") String accountId, @Param("groupId") String groupId);

    List<String> getRuleGroup(@Param("accountId") String accountId);

}
