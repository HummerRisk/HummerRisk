package com.hummer.common.core.dto;


import com.hummer.common.core.domain.CloudNative;

public class CloudNativeDTO extends CloudNative {

    private String userName;

    private boolean isProxy;

    private String proxyIp;

    private String proxyPort;

    private String proxyName;

    private String proxyPassword;

    private String k8sStatus;

    private String resultId;

    private String tagKey;

    private String tagName;

    private String ruleId;

    private String ruleName;

    private String ruleDesc;

    private String resultStatus;

    private String severity;

    private Long returnSum;

    private Long returnConfigSum;

    private Long scanTime;

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

    private String cloudResourcesSum;

    private String cloudReturnSum;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getIsProxy() {
        return isProxy;
    }

    public void setIsProxy(boolean isProxy) {
        this.isProxy = isProxy;
    }

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public boolean isProxy() {
        return isProxy;
    }

    public void setProxy(boolean proxy) {
        isProxy = proxy;
    }

    public String getK8sStatus() {
        return k8sStatus;
    }

    public void setK8sStatus(String k8sStatus) {
        this.k8sStatus = k8sStatus;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Long getReturnSum() {
        return returnSum;
    }

    public void setReturnSum(Long returnSum) {
        this.returnSum = returnSum;
    }

    public Long getReturnConfigSum() {
        return returnConfigSum;
    }

    public void setReturnConfigSum(Long returnConfigSum) {
        this.returnConfigSum = returnConfigSum;
    }

    public Long getScanTime() {
        return scanTime;
    }

    public void setScanTime(Long scanTime) {
        this.scanTime = scanTime;
    }

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

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }
}
