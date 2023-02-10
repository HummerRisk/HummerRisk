package com.hummer.common.core.dto;

import java.io.Serializable;

/**
 * @author harris
 */
public class ResourceDTO extends ResourceDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;
    private String type;
    private String applyUser;
    private String taskName;
    private String resourceStr;
    private String ruleId;
    private String ruleName;
    private String accountName;
    private String suggestion;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getResourceStr() {
        return resourceStr;
    }

    public void setResourceStr(String resourceStr) {
        this.resourceStr = resourceStr;
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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
