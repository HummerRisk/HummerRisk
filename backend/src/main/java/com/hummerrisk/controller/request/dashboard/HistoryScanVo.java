package com.hummerrisk.controller.request.dashboard;

import com.hummerrisk.base.domain.HistoryScan;

import java.util.Map;

public class HistoryScanVo extends HistoryScan {

    private String accountName;

    private String userName;

    private String scanType;

    private String users;

    private String severityType;

    private String startTime;

    private String endTime;

    private Map<String, Object> combine;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getSeverityType() {
        return severityType;
    }

    public void setSeverityType(String severityType) {
        this.severityType = severityType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
