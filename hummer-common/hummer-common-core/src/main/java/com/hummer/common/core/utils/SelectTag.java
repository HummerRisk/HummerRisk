package com.hummer.common.core.utils;

import java.util.LinkedList;
import java.util.List;

public class SelectTag {

    private String accountId;

    private List<String> regions = new LinkedList<>();

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }
}
