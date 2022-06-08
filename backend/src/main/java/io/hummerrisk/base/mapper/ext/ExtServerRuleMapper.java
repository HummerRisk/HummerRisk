package io.hummerrisk.base.mapper.ext;

import io.hummerrisk.controller.request.server.ServerRuleRequest;
import io.hummerrisk.dto.ServerRuleDTO;

import java.util.List;

public interface ExtServerRuleMapper {

    List<ServerRuleDTO> ruleList(ServerRuleRequest request);

}
