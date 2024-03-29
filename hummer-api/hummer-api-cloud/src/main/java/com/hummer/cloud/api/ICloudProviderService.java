package com.hummer.cloud.api;

import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.cloudTask.ManualRequest;
import com.hummer.common.core.domain.request.rule.ScanGroupRequest;
import com.hummer.common.core.dto.QuartzTaskDTO;
import com.hummer.system.api.model.LoginUser;

import java.util.List;

public interface ICloudProviderService {

    List<CloudTask> selectCloudTaskList(CloudTaskExample example);

    CloudTask selectCloudTask(String id);

    Account selectAccount(String id);

    AccountWithBLOBs selectAccountWithBLOBs(String id);

    Rule selectRule(String id);

    String reScan(String resourceId, String accountId, LoginUser loginUser) throws Exception;

    CloudTask saveManualTask(QuartzTaskDTO quartzTaskDTO, String messageOrderId, LoginUser loginUser);

    List<RuleTagMapping> ruleTagMappings(RuleTagMappingExample example);

    List<RuleGroupMapping> selectRuleGroupMappings(RuleGroupMappingExample example);

    List<CloudTask> selectManualTasks(ManualRequest request) throws Exception;

    void insertRuleTagMappings(RuleTagMapping record);

    void deleteRuleTag(String tagKey);

    void deleteRuleTagMappingByExample(RuleTagMappingExample example);

    void deleteRuleGroupMapping(RuleGroupMappingExample record);

    void insertRuleGroupMapping(RuleGroupMapping record);

    List<RuleGroup> ruleGroupList(RuleGroupExample example);

    List<CloudTask> getTopTasksForEmail(MessageOrder messageOrder);

    int getReturnSumForEmail(MessageOrder messageOrder);

    int getResourcesSumForEmail(MessageOrder messageOrder);

    int insertCloudAccount(AccountWithBLOBs account) throws Exception;

    int updateCloudAccount(AccountWithBLOBs account) throws Exception;

    int deleteCloudAccount(String id);

    long handleK8sTask(String accountId);

    long getResourceSum(String accountId);

    long getReturnSum(String accountId);

    void scanK8s(ScanGroupRequest request, CloudNative cloudNative, LoginUser loginUser) throws Exception;

}
