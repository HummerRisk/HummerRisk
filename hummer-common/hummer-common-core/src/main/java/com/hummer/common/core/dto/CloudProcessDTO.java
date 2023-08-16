package com.hummer.common.core.dto;

import com.hummer.common.core.domain.CloudProcess;
import com.hummer.common.core.domain.CloudProcessLogWithBLOBs;

import java.util.List;

public class CloudProcessDTO {

    private Integer activeStep = 3;

    private Integer resultStatus = 2;

    private List<CloudProcess> cloudProcessList;

    private List<CloudProcessLogWithBLOBs> cloudProcessLogList;

    public Integer getActiveStep() {
        return activeStep;
    }

    public void setActiveStep(Integer activeStep) {
        this.activeStep = activeStep;
    }

    public Integer getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(Integer resultStatus) {
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
