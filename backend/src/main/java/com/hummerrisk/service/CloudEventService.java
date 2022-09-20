package com.hummerrisk.service;


import com.alibaba.fastjson.JSON;
import com.aliyun.actiontrail20171204.Client;
import com.aliyun.actiontrail20171204.models.LookupEventsRequest;
import com.aliyun.actiontrail20171204.models.LookupEventsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.cts.v3.CtsClient;
import com.huaweicloud.sdk.cts.v3.model.ListTracesRequest;
import com.huaweicloud.sdk.cts.v3.model.ListTracesResponse;
import com.huaweicloud.sdk.cts.v3.model.Traces;
import com.huaweicloud.sdk.cts.v3.model.UserInfo;
import com.huaweicloud.sdk.cts.v3.region.CtsRegion;
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
import com.tencentcloudapi.cloudaudit.v20190319.CloudauditClient;
import com.tencentcloudapi.cloudaudit.v20190319.models.DescribeEventsRequest;
import com.tencentcloudapi.cloudaudit.v20190319.models.DescribeEventsResponse;
import com.tencentcloudapi.cloudaudit.v20190319.models.Event;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
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
import java.util.Arrays;
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
        cloudEventSyncLog.setCreateTime(DateUtils.getNowDate().getTime());
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
                        cloudEventExample.createCriteria().andCloudAccountIdEqualTo(accountId).andSyncRegionEqualTo(region).andEventTimeBetween(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime).getTime()
                                , DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endTime).getTime());
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

    public List<CloudEvent> getCloudEvents(AccountWithBLOBs account, Map<String, String> accountMap, String startTime, String endTime,
                                           int pageNum, int maxResult) throws Exception {
        List<CloudEvent> result;
        switch (account.getPluginId()) {
            case PlatformUtils.aliyun:
                result = getAliyunCloudEvents(accountMap, startTime, endTime, pageNum, maxResult);
                break;
            case PlatformUtils.huawei:
                result = getHuaweiCloudEvents(accountMap, startTime, endTime, pageNum, maxResult);
                break;
            case PlatformUtils.tencent:
                result = getTencentEvents(accountMap, startTime, endTime, pageNum, maxResult);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + account.getPluginId());
        }
        return result;
    }

    private List<CloudEvent> getHuaweiCloudEvents(Map<String, String> accountMap, String startTime
            , String endTime, int pageNum, int maxResult) {
        String ak = accountMap.get("ak");
        String sk = accountMap.get("sk");
        ICredential auth = new BasicCredentials()
                .withAk(ak)
                .withSk(sk);
        CtsClient client = CtsClient.newBuilder()
                .withCredential(auth)
                .withRegion(CtsRegion.valueOf(accountMap.get("region")))
                .build();
        ListTracesRequest request = new ListTracesRequest();
        request.setFrom(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime).getTime());
        request.setTo(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endTime).getTime());
        request.setLimit(maxResult);
        if (accountMap.get("marker") != null) {
            request.setNext(accountMap.get("marker"));
        }
        request.withTraceType(ListTracesRequest.TraceTypeEnum.fromValue("system"));
        ListTracesResponse response = client.listTraces(request);
        String marker = response.getMetaData().getMarker();
        accountMap.put("marker", marker);
        List<Traces> traces = response.getTraces();
        return traces.stream().map(trace -> {
            int eventLevel = -1;
            if (Traces.TraceRatingEnum.NORMAL == trace.getTraceRating()) {
                eventLevel = 0;
            } else if (Traces.TraceRatingEnum.WARNING == trace.getTraceRating()) {
                eventLevel = 1;
            } else if (Traces.TraceRatingEnum.INCIDENT == trace.getTraceRating()) {
                eventLevel = 2;
            }
            UserInfo user = trace.getUser();
            return CloudEvent.builder().eventId(trace.getTraceId()).eventName(trace.getTraceName()).eventRating(eventLevel)
                    .resourceId(trace.getResourceId()).eventType(trace.getTraceType()).requestParameters(trace.getRequest())
                    .responseElements(trace.getResponse()).apiVersion(trace.getApiVersion()).eventMessage(trace.getMessage())
                    .eventTime(trace.getTime()).userIdentity(user == null ? "{}" : JSON.toJSONString(user)).userName(user == null ? "" : user.getName())
                    .serviceName(trace.getServiceType()).resourceType(trace.getResourceType()).resourceName(trace.getResourceName())
                    .sourceIpAddress(trace.getSourceIp()).requestId(trace.getRequestId()).endpoint(trace.getEndpoint())
                    .resourceUrl(trace.getResourceUrl()).locationInfo(trace.getLocationInfo()).build();
        }).collect(Collectors.toList());
    }

    private List<CloudEvent> getTencentEvents(Map<String, String> accountMap, String startTime
            , String endTime, int pageNum, int maxResult) throws TencentCloudSDKException {
        Credential cred = new Credential(accountMap.get("secretId"), accountMap.get("secretKey"));
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("cloudaudit.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        CloudauditClient client = new CloudauditClient(cred, accountMap.get("region"), clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
        DescribeEventsRequest req = new DescribeEventsRequest();
        req.setStartTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime).getTime() / 1000);
        req.setEndTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endTime).getTime() / 1000);
        req.setMaxResults((long) maxResult);
        if (accountMap.get("marker") != null) {
            req.setNextToken(Long.parseLong(accountMap.get("marker")));
        }
        // 返回的resp是一个DescribeEventsResponse的实例，与请求对象对应
        DescribeEventsResponse resp = client.DescribeEvents(req);
        Long nextToken = resp.getNextToken();
        if (nextToken != null) {
            accountMap.put("marker", nextToken + "");
        }
        Event[] events = resp.getEvents();
        return Arrays.stream(events).map(event -> {
            com.tencentcloudapi.cloudaudit.v20190319.models.Resource resource = event.getResources();
            return CloudEvent.builder().eventId(event.getEventId()).userName(event.getUsername())
                    .eventTime(Long.parseLong(event.getEventTime()) * 1000).resourceType(event.getResourceTypeCn())
                    .eventName(event.getEventNameCn()).cloudAuditEvent(event.getCloudAuditEvent()).eventSource(event.getEventSource())
                    .requestId(event.getRequestID()).acsRegion(event.getResourceRegion()).sourceIpAddress(event.getSourceIPAddress())
                    .referencedResources(resource == null ? "{}" : JSON.toJSONString(resource)).build();
        }).collect(Collectors.toList());

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
                    .eventTime(DateUtils.utcTimeStrToLocalDate("yyyy-MM-dd'T'HH:mm:ss'Z'", (String) item.get("eventTime")).getTime())
                    .eventMessage((String) item.get("eventMessage")).eventSource((String) item.get("eventSource"))
                    .acsRegion((String) item.get("acsRegion")).userAgent((String) item.get("userAgent"))
                    .sourceIpAddress((String) item.get("sourceIpAddress")).serviceName((String) item.get("serviceName"))
                    .resourceName((String) item.get("resourceName")).resourceType((String) item.get("resourceType"))
                    .userName(userIdentity != null ? (String) userIdentity.get("userName") : "").build();
        }).collect(Collectors.toList());
    }
}
