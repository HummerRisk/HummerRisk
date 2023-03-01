package com.hummer.cloud.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.common.core.constant.CloudTaskConstants;
import com.hummer.common.core.constant.CommandEnum;
import com.hummer.common.core.constant.ResourceOperation;
import com.hummer.common.core.constant.ResourceTypeConstants;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.dto.CloudResourceSyncItemDTO;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.*;
import com.hummer.common.core.domain.request.cloudResource.CloudResourceSyncRequest;
import com.hummer.common.core.domain.request.sync.CloudTopology;
import com.hummer.cloud.mapper.*;
import com.hummer.cloud.mapper.ext.ExtCloudResourceSyncItemMapper;
import com.hummer.cloud.mapper.ext.ExtCloudResourceSyncMapper;
import com.hummer.common.security.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.parseArray;
import static com.alibaba.fastjson.JSON.parseObject;


@Service
@Transactional(rollbackFor = Exception.class)
public class CloudSyncService {

    @Resource
    private AccountMapper accountMapper;
    @Resource
    private CloudResourceSyncMapper cloudResourceSyncMapper;
    @Resource
    private ExtCloudResourceSyncMapper extCloudResourceSyncMapper;
    @Resource
    private CloudResourceSyncItemMapper cloudResourceSyncItemMapper;
    @Resource
    private CloudResourceSyncItemLogMapper cloudResourceSyncItemLogMapper;
    @Resource
    private CloudResourceMapper cloudResourceMapper;
    @Resource
    private CloudResourceItemMapper cloudResourceItemMapper;
    @Resource
    private CommonThreadPool commonThreadPool;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private ExtCloudResourceSyncItemMapper extCloudResourceSyncItemMapper;
    @Resource
    private TokenService tokenService;
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

    public void sync(String accountId) throws Exception {

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
        cloudResourceSync.setApplyUser(Objects.requireNonNull(tokenService.getLoginUser().getUser()).getId());
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

                saveCloudResourceSyncItemLog(cloudResourceSyncItem.getId(), "i18n_start_sync_resource", "", true, accountId);

                final String finalScript = CloudTaskConstants.policy.replace("{resourceType}", resourceType);

                commonThreadPool.addTask(() -> {
                    if (!PlatformUtils.checkAvailableRegion(account.getPluginId(), resourceType, region)) {
                        cloudResourceSyncItem.setCount(0);
                        cloudResourceSyncItem.setStatus(CloudTaskConstants.TASK_STATUS.FINISHED.name());
                        cloudResourceSyncItemMapper.updateByPrimaryKey(cloudResourceSyncItem);
                        saveCloudResourceSyncItemLog(cloudResourceSyncItem.getId(), "i18n_end_sync_resource", "不支持该区域", true, accountId);
                        return;
                    }
                    String dirPath = "", resultStr = "", fileName = "policy.yml";
                    boolean readResource = true;
                    try {
                        long nowDate = new Date().getTime();
                        dirPath = CloudTaskConstants.RESULT_FILE_PATH_PREFIX + uuid + "/" + region;
                        CommandUtils.saveAsFile(finalScript, dirPath, "policy.yml", false);
                        Map<String, String> map = PlatformUtils.getAccount(account, region, proxyMapper.selectByPrimaryKey(account.getProxyId()));
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
                            throw new Exception(Translator.get("i18n_create_resource_failed") + ": " + resultStr);

                        String custodianRun = ReadFileUtils.readToBuffer(dirPath + "/all-resources/" + CloudTaskConstants.CUSTODIAN_RUN_RESULT_FILE);
                        String metadata = ReadFileUtils.readJsonFile(dirPath + "/all-resources/", CloudTaskConstants.METADATA_RESULT_FILE);
                        String resources = "[]";
                        if (readResource) {
                            resources = ReadFileUtils.readJsonFile(dirPath + "/all-resources/", CloudTaskConstants.RESOURCES_RESULT_FILE);
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
                            String fid = jsonObject.getString("hummerId") != null ? jsonObject.getString("hummerId") : jsonObject.getString("id");
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
                            cloudResourceItem.setHummerId(fid);
                            cloudResourceItem.setCreateTime(nowDate);
                            cloudResourceItem.setResource(jsonObject.toJSONString());

                            //云资源同步资源详情表
                            cloudResourceItemMapper.insertSelective(cloudResourceItem);
                        }
                        cloudResourceSyncItem.setCount(resourcesArr.size());
                        cloudResourceSyncItem.setStatus(CloudTaskConstants.TASK_STATUS.FINISHED.name());
                        cloudResourceSyncItemMapper.updateByPrimaryKey(cloudResourceSyncItem);
                        saveCloudResourceSyncItemLog(cloudResourceSyncItem.getId(), "i18n_end_sync_resource", "资源总数:" + resourcesArr.size(), true, accountId);

                    } catch (Exception e) {
                        e.printStackTrace();
                        cloudResourceSyncItem.setStatus(CloudTaskConstants.TASK_STATUS.ERROR.name());
                        cloudResourceSyncItemMapper.updateByPrimaryKey(cloudResourceSyncItem);
                        saveCloudResourceSyncItemLog(cloudResourceSyncItem.getId(), "i18n_error_sync_resource", e.getMessage(), false, accountId);
                        LogUtil.error("Sync Resources error :{}", uuid + "/" + region, e.getMessage());
                    }

                });
                //向首页活动添加操作信息
                OperationLogService.log(tokenService.getLoginUser().getUser(), id, account.getName(), ResourceTypeConstants.SYNC.name(), ResourceOperation.SYNC, "i18n_start_sync_resource");
            }
        }
    }

    void saveCloudResourceSyncItemLog(String syncItemId, String operation, String output, boolean result, String accountId) {
        CloudResourceSyncItemLog log = new CloudResourceSyncItemLog();
        String operator = "system";
        try {
            if (tokenService.getLoginUser().getUser() != null) {
                operator = tokenService.getLoginUser().getUser().getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //防止单元测试无session
        }
        log.setOperator(operator);
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

    public void syncResources() throws Exception {
        AccountExample example = new AccountExample();
        example.createCriteria().andStatusEqualTo("VALID");
        List<Account> accounts = accountMapper.selectByExample(example);
        for (Account account : accounts) {
            sync(account.getId());
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

    public CloudTopology cloudTopology() {
        return extCloudResourceSyncMapper.cloudTopology();
    }

}
