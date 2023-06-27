package com.hummer.cloud.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.cloud.mapper.*;
import com.hummer.cloud.mapper.ext.ExtCloudResourceRelaMapper;
import com.hummer.cloud.mapper.ext.ExtCloudResourceSyncItemMapper;
import com.hummer.cloud.mapper.ext.ExtCloudResourceSyncMapper;
import com.hummer.common.core.constant.CloudTaskConstants;
import com.hummer.common.core.constant.CommandEnum;
import com.hummer.common.core.constant.ResourceOperation;
import com.hummer.common.core.constant.ResourceTypeConstants;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.cloudResource.CloudResourceSyncRequest;
import com.hummer.common.core.domain.request.sync.CloudTopology;
import com.hummer.common.core.dto.CloudResourceRelaDTO;
import com.hummer.common.core.dto.CloudResourceSyncItemDTO;
import com.hummer.common.core.dto.TopoChartDTO;
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
    @Autowired
    private CloudResourceRelaMapper cloudResourceRelaMapper;
    @Autowired
    private ExtCloudResourceRelaMapper extCloudResourceRelaMapper;
    @Autowired
    private CloudResourceRelaLinkMapper cloudResourceRelaLinkMapper;
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

                            //计算云资源关系拓扑图数据
                            dealWithResourceRelation(cloudResourceItem);
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

    public TopoChartDTO cloudTopologyRela(String resourceItemId)  {

        TopoChartDTO topoChartDTO = new TopoChartDTO();

        List<CloudResourceRelaDTO> cloudResourceRelaList = extCloudResourceRelaMapper.selectCloudTopologyRela(resourceItemId);

        CloudResourceRelaLinkExample cloudResourceRelaLinkExample = new CloudResourceRelaLinkExample();
        cloudResourceRelaLinkExample.createCriteria().andResourceItemIdEqualTo(resourceItemId);
        List<CloudResourceRelaLink> cloudResourceRelaLinkList = cloudResourceRelaLinkMapper.selectByExample(cloudResourceRelaLinkExample);

        topoChartDTO.setCloudResourceRelaList(cloudResourceRelaList);
        topoChartDTO.setCloudResourceRelaLinkList(cloudResourceRelaLinkList);

        return topoChartDTO;
    }

    public void dealWithResourceRelation (CloudResourceItem cloudResourceItem) throws Exception  {
        try{
            String pluginId = cloudResourceItem.getPluginId();
            if (StringUtils.equals(pluginId, "hummer-aws-plugin")) {
                dealAws(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-azure-plugin")) {
                dealAzure(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-aliyun-plugin")) {
                dealAliyun(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-huawei-plugin")) {
                dealHuawei(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-qcloud-plugin")) {
                dealQcloud(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-vsphere-plugin")) {
                dealVsphere(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-openstack-plugin")) {
                dealOpenstack(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-gcp-plugin")) {
                dealGcp(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-huoshan-plugin")) {
                dealHuoshan(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-baidu-plugin")) {
                dealBaidu(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-qiniu-plugin")) {
                dealQiniu(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-qingcloud-plugin")) {
                dealQingcloud(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-ucloud-plugin")) {
                dealUcloud(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-jdcloud-plugin")) {
                dealJdcloud(cloudResourceItem);
            } else if (StringUtils.equals(pluginId, "hummer-ksyun-plugin")) {
                dealKsyun(cloudResourceItem);
            }

        } catch (Exception e) {
            throw e;
        }
    }

    public void dealAws (CloudResourceItem cloudResourceItem) throws Exception  {
        String json = cloudResourceItem.getResource();
        if(StringUtils.isEmpty(json)) return;

        String resourceType = cloudResourceItem.getResourceType();
        String accountId = cloudResourceItem.getAccountId();
        String regionId = cloudResourceItem.getRegionId();
        String hummerId = cloudResourceItem.getHummerId();
        JSONObject jsonObject = JSONObject.parseObject(json);

        switch (resourceType) {
            case "aws.ec2":
                String PublicIpAddress = jsonObject.getString("PublicIpAddress");
                String Internet = UUIDUtil.newUUID();
                Long x = 100L, y = 100L;
                if (StringUtils.isEmpty(PublicIpAddress)) {
                    CloudResourceRela cloudResourceRela = new CloudResourceRela();

                    cloudResourceRela.setId(Internet);
                    cloudResourceRela.setResourceItemId(cloudResourceItem.getId());
                    cloudResourceRela.setName("No Internet");
                    cloudResourceRela.setPluginId(cloudResourceItem.getPluginId());
                    cloudResourceRela.setAccountId(accountId);
                    cloudResourceRela.setRegionId(regionId);
                    cloudResourceRela.setResourceType("internet");
                    cloudResourceRela.setHummerId("No Internet");
                    cloudResourceRela.setCreateTime(System.currentTimeMillis());
                    cloudResourceRela.setxAxis(x);//100
                    cloudResourceRela.setyAxis(y);//100

                    cloudResourceRelaMapper.insertSelective(cloudResourceRela);
                } else {
                    CloudResourceRela cloudResourceRela = new CloudResourceRela();

                    cloudResourceRela.setId(Internet);
                    cloudResourceRela.setResourceItemId(cloudResourceItem.getId());
                    cloudResourceRela.setName("Internet");
                    cloudResourceRela.setPluginId(cloudResourceItem.getPluginId());
                    cloudResourceRela.setAccountId(accountId);
                    cloudResourceRela.setRegionId(regionId);
                    cloudResourceRela.setResourceType("internet");
                    cloudResourceRela.setHummerId("Internet");
                    cloudResourceRela.setxAxis(x);//100
                    cloudResourceRela.setyAxis(y);//100

                    insertCloudResourceRela(cloudResourceRela);
                }

                JSONArray networkInterfaces = JSONArray.parseArray(!StringUtils.isEmpty(jsonObject.getString("NetworkInterfaces"))?jsonObject.getString("NetworkInterfaces"):"[]");
                JSONArray BlockDeviceMappings = JSONArray.parseArray(!StringUtils.isEmpty(jsonObject.getString("BlockDeviceMappings"))?jsonObject.getString("BlockDeviceMappings"):"[]");

                for (Object obj : networkInterfaces) {
                    JSONObject jsonObj = JSONObject.parseObject(obj.toString());
                    String SubnetId = jsonObj.getString("SubnetId");
                    String VpcId = jsonObj.getString("VpcId");
                    JSONObject Association = JSONObject.parseObject(!StringUtils.isEmpty(jsonObj.getString("Association"))?jsonObj.getString("Association"):"{}");
                    String PublicIp = Association.getString("PublicIp");
                    JSONArray Groups = JSONArray.parseArray(!StringUtils.isEmpty(jsonObj.getString("Groups"))?jsonObj.getString("Groups"):"[]");

                    String SubnetRelaId = UUIDUtil.newUUID();
                    String VpcRelaId = UUIDUtil.newUUID();
                    String PublicRelaIp = UUIDUtil.newUUID();

                    CloudResourceRela cloudResourceRela = new CloudResourceRela();
                    cloudResourceRela.setResourceItemId(cloudResourceItem.getId());
                    cloudResourceRela.setPluginId(cloudResourceItem.getPluginId());
                    cloudResourceRela.setAccountId(accountId);
                    cloudResourceRela.setRegionId(regionId);

                    if (!StringUtils.isEmpty(SubnetId)) {

                        cloudResourceRela.setId(SubnetRelaId);
                        cloudResourceRela.setName(SubnetId);
                        cloudResourceRela.setResourceType("aws.subnet");
                        cloudResourceRela.setHummerId(SubnetId);
                        cloudResourceRela.setxAxis(x);//100
                        cloudResourceRela.setyAxis(y + 100L);//200
                        cloudResourceRela = insertCloudResourceRela(cloudResourceRela);

                        CloudResourceRelaLink cloudResourceRelaLink = new CloudResourceRelaLink();
                        cloudResourceRelaLink.setResourceItemId(cloudResourceItem.getId());
                        cloudResourceRelaLink.setSource(Internet);
                        cloudResourceRelaLink.setTarget(SubnetRelaId);
                        insertCloudResourceRelaLink(cloudResourceRelaLink);

                        y = y + 100L;

                        if (!StringUtils.isEmpty(VpcId)) {
                            CloudResourceRela cloudResourceRela2 = new CloudResourceRela();
                            cloudResourceRela2 = BeanUtils.copyBean(cloudResourceRela2, cloudResourceRela);
                            cloudResourceRela2.setId(VpcRelaId);
                            cloudResourceRela2.setName(SubnetId);
                            cloudResourceRela2.setResourceType("aws.vpc");
                            cloudResourceRela2.setHummerId(SubnetId);
                            cloudResourceRela2.setxAxis(x + 100L);//200
                            cloudResourceRela2.setyAxis(y);//200
                            cloudResourceRela2 = insertCloudResourceRela(cloudResourceRela2);

                            CloudResourceRelaLink cloudResourceRelaLink2 = new CloudResourceRelaLink();
                            cloudResourceRelaLink2.setResourceItemId(cloudResourceItem.getId());
                            cloudResourceRelaLink2.setSource(SubnetRelaId);
                            cloudResourceRelaLink2.setTarget(VpcRelaId);
                            insertCloudResourceRelaLink(cloudResourceRelaLink2);

                            x = x + 100L;

                            if (!StringUtils.isEmpty(PublicIp)) {
                                CloudResourceRela cloudResourceRela3 = new CloudResourceRela();
                                cloudResourceRela3 = BeanUtils.copyBean(cloudResourceRela3, cloudResourceRela);

                                cloudResourceRela3.setId(PublicRelaIp);
                                cloudResourceRela3.setName(PublicIp);
                                cloudResourceRela3.setResourceType("aws.publicip");
                                cloudResourceRela3.setHummerId(PublicIp);
                                cloudResourceRela3.setxAxis(x + 100L);//300
                                cloudResourceRela3.setyAxis(y);//200
                                cloudResourceRela3 = insertCloudResourceRela(cloudResourceRela3);

                                CloudResourceRelaLink cloudResourceRelaLink3 = new CloudResourceRelaLink();
                                cloudResourceRelaLink3.setResourceItemId(cloudResourceItem.getId());
                                cloudResourceRelaLink3.setSource(VpcRelaId);
                                cloudResourceRelaLink3.setTarget(PublicRelaIp);
                                insertCloudResourceRelaLink(cloudResourceRelaLink3);

                                x = x + 200L;
                                y = y - 100L;

                                String EcsRelaId = UUIDUtil.newUUID();

                                CloudResourceRela cloudResourceRela5 = new CloudResourceRela();
                                cloudResourceRela5 = BeanUtils.copyBean(cloudResourceRela5, cloudResourceRela);

                                cloudResourceRela5.setId(EcsRelaId);
                                cloudResourceRela5.setName(cloudResourceItem.getHummerName());
                                cloudResourceRela5.setResourceType(resourceType);
                                cloudResourceRela5.setHummerId(hummerId);
                                cloudResourceRela5.setxAxis(x);//400
                                cloudResourceRela5.setyAxis(y + 100L);//200
                                cloudResourceRela5 = insertCloudResourceRela(cloudResourceRela5);

                                if (Groups.size() > 0) {

                                    for (Object o : Groups) {
                                        JSONObject jsonO = JSONObject.parseObject(o.toString());
                                        String GroupId = jsonO.getString("GroupId");
                                        String GroupName = jsonO.getString("GroupName");
                                        String GroupRelaId = UUIDUtil.newUUID();

                                        CloudResourceRela cloudResourceRela4 = new CloudResourceRela();
                                        cloudResourceRela4 = BeanUtils.copyBean(cloudResourceRela4, cloudResourceRela);

                                        cloudResourceRela4.setId(GroupRelaId);
                                        cloudResourceRela4.setName(GroupName);
                                        cloudResourceRela4.setResourceType("aws.security-group");
                                        cloudResourceRela4.setHummerId(GroupId);
                                        cloudResourceRela4.setxAxis(x);//400
                                        cloudResourceRela4.setyAxis(y + 100L);//200
                                        cloudResourceRela4 = insertCloudResourceRela(cloudResourceRela4);

                                        CloudResourceRelaLink cloudResourceRelaLink4 = new CloudResourceRelaLink();
                                        cloudResourceRelaLink4.setResourceItemId(cloudResourceItem.getId());
                                        cloudResourceRelaLink4.setSource(PublicRelaIp);
                                        cloudResourceRelaLink4.setTarget(GroupRelaId);
                                        insertCloudResourceRelaLink(cloudResourceRelaLink4);

                                        CloudResourceRelaLink cloudResourceRelaLink5 = new CloudResourceRelaLink();
                                        cloudResourceRelaLink5.setResourceItemId(cloudResourceItem.getId());
                                        cloudResourceRelaLink5.setSource(GroupRelaId);
                                        cloudResourceRelaLink5.setTarget(EcsRelaId);
                                        insertCloudResourceRelaLink(cloudResourceRelaLink5);

                                        for (Object b : BlockDeviceMappings) {
                                            String id = UUIDUtil.newUUID();
                                            JSONObject j = JSONObject.parseObject(b.toString());
                                            String DeviceName = j.getString("DeviceName");
                                            JSONObject Ebs = JSONObject.parseObject(j.getString("Ebs"));
                                            String VolumeId = Ebs.getString("VolumeId");

                                            CloudResourceRela cloudResourceRela6 = new CloudResourceRela();
                                            cloudResourceRela6 = BeanUtils.copyBean(cloudResourceRela6, cloudResourceRela);
                                            cloudResourceRela6.setId(id);
                                            cloudResourceRela6.setName(DeviceName);
                                            cloudResourceRela6.setResourceType("aws.ebs");
                                            cloudResourceRela6.setHummerId(VolumeId);
                                            cloudResourceRela6.setxAxis(x);//400
                                            cloudResourceRela6.setyAxis(y + 100L);//200
                                            cloudResourceRela6 = insertCloudResourceRela(cloudResourceRela6);

                                            CloudResourceRelaLink cloudResourceRelaLink6 = new CloudResourceRelaLink();
                                            cloudResourceRelaLink6.setResourceItemId(cloudResourceItem.getId());
                                            cloudResourceRelaLink6.setSource(EcsRelaId);
                                            cloudResourceRelaLink6.setTarget(id);
                                            insertCloudResourceRelaLink(cloudResourceRelaLink6);

                                            y = y + 100;
                                        }
                                    }

                                } else {
                                    CloudResourceRelaLink cloudResourceRelaLink5 = new CloudResourceRelaLink();
                                    cloudResourceRelaLink5.setResourceItemId(cloudResourceItem.getId());
                                    cloudResourceRelaLink5.setSource(cloudResourceRela3.getId());
                                    cloudResourceRelaLink5.setTarget(cloudResourceRela5.getId());
                                    insertCloudResourceRelaLink(cloudResourceRelaLink5);
                                    for (Object b : BlockDeviceMappings) {
                                        String id = UUIDUtil.newUUID();
                                        JSONObject j = JSONObject.parseObject(b.toString());
                                        String DeviceName = j.getString("DeviceName");
                                        JSONObject Ebs = JSONObject.parseObject(j.getString("Ebs"));
                                        String VolumeId = Ebs.getString("VolumeId");

                                        CloudResourceRela cloudResourceRela6 = new CloudResourceRela();
                                        cloudResourceRela6 = BeanUtils.copyBean(cloudResourceRela6, cloudResourceRela);
                                        cloudResourceRela6.setId(id);
                                        cloudResourceRela6.setName(DeviceName);
                                        cloudResourceRela6.setResourceType("aws.ebs");
                                        cloudResourceRela6.setHummerId(VolumeId);
                                        cloudResourceRela6.setxAxis(x);//400
                                        cloudResourceRela6.setyAxis(y + 100L);//200
                                        cloudResourceRela6 = insertCloudResourceRela(cloudResourceRela6);

                                        CloudResourceRelaLink cloudResourceRelaLink6 = new CloudResourceRelaLink();
                                        cloudResourceRelaLink6.setResourceItemId(cloudResourceItem.getId());
                                        cloudResourceRelaLink6.setSource(cloudResourceRela5.getId());
                                        cloudResourceRelaLink6.setTarget(cloudResourceRela6.getId());
                                        insertCloudResourceRelaLink(cloudResourceRelaLink6);

                                        y = y + 100;
                                    }
                                }

                            } else {

                                String EcsRelaId = UUIDUtil.newUUID();

                                CloudResourceRela cloudResourceRela5 = new CloudResourceRela();
                                cloudResourceRela5 = BeanUtils.copyBean(cloudResourceRela5, cloudResourceRela);

                                cloudResourceRela5.setId(EcsRelaId);
                                cloudResourceRela5.setName(cloudResourceItem.getHummerName());
                                cloudResourceRela5.setResourceType(resourceType);
                                cloudResourceRela5.setHummerId(cloudResourceItem.getHummerId());
                                cloudResourceRela5.setxAxis(x);//400
                                cloudResourceRela5.setyAxis(y + 100L);//200
                                cloudResourceRela5 = insertCloudResourceRela(cloudResourceRela5);

                                if (Groups.size() > 0) {

                                    for (Object o : Groups) {
                                        JSONObject jsonO = JSONObject.parseObject(o.toString());
                                        String GroupId = jsonO.getString("GroupId");
                                        String GroupName = jsonO.getString("GroupName");
                                        String GroupRelaId = UUIDUtil.newUUID();

                                        CloudResourceRela cloudResourceRela4 = new CloudResourceRela();
                                        cloudResourceRela4 = BeanUtils.copyBean(cloudResourceRela4, cloudResourceRela);

                                        cloudResourceRela4.setId(GroupRelaId);
                                        cloudResourceRela4.setName(GroupName);
                                        cloudResourceRela4.setResourceType("aws.security-group");
                                        cloudResourceRela4.setHummerId(GroupId);
                                        cloudResourceRela4.setxAxis(x);//400
                                        cloudResourceRela4.setyAxis(y + 100L);//200
                                        cloudResourceRela4 = insertCloudResourceRela(cloudResourceRela4);

                                        CloudResourceRelaLink cloudResourceRelaLink4 = new CloudResourceRelaLink();
                                        cloudResourceRelaLink4.setResourceItemId(cloudResourceItem.getId());
                                        cloudResourceRelaLink4.setSource(PublicRelaIp);
                                        cloudResourceRelaLink4.setTarget(GroupRelaId);
                                        insertCloudResourceRelaLink(cloudResourceRelaLink4);

                                        CloudResourceRelaLink cloudResourceRelaLink5 = new CloudResourceRelaLink();
                                        cloudResourceRelaLink5.setResourceItemId(cloudResourceItem.getId());
                                        cloudResourceRelaLink5.setSource(GroupRelaId);
                                        cloudResourceRelaLink5.setTarget(EcsRelaId);
                                        insertCloudResourceRelaLink(cloudResourceRelaLink5);

                                        for (Object b : BlockDeviceMappings) {
                                            String id = UUIDUtil.newUUID();
                                            JSONObject j = JSONObject.parseObject(b.toString());
                                            String DeviceName = j.getString("DeviceName");
                                            JSONObject Ebs = JSONObject.parseObject(j.getString("Ebs"));
                                            String VolumeId = Ebs.getString("VolumeId");

                                            CloudResourceRela cloudResourceRela6 = new CloudResourceRela();
                                            cloudResourceRela6 = BeanUtils.copyBean(cloudResourceRela6, cloudResourceRela);
                                            cloudResourceRela6.setId(id);
                                            cloudResourceRela6.setName(DeviceName);
                                            cloudResourceRela6.setResourceType("aws.ebs");
                                            cloudResourceRela6.setHummerId(VolumeId);
                                            cloudResourceRela6.setxAxis(x);//400
                                            cloudResourceRela6.setyAxis(y + 100L);//200
                                            cloudResourceRela6 = insertCloudResourceRela(cloudResourceRela6);

                                            CloudResourceRelaLink cloudResourceRelaLink6 = new CloudResourceRelaLink();
                                            cloudResourceRelaLink6.setResourceItemId(cloudResourceItem.getId());
                                            cloudResourceRelaLink6.setSource(EcsRelaId);
                                            cloudResourceRelaLink6.setTarget(id);
                                            insertCloudResourceRelaLink(cloudResourceRelaLink6);

                                            y = y + 100;
                                        }
                                    }

                                } else {
                                    CloudResourceRelaLink cloudResourceRelaLink5 = new CloudResourceRelaLink();
                                    cloudResourceRelaLink5.setResourceItemId(cloudResourceItem.getId());
                                    cloudResourceRelaLink5.setSource(VpcRelaId);
                                    cloudResourceRelaLink5.setTarget(EcsRelaId);
                                    insertCloudResourceRelaLink(cloudResourceRelaLink5);
                                    for (Object b : BlockDeviceMappings) {
                                        String id = UUIDUtil.newUUID();
                                        JSONObject j = JSONObject.parseObject(b.toString());
                                        String DeviceName = j.getString("DeviceName");
                                        JSONObject Ebs = JSONObject.parseObject(j.getString("Ebs"));
                                        String VolumeId = Ebs.getString("VolumeId");

                                        CloudResourceRela cloudResourceRela6 = new CloudResourceRela();
                                        cloudResourceRela6 = BeanUtils.copyBean(cloudResourceRela6, cloudResourceRela);
                                        cloudResourceRela6.setId(id);
                                        cloudResourceRela6.setName(DeviceName);
                                        cloudResourceRela6.setResourceType("aws.ebs");
                                        cloudResourceRela6.setHummerId(VolumeId);
                                        cloudResourceRela6.setxAxis(x);//400
                                        cloudResourceRela6.setyAxis(y + 100L);//200
                                        cloudResourceRela6 = insertCloudResourceRela(cloudResourceRela6);

                                        CloudResourceRelaLink cloudResourceRelaLink6 = new CloudResourceRelaLink();
                                        cloudResourceRelaLink6.setResourceItemId(cloudResourceItem.getId());
                                        cloudResourceRelaLink6.setSource(EcsRelaId);
                                        cloudResourceRelaLink6.setTarget(id);
                                        insertCloudResourceRelaLink(cloudResourceRelaLink6);

                                        y = y + 100;
                                    }
                                }
                            }

                        }

                    }

                }
                break;
            case "aws.ebs":
                break;
            case "aws.elb":
                break;
            case "aws.network-addr":
                break;
            case "aws.rds":
                break;
            case "aws.s3":
                break;
            case "aws.security-group":
                break;
            default:
                break;
        }
    }

    public void dealAzure (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealAliyun (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealHuawei (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealQcloud (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealVsphere (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealOpenstack (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealGcp (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealHuoshan (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealBaidu (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealQiniu (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealQingcloud (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealUcloud (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealJdcloud (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public void dealKsyun (CloudResourceItem cloudResourceItem) throws Exception  {
    }

    public CloudResourceRela insertCloudResourceRela (CloudResourceRela cloudResourceRela) throws Exception  {
        cloudResourceRela.setCreateTime(System.currentTimeMillis());
        CloudResourceRelaExample example = new CloudResourceRelaExample();
        example.createCriteria().andResourceItemIdEqualTo(cloudResourceRela.getResourceItemId()).andHummerIdEqualTo(cloudResourceRela.getHummerId()).andNameEqualTo(cloudResourceRela.getName());
        List<CloudResourceRela> list = cloudResourceRelaMapper.selectByExample(example);
        if(list.size() == 0) {
            cloudResourceRelaMapper.insertSelective(cloudResourceRela);
            cloudResourceRela = list.get(0);
        }
        return cloudResourceRela;
    }

    public void insertCloudResourceRelaLink (CloudResourceRelaLink cloudResourceRelaLink) throws Exception  {
        cloudResourceRelaLink.setCreateTime(System.currentTimeMillis());
        cloudResourceRelaLinkMapper.insertSelective(cloudResourceRelaLink);
    }

}
