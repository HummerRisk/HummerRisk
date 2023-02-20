package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.CloudTask;

/**
 * @author harris
 */
public class CloudTaskDTO extends CloudTask {
    private String customData;
    private String details;
    private Integer count;
    private String ruleId;
    private String taskItemCreateTime;
    private String accountName;

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getTaskItemCreateTime() {
        return taskItemCreateTime;
    }

    public void setTaskItemCreateTime(String taskItemCreateTime) {
        this.taskItemCreateTime = taskItemCreateTime;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
