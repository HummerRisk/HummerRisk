package com.hummerrisk.controller.request.packageSetting;

import com.hummerrisk.base.domain.Package;

import java.util.Map;

/**
 * harris
 */
public class PackageRequest extends Package {

    private String url;

    private Map<String, Object> combine;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
