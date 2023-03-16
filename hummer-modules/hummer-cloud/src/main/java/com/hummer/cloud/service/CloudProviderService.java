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
import org.springframework.context.annotation.Lazy;

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
    @Resource
    private RuleTagMapper ruleTagMapper;
    @Resource
    private CloudTaskItemMapper cloudTaskItemMapper;
    @Resource
    private ResourceService resourceService;
    @Resource
    private CloudTaskItemResourceMapper cloudTaskItemResourceMapper;
    @Resource
    private ResourceMapper resourceMapper;
    @Resource @Lazy
    private OrderService orderService;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private ProwlerService prowlerService;
    @Resource
    private CloudResourceSyncItemMapper cloudResourceSyncItemMapper;
    @Resource
    private CloudResourceSyncMapper cloudResourceSyncMapper;
    @Resource
    private OssService ossService;
    @Resource
    private OssMapper ossMapper;

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



}
