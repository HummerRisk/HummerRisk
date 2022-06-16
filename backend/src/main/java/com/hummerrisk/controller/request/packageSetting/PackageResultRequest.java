package com.hummerrisk.controller.request.packageSetting;


import com.hummerrisk.base.domain.PackageResult;

import java.util.Map;

/**
 * @author harris
 */
public class PackageResultRequest extends PackageResult {

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
