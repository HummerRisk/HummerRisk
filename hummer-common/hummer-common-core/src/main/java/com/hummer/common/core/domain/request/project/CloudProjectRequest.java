package com.hummer.common.core.domain.request.project;

import com.hummer.common.core.domain.CloudProject;

import java.io.Serializable;
import java.util.Map;

public class CloudProjectRequest extends CloudProject implements Serializable {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}