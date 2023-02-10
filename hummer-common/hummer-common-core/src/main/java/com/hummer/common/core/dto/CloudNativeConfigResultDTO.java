package com.hummer.common.core.dto;

import com.hummerrisk.base.domain.CloudNativeConfigResult;


public class CloudNativeConfigResultDTO extends CloudNativeConfigResult {

    private String critical;

    private String high;

    private String medium;

    private String low;

    private String unknown;

    public String getCritical() {
        return critical;
    }

    public void setCritical(String critical) {
        this.critical = critical;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getUnknown() {
        return unknown;
    }

    public void setUnknown(String unknown) {
        this.unknown = unknown;
    }
}
