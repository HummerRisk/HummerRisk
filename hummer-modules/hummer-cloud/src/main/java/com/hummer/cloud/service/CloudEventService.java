package com.hummer.cloud.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.actiontrail20200706.Client;
import com.aliyun.actiontrail20200706.models.LookupEventsRequest;
import com.aliyun.actiontrail20200706.models.LookupEventsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudtrail.AWSCloudTrail;
import com.amazonaws.services.cloudtrail.AWSCloudTrailClient;
import com.amazonaws.services.cloudtrail.model.LookupAttributeKey;
import com.amazonaws.services.cloudtrail.model.LookupEventsResult;
import com.baidubce.http.HttpMethodName;
import com.baidubce.internal.InternalRequest;
import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.cts.v3.CtsClient;
import com.huaweicloud.sdk.cts.v3.model.ListTracesRequest;
import com.huaweicloud.sdk.cts.v3.model.ListTracesResponse;
import com.huaweicloud.sdk.cts.v3.model.Traces;
import com.huaweicloud.sdk.cts.v3.model.UserInfo;
import com.huaweicloud.sdk.cts.v3.region.CtsRegion;
import com.hummer.cloud.mapper.CloudEventMapper;
import com.hummer.cloud.mapper.CloudEventRegionLogMapper;
import com.hummer.cloud.mapper.CloudEventSyncLogMapper;
import com.hummer.cloud.mapper.ProxyMapper;
import com.hummer.cloud.mapper.ext.ExtCloudEventMapper;
import com.hummer.cloud.mapper.ext.ExtCloudEventSyncLogMapper;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.cloudEvent.CloudEventRequest;
import com.hummer.common.core.domain.request.event.CloudEventSyncLogVo;
import com.hummer.common.core.domain.request.event.CloudEventWithBLOBsVo;
import com.hummer.common.core.dto.ChartDTO;
import com.hummer.common.core.dto.CloudEventGroupDTO;
import com.hummer.common.core.dto.CloudEventSourceIpInsightDTO;
import com.hummer.common.core.proxy.baidu.BaiduCredential;
import com.hummer.common.core.proxy.baidu.BaiduRequest;
import com.hummer.common.core.proxy.baidu.QueryEventResponse;
import com.hummer.common.core.utils.DateUtils;
import com.hummer.common.core.utils.LogUtil;
import com.hummer.common.core.utils.PlatformUtils;
import com.hummer.common.core.utils.UUIDUtil;
import com.tencentcloudapi.cloudaudit.v20190319.CloudauditClient;
import com.tencentcloudapi.cloudaudit.v20190319.models.DescribeEventsRequest;
import com.tencentcloudapi.cloudaudit.v20190319.models.DescribeEventsResponse;
import com.tencentcloudapi.cloudaudit.v20190319.models.Event;
import com.tencentcloudapi.cloudaudit.v20190319.models.LookupAttribute;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.volcengine.service.cloudtrail.ICloudTrailService;
import com.volcengine.service.cloudtrail.impl.CloudTrailServiceImpl;
import common.utils.HttpClientUtils;
import common.utils.SignUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional(rollbackFor = Exception.class)
public class CloudEventService {

    @Autowired
    private AccountService accountService;
    @Autowired
    private ProxyMapper proxyMapper;
    @Autowired
    private CloudEventMapper cloudEventMapper;
    @Autowired
    private ExtCloudEventMapper extCloudEventMapper;
    @Autowired
    private CloudEventSyncLogMapper cloudEventSyncLogMapper;
    @Autowired
    private ExtCloudEventSyncLogMapper extCloudEventSyncLogMapper;
    @Autowired
    private CloudEventRegionLogMapper cloudEventRegionLogMapper;
    @Autowired
    @Lazy
    private CommonThreadPool commonThreadPool;
    private static final int MAX_PAGE_NUM = 1000;
    private static final int MAX_INSERT_SIZE = 30;

    private static final String[] LOW_RISK_ARR = {"GET","查","获取","LOGIN","LOGOUT","登陆","登出"};
    @Autowired
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

