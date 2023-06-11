package com.hummer.common.core.dto;

public class ResourceRuleDTO {
    private String hummerId;
    private String hummerName;
    private String resourceType;
    private String severity;
    private String ruleId;
    private String ruleName;

    public String getHummerId() {
        return hummerId;
    }

    public void setHummerId(String hummerId) {
        this.hummerId = hummerId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getHummerName() {
        return hummerName;
    }

    public void setHummerName(String hummerName) {
        this.hummerName = hummerName;
    }
}
