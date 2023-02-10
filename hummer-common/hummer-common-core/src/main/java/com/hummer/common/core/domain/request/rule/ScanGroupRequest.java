package com.hummer.common.core.domain.request.rule;


import java.util.List;

/**
 * @author harris
 */
public class ScanGroupRequest {

    private List<Integer> groups;

    private String accountId;

    public List<Integer> getGroups() {
        return groups;
    }

    public void setGroups(List<Integer> groups) {
        this.groups = groups;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
