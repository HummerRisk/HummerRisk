package com.hummer.common.core.dto;

import com.hummer.common.core.domain.CloudProcess;
import com.hummer.common.core.domain.CloudProcessLog;

import java.util.List;

public class CloudProcessDTO extends CloudProcess {

    private List<CloudProcessLog> cloudProcessLogList;

    public List<CloudProcessLog> getCloudProcessLogList() {
        return cloudProcessLogList;
    }

    public void setCloudProcessLogList(List<CloudProcessLog> cloudProcessLogList) {
        this.cloudProcessLogList = cloudProcessLogList;
    }
}
