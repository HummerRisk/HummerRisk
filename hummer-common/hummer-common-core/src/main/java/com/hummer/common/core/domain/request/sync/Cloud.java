package com.hummer.common.core.domain.request.sync;

import java.util.List;

/**
 * harris
 */
public class Cloud {

    private List<Region> children;

    private String name;

    private String accountId;

    public List<Region> getChildren() {
        return children;
    }

    public void setChildren(List<Region> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
