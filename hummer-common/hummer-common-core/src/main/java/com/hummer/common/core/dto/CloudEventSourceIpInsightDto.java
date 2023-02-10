package com.hummer.common.core.dto;

public class CloudEventSourceIpInsightDto {
    private String sourceIpAddress;
    private Long earliestEventTime;
    private Long lastEventTime;
    private String region;
    private String eventName;
    private Integer eventSum;

    public String getSourceIpAddress() {
        return sourceIpAddress;
    }

    public void setSourceIpAddress(String sourceIpAddress) {
        this.sourceIpAddress = sourceIpAddress;
    }



    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Long getEarliestEventTime() {
        return earliestEventTime;
    }

    public void setEarliestEventTime(Long earliestEventTime) {
        this.earliestEventTime = earliestEventTime;
    }

    public Long getLastEventTime() {
        return lastEventTime;
    }

    public void setLastEventTime(Long lastEventTime) {
        this.lastEventTime = lastEventTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Integer getEventSum() {
        return eventSum;
    }

    public void setEventSum(Integer eventSum) {
        this.eventSum = eventSum;
    }
}
