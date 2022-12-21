package com.hummerrisk.dto;

public class CloudEventSourceIpInsightDto {
    private String sourceIpAddress;
    private Long earliest_event_time;
    private Long last_event_time;
    private String acs_region;
    private String event_name;
    private Integer num;

    public String getSourceIpAddress() {
        return sourceIpAddress;
    }

    public void setSourceIpAddress(String sourceIpAddress) {
        this.sourceIpAddress = sourceIpAddress;
    }

    public Long getEarliest_event_time() {
        return earliest_event_time;
    }

    public void setEarliest_event_time(Long earliest_event_time) {
        this.earliest_event_time = earliest_event_time;
    }

    public Long getLast_event_time() {
        return last_event_time;
    }

    public void setLast_event_time(Long last_event_time) {
        this.last_event_time = last_event_time;
    }

    public String getAcs_region() {
        return acs_region;
    }

    public void setAcs_region(String acs_region) {
        this.acs_region = acs_region;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
