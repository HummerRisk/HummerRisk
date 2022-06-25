package com.hummerrisk.dto;

import com.hummerrisk.base.domain.Account;
import com.hummerrisk.base.domain.CloudTask;

import java.util.List;


public class SourceDTO extends Account {

    private String returnSum;

    private String resourcesSum;

    private List<CloudTask> cloudTaskList;

    private String userName;

    private String scanScore;

    private String resultStatus;

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

    public List<CloudTask> getTaskList() {
        return cloudTaskList;
    }

    public void setTaskList(List<CloudTask> cloudTaskList) {
        this.cloudTaskList = cloudTaskList;
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
}
