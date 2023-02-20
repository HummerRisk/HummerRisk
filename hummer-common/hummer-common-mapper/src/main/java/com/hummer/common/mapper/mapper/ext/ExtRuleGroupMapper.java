package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.RuleDTO;
import com.hummer.common.mapper.dto.RuleGroupDTO;
import com.hummer.common.mapper.domain.request.rule.RuleGroupRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtRuleGroupMapper {

    List<RuleGroupDTO> list(@Param("request") RuleGroupRequest request);

    List<RuleGroupDTO> allCloudRuleGroups(@Param("request") RuleGroupRequest request);

    List<RuleGroupDTO> allVulnRuleGroups(@Param("request") RuleGroupRequest request);

    List<RuleDTO> getRules(@Param("accountId") String accountId, @Param("groupId") String groupId);

    List<String> getRuleGroup(@Param("accountId") String accountId);

}
