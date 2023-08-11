package com.hummer.common.core.dto;

import com.hummer.common.core.domain.CloudGroup;
import com.hummer.common.core.domain.CloudGroupLog;

import java.util.List;

public class CloudGroupDTO extends CloudGroup {

    private List<CloudGroupLog> cloudCloudGroupLogList;

    public List<CloudGroupLog> getCloudCloudGroupLogList() {
        return cloudCloudGroupLogList;
    }

    public void setCloudCloudGroupLogList(List<CloudGroupLog> cloudCloudGroupLogList) {
        this.cloudCloudGroupLogList = cloudCloudGroupLogList;
    }
}
