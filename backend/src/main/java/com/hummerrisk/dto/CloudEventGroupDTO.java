package com.hummerrisk.dto;

public class CloudEventGroupDTO {
    private String eventDate;
    private String cloudAccountId;
    private String syncRegion ;
    private String regionName;
    private String eventName;
    private String userName;
    private String serviceName;
    private String sourceIpAddress;
    private String resourceName;
    private String eventSum;

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCloudAccountId() {
        return cloudAccountId;
    }

    public void setCloudAccountId(String cloudAccountId) {
        this.cloudAccountId = cloudAccountId;
    }

    public String getSyncRegion() {
        return syncRegion;
    }

    public void setSyncRegion(String syncRegion) {
        this.syncRegion = syncRegion;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSourceIpAddress() {
        return sourceIpAddress;
    }

    public void setSourceIpAddress(String sourceIpAddress) {
        this.sourceIpAddress = sourceIpAddress;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getEventSum() {
        return eventSum;
    }

    public void setEventSum(String eventSum) {
        this.eventSum = eventSum;
    }
}
