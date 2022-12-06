package com.hummerrisk.controller.request.config;


import com.hummerrisk.base.domain.CloudNativeConfigResult;

import java.util.Map;

/**
 * @author harris
 */
public class ConfigResultRequest extends CloudNativeConfigResult {

    private String name;

    private String resultId;

    private Map<String, Object> combine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
