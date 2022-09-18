package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtImageMapper;
import com.hummerrisk.base.mapper.ext.ExtImageRepoMapper;
import com.hummerrisk.base.mapper.ext.ExtImageResultMapper;
import com.hummerrisk.base.mapper.ext.ExtImageRuleMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.image.*;
import com.hummerrisk.dto.*;
import com.hummerrisk.service.impl.ExecEngineFactoryImp;
import com.hummerrisk.service.impl.IProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ImageService {

    @Resource
    private ExtImageRepoMapper extImageRepoMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private ImageRepoMapper imageRepoMapper;
    @Resource
    private ExtImageMapper extImageMapper;
    @Resource
    private ExtImageRuleMapper extImageRuleMapper;
    @Resource
    private ImageRuleMapper imageRuleMapper;
    @Resource
    private RuleTagMappingMapper ruleTagMappingMapper;
    @Resource
    private RuleTagMapper ruleTagMapper;
    @Resource
    private ExtImageResultMapper extImageResultMapper;
    @Resource
    private AccountService accountService;
    @Resource
    private ImageResultLogMapper imageResultLogMapper;
    @Resource
    private CommonThreadPool commonThreadPool;
    @Resource
    private ImageResultMapper imageResultMapper;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private NoticeService noticeService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ImageRepoItemMapper imageRepoItemMapper;
    @Resource
    private ImageRepoSyncLogMapper imageRepoSyncLogMapper;
    @Resource
    private ImageTrivyJsonMapper imageTrivyJsonMapper;
    @Resource
    private ExecEngineFactoryImp execEngineFactoryImp;

    public List<ImageRepo> imageRepoList(ImageRepoRequest request) {
        return extImageRepoMapper.imageRepoList(request);
    }

    public List<ImageRepo> allImageRepos() {
        ImageRepoExample example = new ImageRepoExample();
        example.setOrderByClause("update_time desc");
        return imageRepoMapper.selectByExample(example);
    }

    public List<ImageRepoItem> repoItemList(String id) {
        ImageRepoItemExample example = new ImageRepoItemExample();
        example.createCriteria().andRepoIdEqualTo(id);
        List<ImageRepoItem> repoItemList = imageRepoItemMapper.selectByExample(example);
        return repoItemList;
    }

    public ImageRepo addImageRepo(ImageRepo imageRepo) throws Exception {
        String id = UUIDUtil.newUUID();
        imageRepo.setId(id);
        imageRepo.setCreator(SessionUtils.getUserId());
        imageRepo.setCreateTime(System.currentTimeMillis());
        imageRepo.setUpdateTime(System.currentTimeMillis());

        IProvider cp = execEngineFactoryImp.getProvider("imageProvider");
        String resultStr = (String) execEngineFactoryImp.executeMethod(cp, "dockerLogin", imageRepo);
        if(resultStr.contains("Succeeded")) {
            imageRepo.setStatus("VALID");
        } else {
            imageRepo.setStatus("INVALID");
        }

        OperationLogService.log(SessionUtils.getUser(), imageRepo.getId(), imageRepo.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_create_image_repo");
        imageRepoMapper.insertSelective(imageRepo);

        commonThreadPool.addTask(() -> {
            try {
                syncImages(imageRepo);
            } catch (Exception e) {
                LogUtil.error(e);
            } finally {
            }
        });
        return imageRepo;
    }

    /**
     * @return 同步是否成功
     * @throws Exception
     */
    public boolean syncImages(ImageRepo imageRepo) throws Exception {
        long i = 0;
        ImageRepoSyncLog imageRepoSyncLog = new ImageRepoSyncLog();
        try{
            ImageRepoItemExample example = new ImageRepoItemExample();
            example.createCriteria().andRepoIdEqualTo(imageRepo.getId());
            imageRepoItemMapper.deleteByExample(example);
            if (StringUtils.equalsIgnoreCase(imageRepo.getPluginIcon(), "harbor.png")) {
                //* @param path harbor 地址
                // * @param username harbor 用户名
                //* @param password harbor 密码
                String path = imageRepo.getRepo();
                if(path.endsWith("/")){
                    path = path.substring(0,path.length()-1);
                }
                Map<String,String> header = new HashMap<>();
                header.put("Accept", CloudNativeConstants.Accept);
                header.put("Authorization","Basic "+ Base64.getUrlEncoder().encodeToString((imageRepo.getUserName() + ":" + imageRepo.getPassword()).getBytes()));
                String projectStr = HttpClientUtil.HttpGet(path+"/api/v2.0/projects/",header);
                JSONArray projects =  JSON.parseArray(projectStr);
                for (Object o : projects) {
                    JSONObject project = (JSONObject) o;
                    String projectName = project.getString("name");
                    String repositoriesStr = HttpClientUtil.HttpGet(path + "/api/v2.0/projects/" + projectName + "/repositories", header);
                    JSONArray repositories = JSON.parseArray(repositoriesStr);
                    for (Object repository : repositories) {
                        JSONObject rep = (JSONObject) repository;
                        String repName = rep.getString("name");
                        if (repName.indexOf("/") > 0) {
                            repName = repName.split("/", -1)[1];
                        }
                        String artifactsStr = HttpClientUtil.HttpGet(path + "/api/v2.0/projects/" + projectName + "/repositories/" + repName + "/artifacts", header);
                        JSONArray artifacts = JSON.parseArray(artifactsStr);
                        for (Object artifact : artifacts) {
                            JSONObject arti = (JSONObject) artifact;
                            String digest = arti.getString("digest");
                            String push_time = arti.getString("push_time");
                            long size = arti.getLong("size");
                            JSONObject extra_attrs = arti.getJSONObject("extra_attrs");
                            String architecture = extra_attrs.getString("architecture");
                            JSONArray tags = arti.getJSONArray("tags");
                            List<JSONObject> tagList = tags.toJavaList(JSONObject.class);
                            for (JSONObject tag : tagList) {
                                String tagStr = tag.getString("name");
                                ImageRepoItem imageRepoItem = new ImageRepoItem();
                                imageRepoItem.setId(UUIDUtil.newUUID());
                                imageRepoItem.setProject(projectName);
                                imageRepoItem.setRepository(repName);
                                imageRepoItem.setTag(tagStr);
                                imageRepoItem.setDigest(digest);
                                imageRepoItem.setRepoId(imageRepo.getId());
                                imageRepoItem.setPushTime(push_time);
                                imageRepoItem.setArch(architecture);
                                imageRepoItem.setSize(changeFlowFormat(size));
                                imageRepoItem.setPath(path.replaceAll("https://", "").replaceAll("http://", "") + "/" + projectName + "/" + repName + ":" + tagStr);
                                imageRepoItemMapper.insertSelective(imageRepoItem);
                                i++;
                            }
                        }
                    }
                }
            } else if (StringUtils.equalsIgnoreCase(imageRepo.getPluginIcon(), "dockerhub.png")) {
                String loginUrl = "https://hub.docker.com/v2/users/login";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username",imageRepo.getUserName());
                jsonObject.put("password",imageRepo.getPassword());
                HttpHeaders httpHeaders = new HttpHeaders();
                // 设置请求类型
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                // 封装参数和头信息
                ResponseEntity<JSONObject> tokenResult = RestTemplateUtils.postForEntity(loginUrl,jsonObject,httpHeaders, JSONObject.class);
                if(tokenResult == null){
                    throw new RuntimeException("dockerhub认证失败");
                }
                String token = Objects.requireNonNull(tokenResult.getBody()).getString("token");
                httpHeaders.add("Authorization","Bearer "+token);
                ResponseEntity<JSONObject> repositoriesResult = RestTemplateUtils.getForEntity("https://hub.docker.com/v2/namespaces/" + imageRepo.getUserName()+ "/repositories/", httpHeaders, JSONObject.class);
                List<JSONObject> repositories = repositoriesResult.getBody().getJSONArray("results").toJavaList(JSONObject.class);
                for(JSONObject repository:repositories){
                    String repositoryName = repository.getString("name");
                    String namespace = repository.getString("namespace");
                    ResponseEntity<JSONObject> tagsResponse = RestTemplateUtils.getForEntity("https://hub.docker.com/v2/namespaces/"+namespace+"/repositories/"+repositoryName+"/tags", httpHeaders, JSONObject.class);
                    List<JSONObject> tags = tagsResponse.getBody().getJSONArray("results").toJavaList(JSONObject.class);
                    for(JSONObject tag:tags){
                        JSONArray images = tag.getJSONArray("images");
                        String tagStr = tag.getString("name");
                        ImageRepoItem imageRepoItem = new ImageRepoItem();
                        imageRepoItem.setId(UUIDUtil.newUUID());
                        imageRepoItem.setProject(namespace);
                        imageRepoItem.setRepository(repositoryName);
                        imageRepoItem.setTag(tagStr);
                        if(images.size()>0){
                            imageRepoItem.setDigest(images.getJSONObject(0).getString("digest"));
                            imageRepoItem.setPushTime(images.getJSONObject(0).getString("last_pushed"));
                            imageRepoItem.setArch(images.getJSONObject(0).getString("architecture"));
                            imageRepoItem.setSize(changeFlowFormat(images.getJSONObject(0).getLong("size")));
                        }
                        imageRepoItem.setRepoId(imageRepo.getId());

                        imageRepoItem.setPath("hub.docker.com" + "/" + namespace + "/" + repositoryName + ":" + tagStr);
                        imageRepoItemMapper.insertSelective(imageRepoItem);
                        i++;
                    }
                }

            } else if (StringUtils.equalsIgnoreCase(imageRepo.getPluginIcon(), "nexus.png")) {
                String url = imageRepo.getRepo();
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length()-1);
                }
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Authorization","Basic "+ Base64.getUrlEncoder().encodeToString((imageRepo.getUserName() + ":" + imageRepo.getPassword()).getBytes()));
                ResponseEntity<JSONObject> repositoriesResponse = RestTemplateUtils.getForEntity(url+"/v2/_catalog", httpHeaders, JSONObject.class);
                List<String> repositories = repositoriesResponse.getBody().getJSONArray("repositories").toJavaList(String.class);
                for(String repository : repositories){
                    ResponseEntity<JSONObject> tagsResponse = RestTemplateUtils.getForEntity(url + "/v2/" + repository + "/tags/list", httpHeaders, JSONObject.class);
                    List<String> tags = tagsResponse.getBody().getJSONArray("tags").toJavaList(String.class);
                    String name = tagsResponse.getBody().getString("name");
                    for(String tag:tags){
                        String path = url.replaceAll("https://","").replaceAll("http://","") + "/"+name+":"+tag;
                        ImageRepoItem imageRepoItem = new ImageRepoItem();
                        imageRepoItem.setId(UUIDUtil.newUUID());
                        imageRepoItem.setRepository(name);
                        imageRepoItem.setTag(tag);
                        imageRepoItem.setRepoId(imageRepo.getId());
                        imageRepoItem.setPath(path);
                        imageRepoItemMapper.insertSelective(imageRepoItem);
                        i++;
                    }
                }
            } else if (StringUtils.equalsIgnoreCase(imageRepo.getPluginIcon(), "other.png")) {

            }
            imageRepoSyncLog.setRepoId(imageRepo.getId());
            imageRepoSyncLog.setCreateTime(System.currentTimeMillis());
            imageRepoSyncLog.setOperator(SessionUtils.getUser().getName());
            imageRepoSyncLog.setOperation("i18n_sync_image");
            imageRepoSyncLog.setOutput("i18n_sync_image_success");
            imageRepoSyncLog.setResult(true);
            imageRepoSyncLog.setSum(i);
            imageRepoSyncLogMapper.insertSelective(imageRepoSyncLog);
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            imageRepoSyncLog.setRepoId(imageRepo.getId());
            imageRepoSyncLog.setCreateTime(System.currentTimeMillis());
            imageRepoSyncLog.setOperator(SessionUtils.getUser().getName());
            imageRepoSyncLog.setOperation("i18n_sync_image");
            imageRepoSyncLog.setOutput("i18n_sync_image_error: " + e.getMessage());
            imageRepoSyncLog.setResult(false);
            imageRepoSyncLog.setSum(i);
            imageRepoSyncLogMapper.insertSelective(imageRepoSyncLog);
            return false;
        }

        return true;
    }

    public ImageRepo editImageRepo(ImageRepo imageRepo) throws Exception {
        imageRepo.setUpdateTime(System.currentTimeMillis());

        IProvider cp = execEngineFactoryImp.getProvider("imageProvider");
        String resultStr = (String) execEngineFactoryImp.executeMethod(cp, "dockerLogin", imageRepo);

        if(resultStr.contains("Succeeded")) {
            imageRepo.setStatus("VALID");
        } else {
            imageRepo.setStatus("INVALID");
        }

        OperationLogService.log(SessionUtils.getUser(), imageRepo.getId(), imageRepo.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.UPDATE, "i18n_update_image_repo");
        imageRepoMapper.updateByPrimaryKeySelective(imageRepo);

        commonThreadPool.addTask(() -> {
            try {
                syncImages(imageRepo);
            } catch (Exception e) {
                LogUtil.error(e);
            } finally {
            }
        });
        return imageRepo;
    }

    public void deleteImageRepo(String id) throws Exception {
        imageRepoMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.IMAGE.name(), ResourceOperation.DELETE, "i18n_delete_image_repo");
    }

    public List<ImageDTO> imageList(ImageRequest request) {
        return extImageMapper.imageList(request);
    }

    public List<Image> allBindList(String sbomVersionId) {
        ImageExample example = new ImageExample();
        example.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        return imageMapper.selectByExample(example);
    }

    public List<Image> unBindList() {
        return imageMapper.selectByExample(null);
    }

    public Image addImage(MultipartFile iconFile, MultipartFile tarFile, Image request) throws Exception {

        try {
            String id = UUIDUtil.newUUID();
            request.setId(id);
            request.setStatus("VALID");
            request.setCreateTime(System.currentTimeMillis());
            request.setUpdateTime(System.currentTimeMillis());
            request.setCreator(SessionUtils.getUserId());
            if (iconFile != null) {
                String iconFilePath = upload(iconFile, ImageConstants.DEFAULT_BASE_DIR);
                request.setPluginIcon("images/" + iconFilePath);
            }
            if (StringUtils.equalsIgnoreCase(request.getType(), "repo")) {
                ImageRepoItem imageRepoItem = imageRepoItemMapper.selectByPrimaryKey(request.getImageUrl());
                request.setImageUrl(imageRepoItem.getPath().split(":")[0]);
                request.setImageTag(imageRepoItem.getTag());
                request.setSize(imageRepoItem.getSize());
            }
            if (tarFile != null) {
                String tarFilePath = upload(tarFile, ImageConstants.DEFAULT_BASE_DIR);
                request.setPath(tarFilePath);
                request.setSize(changeFlowFormat(tarFile.getSize()));
            }

            imageMapper.insertSelective(request);

            OperationLogService.log(SessionUtils.getUser(), request.getId(), request.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_create_image");

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return request;
    }

    public Image updateImage(MultipartFile iconFile, MultipartFile tarFile, Image request) throws Exception {

        try {
            request.setStatus("VALID");
            request.setUpdateTime(System.currentTimeMillis());
            request.setCreator(SessionUtils.getUserId());
            if (iconFile != null) {
                String iconFilePath = upload(iconFile, ImageConstants.DEFAULT_BASE_DIR);
                request.setPluginIcon("images/" + iconFilePath);
            }
            if (StringUtils.equalsIgnoreCase(request.getType(), "repo")) {
                ImageRepoItem imageRepoItem = imageRepoItemMapper.selectByPrimaryKey(request.getImageUrl());
                request.setImageUrl(imageRepoItem.getPath().split(":")[0]);
                request.setImageTag(imageRepoItem.getTag());
                request.setSize(imageRepoItem.getSize());
            }
            if (tarFile != null) {
                String tarFilePath = upload(tarFile, ImageConstants.DEFAULT_BASE_DIR);
                request.setPath(tarFilePath);
            }
            if(!request.getIsImageRepo()) {
                request.setRepoId("");
            }

            imageMapper.updateByPrimaryKeySelective(request);

            OperationLogService.log(SessionUtils.getUser(), request.getId(), request.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.UPDATE, "i18n_update_image");

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return request;
    }

    public void deleteImage(String id) throws Exception {
        imageMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.IMAGE.name(), ResourceOperation.DELETE, "i18n_delete_image");
    }

    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件名称
     * @throws Exception
     */
    public static final String upload(MultipartFile file, String dir) throws IOException
    {
        try
        {
            String fileName = file.getOriginalFilename();
            String extension = StringUtils.isNotBlank(fileName)?fileName.split("\\.")[fileName.split("\\.").length-1]:"";
            //png、html等小文件存放路径，页面需要显示，项目内目录
            //jar包等大文件存放路径，项目外目录
            return FileUploadUtils.upload(dir, file, "." + extension);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    public List<ImageRuleDTO> ruleList(ImageRuleRequest request) {
        return extImageRuleMapper.ruleList(request);
    }

    public int addImageRule(ImageRuleRequest request) throws Exception {
        ImageRule record = new ImageRule();
        BeanUtils.copyBean(record, request);
        record.setId(UUIDUtil.newUUID());
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        OperationLogService.log(SessionUtils.getUser(), record.getId(), record.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_create_image_rule");
        return imageRuleMapper.insertSelective(record);
    }

    public void saveRuleTagMapping(String ruleId, String tagKey) {
        deleteRuleTag(null, ruleId);
        if (StringUtils.isNotEmpty(tagKey)) {
            RuleTagMapping sfRulesTagMapping = new RuleTagMapping();
            sfRulesTagMapping.setRuleId(ruleId);
            sfRulesTagMapping.setTagKey(tagKey);
            ruleTagMappingMapper.insert(sfRulesTagMapping);
        }
    }

    public void deleteRuleTag(String tagkey, String ruleId) {
        if (StringUtils.isNotBlank(tagkey)) {
            ruleTagMapper.deleteByPrimaryKey(tagkey);
            RuleTagExample ruleTagExample = new RuleTagExample();
            ruleTagExample.createCriteria().andTagKeyEqualTo(tagkey);
            ruleTagMapper.deleteByExample(ruleTagExample);
        }
        if (StringUtils.isNotBlank(ruleId)) {
            RuleTagMappingExample ruleTagMappingExample = new RuleTagMappingExample();
            ruleTagMappingExample.createCriteria().andRuleIdEqualTo(ruleId);
            ruleTagMappingMapper.deleteByExample(ruleTagMappingExample);
        }
    }

    public int updateImageRule(ImageRuleRequest request) throws Exception {
        ImageRule record = new ImageRule();
        BeanUtils.copyBean(record, request);
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        OperationLogService.log(SessionUtils.getUser(), record.getId(), record.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.UPDATE, "i18n_update_image_rule");
        return imageRuleMapper.updateByPrimaryKeySelective(record);
    }

    public void deleteImageRule(String id) throws Exception {
        deleteRuleTag(null, id);
        imageRuleMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.IMAGE.name(), ResourceOperation.DELETE, "i18n_delete_image_rule");
    }

    public int changeStatus(ImageRule rule) throws Exception {
        return imageRuleMapper.updateByPrimaryKeySelective(rule);
    }

    public void scan(String id) throws Exception{
        Image image = imageMapper.selectByPrimaryKey(id);
        Integer scanId = historyService.insertScanHistory(image);
        if(StringUtils.equalsIgnoreCase(image.getStatus(), CloudAccountConstants.Status.VALID.name())) {
            List<ImageRuleDTO> ruleList = ruleList(null);
            ImageResultWithBLOBs result = new ImageResultWithBLOBs();

            deleteResultByImageId(id);
            for(ImageRuleDTO dto : ruleList) {
                BeanUtils.copyBean(result, image);
                result.setId(UUIDUtil.newUUID());
                result.setImageId(id);
                result.setApplyUser(SessionUtils.getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setRuleId(dto.getId());
                result.setRuleName(dto.getName());
                result.setRuleDesc(dto.getDescription());
                result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(dto.getSeverity());
                result.setUserName(SessionUtils.getUser().getName());
                imageResultMapper.insertSelective(result);

                saveImageResultLog(result.getId(), "i18n_start_image_result", "", true);
                OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.SCAN, "i18n_start_image_result");

                historyService.insertScanTaskHistory(result, scanId, image.getId(), TaskEnum.imageAccount.getType());

                historyService.insertHistoryImageTask(BeanUtils.copyBean(new HistoryImageTaskWithBLOBs(), result));
            }
        }

    }

    public void createScan (ImageResultWithBLOBs result) throws Exception {
        try {
            ImageRuleRequest request = new ImageRuleRequest();
            request.setId(result.getRuleId());
            ImageRuleDTO dto = ruleList(request).get(0);
            Image image = imageMapper.selectByPrimaryKey(result.getImageId());
            String trivyJson = "";

            String log = execute(image);
            if (log.contains("docker login")) {
                throw new Exception(log);
            }
            trivyJson = ReadFileUtils.readToBuffer(TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON);

            result.setReturnLog(log);
            result.setTrivyJson(trivyJson);
            result.setRuleId(dto.getId());
            result.setRuleName(dto.getName());
            result.setRuleDesc(dto.getDescription());
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
            saveImageResultLog(result.getId(), "i18n_operation_ex" + ": " + StringUtils.substring(e.getMessage(), 0, 900) + "...", e.getMessage(), false);
        }
    }

    long saveImageResultItem(ImageResultWithBLOBs result) throws Exception {

        int i = 0;
        //插入trivyJsons
        JSONObject jsonG = JSONObject.parseObject(result.getTrivyJson());
        if(jsonG != null) {
            JSONArray trivyJsons = JSONArray.parseArray(jsonG.getString("Results"));
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
        }
        return i;
    }

    public String reScan(String id) throws Exception {
        ImageResultWithBLOBs result = imageResultMapper.selectByPrimaryKey(id);

        result.setUpdateTime(System.currentTimeMillis());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        result.setUserName(SessionUtils.getUser().getName());
        imageResultMapper.updateByPrimaryKeySelective(result);

        this.reScanDeleteImageResult(id);

        saveImageResultLog(result.getId(), "i18n_restart_image_result", "", true);

        OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.RESCAN, "i18n_restart_image_result");

        historyService.updateHistoryImageTask(BeanUtils.copyBean(new HistoryImageTaskWithBLOBs(), result));

        return result.getId();
    }

    public void reScanDeleteImageResult(String id) throws Exception {

        ImageTrivyJsonExample imageTrivyJsonExample = new ImageTrivyJsonExample();
        imageTrivyJsonExample.createCriteria().andResultIdEqualTo(id);
        imageTrivyJsonMapper.deleteByExample(imageTrivyJsonExample);

    }

    public void deleteImageResult(String id) throws Exception {

        ImageResultLogExample logExample = new ImageResultLogExample();
        logExample.createCriteria().andResultIdEqualTo(id);
        imageResultLogMapper.deleteByExample(logExample);

        imageResultMapper.deleteByPrimaryKey(id);
    }

    public void deleteResultByImageId(String id) throws Exception {
        ImageResultExample example = new ImageResultExample();
        example.createCriteria().andImageIdEqualTo(id);
        List<ImageResult> list = imageResultMapper.selectByExample(example);

        for (ImageResult result : list) {
            ImageResultLogExample logExample = new ImageResultLogExample();
            logExample.createCriteria().andResultIdEqualTo(result.getId());
            imageResultLogMapper.deleteByExample(logExample);

        }
        imageResultMapper.deleteByExample(example);
    }

    public String execute(Image image) throws Exception {
        Proxy proxy = new Proxy();
        ImageRepo imageRepo = new ImageRepo();
        if (image.getIsProxy() && image.getProxyId()!=null) {
            proxy = proxyMapper.selectByPrimaryKey(image.getProxyId());
        }
        if(image.getRepoId()!=null) {
            imageRepo = imageRepoMapper.selectByPrimaryKey(image.getRepoId());
        }
        IProvider cp = execEngineFactoryImp.getProvider("imageProvider");
        return (String) execEngineFactoryImp.executeMethod(cp, "execute", image, proxy, imageRepo);
    }

    public List<ImageResultWithBLOBsDTO> resultListWithBLOBs(ImageResultRequest request) {
        List<ImageResultWithBLOBsDTO> list = extImageResultMapper.resultListWithBLOBs(request);
        return list;
    }

    public List<ImageResultDTO> resultList(ImageResultRequest request) {
        List<ImageResultDTO> list = extImageResultMapper.resultList(request);
        return list;
    }

    public HistoryImageReportDTO getImageResultDto(String resultId) {
        HistoryImageReportDTO dto = extImageResultMapper.getImageResultDto(resultId);
        return dto;
    }

    public ImageResultWithBLOBs getImageResult(String resultId) {
        ImageResultWithBLOBs imageResult = imageResultMapper.selectByPrimaryKey(resultId);
        return imageResult;
    }

    public ImageResultWithBLOBs getImageResultWithBLOBs(String resultId) {
        ImageResultWithBLOBs imageResultWithBLOBs = imageResultMapper.selectByPrimaryKey(resultId);
        return imageResultWithBLOBs;
    }

    public List<ImageResultLog> getImageResultLog(String resultId) {
        ImageResultLogExample example = new ImageResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return imageResultLogMapper.selectByExampleWithBLOBs(example);
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

    public List<ImageTrivyJsonWithBLOBs> resultItemList(ImageTrivyJson resourceRequest) {
        ImageTrivyJsonExample example = new ImageTrivyJsonExample();
        example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId());
        return imageTrivyJsonMapper.selectByExampleWithBLOBs(example);
    }

    public List<ImageRepoSyncLog> repoSyncList(String id) {
        ImageRepoSyncLogExample example = new ImageRepoSyncLogExample();
        example.createCriteria().andRepoIdEqualTo(id);
        return imageRepoSyncLogMapper.selectByExampleWithBLOBs(example);
    }

    public void syncImage(@PathVariable String id) throws Exception {
        ImageRepo imageRepo = imageRepoMapper.selectByPrimaryKey(id);
        syncImages(imageRepo);
    }

    /**
     * 转换流量格式为xxGBxxMBxxKB
     */
    public String changeFlowFormat(long flows) {
        flows = flows / 1024;//默认是1024KB
        if (flows > 0 && flows < 1024) {//小于1M
            return flows + "KB";
        } else if (flows >= 1024 && flows < 1048576) {//大于1M小于1G
            int changeM = (int) Math.floor(flows / 1024);//整M数
            int surplusM = (int) Math.floor(flows % 1024);//除M后的余数
            if (surplusM > 0) {//余数大于0KB
                return changeM + 1 + "MB";
            } else {//整M，没有余数
                return changeM + "MB";
            }
        } else if (flows >= 1048576) {//大于1G
            int changeG = (int) Math.floor(flows / 1048576);//整G数
            int surplusG = (int) Math.floor(flows % 1048576);//除G后的余数
            if (surplusG >= 1024) {//余数大于大于1M
                int changeM = (int) Math.floor(surplusG / 1024);
                int surplusM = (int) Math.floor(surplusG % 1024);
                if (surplusM > 0) {//余数大于0KB
                    return changeG + 1 + "GB";
                } else {//整M，没有余数
                    return changeG + 1 + "GB";
                }
            } else if (surplusG < 1024 && surplusG > 0) {//余数小于1M，大于0K
                int surplusM = (int) Math.floor(surplusG % 1024);
                return changeG + 1 + "GB";
            } else {
                return changeG + "GB";
            }
        }
        return "i18n_no_data";
    }

    public void scanImageRepo(ScanImageRepoRequest request) throws Exception {
        try {
            Image image = BeanUtils.copyBean(new Image(), request);
            image.setStatus("VALID");
            image.setCreateTime(System.currentTimeMillis());
            image.setUpdateTime(System.currentTimeMillis());
            image.setCreator(SessionUtils.getUserId());
            image.setType("repo");
            image.setImageUrl(request.getPath().split(":")[0]);
            image.setImageTag(request.getTag());
            image.setPluginIcon("docker.png");
            image.setIsImageRepo(true);
            image.setIsImageIcon(false);

            ImageExample example = new ImageExample();
            example.createCriteria().andImageUrlEqualTo(image.getImageUrl()).andImageTagEqualTo(image.getImageTag()).andRepoIdEqualTo(image.getRepoId());
            List<Image> list = imageMapper.selectByExample(example);
            if (list.size() == 0) {
                image.setId(UUIDUtil.newUUID());
                imageMapper.insertSelective(image);
                OperationLogService.log(SessionUtils.getUser(), image.getId(), image.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_create_image");

            } else {
                image.setId(list.get(0).getId());
                imageMapper.updateByPrimaryKeySelective(image);
                OperationLogService.log(SessionUtils.getUser(), image.getId(), image.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.UPDATE, "i18n_update_image_rule");

            }
            ImageResultExample imageResultExample = new ImageResultExample();
            imageResultExample.createCriteria().andImageIdEqualTo(image.getId());
            List<ImageResult> results = imageResultMapper.selectByExample(imageResultExample);
            if (results.size() > 0) {
                this.reScan(results.get(0).getId());
            } else {
                this.scan(image.getId());
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


}
