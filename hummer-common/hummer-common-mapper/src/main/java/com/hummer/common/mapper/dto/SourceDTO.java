package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.Account;
import com.hummer.common.mapper.domain.CloudTask;

import java.util.List;
import java.util.Map;


public class SourceDTO extends Account {

    private String returnSum;

    private String resourcesSum;

    private List<CloudTask> cloudTaskList;

    private String userName;

    private String scanScore;

    private String resultStatus;

    private String overRules;

    private String allRules;

    private List<Map<String, Object>> regionData;

    private List<Map<String, Object>> severityData;

    private List<Map<String, Object>> resourceTypeData;

    private List<Map<String, Object>> ruleData;

    public String getReturnSum() {
        return returnSum;
    }

    public void setReturnSum(String returnSum) {
        this.returnSum = returnSum;
    }

    public String getResourcesSum() {
        return resourcesSum;
    }

    public void setResourcesSum(String resourcesSum) {
        this.resourcesSum = resourcesSum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getScanScore() {
        return scanScore;
    }

    public void setScanScore(String scanScore) {
        this.scanScore = scanScore;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public List<CloudTask> getCloudTaskList() {
        return cloudTaskList;
    }

    public void setCloudTaskList(List<CloudTask> cloudTaskList) {
        this.cloudTaskList = cloudTaskList;
    }

    public String getOverRules() {
        return overRules;
    }

    public void setOverRules(String overRules) {
        this.overRules = overRules;
    }

    public String getAllRules() {
        return allRules;
    }

    public void setAllRules(String allRules) {
        this.allRules = allRules;
    }

    public List<Map<String, Object>> getRegionData() {
        return regionData;
    }

    public void setRegionData(List<Map<String, Object>> regionData) {
        this.regionData = regionData;
    }

    public List<Map<String, Object>> getSeverityData() {
        return severityData;
    }

    public void setSeverityData(List<Map<String, Object>> severityData) {
        this.severityData = severityData;
    }

    public List<Map<String, Object>> getResourceTypeData() {
        return resourceTypeData;
    }

    public void setResourceTypeData(List<Map<String, Object>> resourceTypeData) {
        this.resourceTypeData = resourceTypeData;
    }

    public List<Map<String, Object>> getRuleData() {
        return ruleData;
    }

    public void setRuleData(List<Map<String, Object>> ruleData) {
        this.ruleData = ruleData;
    }
}
