package com.hummerrisk.controller.request.rule;

import java.util.List;

/**
 * harris
 */
public class BindRuleRequest {

    private List<String> cloudValue;

    private String groupId;

    public List<String> getCloudValue() {
        return cloudValue;
    }

    public void setCloudValue(List<String> cloudValue) {
        this.cloudValue = cloudValue;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
