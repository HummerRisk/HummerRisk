package com.hummerrisk.controller.request.sbom;

import java.util.List;

/**
 * harris
 */
public class SettingVersionRequest {

    private List<String> codeValue;

    private List<String> imageValue;

    private List<String> packageValue;

    private String sbomId;

    private String sbomVersionId;

    public List<String> getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(List<String> codeValue) {
        this.codeValue = codeValue;
    }

    public List<String> getImageValue() {
        return imageValue;
    }

    public void setImageValue(List<String> imageValue) {
        this.imageValue = imageValue;
    }

    public List<String> getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(List<String> packageValue) {
        this.packageValue = packageValue;
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
