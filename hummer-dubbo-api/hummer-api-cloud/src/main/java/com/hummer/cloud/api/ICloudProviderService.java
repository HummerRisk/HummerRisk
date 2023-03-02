package com.hummer.cloud.api;

import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.account.CloudAccountRequest;
import com.hummer.common.core.domain.request.cloudTask.ManualRequest;
import com.hummer.common.core.domain.request.rule.CreateRuleRequest;
import com.hummer.common.core.dto.AccountDTO;
import com.hummer.common.core.dto.QuartzTaskDTO;
import com.hummer.common.core.dto.RuleDTO;

import java.util.List;

public interface ICloudProviderService {

    List<CloudTask> selectCloudTaskList(CloudTaskExample example);

    CloudTask selectCloudTask(String id);

    Account selectAccount(String id);

    AccountWithBLOBs selectAccountWithBLOBs(String id);

    Rule selectRule(String id);

    String reScan(String resourceId, String accountId) throws Exception;

    CloudTask saveManualTask(QuartzTaskDTO quartzTaskDTO, String messageOrderId);

    List<RuleTagMapping> ruleTagMappings(RuleTagMappingExample example);

    List<RuleGroupMapping> ruleGroupMappings(RuleGroupMappingExample example);

    List<AccountDTO> getVulnList(CloudAccountRequest request);

    List<RuleDTO> vulnList(CreateRuleRequest request);

    List<CloudTask> selectManualTasks(ManualRequest request) throws Exception;

    void insertRuleTagMappings(RuleTagMapping record);

    void deleteRuleTag(String tagKey);

    void deleteRuleTagMappingByExample(RuleTagMappingExample example);



}
