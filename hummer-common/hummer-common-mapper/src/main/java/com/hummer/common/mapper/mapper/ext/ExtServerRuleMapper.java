package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.core.domain.request.server.ServerRuleRequest;
import com.hummer.common.core.dto.ServerRuleDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtServerRuleMapper {

    List<ServerRuleDTO> ruleList(@Param("request") ServerRuleRequest request);

}
