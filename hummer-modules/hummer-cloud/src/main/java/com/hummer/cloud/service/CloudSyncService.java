package com.hummer.cloud.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.cloud.mapper.*;
import com.hummer.cloud.mapper.ext.ExtCloudResourceSyncItemMapper;
import com.hummer.cloud.mapper.ext.ExtCloudResourceSyncMapper;
import com.hummer.common.core.constant.CloudTaskConstants;
import com.hummer.common.core.constant.CommandEnum;
import com.hummer.common.core.constant.ResourceOperation;
import com.hummer.common.core.constant.ResourceTypeConstants;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.cloudResource.CloudResourceSyncRequest;
import com.hummer.common.core.domain.request.sync.CloudTopology;
import com.hummer.common.core.dto.CloudResourceSyncItemDTO;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.*;
import com.hummer.system.api.IOperationLogService;
import com.hummer.system.api.ISystemProviderService;
import com.hummer.system.api.model.LoginUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.parseArray;
import static com.alibaba.fastjson.JSON.parseObject;


@Service
@Transactional(rollbackFor = Exception.class)
public class CloudSyncService {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private CloudResourceSyncMapper cloudResourceSyncMapper;
    @Autowired
    private ExtCloudResourceSyncMapper extCloudResourceSyncMapper;
    @Autowired
    private CloudResourceSyncItemMapper cloudResourceSyncItemMapper;
    @Autowired
    private CloudResourceSyncItemLogMapper cloudResourceSyncItemLogMapper;
    @Autowired
    private CloudResourceMapper cloudResourceMapper;
    @Autowired
    private CloudResourceItemMapper cloudResourceItemMapper;
    @Autowired
    private CommonThreadPool commonThreadPool;
    @Autowired
    private ProxyMapper proxyMapper;
    @Autowired
    private ExtCloudResourceSyncItemMapper extCloudResourceSyncItemMapper;
    @DubboReference
    private IOperationLogService operationLogService;
    @DubboReference
    private ISystemProviderService systemProviderService;
    @Autowired
    @Qualifier("loadBalanced")
    private RestTemplate restTemplate;

    /**
     * 获取同步日志
     *
     * @param resourceSyncRequest
     * @return
     */
    public List<CloudResourceSync> getCloudResourceSyncLogs(CloudResourceSyncRequest resourceSyncRequest) {
        return extCloudResourceSyncMapper.selectByRequest(resourceSyncRequest);
    }

    public List<Map<String, Object>> getResourceType(String syncId) {
        return extCloudResourceSyncItemMapper.selectResourceTypeBySyncId(syncId);
    }

    public List<CloudResourceSyncItemDTO> getCloudResourceSyncItem(String syncId) {
        List<CloudResourceSyncItemDTO> cloudResourceSyncItemDtos = extCloudResourceSyncItemMapper.selectBySyncId(syncId);
        List<CloudResourceSyncItemLog> cloudResourceSyncItemLogs = extCloudResourceSyncItemMapper.selectSyncItemLogBySyncId(syncId);
        Map<String, CloudResourceSyncItemDTO> cloudResourceSyncItemDtoMap = cloudResourceSyncItemDtos.stream().collect(Collectors.toMap(CloudResourceSyncItemDTO::getId, Function.identity()));
        for (CloudResourceSyncItemLog cloudResourceSyncItemLog : cloudResourceSyncItemLogs) {
            CloudResourceSyncItemDTO cloudResourceSyncItemDto = cloudResourceSyncItemDtoMap.get(cloudResourceSyncItemLog.getSyncItemId());
            if (cloudResourceSyncItemDto != null) {
                List<CloudResourceSyncItemLog> cloudResourceSyncItemLogs1 = cloudResourceSyncItemDto.getCloudResourceSyncItemLogs();
                if (cloudResourceSyncItemLogs1 == null) {
                    cloudResourceSyncItemLogs1 = new ArrayList<>();
                    cloudResourceSyncItemDto.setCloudResourceSyncItemLogs(cloudResourceSyncItemLogs1);
                }
                cloudResourceSyncItemLogs1.add(cloudResourceSyncItemLog);
            }
        }
        return cloudResourceSyncItemDtos;
    }

