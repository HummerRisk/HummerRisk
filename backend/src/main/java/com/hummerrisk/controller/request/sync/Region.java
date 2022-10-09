package com.hummerrisk.controller.request.sync;


import java.util.List;

/**
 * harris
 */
public class Region {

    private List<ResourceType> children;

    private String name;

    private String accountId;

    private String regionId;

    public List<ResourceType> getChildren() {
        return children;
    }

    public void setChildren(List<ResourceType> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
