package com.hummerrisk.controller.request.k8s;


import com.hummerrisk.base.domain.CloudNativeResultConfigItemWithBLOBs;

import java.util.Map;

/**
 * @author harris
 */
public class K8sConfigResultItemRequest extends CloudNativeResultConfigItemWithBLOBs {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
