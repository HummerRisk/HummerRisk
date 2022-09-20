package com.hummerrisk.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.actiontrail20171204.Client;
import com.aliyun.actiontrail20171204.models.LookupEventsRequest;
import com.aliyun.actiontrail20171204.models.LookupEventsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.auditmanager.AWSAuditManager;
import com.amazonaws.services.auditmanager.AWSAuditManagerClient;
import com.amazonaws.services.auditmanager.model.Evidence;
import com.amazonaws.services.auditmanager.model.GetEvidenceRequest;
import com.amazonaws.services.auditmanager.model.GetEvidenceResult;
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
import com.hummerrisk.commons.utils.LogUtil;
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
        AccountWithBLOBs account = accountService.getAccount(accountId);
        JSONArray jsonArray = JSON.parseArray(account.getRegions());
        CloudEventSyncLog cloudEventSyncLog = new CloudEventSyncLog();
        cloudEventSyncLog.setAccountId(accountId);
        cloudEventSyncLog.setRegion(StringUtils.join(regions, ","));
        cloudEventSyncLog.setRegionName(getRegionName(regions,jsonArray.toJavaList(JSONObject.class)));
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
        extCloudEventSyncLogMapper.insertSelective(cloudEventSyncLog);
        for (String region : regions) {
            CloudEventRegionLog cloudEventRegionLog = new CloudEventRegionLog();
            cloudEventRegionLog.setRegion(region);
            cloudEventRegionLog.setLogId(cloudEventSyncLog.getId());
            cloudEventRegionLog.setStatus(0);
            cloudEventRegionLog.setCreateTime(DateUtils.getNowDate());
            cloudEventRegionLogMapper.insertSelective(cloudEventRegionLog);
        }
        commonThreadPool.addTask(() -> {
            try{
                CloudEventRegionLogExample cloudEventRegionLogExample = new CloudEventRegionLogExample();
                cloudEventRegionLogExample.createCriteria().andLogIdEqualTo(cloudEventSyncLog.getId());
                List<CloudEventRegionLog> cloudEventRegionLogs = cloudEventRegionLogMapper.selectByExample(cloudEventRegionLogExample);
                int dataCount = 0;
                int errorCount = 0;
                for (CloudEventRegionLog cloudEventRegionLog : cloudEventRegionLogs) {
                    int num = saveCloudEvent(cloudEventRegionLog.getId(), account, cloudEventRegionLog.getRegion(), startTime, endTime);
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
            }catch (Exception e){
                e.printStackTrace();
                LogUtil.error("syncEventLog"+e.getMessage());
                cloudEventSyncLog.setStatus(2);
                cloudEventSyncLogMapper.updateByPrimaryKeySelective(cloudEventSyncLog);
            }

        });
    }

    public int saveCloudEvent(Integer logId, AccountWithBLOBs account, String region, String startTime, String endTime) {
        CloudEventRegionLog cloudEventSyncLog = new CloudEventRegionLog();
        cloudEventSyncLog.setId(logId);
        cloudEventSyncLog.setStartTime(DateUtils.getNowDate());
        try {

            Map<String, String> accountMap = PlatformUtils.getAccount(account, region, proxyMapper.selectByPrimaryKey(account.getProxyId()));
            JSONArray jsonArray = JSON.parseArray(account.getRegions());
            accountMap.put("regionName",getRegionName(new String[]{region},jsonArray.toJavaList(JSONObject.class)));
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
                        cloudEventExample.createCriteria().andCloudAccountIdEqualTo(account.getId()).andSyncRegionEqualTo(region).andEventTimeBetween(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime).getTime()
                                , DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endTime).getTime());
                        cloudEventMapper.deleteByExample(cloudEventExample);
                    }
                    result.forEach(item -> {
                        item.setCloudAccountId(account.getId());
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
            LogUtil.error("saveCloudEvent{}"+e.getMessage());
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
            CloudEvent cloudEvent = new CloudEvent();
            cloudEvent.setEventId(trace.getTraceId());
            cloudEvent.setEventName(trace.getTraceName());
            cloudEvent.setEventRating(eventLevel);
            cloudEvent.setResourceId(trace.getResourceId());
            cloudEvent.setEventType(trace.getTraceType());
            cloudEvent.setRequestParameters(trace.getRequest());
            cloudEvent.setResponseElements(trace.getResponse());
            cloudEvent.setApiVersion(trace.getApiVersion());
            cloudEvent.setEventMessage(trace.getMessage());
            cloudEvent.setEventTime(trace.getTime());
            cloudEvent.setUserIdentity(user == null ? "{}" : JSON.toJSONString(user));
            cloudEvent.setUserName(user == null ? "" : user.getName());
            cloudEvent.setServiceName(trace.getServiceType());
            cloudEvent.setResourceType(trace.getResourceType());
            cloudEvent.setResourceName(trace.getResourceName());
            cloudEvent.setSourceIpAddress(trace.getSourceIp());
            cloudEvent.setRequestId(trace.getRequestId());
            cloudEvent.setEndpoint(trace.getEndpoint());
            cloudEvent.setResourceUrl(trace.getResourceUrl());
            cloudEvent.setLocationInfo(trace.getLocationInfo());
            cloudEvent.setRegionName(accountMap.get("regionName"));
            return cloudEvent;        }).collect(Collectors.toList());
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
            String eventName = event.getEventNameCn();
            int eventLevel = -1;
            if(eventName!=null && (eventName.indexOf("查")>-1||eventName.indexOf("Get")>-1)){
                eventLevel = 0;
            }else{
                eventLevel = 1;
            }
            CloudEvent cloudEvent = new CloudEvent();
            cloudEvent.setEventRating(eventLevel);
            cloudEvent.setEventId(event.getEventId());cloudEvent.setUserName(event.getUsername())
            ;cloudEvent.setEventTime(Long.parseLong(event.getEventTime()) * 1000);
            cloudEvent.setResourceType(event.getResourceTypeCn());
            cloudEvent.setEventName(event.getEventNameCn());cloudEvent.setEventSource(event.getEventSource())
                    ;cloudEvent.setRequestId(event.getRequestID());cloudEvent.setAcsRegion(event.getResourceRegion());
                    cloudEvent.setSourceIpAddress(event.getSourceIPAddress());
            cloudEvent.setReferencedResources(resource == null ? "{}" : JSON.toJSONString(resource));
            cloudEvent.setRegionName(accountMap.get("regionName"));
            return cloudEvent;
        }).collect(Collectors.toList());

    }

    public List<CloudEvent> getAwsEvents(Map<String, String> accountMap, String startTime, String endTime,
                                                 int pageNum, int maxResult){
        AWSAuditManager awsAuditManager = AWSAuditManagerClient.builder().build();
        GetEvidenceRequest getEvidenceRequest = new GetEvidenceRequest();
        AWSCredentials awsCredentials = new BasicAWSCredentials("","");
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        getEvidenceRequest.setRequestCredentialsProvider(awsCredentialsProvider);
        GetEvidenceResult evidence = awsAuditManager.getEvidence(getEvidenceRequest);
        Evidence evidence1 = evidence.getEvidence();
        return null;
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
            String eventName = (String)item.get("eventName");
            int eventLevel = -1;
            if(eventName!=null && eventName.indexOf("ConsoleSign")>-1){
                eventLevel = 0;
            }else{
                eventLevel = 1;
            }
            CloudEvent cloudEvent = new CloudEvent();
            cloudEvent.setEventRating(eventLevel);
            cloudEvent.setEventId((String) item.get("eventId"))
                    ; cloudEvent.setEventName((String) item.get("eventName")); cloudEvent.setEventRw((String) item.get("eventRW")); cloudEvent.setEventType((String) item.get("eventType"))
            ;cloudEvent.setEventCategory((String) item.get("eventCategory")); cloudEvent.setEventVersion("eventVersion"); cloudEvent.setUserIdentity(item.get("userIdentity") == null ? "" : JSON.toJSONString(item.get("userIdentity")))
            ;cloudEvent.setAdditionalEventData(item.get("additionalEventData") == null ? "" : JSON.toJSONString(item.get("additionalEventData")))
                    ; cloudEvent.setRequestId((String) item.get("requestId")); cloudEvent.setRequestParameters(item.get("requestParameters") == null ? "" : JSON.toJSONString(item.get("requestParameters")))
                    ; cloudEvent.setReferencedResources(item.get("referencedResources") == null ? "" : JSON.toJSONString(item.get("referencedResources")))
                    ; cloudEvent.setApiVersion((String) item.get("apiVersion")); cloudEvent.setResponseElements(item.get("responseElements") == null ? "" : JSON.toJSONString(item.get("responseElements")))
                    ; cloudEvent.setEventTime(DateUtils.utcTimeStrToLocalDate("yyyy-MM-dd'T'HH:mm:ss'Z'", (String) item.get("eventTime")).getTime())
                    ; cloudEvent.setEventMessage((String) item.get("eventMessage")); cloudEvent.setEventSource((String) item.get("eventSource"))
                    ; cloudEvent.setAcsRegion((String) item.get("acsRegion")); cloudEvent.setUserAgent((String) item.get("userAgent"))
                    ; cloudEvent.setSourceIpAddress((String) item.get("sourceIpAddress")); cloudEvent.setServiceName((String) item.get("serviceName"))
                    ; cloudEvent.setResourceName((String) item.get("resourceName")); cloudEvent.setResourceType((String) item.get("resourceType"))
                    ; cloudEvent.setUserName(userIdentity != null ? (String) userIdentity.get("userName") : ""); cloudEvent.setRegionName(accountMap.get("regionName"));
                    return cloudEvent;
        }).collect(Collectors.toList());
    }

    private String getRegionName(String[] regionArr, List<JSONObject> regionList){
        String[] regionNameArr = new String[regionArr.length];
        for(int i = 0;i< regionArr.length;i++){
            for(JSONObject regionObj:regionList){
                if(regionArr[i].equals(regionObj.getString("regionId"))){
                    regionNameArr[i]= regionObj.getString("regionName");
                }
            }
        }
        return StringUtils.join(regionNameArr,",");
    }
}