    public void sync(String accountId, LoginUser loginUser) throws Exception {
        LoginUser user = loginUser;
        //先清理后插入
        deleteResourceSync(accountId);

        CloudResourceSync cloudResourceSync = new CloudResourceSync();
        AccountWithBLOBs account = accountMapper.selectByPrimaryKey(accountId);
        String id = UUIDUtil.newUUID();
        String[] resourceTypes = PlatformUtils.checkoutResourceType(account.getPluginId());
        JSONArray jsonArray = JSON.parseArray(account.getRegions());
        JSONObject object;
        List<String> regions = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            try {
                object = jsonArray.getJSONObject(i);
                String value = object.getString("regionId");
                regions.add(value);
            } catch (Exception e) {
                String value = jsonArray.get(0).toString();
                regions.add(value);
            }
        }
        cloudResourceSync.setId(id);
        cloudResourceSync.setPluginId(account.getPluginId());
        cloudResourceSync.setPluginIcon(account.getPluginIcon());
        cloudResourceSync.setPluginName(account.getPluginName());
        cloudResourceSync.setAccountId(accountId);
        cloudResourceSync.setCreateTime(System.currentTimeMillis());
        cloudResourceSync.setApplyUser(user.getUserId());
        cloudResourceSync.setResourceTypes(StringUtils.join(resourceTypes, ","));
        cloudResourceSync.setStatus(CloudTaskConstants.TASK_STATUS.APPROVED.name());
        //云资源同步主表
        cloudResourceSyncMapper.insertSelective(cloudResourceSync);

