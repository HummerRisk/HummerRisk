package com.hummerrisk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudEventDto {
    private String eventId;
    private String eventMessage;
    private String eventSource;
    private String eventTime;
    private String acsRegion;
    private String eventName;
    private String sourceIpAddress;
    private String userAgent;
    private String userName;
    private String userType;
    private String accountId;
    private String principalId;
    private String serviceName;
    private String callBackUrl;
    private String mfaChecked;
    private String requestId;


}
