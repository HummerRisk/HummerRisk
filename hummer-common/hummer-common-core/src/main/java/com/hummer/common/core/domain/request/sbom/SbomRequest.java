package com.hummer.common.core.domain.request.sbom;

import com.hummer.common.core.domain.Sbom;

import java.util.Map;

/**
 * harris
 */
public class SbomRequest extends Sbom {

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