        for (String resourceType : resourceTypes) {
            for (String region : regions) {
                CloudResourceSyncItem cloudResourceSyncItem = new CloudResourceSyncItem();
                String uuid = UUIDUtil.newUUID();
                cloudResourceSyncItem.setId(uuid);
                cloudResourceSyncItem.setSyncId(id);
                cloudResourceSyncItem.setStatus(CloudTaskConstants.TASK_STATUS.UNCHECKED.name());
                cloudResourceSyncItem.setCreateTime(System.currentTimeMillis());
                cloudResourceSyncItem.setAccountId(accountId);
                cloudResourceSyncItem.setAccountUrl(account.getPluginIcon());
                cloudResourceSyncItem.setAccountLabel(account.getName());
                cloudResourceSyncItem.setResourceType(resourceType);
                cloudResourceSyncItem.setRegionId(region);
                cloudResourceSyncItem.setRegionName(PlatformUtils.tranforRegionId2RegionName(region, account.getPluginId()));
                //云资源同步子表（区分区域与资源类型）
                cloudResourceSyncItemMapper.insertSelective(cloudResourceSyncItem);

                saveCloudResourceSyncItemLog(cloudResourceSyncItem.getId(), "i18n_start_sync_resource", "", true, accountId, user.getUserId());

                final String finalScript = CloudTaskConstants.policy.replace("{resourceType}", resourceType);

                commonThreadPool.addTask(() -> {
                    if (!PlatformUtils.checkAvailableRegion(account.getPluginId(), resourceType, region)) {
                        cloudResourceSyncItem.setCount(0);
                        cloudResourceSyncItem.setStatus(CloudTaskConstants.TASK_STATUS.FINISHED.name());
                        cloudResourceSyncItemMapper.updateByPrimaryKey(cloudResourceSyncItem);
                        saveCloudResourceSyncItemLog(cloudResourceSyncItem.getId(), "i18n_end_sync_resource", "不支持该区域", true, accountId, user.getUserId());
                        return;
                    }
                    String dirPath = "", resultStr = "", fileName = "policy.yml";
                    boolean readResource = true;
                    try {
                        long nowDate = new Date().getTime();

                        String resources = "[]";
                        String custodianRun = "";
                        String metadata = "";

                        Map<String, String> map = PlatformUtils.getAccount(account, region, proxyMapper.selectByPrimaryKey(account.getProxyId()));

                        if (systemProviderService.license()) {
                            HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                            JSONObject jsonObject = PlatformUtils.fixedScanner(finalScript, map, account.getPluginId());
                            LogUtil.warn("sync all resource {scanner}[api body]: " + jsonObject.toJSONString());

                            HttpEntity<?> httpEntity = new HttpEntity<>(jsonObject, headers);
                            String result = restTemplate.postForObject("http://hummer-scaner/run",httpEntity,String.class);
                            JSONObject resultJson = JSONObject.parseObject(result);
                            String resultCode = resultJson.getString("code").toString();
                            String resultMsg = resultJson.getString("msg").toString();
                            if (!StringUtils.equals(resultCode, "200")) {
                                HRException.throwException(Translator.get("i18n_create_resource_failed") + ": " + resultMsg);
                            }

                            resultStr = resultJson.getString("data").toString();

                            if (PlatformUtils.isUserForbidden(resultStr)) {
                                resultStr = Translator.get("i18n_create_resource_region_failed");
                                readResource = false;
                            }
                            if (resultStr.contains("ERROR"))
                                HRException.throwException(Translator.get("i18n_create_resource_failed") + ": " + resultStr);

                            custodianRun = jsonObject.toJSONString();
                            metadata = jsonObject.toJSONString();

                            if (readResource) {
                                resources = resultStr;
                            }

                        } else {
                            dirPath = CloudTaskConstants.RESULT_FILE_PATH_PREFIX + uuid + "/" + region;
                            CommandUtils.saveAsFile(finalScript, dirPath, "policy.yml", false);
                            String command = PlatformUtils.fixedCommand(CommandEnum.custodian.getCommand(), CommandEnum.run.getCommand(), dirPath, fileName, map);
                            LogUtil.warn(account.getId() + " {}[command]: " + command);
                            resultStr = CommandUtils.commonExecCmdWithResult(command, dirPath);
                            if (LogUtil.getLogger().isDebugEnabled()) {
                                LogUtil.getLogger().debug("resource created: {}", resultStr);
                            }
                            if (PlatformUtils.isUserForbidden(resultStr)) {
                                resultStr = Translator.get("i18n_create_resource_region_failed");
                                readResource = false;
                            }
                            if (resultStr.contains("ERROR"))
                                HRException.throwException(Translator.get("i18n_create_resource_failed") + ": " + resultStr);

                            custodianRun = ReadFileUtils.readToBuffer(dirPath + "/all-resources/" + CloudTaskConstants.CUSTODIAN_RUN_RESULT_FILE);
                            metadata = ReadFileUtils.readJsonFile(dirPath + "/all-resources/", CloudTaskConstants.METADATA_RESULT_FILE);

                            if (readResource) {
                                resources = ReadFileUtils.readJsonFile(dirPath + "/all-resources/", CloudTaskConstants.RESOURCES_RESULT_FILE);
                            }
                        }

                        CloudResourceWithBLOBs cloudResourceWithBLOBs = new CloudResourceWithBLOBs();
                        cloudResourceWithBLOBs.setId(UUIDUtil.newUUID());
                        cloudResourceWithBLOBs.setCustodianRunLog(custodianRun);
                        cloudResourceWithBLOBs.setMetadata(metadata);
                        cloudResourceWithBLOBs.setResources(resources);
                        cloudResourceWithBLOBs.setResourceType(resourceType);
                        cloudResourceWithBLOBs.setPluginIcon(account.getPluginIcon());
                        cloudResourceWithBLOBs.setPluginId(account.getPluginId());
                        cloudResourceWithBLOBs.setPluginName(account.getPluginName());
                        cloudResourceWithBLOBs.setAccountId(accountId);
                        cloudResourceWithBLOBs.setRegionId(region);
                        cloudResourceWithBLOBs.setCreateTime(nowDate);
                        cloudResourceWithBLOBs.setUpdateTime(nowDate);
                        cloudResourceWithBLOBs.setRegionName(cloudResourceSyncItem.getRegionName());
                        JSONArray resourcesArr = parseArray(resources);
                        cloudResourceWithBLOBs.setResourcesSum((long) resourcesArr.size());

                        //云资源同步资源表（查看返回结果与log）
                        cloudResourceMapper.insertSelective(cloudResourceWithBLOBs);

                        for (Object obj : resourcesArr) {
                            //资源详情
                            JSONObject jsonObject = parseObject(obj.toString());
                            String hummerId = jsonObject.getString("hummerId") != null ? jsonObject.getString("hummerId") : jsonObject.getString("id");
                            String hummerName = jsonObject.getString("hummerName") != null ? jsonObject.getString("hummerName") : jsonObject.getString("id");
                            CloudResourceItem cloudResourceItem = new CloudResourceItem();
                            cloudResourceItem.setId(UUIDUtil.newUUID());
                            cloudResourceItem.setAccountId(accountId);
                            cloudResourceItem.setUpdateTime(System.currentTimeMillis());
                            cloudResourceItem.setPluginIcon(account.getPluginIcon());
                            cloudResourceItem.setPluginId(account.getPluginId());
                            cloudResourceItem.setPluginName(account.getPluginName());
                            cloudResourceItem.setRegionId(region);
                            cloudResourceItem.setRegionName(cloudResourceSyncItem.getRegionName());
                            cloudResourceItem.setResourceId(cloudResourceWithBLOBs.getId());
                            cloudResourceItem.setResourceType(resourceType);
                            cloudResourceItem.setHummerId(hummerId);
                            cloudResourceItem.setHummerName(hummerName);
                            cloudResourceItem.setCreateTime(nowDate);
                            cloudResourceItem.setResource(jsonObject.toJSONString());

                            //云资源同步资源详情表
                            cloudResourceItemMapper.insertSelective(cloudResourceItem);
                        }
                        cloudResourceSyncItem.setCount(resourcesArr.size());
                        cloudResourceSyncItem.setStatus(CloudTaskConstants.TASK_STATUS.FINISHED.name());
                        cloudResourceSyncItemMapper.updateByPrimaryKey(cloudResourceSyncItem);
                        saveCloudResourceSyncItemLog(cloudResourceSyncItem.getId(), "i18n_end_sync_resource", "资源总数:" + resourcesArr.size(), true, accountId, user.getUserId());

                    } catch (Exception e) {
                        e.printStackTrace();
                        cloudResourceSyncItem.setStatus(CloudTaskConstants.TASK_STATUS.ERROR.name());
                        cloudResourceSyncItemMapper.updateByPrimaryKey(cloudResourceSyncItem);
                        saveCloudResourceSyncItemLog(cloudResourceSyncItem.getId(), "i18n_error_sync_resource", e.getMessage(), false, accountId, user.getUserId());
                        LogUtil.error("Sync Resources error :{}", uuid + "/" + region, e.getMessage());
                    }

                });
                //向首页活动添加操作信息
                operationLogService.log(user, id, account.getName(), ResourceTypeConstants.SYNC.name(), ResourceOperation.SYNC, "i18n_start_sync_resource");
            }
        }
    }

    void saveCloudResourceSyncItemLog(String syncItemId, String operation, String output, boolean result, String accountId,String operator) {
        CloudResourceSyncItemLog log = new CloudResourceSyncItemLog();
        log.setOperator(operator==null?"system":operator);
        log.setSyncItemId(syncItemId);
        log.setCreateTime(System.currentTimeMillis());
        log.setOperation(operation);
        log.setOutput(output);
        log.setResult(result);
        log.setAccountId(accountId);
        cloudResourceSyncItemLogMapper.insertSelective(log);
    }

    public void deleteResourceSync(String accountId) throws Exception {
        CloudResourceSyncExample cloudResourceSyncExample = new CloudResourceSyncExample();
        cloudResourceSyncExample.createCriteria().andAccountIdEqualTo(accountId);
        cloudResourceSyncMapper.deleteByExample(cloudResourceSyncExample);

        CloudResourceSyncItemExample cloudResourceSyncItemExample = new CloudResourceSyncItemExample();
        cloudResourceSyncItemExample.createCriteria().andAccountIdEqualTo(accountId);
        cloudResourceSyncItemMapper.deleteByExample(cloudResourceSyncItemExample);

        CloudResourceSyncItemLogExample cloudResourceSyncItemLogExample = new CloudResourceSyncItemLogExample();
        cloudResourceSyncItemLogExample.createCriteria().andAccountIdEqualTo(accountId);
        cloudResourceSyncItemLogMapper.deleteByExample(cloudResourceSyncItemLogExample);

        CloudResourceExample cloudResourceExample = new CloudResourceExample();
        cloudResourceExample.createCriteria().andAccountIdEqualTo(accountId);
        cloudResourceMapper.deleteByExample(cloudResourceExample);

        CloudResourceItemExample cloudResourceItemExample = new CloudResourceItemExample();
        cloudResourceItemExample.createCriteria().andAccountIdEqualTo(accountId);
        cloudResourceItemMapper.deleteByExample(cloudResourceItemExample);
    }

    public void syncResources(LoginUser loginUser) throws Exception {
        AccountExample example = new AccountExample();
        example.createCriteria().andStatusEqualTo("VALID");
        List<Account> accounts = accountMapper.selectByExample(example);
        for (Account account : accounts) {
            sync(account.getId(), loginUser);
        }
    }

    public void deleteSync(String id) {
        CloudResourceSync cloudResourceSync = cloudResourceSyncMapper.selectByPrimaryKey(id);
        cloudResourceSyncMapper.deleteByPrimaryKey(id);
        CloudResourceSyncItemExample cloudResourceSyncItemExample = new CloudResourceSyncItemExample();
        cloudResourceSyncItemExample.createCriteria().andSyncIdEqualTo(id);
        cloudResourceSyncItemMapper.deleteByExample(cloudResourceSyncItemExample);
        CloudResourceSyncItemLogExample cloudResourceSyncItemLogExample = new CloudResourceSyncItemLogExample();
        cloudResourceSyncItemLogExample.createCriteria().andAccountIdEqualTo(cloudResourceSync.getAccountId());
        cloudResourceSyncItemLogMapper.deleteByExample(cloudResourceSyncItemLogExample);
    }

    public void deleteLogs(List<String> ids) throws Exception {
        ids.forEach(id -> {
            try {
                deleteSync(id);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public CloudTopology cloudTopology(String accountId) {
        return extCloudResourceSyncMapper.cloudTopology(accountId);
    }

}
