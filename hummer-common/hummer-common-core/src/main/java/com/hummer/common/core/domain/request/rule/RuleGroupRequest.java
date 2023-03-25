package com.hummer.common.core.domain.request.rule;


import com.hummer.common.core.domain.RuleGroup;

import java.util.Map;

/**
 * @author harris
 */
public class RuleGroupRequest extends RuleGroup {

    private String accountId;

    private String type;

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

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}