    public List<CloudEventSyncLogVo> getCloudEventSyncLog(CloudEventRequest cloudEventRequest) {
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

    public List<CloudEventWithBLOBsVo> getCloudEvents(CloudEventRequest cloudEventRequest) {
        return extCloudEventMapper.getCloudEventList(cloudEventRequest);
    }

    public List<CloudEventGroupDTO> getCloudEventGroup(CloudEventRequest cloudEventRequest){
        return extCloudEventMapper.selectEventGroup(cloudEventRequest);
    }

    public List<CloudEventSourceIpInsightDTO> getSourceIpInsight(CloudEventRequest cloudEventRequest){
        return extCloudEventMapper.selectSourceIpInsight(cloudEventRequest);
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
        cloudEventSyncLog.setRegionName(getRegionName(regions, jsonArray.toJavaList(JSONObject.class)));
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
        CloudEventRegionLogExample cloudEventRegionLogExample = new CloudEventRegionLogExample();
        cloudEventRegionLogExample.createCriteria().andLogIdEqualTo(cloudEventSyncLog.getId());
        List<CloudEventRegionLog> cloudEventRegionLogs = cloudEventRegionLogMapper.selectByExample(cloudEventRegionLogExample);
        commonThreadPool.addTask(() -> {
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.error("syncEventLog{}:" + e.getMessage());
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
            accountMap.put("regionName", PlatformUtils.tranforRegionId2RegionName(region, account.getPluginId()));
            List<CloudEventWithBLOBs> result1 = new ArrayList<>();
            int pageNum = 1;
            int maxNum = 20;
            boolean haveNextPage = true;
            while (haveNextPage) {
                List<CloudEventWithBLOBs> pageResult = getCloudEvents(account, accountMap, startTime, endTime, pageNum, maxNum);
                result1.addAll(pageResult);
                if (pageResult.size() < maxNum || pageNum > MAX_PAGE_NUM) {
                    haveNextPage = false;
                }
                if("true".equals(accountMap.get("isEnd"))){
                    haveNextPage = false;
                }
                pageNum++;
            }
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            Map<String,CloudEventWithBLOBs> cloudEventMap = new HashMap<>();
            result1.forEach(item -> {
                item.setCloudAccountId(account.getId());
                item.setSyncRegion(region);
                item.setId(UUIDUtil.newUUID());
                if(StringUtils.isBlank(item.getEventId())){
                    item.setEventId(UUIDUtil.newUUID());
                }
                cloudEventMap.put(item.getEventId(),item);
            });

            List<CloudEventWithBLOBs> resultTemp  = new ArrayList<>(cloudEventMap.values());
            List<CloudEventWithBLOBs> result = resultTemp.stream().filter(item->{
                return item.getSyncRegion().equals(item.getAcsRegion());
            }).collect(Collectors.toList());
            template.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus arg0) {

                    if (result1.size() > 0) {
                        CloudEventExample cloudEventExample = new CloudEventExample();
                        cloudEventExample.createCriteria().andCloudAccountIdEqualTo(account.getId()).andSyncRegionEqualTo(region).andEventTimeBetween(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime).getTime()
                                , DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endTime).getTime());
                        cloudEventMapper.deleteByExample(cloudEventExample);
                    }

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
                    cloudEventSyncLog.setRegionName(accountMap.get("regionName"));
                    cloudEventSyncLog.setDataCount(num);
                    cloudEventSyncLog.setStatus(1);
                    cloudEventSyncLog.setEndTime(DateUtils.getNowDate());
                    cloudEventRegionLogMapper.updateByPrimaryKeySelective(cloudEventSyncLog);
                }
            });
            return result.size();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error("saveCloudEvent{}:" + e.getMessage());
            cloudEventSyncLog.setStatus(2);
            String exceptionStr = e.getMessage();
            if (exceptionStr != null && exceptionStr.length() > 512) {
                exceptionStr = exceptionStr.substring(0, 511);
            }
            cloudEventSyncLog.setException(exceptionStr);
            cloudEventRegionLogMapper.updateByPrimaryKeySelective(cloudEventSyncLog);
        }
        return -1;
    }

    public List<CloudEventWithBLOBs> getCloudEvents(AccountWithBLOBs account, Map<String, String> accountMap, String startTime, String endTime,
                                                    int pageNum, int maxResult) throws Exception {
        List<CloudEventWithBLOBs> result;
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
            case PlatformUtils.aws:
                result = getAwsEvents(accountMap,startTime,endTime,pageNum,maxResult);
                break;
            case PlatformUtils.baidu:
                result = getBaiduEvents(accountMap,startTime,endTime,pageNum,maxResult);
                break;
            case PlatformUtils.huoshan:
                result = getVolcEvents(accountMap,startTime,endTime,pageNum,maxResult);
                break;
            case PlatformUtils.ksyun:
                result = getKsyunEvents(accountMap,startTime,endTime,pageNum,maxResult);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + account.getPluginId());
        }
        return result;
    }


    private List<CloudEventWithBLOBs> getKsyunEvents(Map<String, String> accountMap, String startTime, String endTime, int pageNum, int maxResult) throws Exception {
        String nextToken = accountMap.get("nextToken");
        JSONObject requestParams = new JSONObject();
        requestParams.put("Accesskey",accountMap.get("AccessKey"));
        requestParams.put("Service","actiontrail");
        requestParams.put("Action","ListOperateLogs");
        requestParams.put("Version","2019-04-01");
        requestParams.put("Timestamp",DateUtils.localDateToUtcDateStr("yyyy-MM-dd'T'HH:mm:ss'Z'",new Date()));
        requestParams.put("SignatureVersion","1.0");
        requestParams.put("SignatureMethod","HMAC-SHA256");
        requestParams.put("Region",accountMap.get("region"));
        requestParams.put("EventBeginDate",startTime.substring(0,10));
        requestParams.put("EventEndDate",DateUtils.addDateStr("yyyy-MM-dd",endTime.substring(0,10),1));
        requestParams.put("PageSize",maxResult);
        if(nextToken!=null){
            requestParams.put("SearchAfter",nextToken);
        }
        String signature = SignUtils.signature(requestParams, accountMap.get("SecretAccessKey"));
        requestParams.put("Signature",signature);
        String responseStr = HttpClientUtils.httpGet("actiontrail.api.ksyun.com", requestParams, null);
        JSONObject responseObject = JSONObject.parseObject(responseStr);
        String searchAfter = responseObject.getString("SearchAfter");
        if(searchAfter.equals(nextToken)){
            accountMap.put("isEnd","true");
        }else{
            accountMap.put("nextToken",searchAfter);
        }
        JSONArray events = responseObject.getJSONArray("Events");
        if(events.size() > 0){
            return events.toJavaList(JSONObject.class).stream().map(item->{
                String eventName = item.getString("EventName");
                eventName = eventName == null?"":eventName.toUpperCase();
                CloudEventWithBLOBs cloudEventWithBLOBs = new CloudEventWithBLOBs();
                if(eventName.contains("DELETE")||eventName.contains("TERMINATE")){
                    cloudEventWithBLOBs.setEventRating(2);
                }
                if(eventName.contains("MODIFY")){
                    cloudEventWithBLOBs.setEventRating(1);
                }
                String region = item.getString("Region");
                String regionName = item.getString("RegionCn");
                if(StringUtils.isBlank(region)){
                    region = "cn-beijing-6";
                    regionName = "华北1（北京）";
                }
                cloudEventWithBLOBs.setEventId(item.getString("EventId"));
                cloudEventWithBLOBs.setEventType(item.getString("EventType"));
                cloudEventWithBLOBs.setRequestId(item.getString("RequestId"));
                cloudEventWithBLOBs.setUserAgent(item.getString("UserAgent"));
                cloudEventWithBLOBs.setSourceIpAddress(item.getString("SourceIpAddress"));
                cloudEventWithBLOBs.setSyncRegion(accountMap.get("region"));
                cloudEventWithBLOBs.setAcsRegion(region);
                cloudEventWithBLOBs.setRegionName(regionName);
                String requestParameters =  item.getString("RequestParameters");
                cloudEventWithBLOBs.setRequestParameters(requestParameters!=null && requestParameters.length()>500?requestParameters.substring(0,500):requestParameters);
                cloudEventWithBLOBs.setEventSource(item.getString("EventSource"));
                cloudEventWithBLOBs.setEventRw(item.getString("EventRw"));
                cloudEventWithBLOBs.setServiceName(item.getString("ServiceName"));
                cloudEventWithBLOBs.setEventTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss",item.getString("EventTime")).getTime());
                cloudEventWithBLOBs.setEventName(item.getString("EventName"));
                cloudEventWithBLOBs.setUserIdentity(item.getString("UserIdentity"));
                cloudEventWithBLOBs.setCloudAuditEvent(item.toJSONString());
                JSONObject userIdentity = item.getJSONObject("UserIdentity");
                if(userIdentity != null){
                    cloudEventWithBLOBs.setUserName(userIdentity.getString("UserName"));
                }
                return cloudEventWithBLOBs;
            }).collect(Collectors.toList());
        }else{
            return new ArrayList<CloudEventWithBLOBs>();
        }
    }

    private List<CloudEventWithBLOBs> getVolcEvents(Map<String, String> accountMap, String startTime, String endTime, int pageNum, int maxResult) throws Exception {
        ICloudTrailService cloudTrailService = CloudTrailServiceImpl.getInstance();
        cloudTrailService.setAccessKey(accountMap.get("AccessKeyId"));
        cloudTrailService.setSecretKey(accountMap.get("SecretAccessKey"));
        cloudTrailService.setRegion(accountMap.get("region"));
        com.volcengine.model.request.cloudtrail.LookupEventsRequest request = new com.volcengine.model.request.cloudtrail.LookupEventsRequest();
        request.setStartTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime).getTime());
        request.setEndTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endTime).getTime());
        request.setMaxResults(maxResult);
        //LookupAttribute lookupAttribute = new LookupAttribute();
        //request.setLookupConditions();
        if(accountMap.get("nextToken")!=null){
            request.setNextToken(accountMap.get("nextToken"));
        }
        com.volcengine.model.response.cloudtrail.LookupEventsResponse lookupEventsResponse = cloudTrailService.lookupEvents(request);
        List<com.volcengine.model.response.cloudtrail.LookupEventsResponse.TrailEvent> trails = lookupEventsResponse.getResult().getTrails();
        String nextToken = lookupEventsResponse.getResult().getNextToken();
        if(nextToken!=null){
            accountMap.put("nextToken",nextToken);
        }else{
            accountMap.put("isEnd","true");
        }
        return trails.stream().map(item->{
            CloudEventWithBLOBs cloudEventWithBLOBs = new CloudEventWithBLOBs();
            JSONObject detail = JSONObject.parseObject(item.getEventDetail());
            cloudEventWithBLOBs.setEventId(item.getEventID());
            cloudEventWithBLOBs.setEventName(item.getEventName());
            cloudEventWithBLOBs.setEventTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss",item.getEventTime().substring(0,19).replace("T"," ")).getTime());
            cloudEventWithBLOBs.setRequestId(item.getRequestID());
            cloudEventWithBLOBs.setSourceIpAddress(item.getSourceIPAddress());
            cloudEventWithBLOBs.setEventSource(item.getEventSource());
            cloudEventWithBLOBs.setUserName(item.getUserName());
            cloudEventWithBLOBs.setAcsRegion(item.getRegion());
            cloudEventWithBLOBs.setSyncRegion(accountMap.get("region"));
            cloudEventWithBLOBs.setRegionName(accountMap.get("regionName"));
            cloudEventWithBLOBs.setUserIdentity(detail.getString("UserIdentity"));
            cloudEventWithBLOBs.setReferencedResources(detail.getString("Resources"));
            cloudEventWithBLOBs.setEventType(detail.getString("EventType"));
            cloudEventWithBLOBs.setRequestParameters(detail.getString("RequestParameters"));
            cloudEventWithBLOBs.setResponseElements(detail.getString("ResponseElements"));
            cloudEventWithBLOBs.setCloudAuditEvent(item.getEventDetail());
            return cloudEventWithBLOBs;
        }).collect(Collectors.toList());
    }

    private List<CloudEventWithBLOBs> getBaiduEvents(Map<String, String> accountMap, String startTime, String endTime, int pageNum, int maxResult) throws Exception {
        startTime = DateUtils.localDateStrToUtcDateStr("yyyy-MM-dd HH:mm:ss", startTime).replace(" ", "T") + "Z";
        endTime = DateUtils.localDateStrToUtcDateStr("yyyy-MM-dd HH:mm:ss", endTime).replace(" ", "T") + "Z";
        BaiduCredential baiduCredential = new BaiduCredential();
        baiduCredential.setAccessKeyId(accountMap.get("AccessKeyId"));
        baiduCredential.setSecretAccessKey(accountMap.get("SecretAccessKey"));
        BaiduRequest req = new BaiduRequest();
        req.setBaiduCredential(baiduCredential);
        Map<String,String> params = new HashMap<>();
        Map<String,String> headers = new HashMap<>();
        BaiduRequest.QueryEventRequest queryEventRequest = req.createQueryEventRequest();
        queryEventRequest.setStartTime(startTime);
        queryEventRequest.setEndTime(endTime);
        queryEventRequest.setPageNo(pageNum);
        queryEventRequest.setPageSize(maxResult);
        InternalRequest internalRequest = req.createRequest(HttpMethodName.POST,"/v1/events/query"
                ,params,headers,queryEventRequest,"http://iam.bj.baidubce.com","Iam");
        QueryEventResponse response = req.execute(internalRequest, QueryEventResponse.class, "Iam");
        return response.getData().stream().map(item -> {
            CloudEventWithBLOBs cloudEvent = new CloudEventWithBLOBs();
            cloudEvent.setEventId(UUIDUtil.newUUID());
            cloudEvent.setEventType(item.getString("eventType"));
            cloudEvent.setEventSource(item.getString("eventSource"));
            cloudEvent.setEventName(item.getString("eventName"));
            cloudEvent.setEventTime(item.getLong("eventTimeInMilliseconds"));
            cloudEvent.setSourceIpAddress(item.getString("userIpAddress"));
            cloudEvent.setUserAgent(item.getString("userAgent"));
            String region = item.getString("regionId");
            if(StringUtils.isBlank(region)){
                region = "bj";
            }
            cloudEvent.setSyncRegion(region);
            cloudEvent.setAcsRegion(region);
            cloudEvent.setRegionName(accountMap.get("regionName"));
            cloudEvent.setRequestId(item.getString("requestId"));
            cloudEvent.setEventMessage(item.getString("errorMessage"));
            cloudEvent.setApiVersion(item.getString("apiVersion"));
            cloudEvent.setUserIdentity(item.getString("userIdentity"));
            JSONObject userIdentity = item.getJSONObject("userIdentity");
            if(userIdentity != null){
                cloudEvent.setUserName(userIdentity.getString("userDisplayName"));
            }
            cloudEvent.setReferencedResources(item.getString("resources"));
            JSONArray resources = item.getJSONArray("resources");
            if(resources != null && resources.size() > 0){
                cloudEvent.setResourceType(resources.getJSONObject(0).getString("resourceType"));
                cloudEvent.setResourceName(resources.getJSONObject(0).getString("resourceName"));
            }
            cloudEvent.setEventRating(0);
            cloudEvent.setCloudAuditEvent(item.toJSONString());
            return cloudEvent;
        }).collect(Collectors.toList());
    }

    private List<CloudEventWithBLOBs> getHuaweiCloudEvents(Map<String, String> accountMap, String startTime
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
            CloudEventWithBLOBs cloudEvent = new CloudEventWithBLOBs();
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
            return cloudEvent;
        }).collect(Collectors.toList());
    }

    private List<CloudEventWithBLOBs> getTencentEvents(Map<String, String> accountMap, String startTime
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
        LookupAttribute attributeActionType = new LookupAttribute();
        attributeActionType.setAttributeKey("ActionType");
        attributeActionType.setAttributeValue("Write");
        req.setLookupAttributes(new LookupAttribute[]{attributeActionType});
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
            if (checkLowRisk(eventName)) {
                eventLevel = 0;
            } else if (eventName != null && (eventName.indexOf("删") > -1 || eventName.toUpperCase().indexOf("DELETE") > -1)) {
                eventLevel = 2;
            } else {
                eventLevel = 1;
            }

            CloudEventWithBLOBs cloudEvent = new CloudEventWithBLOBs();
            cloudEvent.setEventRating(eventLevel);
            cloudEvent.setEventId(event.getEventId());
            cloudEvent.setUserName(event.getUsername());
            cloudEvent.setEventTime(Long.parseLong(event.getEventTime()) * 1000);
            cloudEvent.setResourceType(event.getResourceTypeCn());
            cloudEvent.setResourceName(resource.getResourceName());
            cloudEvent.setResourceType(resource.getResourceType());
            cloudEvent.setEventName(event.getEventNameCn());
            cloudEvent.setEventSource(event.getEventSource());
            cloudEvent.setRequestId(event.getRequestID());
            cloudEvent.setAcsRegion(event.getResourceRegion());
            cloudEvent.setSourceIpAddress(event.getSourceIPAddress());
            cloudEvent.setReferencedResources(resource == null ? "{}" : JSON.toJSONString(resource));
            cloudEvent.setRegionName(accountMap.get("regionName"));
            return cloudEvent;
        }).collect(Collectors.toList());

    }

    public List<CloudEventWithBLOBs> getAwsEvents(Map<String, String> accountMap, String startTime, String endTime,
                                                  int pageNum, int maxResult) {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accountMap.get("accessKey"), accountMap.get("secretKey"));
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AWSCloudTrail awsCloudTrail = AWSCloudTrailClient.builder().withRegion(accountMap.get("region")).withCredentials(awsCredentialsProvider).build();
        com.amazonaws.services.cloudtrail.model.LookupEventsRequest eventsRequest = new com.amazonaws.services.cloudtrail.model.LookupEventsRequest();
        eventsRequest.setMaxResults(maxResult);
        eventsRequest.setStartTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startTime));
        eventsRequest.setEndTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endTime));
        com.amazonaws.services.cloudtrail.model.LookupAttribute lookupAttribute = new com.amazonaws.services.cloudtrail.model.LookupAttribute();
        lookupAttribute.setAttributeKey(LookupAttributeKey.ReadOnly);
        lookupAttribute.setAttributeValue("false");
        List<com.amazonaws.services.cloudtrail.model.LookupAttribute> list = new ArrayList<>();
        list.add(lookupAttribute);
        eventsRequest.setLookupAttributes(list);
        if(accountMap.get("nextToken")!=null){
            eventsRequest.setNextToken(accountMap.get("nextToken"));
        }
        LookupEventsResult lookupEventsResult = awsCloudTrail.lookupEvents(eventsRequest);
        if(lookupEventsResult.getNextToken()!=null){
            accountMap.put("nextToken",lookupEventsResult.getNextToken());
        }else{
            accountMap.put("isEnd","true");
        }
        List<com.amazonaws.services.cloudtrail.model.Event> events = lookupEventsResult.getEvents();
        return events.stream().map(item->{
            CloudEventWithBLOBs cloudEvent = new CloudEventWithBLOBs();
            cloudEvent.setEventId(item.getEventId());
            cloudEvent.setEventName(item.getEventName());
            //cloudEvent.setUserIdentity(item.getAccessKeyId());
            cloudEvent.setEventTime(item.getEventTime().getTime());
            cloudEvent.setEventSource(item.getEventSource());
            cloudEvent.setUserName(item.getUsername());
            cloudEvent.setReferencedResources(JSON.toJSONString(item.getResources()));
            String cloudTrailEvent = item.getCloudTrailEvent();
            JSONObject jsonObject =  JSON.parseObject(cloudTrailEvent);
            cloudEvent.setUserIdentity(jsonObject.get("userIdentity")==null?null:jsonObject.get("userIdentity").toString());
            cloudEvent.setAcsRegion(jsonObject.getString("awsRegion"));
            cloudEvent.setSourceIpAddress(jsonObject.getString("sourceIPAddress"));
            cloudEvent.setUserAgent(jsonObject.getString("userAgent"));
            cloudEvent.setRequestParameters(jsonObject.getString("requestParameters"));
            cloudEvent.setResponseElements(jsonObject.getString("responseElements"));
            cloudEvent.setAdditionalEventData(jsonObject.getString("additionalEventData"));
            cloudEvent.setEventType(jsonObject.getString("eventType"));
            cloudEvent.setEventCategory(jsonObject.getString("eventCategory"));
            cloudEvent.setEventVersion(jsonObject.getString("eventVersion"));
            cloudEvent.setCloudAuditEvent(jsonObject.toJSONString());
            cloudEvent.setRegionName(accountMap.get("regionName"));
            return cloudEvent;
        }).collect(Collectors.toList());

    }

    public List<CloudEventWithBLOBs> getAliyunCloudEvents(Map<String, String> accountMap, String startTime, String endTime,
                                                          int pageNum, int maxResult) throws Exception {
        startTime = DateUtils.localDateStrToUtcDateStr("yyyy-MM-dd HH:mm:ss", startTime).replace(" ", "T") + "Z";
        endTime = DateUtils.localDateStrToUtcDateStr("yyyy-MM-dd HH:mm:ss", endTime).replace(" ", "T") + "Z";
        Config config = new Config().setAccessKeyId(accountMap.get("accessKey")).setAccessKeySecret(accountMap.get("secretKey"));
        config.endpoint = "actiontrail." + accountMap.get("region") + ".aliyuncs.com";
        Client client = new Client(config);
        LookupEventsRequest lookupEventsRequest = new LookupEventsRequest();
        lookupEventsRequest.setStartTime(startTime);
        lookupEventsRequest.setEndTime(endTime);
        if(accountMap.get("nextToken")!=null){
            lookupEventsRequest.setNextToken(accountMap.get("nextToken"));
        }
        lookupEventsRequest.setMaxResults(String.valueOf(maxResult));
        //lookupEventsRequest.setEventRW("Write");
        LookupEventsResponse lookupEventsResponse = client.lookupEvents(lookupEventsRequest);
        if(lookupEventsResponse.getBody().getNextToken()!=null){
            accountMap.put("nextToken",lookupEventsResponse.getBody().getNextToken());
        }else{
            accountMap.put("isEnd","true");
        }
        List<Map<String, ?>> events = lookupEventsResponse.getBody().events;
        return events.stream().map(item -> {
            Map<String, ?> userIdentity = (Map) item.get("userIdentity");
            String eventName = (String) item.get("eventName");
            int eventLevel = -1;
            if (eventName != null && eventName.indexOf("ConsoleSign") > -1) {
                eventLevel = 0;
            } else if (eventName != null && (eventName.toUpperCase().indexOf("DELETE") > -1)) {
                eventLevel = 2;
            } else {
                eventLevel = 1;
            }
            CloudEventWithBLOBs cloudEvent = new CloudEventWithBLOBs();
            cloudEvent.setEventRating(eventLevel);
            cloudEvent.setEventId((String) item.get("eventId"));
            cloudEvent.setEventName((String) item.get("eventName"));
            cloudEvent.setEventRw((String) item.get("eventRW"));
            cloudEvent.setEventType((String) item.get("eventType"));
            cloudEvent.setEventCategory((String) item.get("eventCategory"));
            cloudEvent.setEventVersion("eventVersion");
            cloudEvent.setUserIdentity(item.get("userIdentity") == null ? "" : JSON.toJSONString(item.get("userIdentity")));
            cloudEvent.setAdditionalEventData(item.get("additionalEventData") == null ? "" : JSON.toJSONString(item.get("additionalEventData")));
            cloudEvent.setRequestId((String) item.get("requestId"));
            cloudEvent.setRequestParameters(item.get("requestParameters") == null ? "" : JSON.toJSONString(item.get("requestParameters")));
            cloudEvent.setReferencedResources(item.get("referencedResources") == null ? "" : JSON.toJSONString(item.get("referencedResources")));
            cloudEvent.setApiVersion((String) item.get("apiVersion"));
            cloudEvent.setResponseElements(item.get("responseElements") == null ? "" : JSON.toJSONString(item.get("responseElements")));
            cloudEvent.setEventTime(DateUtils.utcTimeStrToLocalDate("yyyy-MM-dd'T'HH:mm:ss'Z'", (String) item.get("eventTime")).getTime());
            cloudEvent.setEventMessage((String) item.get("eventMessage"));
            cloudEvent.setEventSource((String) item.get("eventSource"));
            cloudEvent.setAcsRegion((String) item.get("acsRegion"));
            cloudEvent.setUserAgent((String) item.get("userAgent"));
            cloudEvent.setSourceIpAddress((String) item.get("sourceIpAddress"));
            cloudEvent.setServiceName((String) item.get("serviceName"));
            cloudEvent.setResourceName((String) item.get("resourceName"));
            cloudEvent.setResourceType((String) item.get("resourceType"));
            cloudEvent.setUserName(userIdentity != null ? (String) userIdentity.get("userName") : "");
            cloudEvent.setRegionName(accountMap.get("regionName"));
            return cloudEvent;
        }).collect(Collectors.toList());
    }

    private String getRegionName(String[] regionArr, List<JSONObject> regionList) {
        String[] regionNameArr = new String[regionArr.length];
        for (int i = 0; i < regionArr.length; i++) {
            for (JSONObject regionObj : regionList) {
                if (regionArr[i].equals(regionObj.getString("regionId"))) {
                    regionNameArr[i] = regionObj.getString("regionName");
                }
            }
        }
        return StringUtils.join(regionNameArr, ",");
    }

    public Map<String, Object> topInfo(Map<String, Object> params) {
        return extCloudEventMapper.topInfo(params);
    }

    public List<Map<String, Object>> cloudChart() {
        return extCloudEventMapper.cloudChart();
    }

    public List<Map<String, Object>> regionChart() {
        return extCloudEventMapper.regionChart();
    }

    public List<Map<String, Object>> severityChart() {
        return extCloudEventMapper.severityChart();
    }

    public boolean checkLowRisk(String content){
        content = content.toUpperCase();
        for (String s : LOW_RISK_ARR) {
            if(content.contains(s.toUpperCase())){
                return true;
            }
        }
        return false;
    }

    public ChartDTO ipAccessChart(String ip,String startDate,String endDate){
        List<Map<String, Object>> ipAccessList = extCloudEventMapper.selectIpAccessTimesGroupByDate(ip, startDate, endDate);
        Map<String,Integer> ipAccessMap = ipAccessList.stream().collect(Collectors.toMap(item->{
            return (String)item.get("accessDate");
        },item->{
            Long times = (Long) item.get("times");
            return times.intValue();
        }, (key1, key2) -> key2));
        List<String> dateList = DateUtils.getBetweenDate(startDate,endDate,"yyyy-MM-dd");
        ChartDTO chartDTO = new ChartDTO();
        chartDTO.setXAxis(dateList);
        List<Integer> yAxis = new ArrayList<>();
        dateList.forEach(item->{
            if(ipAccessMap.get(item) != null) {
                yAxis.add(ipAccessMap.get(item));
            }else {
                yAxis.add(0);
            }
        });
        chartDTO.setYAxis(yAxis);
        return chartDTO;
    }

    public void deleteLogs(List<Integer> ids) throws Exception {
        ids.forEach(id -> {
            try {
                deleteCloudEventSyncLog(id);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }


}
