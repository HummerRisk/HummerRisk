package com.hummerrisk.service;


import com.alibaba.fastjson.JSON;
import com.aliyun.actiontrail20171204.Client;
import com.aliyun.actiontrail20171204.models.LookupEventsRequest;
import com.aliyun.actiontrail20171204.models.LookupEventsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.CloudEventMapper;
import com.hummerrisk.base.mapper.CloudEventRegionLogMapper;
import com.hummerrisk.base.mapper.CloudEventSyncLogMapper;
import com.hummerrisk.base.mapper.ProxyMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudEventMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudEventSyncLogMapper;
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
    private ExtCloudEventSyncLogMapper extCloudEventSyncLogMapper;

    @Resource
    private CloudEventRegionLogMapper cloudEventRegionLogMapper;
    @Resource
    @Lazy
    private CommonThreadPool commonThreadPool;
    private static final int MAX_PAGE_NUM = 1000;
    private static final int MAX_INSERT_SIZE = 30;
    @Resource
    private PlatformTransactionManager transactionManager;

    public List<CloudEventRegionLog> getCloudEventRegionLog(int logId) {
        CloudEventRegionLogExample cloudEventRegionLogExample = new CloudEventRegionLogExample();
        cloudEventRegionLogExample.createCriteria().andLogIdEqualTo(logId);
        return cloudEventRegionLogMapper.selectByExample(cloudEventRegionLogExample);
    }

    public List<CloudEventSyncLog> getCloudEventSyncLog(String accountId, String region) {
        CloudEventSyncLogExample cloudEventSyncLogExample = new CloudEventSyncLogExample();
        cloudEventSyncLogExample.setOrderByClause(" create_time desc");
        CloudEventSyncLogExample.Criteria criteria = cloudEventSyncLogExample.createCriteria();
        if (StringUtils.isNotBlank(accountId)) {
            criteria.andAccountIdEqualTo(accountId);
        }
        if (StringUtils.isNotBlank(region)) {
            criteria.andRegionEqualTo(region);
        }
        return cloudEventSyncLogMapper.selectByExample(cloudEventSyncLogExample);
    }

    public List<CloudEventSyncLog> getCloudEventSyncLog(CloudEventRequest cloudEventRequest) {
        return extCloudEventSyncLogMapper.getCloudEventSyncLog(cloudEventRequest);
    }

    public CloudEventSyncLog selectCloudEventSyncLog(int id) {

        return cloudEventSyncLogMapper.selectByPrimaryKey(id);
    }

    public void deleteCloudEventSyncLog(int id) {
        CloudEventSyncLogExample cloudEventSyncLogExample = new CloudEventSyncLogExample();
        cloudEventSyncLogExample.createCriteria().andIdEqualTo(id);
        cloudEventSyncLogMapper.deleteByExample(cloudEventSyncLogExample);
    }

    public void deleteCloudEvent(String id) {
        CloudEventExample cloudEventExample = new CloudEventExample();
        cloudEventExample.createCriteria().andEventIdEqualTo(id);
        cloudEventMapper.deleteByExample(cloudEventExample);
    }

    public List<CloudEvent> getCloudEvents(CloudEventRequest cloudEventRequest) {
        return extCloudEventMapper.getCloudEventList(cloudEventRequest);
    }

    public void syncCloudEvents(String accountId, String[] regions, String startTime, String endTime) {
        if (accountId == null || regions.length == 0 || startTime == null || endTime == null) {
            throw new RuntimeException("参数为空");
        }
        CloudEventSyncLog cloudEventSyncLog = new CloudEventSyncLog();
        cloudEventSyncLog.setAccountId(accountId);
        cloudEventSyncLog.setRegion(StringUtils.join(regions, ","));
        cloudEventSyncLog.setCreateTime(DateUtils.getNowDate());
        cloudEventSyncLog.setRequestStartTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime));
        cloudEventSyncLog.setRequestEndTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endTime));
        cloudEventSyncLog.setStatus(0);
        CloudEventSyncLogExample cloudEventSyncLogExample = new CloudEventSyncLogExample();
        cloudEventSyncLogExample.createCriteria().andStatusEqualTo(0).andAccountIdEqualTo(accountId);
        List<CloudEventSyncLog> cloudEventSyncLogs = cloudEventSyncLogMapper.selectByExample(cloudEventSyncLogExample);
        if (cloudEventSyncLogs.size() > 0) {
            throw new RuntimeException("task running!");
        }
        cloudEventSyncLogMapper.insertSelective(cloudEventSyncLog);
        for (String region : regions) {
            CloudEventRegionLog cloudEventRegionLog = new CloudEventRegionLog();
            cloudEventRegionLog.setRegion(region);
            cloudEventRegionLog.setLogId(cloudEventSyncLog.getId());
            cloudEventRegionLog.setStatus(0);
            cloudEventRegionLog.setCreateTime(DateUtils.getNowDate());
            cloudEventRegionLogMapper.insertSelective(cloudEventRegionLog);
        }
        commonThreadPool.addTask(() -> {
            CloudEventRegionLogExample cloudEventRegionLogExample = new CloudEventRegionLogExample();
            cloudEventRegionLogExample.createCriteria().andLogIdEqualTo(cloudEventSyncLog.getId());
            List<CloudEventRegionLog> cloudEventRegionLogs = cloudEventRegionLogMapper.selectByExample(cloudEventRegionLogExample);
            int dataCount = 0;
            int errorCount = 0;
            for (CloudEventRegionLog cloudEventRegionLog : cloudEventRegionLogs) {
                int num = saveCloudEvent(cloudEventRegionLog.getId(), accountId, cloudEventRegionLog.getRegion(), startTime, endTime);
                if (num > -1)
                    dataCount += num;
                else
                    errorCount++;
            }
            if (errorCount == cloudEventRegionLogs.size()) {
                cloudEventSyncLog.setStatus(2);
            } else if (errorCount > 0) {
                cloudEventSyncLog.setStatus(3);
            } else {
                cloudEventSyncLog.setStatus(1);
            }
            cloudEventSyncLog.setDataCount(dataCount);
            cloudEventSyncLogMapper.updateByPrimaryKeySelective(cloudEventSyncLog);
        });
    }

    public int saveCloudEvent(Integer logId, String accountId, String region, String startTime, String endTime) {
        CloudEventRegionLog cloudEventSyncLog = new CloudEventRegionLog();
        cloudEventSyncLog.setId(logId);
        cloudEventSyncLog.setStartTime(DateUtils.getNowDate());
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

                    if (result.size() > 0) {
                        CloudEventExample cloudEventExample = new CloudEventExample();
                        cloudEventExample.createCriteria().andCloudAccountIdEqualTo(accountId).andSyncRegionEqualTo(region).andEventTimeBetween(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime)
                                , DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endTime));
                        cloudEventMapper.deleteByExample(cloudEventExample);
                    }
                    result.forEach(item -> {
                        item.setCloudAccountId(accountId);
                        item.setSyncRegion(region);
                    });
                    int resultSize = result.size();
                    int num = 0;
                    if (resultSize > MAX_INSERT_SIZE) {
                        int times = resultSize / MAX_INSERT_SIZE;
                        for (int i = 0; i < times; i++) {
                            num += extCloudEventMapper.batchCloudEvents(result.subList(MAX_INSERT_SIZE * i, MAX_INSERT_SIZE * (i + 1)));
                        }
                        if (MAX_INSERT_SIZE * times < resultSize) {
                            num += extCloudEventMapper.batchCloudEvents(result.subList(MAX_INSERT_SIZE * times, resultSize));
                        }
                    } else if (resultSize > 0) {
                        num += extCloudEventMapper.batchCloudEvents(result);
                    }
                    cloudEventSyncLog.setDataCount(num);
                    cloudEventSyncLog.setStatus(1);
                    cloudEventSyncLog.setEndTime(DateUtils.getNowDate());
                    cloudEventRegionLogMapper.updateByPrimaryKeySelective(cloudEventSyncLog);
                }
            });
            return result.size();
        } catch (Exception e) {
            e.printStackTrace();
            cloudEventSyncLog.setStatus(2);
            String exceptionStr = e.getMessage();
            if (exceptionStr.length() > 512) {
                exceptionStr = exceptionStr.substring(0, 511);
            }
            cloudEventSyncLog.setException(exceptionStr);
            cloudEventRegionLogMapper.updateByPrimaryKeySelective(cloudEventSyncLog);
        }
        return -1;
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
        config.endpoint = "actiontrail." + accountMap.get("region") + ".aliyuncs.com";
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
            Map<String, ?> userIdentity = (Map) item.get("userIdentity");

            return CloudEvent.builder().eventId((String) item.get("eventId"))
                    .eventName((String) item.get("eventName")).eventRw((String) item.get("eventRW")).eventType((String) item.get("eventType"))
                    .eventCategory((String) item.get("eventCategory")).eventVersion("eventVersion").userIdentity(item.get("userIdentity") == null ? "" : JSON.toJSONString(item.get("userIdentity")))
                    .additionalEventData(item.get("additionalEventData") == null ? "" : JSON.toJSONString(item.get("additionalEventData")))
                    .requestId((String) item.get("requestId")).requestParameters(item.get("requestParameters") == null ? "" : JSON.toJSONString(item.get("requestParameters")))
                    .referencedResources(item.get("referencedResources") == null ? "" : JSON.toJSONString(item.get("referencedResources")))
                    .apiVersion((String) item.get("apiVersion")).responseElements(item.get("responseElements") == null ? "" : JSON.toJSONString(item.get("responseElements")))
                    .eventTime(DateUtils.utcTimeStrToLocalDate("yyyy-MM-dd'T'HH:mm:ss'Z'", (String) item.get("eventTime")))
                    .eventMessage((String) item.get("eventMessage")).eventSource((String) item.get("eventSource"))
                    .acsRegion((String) item.get("acsRegion")).userAgent((String) item.get("userAgent"))
                    .sourceIpAddress((String) item.get("sourceIpAddress")).serviceName((String) item.get("serviceName"))
                    .resourceName((String) item.get("resourceName")).resourceType((String) item.get("resourceType"))
                    .userName(userIdentity != null ? (String) userIdentity.get("userName") : "").build();
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
