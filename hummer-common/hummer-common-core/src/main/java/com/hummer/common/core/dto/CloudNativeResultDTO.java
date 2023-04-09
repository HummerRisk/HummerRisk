package com.hummer.common.core.dto;


import com.hummer.common.core.domain.CloudNativeResultWithBLOBs;

public class CloudNativeResultDTO extends CloudNativeResultWithBLOBs {

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

    private String fail;

    private String warn;

    private String info;

    private String pass;

    private Long scanTime;

    private String cloudResourcesSum;

    private String cloudReturnSum;

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

    public String getFail() {
        return fail;
    }

    public void setFail(String fail) {
        this.fail = fail;
    }

    public String getWarn() {
        return warn;
    }

    public void setWarn(String warn) {
        this.warn = warn;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Long getScanTime() {
        return scanTime;
    }

    public void setScanTime(Long scanTime) {
        this.scanTime = scanTime;
    }

    public String getCloudResourcesSum() {
        return cloudResourcesSum;
    }

    public void setCloudResourcesSum(String cloudResourcesSum) {
        this.cloudResourcesSum = cloudResourcesSum;
    }

    public String getCloudReturnSum() {
        return cloudReturnSum;
    }

    public void setCloudReturnSum(String cloudReturnSum) {
        this.cloudReturnSum = cloudReturnSum;
    }
}
