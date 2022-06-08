package io.hummerrisk.service;

import io.hummerrisk.base.mapper.ext.ExtAccountMapper;
import io.hummerrisk.base.mapper.ext.ExtRuleMapper;
import io.hummerrisk.controller.request.account.CloudAccountRequest;
import io.hummerrisk.controller.request.rule.CreateRuleRequest;
import io.hummerrisk.dto.AccountDTO;
import io.hummerrisk.dto.RuleDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VulnService {

    @Resource
    private ExtAccountMapper extAccountMapper;
    @Resource
    private ExtRuleMapper extRuleMapper;

    public List<AccountDTO> getVulnList(CloudAccountRequest request) {
        return extAccountMapper.getVulnList(request);
    }

    public List<RuleDTO> vulnList(CreateRuleRequest ruleRequest) {
        return extRuleMapper.vulnList(ruleRequest);
    }
}
