package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtCloudNativeResultMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.k8s.K8sResultRequest;
import com.hummerrisk.proxy.k8s.K8sRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ImageMapper imageMapper;
    @Resource
    private ImageResultMapper imageResultMapper;
    @Resource
    private ImageTrivyJsonMapper imageTrivyJsonMapper;
    @Resource
    private ImageResultLogMapper imageResultLogMapper;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private ImageRepoMapper imageRepoMapper;
    @Resource
    private CloudNativeSourceMapper cloudNativeSourceMapper;

    public void scan(String id) throws Exception {
        CloudNative cloudNative = cloudNativeMapper.selectByPrimaryKey(id);
        Integer scanId = historyService.insertScanHistory(cloudNative);
        if(StringUtils.equalsIgnoreCase(cloudNative.getStatus(), CloudAccountConstants.Status.VALID.name())) {
            CloudNativeResultWithBLOBs result = new CloudNativeResultWithBLOBs();

            deleteResultByCloudNativeId(id);

            BeanUtils.copyBean(result, cloudNative);
            result.setId(UUIDUtil.newUUID());
            result.setCloudNativeId(id);
            result.setApplyUser(SessionUtils.getUserId());
            result.setCreateTime(System.currentTimeMillis());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
            result.setUserName(SessionUtils.getUser().getName());
            cloudNativeResultMapper.insertSelective(result);

            saveCloudNativeResultLog(result.getId(), "i18n_start_k8s_result", "", true);
            OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.CLOUD_NATIVE.name(), ResourceOperation.CREATE, "i18n_start_k8s_result");

            historyService.insertScanTaskHistory(result, scanId, cloudNative.getId(), TaskEnum.k8sAccount.getType());

            historyService.insertHistoryCloudNativeResult(BeanUtils.copyBean(new HistoryCloudNativeResultWithBLOBs(), result));
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

            } else if(StringUtils.equalsIgnoreCase(PlatformUtils.openshift, cloudNative.getPluginId())) {

            } else if(StringUtils.equalsIgnoreCase(PlatformUtils.kubesphere, cloudNative.getPluginId())) {

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
            saveCloudNativeResultLog(result.getId(), "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false);
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

    public List<CloudNativeResult> resultList(K8sResultRequest request) {
        List<CloudNativeResult> list = extCloudNativeResultMapper.resultList(request);
        return list;
    }

    public List<CloudNativeResultItem> resultItemList(K8sResultRequest resourceRequest) {
        CloudNativeResultItemExample example = new CloudNativeResultItemExample();
        if(resourceRequest.getName()!=null) {
            example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId()).andTitleLike(resourceRequest.getName());
        } else {
            example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId());
        }
        return cloudNativeResultItemMapper.selectByExample(example);
    }

    public CloudNativeResult getCloudNativeResult(String resultId) {
        CloudNativeResult cloudNativeResult = cloudNativeResultMapper.selectByPrimaryKey(resultId);
        return cloudNativeResult;
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

    public void imageScan(String id) throws Exception {
        Image image = imageMapper.selectByPrimaryKey(id);
        Integer scanId = historyService.insertScanHistory(image);
        if(StringUtils.equalsIgnoreCase(image.getStatus(), CloudAccountConstants.Status.VALID.name())) {
            ImageResultWithBLOBs result = new ImageResultWithBLOBs();

            deleteResultByImageId(id);

            BeanUtils.copyBean(result, image);
            result.setId(UUIDUtil.newUUID());
            result.setImageId(id);
            result.setApplyUser(SessionUtils.getUserId());
            result.setCreateTime(System.currentTimeMillis());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
            result.setUserName(SessionUtils.getUser().getName());
            result.setScanType(CloudTaskConstants.IMAGE_TYPE.trivy.name());
            imageResultMapper.insertSelective(result);

            saveImageResultLog(result.getId(), "i18n_start_image_result", "", true);
            OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.SCAN, "i18n_start_image_result");

            historyService.insertScanTaskHistory(result, scanId, image.getId(), TaskEnum.imageAccount.getType());

            historyService.insertHistoryImageTask(BeanUtils.copyBean(new HistoryImageTaskWithBLOBs(), result));
        }
    }

    public String imageReScan(String id) throws Exception {
        ImageResultWithBLOBs result = imageResultMapper.selectByPrimaryKey(id);

        result.setUpdateTime(System.currentTimeMillis());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        result.setUserName(SessionUtils.getUser().getName());
        result.setScanType(CloudTaskConstants.IMAGE_TYPE.trivy.name());
        imageResultMapper.updateByPrimaryKeySelective(result);

        reScanDeleteImageResult(id);

        saveImageResultLog(result.getId(), "i18n_restart_image_result", "", true);

        OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.RESCAN, "i18n_restart_image_result");

        historyService.updateHistoryImageTask(BeanUtils.copyBean(new HistoryImageTaskWithBLOBs(), result));

        return result.getId();
    }

    public void deleteResultByImageId(String id) throws Exception {
        ImageResultExample example = new ImageResultExample();
        example.createCriteria().andImageIdEqualTo(id).andScanTypeEqualTo(CloudTaskConstants.IMAGE_TYPE.trivy.name());
        List<ImageResult> list = imageResultMapper.selectByExample(example);

        for (ImageResult result : list) {
            ImageResultLogExample logExample = new ImageResultLogExample();
            logExample.createCriteria().andResultIdEqualTo(result.getId());
            imageResultLogMapper.deleteByExample(logExample);

        }
        imageResultMapper.deleteByExample(example);
    }

    void saveImageResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        ImageResultLog imageResultLog = new ImageResultLog();
        String operator = "system";
        try {
            if (SessionUtils.getUser() != null) {
                operator = SessionUtils.getUser().getId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        imageResultLog.setOperator(operator);
        imageResultLog.setResultId(resultId);
        imageResultLog.setCreateTime(System.currentTimeMillis());
        imageResultLog.setOperation(operation);
        imageResultLog.setOutput(output);
        imageResultLog.setResult(result);
        imageResultLogMapper.insertSelective(imageResultLog);

        historyService.insertHistoryImageTaskLog(BeanUtils.copyBean(new HistoryImageTaskLog(), imageResultLog));
    }

    public void reScanDeleteImageResult(String id) throws Exception {

        ImageTrivyJsonExample imageTrivyJsonExample = new ImageTrivyJsonExample();
        imageTrivyJsonExample.createCriteria().andResultIdEqualTo(id);
        imageTrivyJsonMapper.deleteByExample(imageTrivyJsonExample);

    }

    public void createImageScan (ImageResultWithBLOBs result) throws Exception {
        try {
            Image image = imageMapper.selectByPrimaryKey(result.getImageId());
            String trivyJson = "";

            String log = execute(image);
            if (log.contains("docker login")) {
                throw new Exception(log);
            }
            trivyJson = ReadFileUtils.readToBuffer(TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON);

            result.setReturnLog(log);
            result.setTrivyJson(trivyJson);
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());

            long count = saveImageResultItem(result);
            result.setReturnSum(count);
            imageResultMapper.updateByPrimaryKeySelective(result);

            noticeService.createImageMessageOrder(result);
            saveImageResultLog(result.getId(), "i18n_end_image_result", "", true);

            historyService.updateHistoryImageTask(BeanUtils.copyBean(new HistoryImageTaskWithBLOBs(), result));
        } catch (Exception e) {
            LogUtil.error("create ImageResult: " + e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            imageResultMapper.updateByPrimaryKeySelective(result);
            historyService.updateHistoryImageTask(BeanUtils.copyBean(new HistoryImageTaskWithBLOBs(), result));
            saveImageResultLog(result.getId(), "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false);
        }
    }

    public String execute(Image image) throws Exception {
        try {
            Proxy proxy;
            String _proxy = "";
            String dockerLogin = "";
            if(image.getIsProxy()!=null && image.getIsProxy()) {
                proxy = proxyMapper.selectByPrimaryKey(image.getProxyId());
                String proxyType = proxy.getProxyType();
                String proxyIp = proxy.getProxyIp();
                String proxyPort = proxy.getProxyPort();
                String proxyName = proxy.getProxyName();
                String proxyPassword = proxy.getProxyPassword();
                if (StringUtils.isNotEmpty(proxyType)) {
                    if (StringUtils.equalsIgnoreCase(proxyType, CloudAccountConstants.ProxyType.Http.toString())) {
                        if (StringUtils.isNotEmpty(proxyName)) {
                            _proxy = "export http_proxy=http://" + proxyIp + ":" + proxyPassword + "@" + proxyIp + ":" + proxyPort + ";" + "\n";
                        } else {
                            _proxy = "export http_proxy=http://" + proxyIp + ":" + proxyPort + ";" + "\n";
                        }
                    } else if (StringUtils.equalsIgnoreCase(proxyType, CloudAccountConstants.ProxyType.Https.toString())) {
                        if (StringUtils.isNotEmpty(proxyName)) {
                            _proxy = "export https_proxy=http://" + proxyIp + ":" + proxyPassword + "@" + proxyIp + ":" + proxyPort + ";" + "\n";
                        } else {
                            _proxy = "export https_proxy=http://" + proxyIp + ":" + proxyPort + ";" + "\n";
                        }
                    }
                } else {
                    _proxy = "unset http_proxy;" + "\n" +
                            "unset https_proxy;" + "\n";
                }
            }
            if(image.getRepoId()!=null) {
                ImageRepo imageRepo = imageRepoMapper.selectByPrimaryKey(image.getRepoId());
                dockerLogin = "docker login " + imageRepo.getRepo() + " " + "-u " + imageRepo.getUserName() + " -p " + imageRepo.getPassword() + "\n";
            }
            String fileName = "";
            if (StringUtils.equalsIgnoreCase("image", image.getType())) {
                fileName = image.getImageUrl() + ":" + image.getImageTag();
            } else {
                fileName = TrivyConstants.DEFAULT_BASE_DIR + image.getPath();
            }
            String command = _proxy + dockerLogin + TrivyConstants.TRIVY_IMAGE + fileName + TrivyConstants.TRIVY_TYPE + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON + TrivyConstants.TRIVY_SKIP;
            LogUtil.info(image.getId() + " {k8sImage}[command]: " + image.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, TrivyConstants.DEFAULT_BASE_DIR);
            if(resultStr.contains("ERROR") || resultStr.contains("error")) {
                throw new Exception(resultStr);
            }
            return resultStr;
        } catch (Exception e) {
            return "";
        }
    }

    long saveImageResultItem(ImageResultWithBLOBs result) throws Exception {

        //插入trivyJsons
        JSONObject jsonG = JSONObject.parseObject(result.getTrivyJson());
        JSONArray trivyJsons = JSONArray.parseArray(jsonG.getString("Results"));
        int i = 0;
        if(trivyJsons != null) {
            for (Object obj : trivyJsons) {
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray vulnJsons = JSONArray.parseArray(jsonObject.getString("Vulnerabilities"));
                for (Object o : vulnJsons) {
                    JSONObject resultObject = (JSONObject) o;
                    ImageTrivyJsonWithBLOBs imageTrivyJsonWithBLOBs = new ImageTrivyJsonWithBLOBs();
                    imageTrivyJsonWithBLOBs.setResultId(result.getId());
                    imageTrivyJsonWithBLOBs.setVulnerabilityId(resultObject.getString("VulnerabilityID"));
                    imageTrivyJsonWithBLOBs.setPkgName(resultObject.getString("PkgName"));
                    imageTrivyJsonWithBLOBs.setInstalledVersion(resultObject.getString("InstalledVersion"));
                    imageTrivyJsonWithBLOBs.setFixedVersion(resultObject.getString("FixedVersion"));
                    imageTrivyJsonWithBLOBs.setLayer(resultObject.getString("Layer"));
                    imageTrivyJsonWithBLOBs.setSeveritySource(resultObject.getString("SeveritySource"));
                    imageTrivyJsonWithBLOBs.setPrimaryUrl(resultObject.getString("PrimaryURL"));
                    imageTrivyJsonWithBLOBs.setDataSource(resultObject.getString("DataSource"));
                    imageTrivyJsonWithBLOBs.setTitle(resultObject.getString("Title"));
                    imageTrivyJsonWithBLOBs.setDescription(resultObject.getString("Description"));
                    imageTrivyJsonWithBLOBs.setSeverity(resultObject.getString("Severity"));
                    imageTrivyJsonWithBLOBs.setCweIds(resultObject.getString("CweIDs"));
                    imageTrivyJsonWithBLOBs.setCvss(resultObject.getString("CVSS"));
                    imageTrivyJsonWithBLOBs.setReferences(resultObject.getString("References"));
                    imageTrivyJsonWithBLOBs.setPublishedDate(resultObject.getString("PublishedDate"));
                    imageTrivyJsonWithBLOBs.setLastModifiedDate(resultObject.getString("LastModifiedDate"));
                    imageTrivyJsonMapper.insertSelective(imageTrivyJsonWithBLOBs);
                    i++;
                }

            }
        }

        return i;
    }

    public List<ImageTrivyJsonWithBLOBs> k8sImageResultItemList(ImageTrivyJson resourceRequest) {
        ImageTrivyJsonExample example = new ImageTrivyJsonExample();
        if(resourceRequest.getPkgName()!=null) {
            example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId()).andPkgNameLike(resourceRequest.getPkgName());
        } else {
            example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId());
        }
        return imageTrivyJsonMapper.selectByExampleWithBLOBs(example);
    }

    public ImageResultWithBLOBs getImageResult(String resultId) {
        return imageResultMapper.selectByPrimaryKey(resultId);
    }

    public List<CloudNativeSource> allCloudNativeSource2YamlList() {
        CloudNativeSourceExample example = new CloudNativeSourceExample();
        example.createCriteria().andSourceTypeNotEqualTo("Version").andSourceYamlIsNotNull();
        return cloudNativeSourceMapper.selectByExampleWithBLOBs(example);
    }
}
