package com.hummer.common.mapper.domain;

public class CloudResourceSummary {
    private String resourceType;
    private Integer count;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
