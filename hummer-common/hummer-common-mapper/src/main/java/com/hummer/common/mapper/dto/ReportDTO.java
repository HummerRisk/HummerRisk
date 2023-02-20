package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.CloudTask;
import com.hummer.common.mapper.domain.RuleInspectionReport;

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

    public List<CloudTask> getCloudTaskList() {
        return cloudTaskList;
    }

    public void setCloudTaskList(List<CloudTask> cloudTaskList) {
        this.cloudTaskList = cloudTaskList;
    }
}
