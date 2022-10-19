package com.hummerrisk.controller.request.cloudNative;

import com.hummerrisk.base.domain.CloudNativeSourceSyncLogWithBLOBs;

import java.util.Map;

/**
 * harris
 */
public class CloudNativeSyncLogRequest extends CloudNativeSourceSyncLogWithBLOBs {

    private String name;

    private Map<String, Object> combine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
