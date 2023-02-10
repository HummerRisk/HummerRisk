package com.hummer.common.core.dto;

import com.hummerrisk.base.domain.CloudResourceSyncItem;
import com.hummerrisk.base.domain.CloudResourceSyncItemLog;

import java.util.List;

public class CloudResourceSyncItemDto extends CloudResourceSyncItem {
    private List<CloudResourceSyncItemLog> cloudResourceSyncItemLogs;

    public List<CloudResourceSyncItemLog> getCloudResourceSyncItemLogs() {
        return cloudResourceSyncItemLogs;
    }

    public void setCloudResourceSyncItemLogs(List<CloudResourceSyncItemLog> cloudResourceSyncItemLogs) {
        this.cloudResourceSyncItemLogs = cloudResourceSyncItemLogs;
    }
}
