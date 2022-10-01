package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.rule.RuleGroupRequest;
import com.hummerrisk.dto.RuleDTO;
import com.hummerrisk.dto.RuleGroupDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtRuleGroupMapper {

    List<RuleGroupDTO> list(@Param("request") RuleGroupRequest request);

    List<RuleDTO> getRules(@Param("accountId") String accountId, @Param("groupId") String groupId);

    List<String> getRuleGroup(@Param("accountId") String accountId);

}
