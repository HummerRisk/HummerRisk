package com.hummer.common.core.domain.request.rule;


import com.hummerrisk.base.domain.RuleGroup;

import java.util.Map;

/**
 * @author harris
 */
public class RuleGroupRequest extends RuleGroup {

    private String accountId;

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
