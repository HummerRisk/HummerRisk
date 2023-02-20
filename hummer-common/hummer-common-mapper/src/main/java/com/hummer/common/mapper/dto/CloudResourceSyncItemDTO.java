package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.CloudResourceSyncItem;
import com.hummer.common.mapper.domain.CloudResourceSyncItemLog;

import java.util.List;

public class CloudResourceSyncItemDTO extends CloudResourceSyncItem {
    private List<CloudResourceSyncItemLog> cloudResourceSyncItemLogs;

    public List<CloudResourceSyncItemLog> getCloudResourceSyncItemLogs() {
        return cloudResourceSyncItemLogs;
    }

    public void setCloudResourceSyncItemLogs(List<CloudResourceSyncItemLog> cloudResourceSyncItemLogs) {
        this.cloudResourceSyncItemLogs = cloudResourceSyncItemLogs;
    }
}
