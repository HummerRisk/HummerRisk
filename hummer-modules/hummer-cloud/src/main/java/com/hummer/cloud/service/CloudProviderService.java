package com.hummer.cloud.service;

import com.hummer.cloud.mapper.*;
import com.hummer.cloud.mapper.ext.ExtAccountMapper;
import com.hummer.cloud.mapper.ext.ExtCloudTaskMapper;
import com.hummer.cloud.mapper.ext.ExtResourceMapper;
import com.hummer.cloud.mapper.ext.ExtRuleMapper;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.account.CloudAccountRequest;
import com.hummer.common.core.domain.request.cloudTask.ManualRequest;
import com.hummer.common.core.domain.request.rule.CreateRuleRequest;
import com.hummer.common.core.dto.AccountDTO;
import com.hummer.common.core.dto.QuartzTaskDTO;
import com.hummer.common.core.dto.RuleDTO;
import com.hummer.cloud.api.ICloudProviderService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author harris
 */
@DubboService
public class CloudProviderService implements ICloudProviderService {

    @Resource
    private CloudTaskMapper cloudTaskMapper;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private ExtResourceMapper extResourceMapper;
    @Resource
    private RuleMapper ruleMapper;
    @Resource
    private RuleTagMappingMapper ruleTagMappingMapper;
    @Resource
    private RuleGroupMappingMapper ruleGroupMappingMapper;
    @Resource
    private RuleService ruleService;
    @Resource
    private CloudTaskService cloudTaskService;
    @Resource
    private ExtAccountMapper extAccountMapper;
    @Resource
    private ExtRuleMapper extRuleMapper;
    @Resource
    private ExtCloudTaskMapper extCloudTaskMapper;

    @Override
    public List<CloudTask> selectCloudTaskList(CloudTaskExample example) {
        return cloudTaskMapper.selectByExample(example);
    }

    @Override
    public CloudTask selectCloudTask(String id) {
        return cloudTaskMapper.selectByPrimaryKey(id);
    }

    @Override
    public Account selectAccount(String id) {
        return accountMapper.selectByPrimaryKey(id);
    }

    @Override
    public AccountWithBLOBs selectAccountWithBLOBs(String id) {
        return accountMapper.selectByPrimaryKey(id);
    }

    @Override
    public Rule selectRule(String id) {
        return ruleMapper.selectByPrimaryKey(id);
    }

    @Override
    public String reScan(String resourceId, String accountId) throws Exception {
        return ruleService.reScan(resourceId, accountId);
    }

    @Override
    public CloudTask saveManualTask(QuartzTaskDTO quartzTaskDTO, String messageOrderId) {
        return cloudTaskService.saveManualTask(quartzTaskDTO, messageOrderId);
    }

    @Override
    public List<RuleTagMapping> ruleTagMappings(RuleTagMappingExample example) {
        return ruleTagMappingMapper.selectByExample(example);
    }

    @Override
    public List<RuleGroupMapping> ruleGroupMappings(RuleGroupMappingExample example) {
        return ruleGroupMappingMapper.selectByExample(example);
    }

    @Override
    public List<AccountDTO> getVulnList(CloudAccountRequest request) {
        return extAccountMapper.getVulnList(request);
    }

    @Override
    public List<RuleDTO> vulnList(CreateRuleRequest request) {
        return extRuleMapper.vulnList(request);
    }

    @Override
    public List<CloudTask> selectManualTasks(ManualRequest request) throws Exception {
        return extCloudTaskMapper.selectManualVulnTasks(request);
    }

}
