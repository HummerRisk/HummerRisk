package com.hummerrisk.dto;

import com.hummerrisk.base.domain.CloudTask;
import com.hummerrisk.base.domain.RuleInspectionReport;

import java.util.List;


public class ReportDTO extends RuleInspectionReport {

    private String Status;

    private List<CloudTask> cloudTaskList;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public List<CloudTask> getTaskList() {
        return cloudTaskList;
    }

    public void setTaskList(List<CloudTask> cloudTaskList) {
        this.cloudTaskList = cloudTaskList;
    }
}
