package com.hummerrisk.service;


import com.aliyun.actiontrail20171204.Client;
import com.aliyun.actiontrail20171204.models.LookupEventsRequest;
import com.aliyun.actiontrail20171204.models.LookupEventsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.CloudEventMapper;
import com.hummerrisk.base.mapper.CloudEventSyncLogMapper;
import com.hummerrisk.base.mapper.ProxyMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudEventMapper;
import com.hummerrisk.commons.utils.CommonThreadPool;
import com.hummerrisk.commons.utils.DateUtils;
import com.hummerrisk.commons.utils.PlatformUtils;
import com.hummerrisk.controller.request.cloudEvent.CloudEventRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional(rollbackFor = Exception.class)
public class CloudEventService {

    @Resource
    private AccountService accountService;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private CloudEventMapper cloudEventMapper;
    @Resource
    private ExtCloudEventMapper extCloudEventMapper;
    @Resource
    private CloudEventSyncLogMapper cloudEventSyncLogMapper;
    @Resource
    @Lazy
    private CommonThreadPool commonThreadPool;
    private static final int MAX_PAGE_NUM = 1000;

    @Resource
    private PlatformTransactionManager transactionManager;

    public List<CloudEventSyncLog> getCloudEventSyncLog(String accountId,String region){
        CloudEventSyncLogExample cloudEventSyncLogExample = new CloudEventSyncLogExample();
        cloudEventSyncLogExample.setOrderByClause(" create_time desc");
        CloudEventSyncLogExample.Criteria criteria =  cloudEventSyncLogExample.createCriteria();
        if(StringUtils.isNotBlank(accountId)){
            criteria.andAccountIdEqualTo(accountId);
        }
        if(StringUtils.isNotBlank(region)){
            criteria.andRegionEqualTo(region);
        }
        return cloudEventSyncLogMapper.selectByExample(cloudEventSyncLogExample);
    }

    public void deleteCloudEventSyncLog(int id){
        CloudEventSyncLogExample cloudEventSyncLogExample = new CloudEventSyncLogExample();
        cloudEventSyncLogExample.createCriteria().andIdEqualTo(id);
        cloudEventSyncLogMapper.deleteByExample(cloudEventSyncLogExample);
    }

    public void deleteCloudEvent(String id){
        CloudEventExample cloudEventExample = new CloudEventExample();
        cloudEventExample.createCriteria().andEventIdEqualTo(id);
        cloudEventMapper.deleteByExample(cloudEventExample);
    }

