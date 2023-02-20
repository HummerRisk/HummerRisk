package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.CloudNativeSourceImage;

public class CloudNativeSourceImageDTO extends CloudNativeSourceImage {

    private String risk;

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }
}
