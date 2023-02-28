package com.hummer.common.core.domain;

import java.io.Serializable;

public class ScanSetting implements Serializable {

    private String skipDbUpdate;

    private String ignoreUnfixed;

    private String offlineScan;

    private String securityChecks;

    private String severity;

    public String getSkipDbUpdate() {
        return skipDbUpdate;
    }

    public void setSkipDbUpdate(String skipDbUpdate) {
        this.skipDbUpdate = skipDbUpdate;
    }

    public String getIgnoreUnfixed() {
        return ignoreUnfixed;
    }

    public void setIgnoreUnfixed(String ignoreUnfixed) {
        this.ignoreUnfixed = ignoreUnfixed;
    }

    public String getOfflineScan() {
        return offlineScan;
    }

    public void setOfflineScan(String offlineScan) {
        this.offlineScan = offlineScan;
    }

    public String getSecurityChecks() {
        return securityChecks;
    }

    public void setSecurityChecks(String securityChecks) {
        this.securityChecks = securityChecks;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