    public List<CloudEvent> getCloudEvents(CloudEventRequest cloudEventRequest) {
        CloudEventExample cloudEventExample = new CloudEventExample();
        cloudEventExample.setOrderByClause(" event_time desc ");
        CloudEventExample.Criteria criteria =cloudEventExample.createCriteria();
        if(StringUtils.isNotBlank(cloudEventRequest.getAccountId())){
            criteria.andCloudAccountIdEqualTo(cloudEventRequest.getAccountId());
        }
        if(StringUtils.isNotBlank(cloudEventRequest.getRegion())){
            criteria.andSyncRegionEqualTo(cloudEventRequest.getRegion());
        }
        if(StringUtils.isNotBlank(cloudEventRequest.getStartTime())){
            criteria.andEventTimeBetween(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", cloudEventRequest.getStartTime())
                    , DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", cloudEventRequest.getEndTime()));
        }
        return cloudEventMapper.selectByExample(cloudEventExample);
    }

    public void syncCloudEvents(String accountId, String region, String startTime, String endTime) {
        if(accountId==null||region==null||startTime==null||endTime==null){
            throw new RuntimeException("参数为空");
        }
        CloudEventSyncLog cloudEventSyncLog = new CloudEventSyncLog();
        cloudEventSyncLog.setAccountId(accountId);
        cloudEventSyncLog.setRegion(region);
        cloudEventSyncLog.setCreateTime(DateUtils.getNowDate());
        cloudEventSyncLog.setRequestStartTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime));
        cloudEventSyncLog.setRequestEndTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endTime));
        cloudEventSyncLog.setStatus(0);
        CloudEventSyncLogExample cloudEventSyncLogExample = new CloudEventSyncLogExample();
        cloudEventSyncLogExample.createCriteria().andStatusEqualTo(0).andAccountIdEqualTo(accountId)
                .andRegionEqualTo(region);
        List<CloudEventSyncLog> cloudEventSyncLogs = cloudEventSyncLogMapper.selectByExample(cloudEventSyncLogExample);
        if (cloudEventSyncLogs.size() > 0) {
            throw new RuntimeException("task running!");
        }
        cloudEventSyncLogMapper.insertSelective(cloudEventSyncLog);
        commonThreadPool.addTask(() -> {
            saveCloudEvent(cloudEventSyncLog.getId(), accountId, region, startTime, endTime);
        });
    }

    public void saveCloudEvent(Integer logId, String accountId, String region, String startTime, String endTime) {
        try {
            AccountWithBLOBs account = accountService.getAccount(accountId);
            Map<String, String> accountMap = PlatformUtils.getAccount(account, region, proxyMapper.selectByPrimaryKey(account.getProxyId()));
            List<CloudEvent> result = new ArrayList<>();
            int pageNum = 1;
            int maxNum = 20;
            boolean haveNextPage = true;
            while (haveNextPage) {
                List<CloudEvent> pageResult = getCloudEvents(account, accountMap, startTime, endTime, pageNum, maxNum);
                result.addAll(pageResult);
                if (pageResult.size() < maxNum || pageNum > MAX_PAGE_NUM) {
                    haveNextPage = false;
                }
                pageNum++;
            }
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            template.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus arg0) {
                    CloudEventSyncLog cloudEventSyncLog = new CloudEventSyncLog();
                    cloudEventSyncLog.setId(logId);
                    if (result.size() > 0) {
                        cloudEventSyncLog.setStartTime(result.get(result.size() - 1).getEventTime());
                        cloudEventSyncLog.setEndTime(result.get(0).getEventTime());
                        CloudEventExample cloudEventExample = new CloudEventExample();
                        cloudEventExample.createCriteria().andCloudAccountIdEqualTo(accountId).andSyncRegionEqualTo(region).andEventTimeBetween(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime)
                                , DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endTime));
                        cloudEventMapper.deleteByExample(cloudEventExample);
                    } else {
                        cloudEventSyncLog.setStartTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime));
                        cloudEventSyncLog.setEndTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime));
                    }
                    result.forEach(item -> {
                        item.setCloudAccountId(accountId);
                        item.setSyncRegion(region);
                    });
                    /*int times = result.size()%50;
                    for(int i = 0;i<times;i++){
                        List<CloudEvent>
                    }*/
                    int num = 0;
                    if(result.size()>0){
                        num = extCloudEventMapper.batchCloudEvents(result);
                    }
                    cloudEventSyncLog.setDataCount(num);
                    cloudEventSyncLog.setStatus(1);
                    cloudEventSyncLogMapper.updateByPrimaryKeySelective(cloudEventSyncLog);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            CloudEventSyncLog cloudEventSyncLog = new CloudEventSyncLog();
            cloudEventSyncLog.setId(logId);
            cloudEventSyncLog.setStatus(2);
            cloudEventSyncLog.setException("日志同步异常");
            cloudEventSyncLogMapper.updateByPrimaryKeySelective(cloudEventSyncLog);
        }
    }

    public List<CloudEvent> getCloudEvents(String accountId, String region, String startTime, String endTime,
                                           int pageNum, int maxResult) throws Exception {
        AccountWithBLOBs account = accountService.getAccount(accountId);
        Map<String, String> accountMap = PlatformUtils.getAccount(account, region, proxyMapper.selectByPrimaryKey(account.getProxyId()));
        return getCloudEvents(account, accountMap, startTime, endTime, pageNum, maxResult);
    }

    public List<CloudEvent> getCloudEvents(AccountWithBLOBs account, Map<String, String> accountMap, String startTime, String endTime,
                                           int pageNum, int maxResult) throws Exception {
        List<CloudEvent> result;
        switch (account.getPluginId()) {
            case PlatformUtils.aliyun:
                result = getAliyunCloudEvents(accountMap, startTime, endTime, pageNum, maxResult);
                break;
            case PlatformUtils.huawei:
                result = getHuaweiCloudEvents(accountMap, startTime, endTime, pageNum, maxResult);
            default:
                throw new IllegalStateException("Unexpected value: " + account.getPluginId());
        }
        return result;
    }

    private List<CloudEvent> getHuaweiCloudEvents(Map<String, String> accountMap, String startTime
            , String endTime, int pageNum, int maxResult) {
        return new ArrayList<>();
    }

    private List<CloudEvent> getTencentEvents(Map<String, String> accountMap, String startTime
            , String endTime, int pageNum, int maxResult) {
        return new ArrayList<>();
    }

    public List<CloudEvent> getAliyunCloudEvents(Map<String, String> accountMap, String startTime, String endTime,
                                                 int pageNum, int maxResult) throws Exception {
        startTime = DateUtils.localDateStrToUtcDateStr("yyyy-MM-dd HH:mm:ss", startTime).replace(" ", "T") + "Z";
        endTime = DateUtils.localDateStrToUtcDateStr("yyyy-MM-dd HH:mm:ss", endTime).replace(" ", "T") + "Z";
        Config config = new Config().setAccessKeyId(accountMap.get("accessKey")).setAccessKeySecret(accountMap.get("secretKey"));
        config.endpoint = "actiontrail."+accountMap.get("region")+".aliyuncs.com";
        Client client = new Client(config);
        LookupEventsRequest lookupEventsRequest = new LookupEventsRequest();
        lookupEventsRequest.setStartTime(startTime);
        lookupEventsRequest.setEndTime(endTime);
        lookupEventsRequest.setNextToken((pageNum - 1) * maxResult + "");
        lookupEventsRequest.setMaxResults(maxResult + "");
        lookupEventsRequest.setEventRW("Write");
        LookupEventsResponse lookupEventsResponse = client.lookupEvents(lookupEventsRequest);
        List<Map<String, ?>> events = lookupEventsResponse.getBody().events;
        return events.stream().map(item -> {
            Map<String,?> userIdentity = (Map)item.get("userIdentity");

            return CloudEvent.builder().eventId((String) item.get("eventId"))
                    .eventName((String) item.get("eventName")).eventRw((String) item.get("eventRW"))
                    .eventTime(DateUtils.utcTimeStrToLocalDate("yyyy-MM-dd'T'HH:mm:ss'Z'", (String) item.get("eventTime")))
                    .eventMessage((String) item.get("eventMessage")).eventSource((String) item.get("eventSource"))
                    .acsRegion((String) item.get("acsRegion")).userAgent((String) item.get("userAgent"))
                    .sourceIpAddress((String) item.get("sourceIpAddress")).serviceName((String) item.get("serviceName"))
                    .resourceName((String) item.get("resourceName")).resourceType((String) item.get("resourceType"))
                    .userName(userIdentity!=null?(String)userIdentity.get("userName"):"").build();

        }).collect(Collectors.toList());
    }

    /*public List<CloudEvent> getAliyunCloudEvents(Map<String, String> accountMap, String startTime, String endTime,
                                                 int pageNum, int maxResult) throws Exception {
        startTime = DateUtils.localDateStrToUtcDateStr("yyyy-MM-dd HH:mm:ss", startTime).replace(" ", "T") + "Z";
        endTime = DateUtils.localDateStrToUtcDateStr("yyyy-MM-dd HH:mm:ss", endTime).replace(" ", "T") + "Z";
        DefaultProfile profile = DefaultProfile.getProfile(accountMap.get("region"), accountMap.get("accessKey"), accountMap.get("secretKey"));
        IAcsClient client = new DefaultAcsClient(profile);
        LookupEventsRequest lookupEventsRequest = new LookupEventsRequest();
        lookupEventsRequest.setStartTime(startTime);
        lookupEventsRequest.setEndTime(endTime);
        lookupEventsRequest.setNextToken((pageNum - 1) * maxResult + "");
        lookupEventsRequest.setMaxResults(maxResult + "");
        //lookupEventsRequest.setEventRW("All");
        LookupEventsResponse lookupEventsResponse = client.getAcsResponse(lookupEventsRequest);
        List<Map<Object, Object>> events = lookupEventsResponse.getEvents();
        return events.stream().map(item -> {
            return CloudEvent.builder().eventId((String) item.get("eventId"))
                    .eventName((String) item.get("eventName")).eventRw((String) item.get("eventRw"))
                    .eventTime(DateUtils.utcTimeStrToLocalDate("yyyy-MM-dd'T'HH:mm:ss'Z'", (String) item.get("eventTime")))
                    .eventMessage((String) item.get("eventMessage")).eventSource((String) item.get("eventSource"))
                    .acsRegion((String) item.get("acsRegion")).userAgent((String) item.get("userAgent"))
                    .sourceIpAddress((String) item.get("sourceIpAddress")).serviceName((String) item.get("serviceName")).build();

        }).collect(Collectors.toList());
    }*/

}