package com.hummer.common.core.dto;


import com.hummer.common.core.domain.CloudNativeSourceImage;

public class CloudNativeSourceImageDTO extends CloudNativeSourceImage {

    private String risk;

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }
}
