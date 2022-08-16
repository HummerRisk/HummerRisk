package com.hummerrisk.service;

import com.aliyun.actiontrail20171204.Client;
import com.aliyun.actiontrail20171204.models.LookupEventsRequest;
import com.aliyun.actiontrail20171204.models.LookupEventsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.hummerrisk.base.domain.Account;
import com.hummerrisk.base.domain.AccountWithBLOBs;
import com.hummerrisk.base.mapper.ProxyMapper;
import com.hummerrisk.commons.utils.PlatformUtils;
import com.hummerrisk.dto.CloudEventDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class CloudEventService {

    @Resource
    private AccountService accountService;
    @Resource
    private ProxyMapper proxyMapper;

    public List<CloudEventDto>  getCloudEvents(String accountId, String region, String startTime, String endTime,
                                              int pageNum, int maxResult) throws Exception {
        List<CloudEventDto> result;
        AccountWithBLOBs account = accountService.getAccount(accountId);
        Map<String, String> accountMap = PlatformUtils.getAccount(account, region, proxyMapper.selectByPrimaryKey(account.getProxyId()));
        switch (account.getPluginId()){
            case PlatformUtils.aliyun:
                result = getAliyunCloudEvents(accountMap,startTime,endTime,pageNum,maxResult);
                break;
            case PlatformUtils.huawei:
                result = getHuaweiCloudEvents();
            default:
                throw new IllegalStateException("Unexpected value: " + account.getPluginId());
        }
        return result;
    }

    private List<CloudEventDto> getHuaweiCloudEvents() {
        return new ArrayList<>();
    }

    public List<CloudEventDto> getAliyunCloudEvents(Map<String, String> accountMap,String startTime,String endTime
            ,int pageNum,int maxResult) throws Exception {
        Config config = new Config().setAccessKeyId(accountMap.get("accessKey")).setAccessKeySecret(accountMap.get("secretKey"));
        config.endpoint = "actiontrail."+accountMap.get("region")+".aliyuncs.com";
        Client client = new Client(config);
        LookupEventsRequest lookupEventsRequest = new LookupEventsRequest();
        lookupEventsRequest.setStartTime(startTime);
        lookupEventsRequest.setEndTime(endTime);
        lookupEventsRequest.setNextToken((pageNum-1)*maxResult+"");
        lookupEventsRequest.setMaxResults(maxResult+"");
        LookupEventsResponse lookupEventsResponse = client.lookupEvents(lookupEventsRequest);
        List<Map<String, ?>> events = lookupEventsResponse.body.getEvents();
        return events.stream().map(item -> {
            return CloudEventDto.builder().eventId((String)item.get("eventId"))
                    .eventTime((String) item.get("eventTime")).eventName((String)item.get("eventName"))
                    .eventMessage((String)item.get("eventMessage")).eventSource((String)item.get("eventSource"))
                    .acsRegion((String)item.get("acsRegion")).userAgent((String)item.get("userAgent"))
                    .sourceIpAddress((String) item.get("sourceIpAddress")).serviceName((String) item.get("serviceName")).build();

        }).collect(Collectors.toList());
    }

}
