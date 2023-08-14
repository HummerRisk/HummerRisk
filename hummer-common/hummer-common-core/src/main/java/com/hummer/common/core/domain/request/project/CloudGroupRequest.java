package com.hummer.common.core.domain.request.project;

import com.hummer.common.core.domain.CloudGroup;

import java.io.Serializable;
import java.util.Map;

public class CloudGroupRequest extends CloudGroup implements Serializable {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}