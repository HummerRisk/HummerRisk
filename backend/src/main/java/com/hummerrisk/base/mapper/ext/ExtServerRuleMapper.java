package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.server.ServerRuleRequest;
import com.hummerrisk.dto.ServerRuleDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtServerRuleMapper {

    List<ServerRuleDTO> ruleList(@Param("request") ServerRuleRequest request);

}
