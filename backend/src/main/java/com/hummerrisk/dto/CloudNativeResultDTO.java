package com.hummerrisk.dto;

import com.hummerrisk.base.domain.CloudNativeResult;


public class CloudNativeResultDTO extends CloudNativeResult {

    private String critical;

    private String high;

    private String medium;

    private String low;

    private String unknown;

    private String configCritical;

    private String configHigh;

    private String configMedium;

    private String configLow;

    private String configUnknown;

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

    public String getConfigCritical() {
        return configCritical;
    }

    public void setConfigCritical(String configCritical) {
        this.configCritical = configCritical;
    }

    public String getConfigHigh() {
        return configHigh;
    }

    public void setConfigHigh(String configHigh) {
        this.configHigh = configHigh;
    }

    public String getConfigMedium() {
        return configMedium;
    }

    public void setConfigMedium(String configMedium) {
        this.configMedium = configMedium;
    }

    public String getConfigLow() {
        return configLow;
    }

    public void setConfigLow(String configLow) {
        this.configLow = configLow;
    }

    public String getConfigUnknown() {
        return configUnknown;
    }

    public void setConfigUnknown(String configUnknown) {
        this.configUnknown = configUnknown;
    }
}
