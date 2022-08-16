package com.hummerrisk.controller.request.k8s;


import com.hummerrisk.base.domain.CloudNativeResult;

import java.util.Map;

/**
 * @author harris
 */
public class K8sResultRequest extends CloudNativeResult {

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
