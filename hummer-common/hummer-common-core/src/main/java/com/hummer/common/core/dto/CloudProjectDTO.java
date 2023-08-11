package com.hummer.common.core.dto;

import com.hummer.common.core.domain.CloudProject;
import com.hummer.common.core.domain.CloudProjectLog;

import java.util.List;

public class CloudProjectDTO extends CloudProject {

    private List<CloudProjectLog> cloudProjectLogList;

    public List<CloudProjectLog> getCloudProjectLogList() {
        return cloudProjectLogList;
    }

    public void setCloudProjectLogList(List<CloudProjectLog> cloudProjectLogList) {
        this.cloudProjectLogList = cloudProjectLogList;
    }
}
