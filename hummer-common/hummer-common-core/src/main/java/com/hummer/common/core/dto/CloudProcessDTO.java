package com.hummer.common.core.dto;

import com.hummer.common.core.domain.CloudProcess;
import com.hummer.common.core.domain.CloudProcessLogWithBLOBs;

import java.util.List;

public class CloudProcessDTO {

    private String id;//当前步骤的id

    private Integer processStep = 3;

    private String resultStatus;

    private Integer processOrder = 1;

    private String processName;

    private Integer processRate;

    private String projectId;

    private String execTime;

    private List<CloudProcess> cloudProcessList;

    private List<CloudProcessLogWithBLOBs> cloudProcessLogList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getProcessStep() {
        return processStep;
    }

    public void setProcessStep(Integer processStep) {
        this.processStep = processStep;
    }

    public Integer getProcessOrder() {
        return processOrder;
    }

    public void setProcessOrder(Integer processOrder) {
        this.processOrder = processOrder;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Integer getProcessRate() {
        return processRate;
    }

    public void setProcessRate(Integer processRate) {
        this.processRate = processRate;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getExecTime() {
        return execTime;
    }

    public void setExecTime(String execTime) {
        this.execTime = execTime;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public List<CloudProcess> getCloudProcessList() {
        return cloudProcessList;
    }

    public void setCloudProcessList(List<CloudProcess> cloudProcessList) {
        this.cloudProcessList = cloudProcessList;
    }

    public List<CloudProcessLogWithBLOBs> getCloudProcessLogList() {
        return cloudProcessLogList;
    }

    public void setCloudProcessLogList(List<CloudProcessLogWithBLOBs> cloudProcessLogList) {
        this.cloudProcessLogList = cloudProcessLogList;
    }
}
