package com.hummerrisk.controller.request.image;

import com.hummerrisk.base.domain.ImageRepoItem;

import java.util.Map;

/**
 * harris
 */
public class ScanImageRepoRequest extends ImageRepoItem {

    private Map<String, Object> combine;

    private String name;

    private String sbomId;

    private String sbomVersionId;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSbomId() {
        return sbomId;
    }

    public void setSbomId(String sbomId) {
        this.sbomId = sbomId;
    }

    public String getSbomVersionId() {
        return sbomVersionId;
    }

    public void setSbomVersionId(String sbomVersionId) {
        this.sbomVersionId = sbomVersionId;
    }
}
