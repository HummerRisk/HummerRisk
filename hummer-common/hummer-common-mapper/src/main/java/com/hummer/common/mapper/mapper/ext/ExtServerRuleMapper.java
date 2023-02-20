package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.ServerRuleDTO;
import com.hummer.common.mapper.domain.request.server.ServerRuleRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtServerRuleMapper {

    List<ServerRuleDTO> ruleList(@Param("request") ServerRuleRequest request);

}
