package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtCloudNativeMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudNativeResultMapper;
import com.hummerrisk.base.mapper.ext.ExtCloudNativeSourceMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.cloudNative.*;
import com.hummerrisk.controller.request.image.ImageRequest;
import com.hummerrisk.controller.request.k8s.K8sResultRequest;
import com.hummerrisk.dto.*;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.proxy.k8s.K8sRequest;
import com.hummerrisk.proxy.kubesphere.KubeSphereRequest;
import com.hummerrisk.proxy.rancher.RancherRequest;
import io.kubernetes.client.openapi.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class K8sService {

    @Resource
    private CloudNativeMapper cloudNativeMapper;
    @Resource
    private CloudNativeResultMapper cloudNativeResultMapper;
    @Resource
    private ExtCloudNativeResultMapper extCloudNativeResultMapper;
    @Resource
    private CloudNativeResultLogMapper cloudNativeResultLogMapper;
    @Resource
    private HistoryService historyService;
    @Resource
    private NoticeService noticeService;
    @Resource
    private CloudNativeResultItemMapper cloudNativeResultItemMapper;
    @Resource
    private CloudNativeSourceMapper cloudNativeSourceMapper;
    @Resource
    private CloudNativeSourceSyncLogMapper cloudNativeSourceSyncLogMapper;
    @Resource
    private CloudNativeRuleMapper cloudNativeRuleMapper;
    @Resource
    private HistoryCloudNativeResultMapper historyCloudNativeResultMapper;
    @Resource
    private ExtCloudNativeMapper extCloudNativeMapper;
    @Resource
    private HistoryCloudNativeResultItemMapper historyCloudNativeResultItemMapper;
    @Resource
    private HistoryCloudNativeResultLogMapper historyCloudNativeResultLogMapper;
    @Resource
    private ExtCloudNativeSourceMapper extCloudNativeSourceMapper;
    @Resource
    private PluginMapper pluginMapper;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private CommonThreadPool commonThreadPool;


    public List<CloudNativeDTO> getCloudNativeList(CloudNativeRequest request) {
        return extCloudNativeMapper.getCloudNativeList(request);
    }

    public List<CloudNativeDTO> allCloudNativeList(CloudNativeRequest request) {
        return extCloudNativeMapper.getCloudNativeList(request);
    }

    public CloudNative getCloudNative(String id) {
        return cloudNativeMapper.selectByPrimaryKey(id);
    }

    public boolean validate(List<String> ids) {
        ids.forEach(id -> {
            try {
                boolean validate = validate(id);
                if(!validate) throw new HRException(Translator.get("failed_cloud_native"));
            } catch (Exception e) {
                LogUtil.error(e.getMessage());
                throw new HRException(e.getMessage());
            }
        });
        return true;
    }


    public boolean validate(String id) throws IOException, ApiException {
        CloudNative cloudNative = cloudNativeMapper.selectByPrimaryKey(id);
        //检验账号的有效性
        boolean valid = validateAccount(cloudNative);
        if (valid) {
            cloudNative.setStatus(CloudAccountConstants.Status.VALID.name());
            addCloudNativeSource(cloudNative);
        } else {
            cloudNative.setStatus(CloudAccountConstants.Status.INVALID.name());
        }
        cloudNativeMapper.updateByPrimaryKeySelective(cloudNative);
        return valid;
    }

    public boolean operatorStatusValidate(String id) throws Exception {
        CloudNative cloudNative = cloudNativeMapper.selectByPrimaryKey(id);
        boolean valid = validateOperatorStatus(cloudNative);
        if (valid) {
            cloudNative.setOperatorStatus(CloudAccountConstants.Status.VALID.name());
        } else {
            cloudNative.setOperatorStatus(CloudAccountConstants.Status.INVALID.name());
        }
        cloudNativeMapper.updateByPrimaryKeySelective(cloudNative);
        return valid;
    }

    private boolean validateOperatorStatus(CloudNative cloudNative) {
        try {
            //检验
            K8sRequest k8sRequest = new K8sRequest();
            k8sRequest.setCredential(cloudNative.getCredential());
            String token = "Bearer " + k8sRequest.getToken();
            String url = k8sRequest.getUrl();
            if (url.endsWith("/")) {
                url = url + CloudNativeConstants.URL6;
            } else {
                url = url + CloudNativeConstants.URL5;
            }
            Map<String, String> param = new HashMap<>();
            param.put("Accept", CloudNativeConstants.Accept);
            param.put("Authorization", token);
            boolean valid = HttpClientUtil.operatorStatus(url, param);
            return valid;
        } catch (Exception e) {
            LogUtil.error(String.format("HRException in verifying cloud native, cloud native Operator Status: [%s], plugin: [%s], error information:%s", cloudNative.getName(), cloudNative.getPluginName(), e.getMessage()), e);
            return false;
        }
    }

    private boolean validateAccount(CloudNative cloudNative) {
        try {
            Proxy proxy = new Proxy();
            if (cloudNative.getProxyId() != null) proxy = proxyMapper.selectByPrimaryKey(cloudNative.getProxyId());
            return PlatformUtils.validateCloudNative(cloudNative, proxy);
        } catch (Exception e) {
            LogUtil.error(String.format("HRException in verifying cloud native, cloud native: [%s], plugin: [%s], error information:%s", cloudNative.getName(), cloudNative.getPluginName(), e.getMessage()), e);
            return false;
        }
    }

    public CloudNative addCloudNative(CreateCloudNativeRequest request) {
        try{
            //参数校验
            if (StringUtils.isEmpty(request.getCredential())
                    || StringUtils.isEmpty(request.getName()) || StringUtils.isEmpty(request.getPluginId())) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_name_or_plugin"));
            }

            //云账号名称不能重复
            CloudNativeExample cloudNativeExample = new CloudNativeExample();
            cloudNativeExample.createCriteria().andNameEqualTo(request.getName());
            List<CloudNative> cloudNatives = cloudNativeMapper.selectByExampleWithBLOBs(cloudNativeExample);
            if (!CollectionUtils.isEmpty(cloudNatives)) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_name_duplicate"));
            }

            CloudNative account = new CloudNative();

            //校验云插件是否存在
            Plugin plugin = pluginMapper.selectByPrimaryKey(request.getPluginId());
            if (plugin == null) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_no_exist_plugin"));
            } else {
                BeanUtils.copyBean(account, request);
                account.setPluginIcon(Objects.requireNonNull(plugin.getIcon()));
                account.setPluginName(plugin.getName());
                account.setCreateTime(System.currentTimeMillis());
                account.setUpdateTime(System.currentTimeMillis());
                account.setCreator(Objects.requireNonNull(SessionUtils.getUser()).getId());
                account.setId(UUIDUtil.newUUID());
                //检验账号的有效性
                boolean valid = validateAccount(account);
                if (valid) {
                    account.setStatus(CloudAccountConstants.Status.VALID.name());
                    addCloudNativeSource(account);
                } else {
                    account.setStatus(CloudAccountConstants.Status.INVALID.name());
                }
                //检验operator
                boolean operatorStatusValidate = validateOperatorStatus(account);
                if (operatorStatusValidate) {
                    account.setOperatorStatus(CloudAccountConstants.Status.VALID.name());
                } else {
                    account.setOperatorStatus(CloudAccountConstants.Status.INVALID.name());
                }
                cloudNativeMapper.insertSelective(account);
                OperationLogService.log(SessionUtils.getUser(), account.getId(), account.getName(), ResourceTypeConstants.CLOUD_NATIVE.name(), ResourceOperation.CREATE, "i18n_create_cloud_native");
                return account;
            }
        } catch (Exception e) {
            HRException.throwException(e.getMessage());
        }
        return null;
    }

    public CloudNative editCloudNative(UpdateCloudNativeRequest request) throws Exception {
        try {
            //参数校验
            if (StringUtils.isEmpty(request.getCredential())
                    || StringUtils.isEmpty(request.getId())) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_id_or_plugin"));
            }

            //云账号名称不能重复
            CloudNativeExample cloudNativeExample = new CloudNativeExample();
            cloudNativeExample.createCriteria().andNameEqualTo(request.getName()).andIdNotEqualTo(request.getId());
            List<CloudNative> cloudNatives = cloudNativeMapper.selectByExampleWithBLOBs(cloudNativeExample);
            if (!CollectionUtils.isEmpty(cloudNatives)) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_name_duplicate"));
            }

            if (cloudNativeMapper.selectByPrimaryKey(request.getId()) == null) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_no_exist_id"));
            }

            CloudNative account = new CloudNative();
            //校验云插件是否存在
            Plugin plugin = pluginMapper.selectByPrimaryKey(request.getPluginId());
            if (plugin == null) {
                HRException.throwException(Translator.get("i18n_ex_cloud_native_no_exist_plugin"));
            } else {
                BeanUtils.copyBean(account, request);
                account.setPluginIcon(plugin.getIcon());
                account.setPluginName(plugin.getName());
                account.setUpdateTime(System.currentTimeMillis());
                //检验账号的有效性
                boolean valid = validateAccount(account);
                if (valid) {
                    account.setStatus(CloudAccountConstants.Status.VALID.name());
                    addCloudNativeSource(account);
                } else {
                    account.setStatus(CloudAccountConstants.Status.INVALID.name());
                }
                //检验operator
                boolean operatorStatusValidate = validateOperatorStatus(account);
                if (operatorStatusValidate) {
                    account.setOperatorStatus(CloudAccountConstants.Status.VALID.name());
                } else {
                    account.setOperatorStatus(CloudAccountConstants.Status.INVALID.name());
                }
                cloudNativeMapper.updateByPrimaryKeySelective(account);
                account = cloudNativeMapper.selectByPrimaryKey(account.getId());
                //检验账号已更新状态
                OperationLogService.log(SessionUtils.getUser(), account.getId(), account.getName(), ResourceTypeConstants.CLOUD_NATIVE.name(), ResourceOperation.UPDATE, "i18n_update_cloud_native");
                return account;
            }

        } catch (HRException | ClientException e) {
            HRException.throwException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    public void delete(String accountId) {
        CloudNative cloudNative = cloudNativeMapper.selectByPrimaryKey(accountId);
        cloudNativeMapper.deleteByPrimaryKey(accountId);
        OperationLogService.log(SessionUtils.getUser(), accountId, cloudNative.getName(), ResourceTypeConstants.CLOUD_NATIVE.name(), ResourceOperation.DELETE, "i18n_delete_cloud_native");
    }

    public void addCloudNativeSource(CloudNative cloudNative) throws IOException, ApiException {
        commonThreadPool.addTask(() -> {
            CloudNativeSourceSyncLog record = new CloudNativeSourceSyncLog();
            long i = 0;
            try {
                CloudNativeSourceExample example = new CloudNativeSourceExample();
                example.createCriteria().andCloudNativeIdEqualTo(cloudNative.getId());
                cloudNativeSourceMapper.deleteByExample(example);

                List<CloudNativeSource> list = new LinkedList<>();
                K8sRequest k8sRequest = new K8sRequest();
                k8sRequest.setCredential(cloudNative.getCredential());
                list.addAll(k8sRequest.getVersion(cloudNative));
                list.addAll(k8sRequest.getNameSpace(cloudNative));
                list.addAll(k8sRequest.getNode(cloudNative));
                list.addAll(k8sRequest.getPod(cloudNative));
                list.addAll(k8sRequest.getService(cloudNative));
                list.addAll(k8sRequest.getDeployment(cloudNative));
                list.addAll(k8sRequest.getDaemonSet(cloudNative));
                list.addAll(k8sRequest.getIngress(cloudNative));
                list.addAll(k8sRequest.getRole(cloudNative));
                list.addAll(k8sRequest.getSecret(cloudNative));
                list.addAll(k8sRequest.getConfigMap(cloudNative));
                list.addAll(k8sRequest.getStatefulSet(cloudNative));
                list.addAll(k8sRequest.getCronJob(cloudNative));
                list.addAll(k8sRequest.getJob(cloudNative));
                list.addAll(k8sRequest.getPv(cloudNative));
                list.addAll(k8sRequest.getPvc(cloudNative));
                list.addAll(k8sRequest.getLease(cloudNative));
                list.addAll(k8sRequest.getEndpointSlice(cloudNative));
                list.addAll(k8sRequest.getEvent(cloudNative));
                list.addAll(k8sRequest.getNetworkPolicy(cloudNative));
                for (CloudNativeSource cloudNativeSource : list) {
                    cloudNativeSourceMapper.insertSelective(cloudNativeSource);
                    i++;
                }
                record.setOutput("i18n_sync_k8s_success");
                record.setResult(true);
            } catch (IOException e) {
                LogUtil.error(e.getMessage());
                record.setOutput("i18n_sync_k8s_error:" + e.getMessage());
                record.setResult(false);
            } catch (ApiException e) {
                LogUtil.error(e.getMessage());
                record.setOutput("i18n_sync_k8s_error:" + e.getMessage());
                record.setResult(false);
            } catch (Exception e) {
                LogUtil.error(e.getMessage());
                record.setOutput("i18n_sync_k8s_error:" + e.getMessage());
                record.setResult(false);
            }
            record.setSum(i);
            record.setOperation("i18n_sync_k8s");
            record.setCloudNativeId(cloudNative.getId());
            record.setCreateTime(System.currentTimeMillis());
            record.setOperator(SessionUtils.getUser().getName());
            cloudNativeSourceSyncLogMapper.insertSelective(record);
        });
    }

    public List<CloudNativeSourceDTO> getCloudNativeSourceList(CloudNativeSourceRequest request) {
        return extCloudNativeSourceMapper.getCloudNativeSourceList(request);
    }

    public SituationDTO situationInfo(Map<String, Object> params) {
        return extCloudNativeSourceMapper.situationInfo(params);
    }

    public void scan(String id) throws Exception {
        CloudNative cloudNative = cloudNativeMapper.selectByPrimaryKey(id);
        Integer scanId = historyService.insertScanHistory(cloudNative);
        if(StringUtils.equalsIgnoreCase(cloudNative.getStatus(), CloudAccountConstants.Status.VALID.name())) {
            List<CloudNativeRule> ruleList = cloudNativeRuleMapper.selectByExample(null);
            CloudNativeResultWithBLOBs result = new CloudNativeResultWithBLOBs();

            deleteResultByCloudNativeId(id);
            for(CloudNativeRule rule : ruleList) {
                BeanUtils.copyBean(result, cloudNative);
                result.setId(UUIDUtil.newUUID());
                result.setCloudNativeId(id);
                result.setApplyUser(SessionUtils.getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
                result.setUserName(SessionUtils.getUser().getName());
                result.setRuleId(rule.getId());
                result.setRuleName(rule.getName());
                result.setRuleDesc(rule.getDescription());
                result.setSeverity(rule.getSeverity());
                cloudNativeResultMapper.insertSelective(result);

                saveCloudNativeResultLog(result.getId(), "i18n_start_k8s_result", "", true);
                OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.CLOUD_NATIVE.name(), ResourceOperation.CREATE, "i18n_start_k8s_result");

                historyService.insertScanTaskHistory(result, scanId, cloudNative.getId(), TaskEnum.k8sAccount.getType());

                historyService.insertHistoryCloudNativeResult(BeanUtils.copyBean(new HistoryCloudNativeResultWithBLOBs(), result));
            }
        }
    }

    public String reScan(String id) throws Exception {
        CloudNativeResultWithBLOBs result = cloudNativeResultMapper.selectByPrimaryKey(id);

        result.setUpdateTime(System.currentTimeMillis());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        result.setUserName(SessionUtils.getUser().getName());
        cloudNativeResultMapper.updateByPrimaryKeySelective(result);

        reScanDeleteCloudNativeResult(id);

        saveCloudNativeResultLog(result.getId(), "i18n_restart_k8s_result", "", true);

        OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.CLOUD_NATIVE.name(), ResourceOperation.CREATE, "i18n_restart_k8s_result");

        historyService.updateHistoryCloudNativeResult(BeanUtils.copyBean(new HistoryCloudNativeResultWithBLOBs(), result));

        return result.getId();
    }

    public void createScan (CloudNativeResultWithBLOBs result) throws Exception {
        try {
            CloudNative cloudNative = cloudNativeMapper.selectByPrimaryKey(result.getCloudNativeId());
            if (StringUtils.equalsIgnoreCase(PlatformUtils.k8s, cloudNative.getPluginId())) {
                K8sRequest k8sRequest = new K8sRequest();
                k8sRequest.setCredential(cloudNative.getCredential());
                String token = "Bearer " + k8sRequest.getToken();
                String url = k8sRequest.getUrl();
                if (url.endsWith("/")) {
                    url = url + CloudNativeConstants.URL2;
                } else {
                    url = url + CloudNativeConstants.URL1;
                }
                Map<String, String> param = new HashMap<>();
                param.put("Accept", CloudNativeConstants.Accept);
                param.put("Authorization", token);
                String reponse1 = HttpClientUtil.HttpGet(url, param);
                result.setConfigAuditReport(reponse1);
                String url2 = k8sRequest.getUrl();
                if (url2.endsWith("/")) {
                    url2 = url2 + CloudNativeConstants.URL4;
                } else {
                    url2 = url2 + CloudNativeConstants.URL3;
                }
                String reponse2 = HttpClientUtil.HttpGet(url2, param);
                result.setVulnerabilityReport(reponse2);
            } else if(StringUtils.equalsIgnoreCase(PlatformUtils.rancher, cloudNative.getPluginId())) {
                RancherRequest rancherRequest = new RancherRequest();
                rancherRequest.setCredential(cloudNative.getCredential());
                String token = "Bearer " + rancherRequest.getToken();
                String url = rancherRequest.getUrl();
                if (url.endsWith("/")) {
                    url = url + CloudNativeConstants.URL2;
                } else {
                    url = url + CloudNativeConstants.URL1;
                }
                Map<String, String> param = new HashMap<>();
                param.put("Accept", CloudNativeConstants.Accept);
                param.put("Authorization", token);
                String reponse1 = HttpClientUtil.HttpGet(url, param);
                result.setConfigAuditReport(reponse1);
                String url2 = rancherRequest.getUrl();
                if (url2.endsWith("/")) {
                    url2 = url2 + CloudNativeConstants.URL4;
                } else {
                    url2 = url2 + CloudNativeConstants.URL3;
                }
                String reponse2 = HttpClientUtil.HttpGet(url2, param);
                result.setVulnerabilityReport(reponse2);
            } else if(StringUtils.equalsIgnoreCase(PlatformUtils.openshift, cloudNative.getPluginId())) {

            } else if(StringUtils.equalsIgnoreCase(PlatformUtils.kubesphere, cloudNative.getPluginId())) {
                KubeSphereRequest kubeSphereRequest = new KubeSphereRequest();
                kubeSphereRequest.setCredential(cloudNative.getCredential());
                String token = "Bearer " + kubeSphereRequest.getToken();
                String url = kubeSphereRequest.getUrl();
                if (url.endsWith("/")) {
                    url = url + CloudNativeConstants.URL2;
                } else {
                    url = url + CloudNativeConstants.URL1;
                }
                Map<String, String> param = new HashMap<>();
                param.put("Accept", CloudNativeConstants.Accept);
                param.put("Authorization", token);
                String reponse1 = HttpClientUtil.HttpGet(url, param);
                result.setConfigAuditReport(reponse1);
                String url2 = kubeSphereRequest.getUrl();
                if (url2.endsWith("/")) {
                    url2 = url2 + CloudNativeConstants.URL4;
                } else {
                    url2 = url2 + CloudNativeConstants.URL3;
                }
                String reponse2 = HttpClientUtil.HttpGet(url2, param);
                result.setVulnerabilityReport(reponse2);
            }
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());

            long count = saveResultItem(result);
            result.setReturnSum(count);
            cloudNativeResultMapper.updateByPrimaryKeySelective(result);

            noticeService.createCloudNativeMessageOrder(result);
            saveCloudNativeResultLog(result.getId(), "i18n_end_k8s_result", "", true);

            historyService.updateHistoryCloudNativeResult(BeanUtils.copyBean(new HistoryCloudNativeResultWithBLOBs(), result));
        } catch (Exception e) {
            LogUtil.error("create K8sResult: " + e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            cloudNativeResultMapper.updateByPrimaryKeySelective(result);
            historyService.updateHistoryCloudNativeResult(BeanUtils.copyBean(new HistoryCloudNativeResultWithBLOBs(), result));
            saveCloudNativeResultLog(result.getId(), "i18n_operation_ex" + ": " + StringUtils.substring(e.getMessage(), 0, 900) + "...", e.getMessage(), false);
        }
    }

    public void deleteResultByCloudNativeId(String id) throws Exception {
        CloudNativeResultExample example = new CloudNativeResultExample();
        example.createCriteria().andCloudNativeIdEqualTo(id);
        List<CloudNativeResult> list = cloudNativeResultMapper.selectByExample(example);

        for (CloudNativeResult result : list) {
            CloudNativeResultItemExample cloudNativeResultItemExample = new CloudNativeResultItemExample();
            cloudNativeResultItemExample.createCriteria().andResultIdEqualTo(result.getId());
            cloudNativeResultItemMapper.deleteByExample(cloudNativeResultItemExample);
            CloudNativeResultLogExample logExample = new CloudNativeResultLogExample();
            logExample.createCriteria().andResultIdEqualTo(result.getId());
            cloudNativeResultLogMapper.deleteByExample(logExample);
        }
        cloudNativeResultMapper.deleteByExample(example);
    }

    public void reScanDeleteCloudNativeResult(String id) throws Exception {
        CloudNativeResultItemExample cloudNativeResultItemExample = new CloudNativeResultItemExample();
        cloudNativeResultItemExample.createCriteria().andResultIdEqualTo(id);
        cloudNativeResultItemMapper.deleteByExample(cloudNativeResultItemExample);
    }

    void saveCloudNativeResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        CloudNativeResultLog cloudNativeResultLog = new CloudNativeResultLog();
        String operator = "system";
        try {
            if (SessionUtils.getUser() != null) {
                operator = SessionUtils.getUser().getId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        cloudNativeResultLog.setOperator(operator);
        cloudNativeResultLog.setResultId(resultId);
        cloudNativeResultLog.setCreateTime(System.currentTimeMillis());
        cloudNativeResultLog.setOperation(operation);
        cloudNativeResultLog.setOutput(output);
        cloudNativeResultLog.setResult(result);
        cloudNativeResultLogMapper.insertSelective(cloudNativeResultLog);

        historyService.insertHistoryCloudNativeResultLog(BeanUtils.copyBean(new HistoryCloudNativeResultLog(), cloudNativeResultLog));
    }

    long saveResultItem(CloudNativeResultWithBLOBs result) throws Exception {

        String json = result.getVulnerabilityReport();
        JSONObject jsonObject1 = JSON.parseObject(json);
        JSONArray jsonArray1 = jsonObject1.getJSONArray("items");
        int i = 0;
        if(jsonArray1 != null) {
            for(Object object : jsonArray1) {
                JSONObject obj1 = (JSONObject) object;
                JSONObject jsonObject2 = obj1.getJSONObject("report");
                JSONArray jsonArray = jsonObject2.getJSONArray("vulnerabilities");
                for(Object object2 : jsonArray) {
                    JSONObject obj2 = (JSONObject) object2;
                    CloudNativeResultItem cloudNativeResultItem = new CloudNativeResultItem();
                    cloudNativeResultItem.setId(UUIDUtil.newUUID());
                    cloudNativeResultItem.setResultId(result.getId());
                    cloudNativeResultItem.setPrimaryLink(obj2.getString("primaryLink"));
                    cloudNativeResultItem.setScore(obj2.getString("score"));
                    cloudNativeResultItem.setSeverity(obj2.getString("severity"));
                    cloudNativeResultItem.setTarget(obj2.getString("target"));
                    cloudNativeResultItem.setTitle(obj2.getString("title"));
                    cloudNativeResultItem.setVulnerabilityId(obj2.getString("vulnerabilityID"));
                    cloudNativeResultItem.setInstalledVersion(obj2.getString("installedVersion"));
                    cloudNativeResultItem.setFixedVersion(obj2.getString("fixedVersion"));
                    cloudNativeResultItem.setLinks(obj2.getString("links"));
                    cloudNativeResultItem.setCreateTime(System.currentTimeMillis());
                    cloudNativeResultItemMapper.insertSelective(cloudNativeResultItem);

                    historyService.insertHistoryCloudNativeResultItem(BeanUtils.copyBean(new HistoryCloudNativeResultItem(), cloudNativeResultItem));
                    i++;
                }
            }
        }
        return i;
    }

    public List<CloudNativeResultDTO> resultList(K8sResultRequest request) {
        List<CloudNativeResultDTO> list = extCloudNativeResultMapper.resultList(request);
        return list;
    }

    public List<CloudNativeResultItem> resultItemList(K8sResultRequest resourceRequest) {
        CloudNativeResultItemExample example = new CloudNativeResultItemExample();
        if(resourceRequest.getName()!=null) {
            example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId()).andTitleLike(resourceRequest.getName());
        } else {
            example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId());
        }
        example.setOrderByClause("FIELD(`severity`, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'UNKNOWN')");
        return cloudNativeResultItemMapper.selectByExample(example);
    }

    public CloudNativeResultDTO getCloudNativeResult(String resultId) {
        CloudNativeResultDTO cloudNativeResult = extCloudNativeResultMapper.getCloudNativeResult(resultId);
        return cloudNativeResult;
    }

    public List<ImageDTO> imageList(ImageRequest request) {
        return extCloudNativeResultMapper.imageList(request);
    }

    public void deleteCloudNativeResult(String id) throws Exception {

        CloudNativeResultLogExample logExample = new CloudNativeResultLogExample();
        logExample.createCriteria().andResultIdEqualTo(id);
        cloudNativeResultLogMapper.deleteByExample(logExample);

        cloudNativeResultMapper.deleteByPrimaryKey(id);
    }

    public List<CloudNativeResultLog> getCloudNativeResultLog(String resultId) {
        CloudNativeResultLogExample example = new CloudNativeResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return cloudNativeResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public CloudNativeResultWithBLOBs getCloudNativeResultWithBLOBs(String resultId) {
        CloudNativeResultWithBLOBs cloudNativeResultWithBLOBs = cloudNativeResultMapper.selectByPrimaryKey(resultId);
        return cloudNativeResultWithBLOBs;
    }

    public List<CloudNativeSource> allCloudNativeSource2YamlList() {
        CloudNativeSourceExample example = new CloudNativeSourceExample();
        example.createCriteria().andSourceTypeNotEqualTo("Version").andSourceYamlIsNotNull();
        return cloudNativeSourceMapper.selectByExampleWithBLOBs(example);
    }

    public List<CloudNativeSourceSyncLog> syncList(CloudNativeSyncLogRequest request) {
        return extCloudNativeResultMapper.syncList(request);
    }

    public void syncSource(String id) throws Exception {
        CloudNative cloudNative = cloudNativeMapper.selectByPrimaryKey(id);
        addCloudNativeSource(cloudNative);
    }

    public void deleteSyncLog(Integer id) throws Exception {
        cloudNativeSourceSyncLogMapper.deleteByPrimaryKey(id);
    }

    public MetricChartDTO metricChart (String resultId) {
        return extCloudNativeResultMapper.metricChart(resultId);
    }

    public String download(Map<String, Object> map) {
        HistoryCloudNativeResultWithBLOBs historyCloudNativeResultWithBLOBs = historyCloudNativeResultMapper.selectByPrimaryKey(map.get("id").toString());
        String str = historyCloudNativeResultWithBLOBs.getVulnerabilityReport();
        return str;
    }

    public Map<String, Object> topInfo(Map<String, Object> params) {
        return extCloudNativeMapper.topInfo(params);
    }

    public List<Map<String, Object>> k8sChart() {
        return extCloudNativeMapper.k8sChart();
    }

    public List<Map<String, Object>> severityChart() {
        return extCloudNativeMapper.severityChart();
    }

    public List<CloudNative> allList() {
        return cloudNativeMapper.selectByExample(null);
    }

    public List<HistoryCloudNativeResultDTO> history(Map<String, Object> params) {
        List<HistoryCloudNativeResultDTO> historyList = extCloudNativeResultMapper.history(params);
        return historyList;
    }

    public List<HistoryCloudNativeResultItem> historyResultItemList(CloudNativeResultItem item) {
        HistoryCloudNativeResultItemExample example = new HistoryCloudNativeResultItemExample();
        example.createCriteria().andResultIdEqualTo(item.getResultId());
        example.setOrderByClause("FIELD(`severity`, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'UNKNOWN')");
        return historyCloudNativeResultItemMapper.selectByExampleWithBLOBs(example);
    }

    public void deleteHistoryK8sResult(String id) throws Exception {
        HistoryCloudNativeResultLogExample logExample = new HistoryCloudNativeResultLogExample();
        logExample.createCriteria().andResultIdEqualTo(id);
        historyCloudNativeResultLogMapper.deleteByExample(logExample);
        historyCloudNativeResultMapper.deleteByPrimaryKey(id);
    }

    public List<Map<String, Object>> k8sTopology(String cloudNativeId) {
        return extCloudNativeSourceMapper.k8sTopology(cloudNativeId);
    }

}
