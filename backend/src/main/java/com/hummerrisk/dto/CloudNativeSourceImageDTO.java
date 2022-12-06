package com.hummerrisk.dto;

import com.hummerrisk.base.domain.CloudNativeSourceImage;


public class CloudNativeSourceImageDTO extends CloudNativeSourceImage {

    private String risk;

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }
}
