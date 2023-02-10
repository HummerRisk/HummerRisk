package com.hummer.common.core.domain.request.k8s;


import com.hummerrisk.base.domain.CloudNativeResultItem;

import java.util.Map;

/**
 * @author harris
 */
public class K8sResultItemRequest extends CloudNativeResultItem {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
