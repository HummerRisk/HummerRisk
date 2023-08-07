package com.hummer.cloud.service;

import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.cloud.mapper.*;
import com.hummer.cloud.mapper.ext.ExtCloudTaskMapper;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.cloudTask.ManualRequest;
import com.hummer.common.core.dto.QuartzTaskDTO;
import com.hummer.system.api.model.LoginUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author harris
 */
@DubboService
public class CloudProviderService implements ICloudProviderService {

    @Autowired
    private CloudTaskMapper cloudTaskMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private RuleTagMappingMapper ruleTagMappingMapper;
    @Autowired
    private RuleGroupMappingMapper ruleGroupMappingMapper;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private CloudTaskService cloudTaskService;
    @Autowired
    private ExtCloudTaskMapper extCloudTaskMapper;
    @Autowired
    private RuleTagMapper ruleTagMapper;
    @Autowired
    private RuleGroupMapper ruleGroupMapper;
    @Autowired
    private AccountService accountService;


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
    public String reScan(String resourceId, String accountId, LoginUser loginUser) throws Exception {
        return ruleService.reScan(resourceId, accountId, loginUser);
    }

    @Override
    public CloudTask saveManualTask(QuartzTaskDTO quartzTaskDTO, String messageOrderId, LoginUser loginUser) {
        return cloudTaskService.saveManualTask(quartzTaskDTO, messageOrderId, loginUser);
    }

    @Override
    public List<RuleTagMapping> ruleTagMappings(RuleTagMappingExample example) {
        return ruleTagMappingMapper.selectByExample(example);
    }

    @Override
    public List<RuleGroupMapping> selectRuleGroupMappings(RuleGroupMappingExample example) {
        return ruleGroupMappingMapper.selectByExample(example);
    }

    @Override
    public List<CloudTask> selectManualTasks(ManualRequest request) throws Exception {
        return null;
    }

    @Override
    public void insertRuleTagMappings(RuleTagMapping record) {
        ruleTagMappingMapper.insertSelective(record);
    }

    @Override
    public void deleteRuleTag(String tagKey) {
        ruleTagMapper.deleteByPrimaryKey(tagKey);
    }

    @Override
    public void deleteRuleTagMappingByExample(RuleTagMappingExample example) {
        ruleTagMappingMapper.deleteByExample(example);
    }

    @Override
    public void deleteRuleGroupMapping(RuleGroupMappingExample example) {
        ruleGroupMappingMapper.deleteByExample(example);
    }

    @Override
    public void insertRuleGroupMapping(RuleGroupMapping record) {
        ruleGroupMappingMapper.insertSelective(record);
    }

    @Override
    public List<RuleGroup> ruleGroupList(RuleGroupExample example) {
        return ruleGroupMapper.selectByExample(example);
    }

    @Override
    public List<CloudTask> getTopTasksForEmail(MessageOrder messageOrder) {
        return extCloudTaskMapper.getTopTasksForEmail(messageOrder);
    }

    @Override
    public int getReturnSumForEmail(MessageOrder messageOrder) {
        return extCloudTaskMapper.getReturnSumForEmail(messageOrder);
    }

    @Override
    public int getResourcesSumForEmail(MessageOrder messageOrder) {
        return extCloudTaskMapper.getResourcesSumForEmail(messageOrder);
    }

    @Override
    public int insertCloudAccount(AccountWithBLOBs account) throws Exception {
        AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(account.getId());
        if (accountWithBLOBs != null)  return 1;
        int i = accountMapper.insertSelective(account);
        accountService.updateRegionsThrows(account);
        return i;
    }

    @Override
    public int updateCloudAccount(AccountWithBLOBs account) throws Exception {
        AccountWithBLOBs accountWithBLOBs = accountMapper.selectByPrimaryKey(account.getId());
        if (accountWithBLOBs != null) {
            int i = accountMapper.updateByPrimaryKeySelective(account);
            accountService.updateRegionsThrows(account);
            return i;
        } else {
            int i = accountMapper.insertSelective(account);
            accountService.updateRegionsThrows(account);
            return i;
        }
    }

    @Override
    public int deleteCloudAccount(String id) {
        return accountMapper.deleteByPrimaryKey(id);
    }

    @Override
    public long handleK8sTask(String accountId) {
        return cloudTaskService.handleK8sTask(accountId);
    }

    @Override
    public long getResourceSum(String accountId) {
        return cloudTaskService.getResourceSum(accountId);
    }

    @Override
    public long getReturnSum(String accountId) {
        return cloudTaskService.getReturnSum(accountId);
    }

}
