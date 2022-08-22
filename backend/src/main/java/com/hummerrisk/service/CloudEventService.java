package com.hummerrisk.service;

import com.aliyun.actiontrail20171204.Client;
import com.aliyun.actiontrail20171204.models.LookupEventsRequest;
import com.aliyun.actiontrail20171204.models.LookupEventsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.hummerrisk.base.domain.AccountWithBLOBs;
import com.hummerrisk.base.domain.CloudEvent;
import com.hummerrisk.base.domain.CloudEventSyncLog;
import com.hummerrisk.base.domain.CloudEventSyncLogExample;
import com.hummerrisk.base.mapper.CloudEventMapper;
import com.hummerrisk.base.mapper.CloudEventSyncLogMapper;
import com.hummerrisk.base.mapper.ProxyMapper;
import com.hummerrisk.commons.utils.DateUtils;
import com.hummerrisk.commons.utils.PlatformUtils;
import com.hummerrisk.dto.CloudEventDto;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class CloudEventService {

    @Resource
    private AccountService accountService;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private CloudEventMapper cloudEventMapper;
    @Resource
    private CloudEventSyncLogMapper cloudEventSyncLogMapper;
    private static final int MAX_PAGE_NUM = 1000;

    public void syncCloudEvents(String accountId,String region,String startTime,String endTime){
        CloudEventSyncLog cloudEventSyncLog = new CloudEventSyncLog();
        cloudEventSyncLog.setAccountId(accountId);
        cloudEventSyncLog.setRegion(region);
        cloudEventSyncLog.setCreateTime(DateUtils.getNowDate());
        cloudEventSyncLog.setRequestStartTime(DateUtils.dateTime("yyyy-MM-dd HH:mi:ss",startTime));
        cloudEventSyncLog.setRequestEndTime(DateUtils.dateTime("yyyy-MM-dd HH:mi:ss",endTime));
        CloudEventSyncLogExample cloudEventSyncLogExample = new CloudEventSyncLogExample();
        cloudEventSyncLogExample.createCriteria()
                        .andEndTimeGreaterThan(DateUtils.dateTime("yyyy-MM-dd HH:mi:ss",startTime));
        List<CloudEventSyncLog> cloudEventSyncLogs = cloudEventSyncLogMapper.selectByExample(cloudEventSyncLogExample);

        cloudEventSyncLogMapper.insertSelective(cloudEventSyncLog);
    }

    public void saveCloudEvent(Integer logId,String accountId, String region, String startTime, String endTime){
        try{
            AccountWithBLOBs account = accountService.getAccount(accountId);
            Map<String, String> accountMap = PlatformUtils.getAccount(account, region, proxyMapper.selectByPrimaryKey(account.getProxyId()));
            List<CloudEvent> result = new ArrayList<>();
            int pageNum = 1;
            int maxNum = 50;
            boolean haveNextPage = true;
            while (haveNextPage){
                List<CloudEvent> pageResult = getCloudEvents(account,accountMap,startTime,endTime,pageNum,maxNum);
                result.addAll(pageResult);
                if(pageResult.size() < maxNum || pageNum > MAX_PAGE_NUM){
                    haveNextPage = false;
                }
                pageNum++;
            }
            int num = cloudEventMapper.batchCloudEvents(result);
            CloudEventSyncLog cloudEventSyncLog = new CloudEventSyncLog();
            cloudEventSyncLog.setId(logId);
            cloudEventSyncLog.setDataCount(num);
            if(result.size()>0){
                cloudEventSyncLog.setEndTime(result.get(0).getEventTime());
            }else{
                cloudEventSyncLog.setEndTime(DateUtils.dateTime("yyyy-MM-dd HH:mi:ss",startTime));
            }
            cloudEventSyncLog.setStatus(1);
            cloudEventSyncLogMapper.updateByPrimaryKeySelective(cloudEventSyncLog);
        }catch (Exception e){
            CloudEventSyncLog cloudEventSyncLog = new CloudEventSyncLog();
            cloudEventSyncLog.setId(logId);
            cloudEventSyncLog.setStatus(2);
            cloudEventSyncLog.setException(e.getMessage());
            cloudEventSyncLogMapper.updateByPrimaryKeySelective(cloudEventSyncLog);
        }
    }

    public List<CloudEvent>  getCloudEvents(String accountId, String region, String startTime, String endTime,
                                              int pageNum, int maxResult) throws Exception {
        AccountWithBLOBs account = accountService.getAccount(accountId);
        Map<String, String> accountMap = PlatformUtils.getAccount(account, region, proxyMapper.selectByPrimaryKey(account.getProxyId()));
        return getCloudEvents(account,accountMap,startTime,endTime,pageNum,maxResult);
    }

    public List<CloudEvent>  getCloudEvents(AccountWithBLOBs account,Map<String, String> accountMap,String startTime, String endTime,
                                               int pageNum, int maxResult) throws Exception {
        List<CloudEvent> result;
        switch (account.getPluginId()){
            case PlatformUtils.aliyun:
                result = getAliyunCloudEvents(accountMap,startTime,endTime,pageNum,maxResult);
                break;
            case PlatformUtils.huawei:
                result = getHuaweiCloudEvents(accountMap,startTime,endTime,pageNum,maxResult);
            default:
                throw new IllegalStateException("Unexpected value: " + account.getPluginId());
        }
        return result;
    }

    private List<CloudEvent> getHuaweiCloudEvents(Map<String,String> accountMap,String startTime
            , String endTime,int pageNum,int maxResult) {
        return new ArrayList<>();
    }

    private List<CloudEvent> getTencentEvents(Map<String,String> accountMap,String startTime
            , String endTime,int pageNum,int maxResult) {
        return new ArrayList<>();
    }
    public List<CloudEvent> getAliyunCloudEvents(Map<String, String> accountMap, String startTime, String endTime
            , int pageNum, int maxResult) throws Exception {
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
            return CloudEvent.builder().eventId((String)item.get("eventId"))
                    .eventName((String)item.get("eventName"))
                    .eventMessage((String)item.get("eventMessage")).eventSource((String)item.get("eventSource"))
                    .acsRegion((String)item.get("acsRegion")).userAgent((String)item.get("userAgent"))
                    .sourceIpAddress((String) item.get("sourceIpAddress")).serviceName((String) item.get("serviceName")).build();

        }).collect(Collectors.toList());
    }

}
