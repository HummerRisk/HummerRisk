package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.server.ServerRuleRequest;
import com.hummerrisk.dto.ServerRuleDTO;

import java.util.List;

public interface ExtServerRuleMapper {

    List<ServerRuleDTO> ruleList(ServerRuleRequest request);

}
