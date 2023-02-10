package com.hummer.common.core.domain.request.sbom;

import com.hummerrisk.base.domain.SbomVersion;

import java.util.Map;

/**
 * harris
 */
public class SbomVersionRequest extends SbomVersion {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
