package com.hummer.common.mapper.domain.request.cloudNative;

import com.hummer.common.mapper.domain.CloudNativeSourceSyncLogWithBLOBs;

import java.util.Map;

/**
 * harris
 */
public class CloudNativeSyncLogRequest extends CloudNativeSourceSyncLogWithBLOBs {

    private String name;

    private String k8sName;

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

    public String getK8sName() {
        return k8sName;
    }

    public void setK8sName(String k8sName) {
        this.k8sName = k8sName;
    }
}
