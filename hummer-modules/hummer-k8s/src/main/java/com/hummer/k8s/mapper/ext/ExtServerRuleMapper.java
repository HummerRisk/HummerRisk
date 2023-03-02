package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.dto.ServerRuleDTO;
import com.hummer.common.core.domain.request.server.ServerRuleRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtServerRuleMapper {

    List<ServerRuleDTO> ruleList(@Param("request") ServerRuleRequest request);

}
