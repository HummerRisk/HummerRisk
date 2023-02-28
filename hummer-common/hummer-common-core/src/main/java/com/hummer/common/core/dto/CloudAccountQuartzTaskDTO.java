package com.hummer.common.core.dto;


import com.hummer.common.core.domain.CloudAccountQuartzTask;

import java.util.List;

public class CloudAccountQuartzTaskDTO extends CloudAccountQuartzTask {

    private String accountId;

    private List<String> AccountIds;

    private List<String> ruleIds;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<String> getAccountIds() {
        return AccountIds;
    }

    public void setAccountIds(List<String> accountIds) {
        AccountIds = accountIds;
    }

    public List<String> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<String> ruleIds) {
        this.ruleIds = ruleIds;
    }
}
