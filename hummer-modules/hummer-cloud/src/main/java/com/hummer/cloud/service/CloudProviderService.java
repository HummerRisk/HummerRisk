package com.hummer.cloud.service;

import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.cloud.mapper.*;
import com.hummer.cloud.mapper.ext.ExtAccountMapper;
import com.hummer.cloud.mapper.ext.ExtCloudTaskMapper;
import com.hummer.cloud.mapper.ext.ExtResourceMapper;
import com.hummer.cloud.mapper.ext.ExtRuleMapper;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.cloudTask.ManualRequest;
import com.hummer.common.core.dto.QuartzTaskDTO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

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
    private ExtResourceMapper extResourceMapper;
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
    private ExtAccountMapper extAccountMapper;
    @Autowired
    private ExtRuleMapper extRuleMapper;
    @Autowired
    private ExtCloudTaskMapper extCloudTaskMapper;
    @Autowired
    private RuleTagMapper ruleTagMapper;
    @Autowired
    private CloudTaskItemMapper cloudTaskItemMapper;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private CloudTaskItemResourceMapper cloudTaskItemResourceMapper;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired @Lazy
    private OrderService orderService;
    @Autowired
    private ProxyMapper proxyMapper;
    @Autowired
    private ProwlerService prowlerService;
    @Autowired
    private CloudResourceSyncItemMapper cloudResourceSyncItemMapper;
    @Autowired
    private CloudResourceSyncMapper cloudResourceSyncMapper;
    @Autowired
    private OssService ossService;
    @Autowired
    private OssMapper ossMapper;
    @Autowired
    private RuleGroupMapper ruleGroupMapper;


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


}
