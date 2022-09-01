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
import com.hummerrisk.controller.request.image.ImageRepoRequest;
import com.hummerrisk.controller.request.image.ImageRequest;
import com.hummerrisk.controller.request.image.ImageResultRequest;
import com.hummerrisk.controller.request.image.ImageRuleRequest;
import com.hummerrisk.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

import static com.hummerrisk.service.SysListener.changeFlowFormat;

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
    private UserMapper userMapper;
    @Resource
    private ImageResultMapper imageResultMapper;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private NoticeService noticeService;
    @Resource
    private ImageGrypeTableMapper imageGrypeTableMapper;
    @Resource
    private ImageGrypeJsonMapper imageGrypeJsonMapper;
    @Resource
    private ImageSyftTableMapper imageSyftTableMapper;
    @Resource
    private ImageSyftJsonMapper imageSyftJsonMapper;
    @Resource
    private HistoryService historyService;

    public List<ImageRepo> imageRepoList(ImageRepoRequest request) {
        return extImageRepoMapper.imageRepoList(request);
    }

    public List<ImageRepo> allImageRepos() {
        ImageRepoExample example = new ImageRepoExample();
        example.setOrderByClause("update_time desc");
        return imageRepoMapper.selectByExample(example);
    }

    public ImageRepo addImageRepo(ImageRepo imageRepo) throws Exception {
        String id = UUIDUtil.newUUID();
        imageRepo.setId(id);
        imageRepo.setCreator(SessionUtils.getUserId());
        imageRepo.setCreateTime(System.currentTimeMillis());
        imageRepo.setUpdateTime(System.currentTimeMillis());

        String dockerLogin = "docker login " + imageRepo.getRepo() + " " + "-u " + imageRepo.getUserName() + " -p " + imageRepo.getPassword();
        String resultStr = CommandUtils.commonExecCmdWithResult(dockerLogin, ImageConstants.DEFAULT_BASE_DIR);
        if(resultStr.contains("Succeeded")) {
            imageRepo.setStatus("VALID");
        } else {
            imageRepo.setStatus("INVALID");
        }

        OperationLogService.log(SessionUtils.getUser(), imageRepo.getId(), imageRepo.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_create_image_repo");
        imageRepoMapper.insertSelective(imageRepo);

        return imageRepo;
    }

    /**
     * 过去harbor镜像列表
     * @param path harbor 地址
     * @param username harbor 用户名
     * @param password harbor 密码
     * @return 镜像列表
     * @throws Exception
     */
    public List<Map<String,String>> getHarborImages(String path,String username,String password) throws Exception {
        if(path.endsWith("/")){
            path = path.substring(0,path.length()-1);
        }
        List<Map<String,String>> result = new ArrayList<>();
        Map<String,String> header = new HashMap<>();
        header.put("Authorization","Basic "+ Base64.getUrlEncoder().encodeToString((username + ":" + password).getBytes()));
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
                    JSONArray tags = arti.getJSONArray("tags");
                    List<JSONObject> tagList = tags.toJavaList(JSONObject.class);
                    for (JSONObject tag : tagList) {
                        String tagStr = tag.getString("name");
                        Map<String, String> imageMap = new HashMap<>();
                        imageMap.put("project", projectName);
                        imageMap.put("repositories", repName);
                        imageMap.put("tag", tagStr);
                        imageMap.put("imagePath", path + "/" + projectName + "/" + repName + ":" + tagStr);
                        result.add(imageMap);
                    }
                }
            }
        }
        return result;
    }

    public ImageRepo editImageRepo(ImageRepo imageRepo) throws Exception {
        imageRepo.setUpdateTime(System.currentTimeMillis());
        String dockerLogin = "docker login " + imageRepo.getRepo() + " " + "-u " + imageRepo.getUserName() + " -p " + imageRepo.getPassword();
        String resultStr = CommandUtils.commonExecCmdWithResult(dockerLogin, ImageConstants.DEFAULT_BASE_DIR);
        if(resultStr.contains("Succeeded")) {
            imageRepo.setStatus("VALID");
        } else {
            imageRepo.setStatus("INVALID");
        }

        OperationLogService.log(SessionUtils.getUser(), imageRepo.getId(), imageRepo.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.UPDATE, "i18n_update_image_repo");
        imageRepoMapper.updateByPrimaryKeySelective(imageRepo);
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
                result.setScanType(CloudTaskConstants.IMAGE_TYPE.grype.name());
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
            String script = dto.getScript();
            JSONArray jsonArray = JSON.parseArray(dto.getParameter());
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                String key = "${{" + jsonObject.getString("key") + "}}";
                if (script.contains(key)) {
                    script = script.replace(key, jsonObject.getString("defaultValue"));
                }
            }
            String grypeTable = "", grypeJson = "", syftTable = "", syftJson = "";

            String log = execute(image, ImageConstants.GRYPE, ImageConstants.TABLE);
            if (log.contains("docker login")) {
                throw new Exception(log);
            }
            grypeTable = ReadFileUtils.readToBuffer(ImageConstants.DEFAULT_BASE_DIR + ImageConstants.GRYPE_TABLE_TXT);
            execute(image, ImageConstants.GRYPE, ImageConstants.JSON);
            grypeJson = ReadFileUtils.readToBuffer(ImageConstants.DEFAULT_BASE_DIR + ImageConstants.GRYPE_JSON_TXT);
            execute(image, ImageConstants.SYFT, "");
            syftTable = ReadFileUtils.readToBuffer(ImageConstants.DEFAULT_BASE_DIR + ImageConstants.SYFT_TABLE_TXT);
            syftJson = ReadFileUtils.readToBuffer(ImageConstants.DEFAULT_BASE_DIR + ImageConstants.SYFT_JSON_TXT);

            result.setReturnLog(log);
            result.setGrypeTable(grypeTable);
            result.setGrypeJson(grypeJson);
            result.setSyftTable(syftTable);
            result.setSyftJson(syftJson);
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());

            long count = saveResultItem(result);
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

    long saveResultItem(ImageResultWithBLOBs result) throws Exception {

        //插入grypeTables
        String grypeTables[] = result.getGrypeTable().split("\\r?\\n");
        for (String line : grypeTables) {
            if (line.contains("NAME")) {continue;}//去除首行table title
            line = line.replaceAll(" +"," ");//将多个空格替换为一个
            String[] newStr = line.split(" ");//截取table
            ImageGrypeTable imageGrypeTable = new ImageGrypeTable();
            for (int i = 0; i < newStr.length; i++) {
                String str = newStr[i];
                if(!str.isEmpty()) {//FIXED-IN可能为空
//                    NAME               INSTALLED     FIXED-IN      TYPE  VULNERABILITY   SEVERITY
//                    apk-tools          2.10.4-r2     2.10.7-r0     apk   CVE-2021-36159  Critical
//                    apk-tools          2.10.4-r2     2.10.6-r0     apk   CVE-2021-30139  High
//                    busybox            1.30.1-r2                   apk   CVE-2021-42384  High
                    if (i == 0) {
                        imageGrypeTable.setName(str);
                    } else if (i == 1) {
                        imageGrypeTable.setInstalled(str);
                    } else if (i == 2 && newStr.length == 6) {
                        imageGrypeTable.setFixedIn(str);
                    } else if (i == 2 && newStr.length == 5) {
                        imageGrypeTable.setType(str);
                    } else if (i == 3 && newStr.length == 6) {
                        imageGrypeTable.setType(str);
                    } else if (i == 3 && newStr.length == 5) {
                        imageGrypeTable.setVulnerability(str);
                    } else if (i == 4 && newStr.length == 6) {
                        imageGrypeTable.setVulnerability(str);
                    } else if (i == 4 && newStr.length == 5) {
                        imageGrypeTable.setSeverity(str);
                    } else if (i == 5 && newStr.length == 6) {
                        imageGrypeTable.setSeverity(str);
                    }
                }
            }
            imageGrypeTable.setResultId(result.getId());
            imageGrypeTableMapper.insertSelective(imageGrypeTable);
        }

        //插入grypeJsons
        JSONObject jsonG = JSONObject.parseObject(result.getGrypeJson());
        JSONArray grypeJsons = JSONArray.parseArray(jsonG.getString("matches"));
        for (Object obj : grypeJsons) {
            ImageGrypeJsonWithBLOBs imageGrypeJson = new ImageGrypeJsonWithBLOBs();
            String artifact = ((JSONObject) obj).getString("artifact");
            String vulnerability = ((JSONObject) obj).getString("vulnerability");
            imageGrypeJson.setArtifact(artifact);
            imageGrypeJson.setVulnerability(vulnerability);
            imageGrypeJson.setRelatedVulnerabilities(((JSONObject) obj).getString("relatedVulnerabilities"));
            imageGrypeJson.setMatchDetails(((JSONObject) obj).getString("matchDetails"));
            JSONObject artifactJson = JSONObject.parseObject(artifact);
            JSONObject vulnerabilityJson = JSONObject.parseObject(vulnerability);
            imageGrypeJson.setName(artifactJson.getString("name"));
            imageGrypeJson.setVersion(artifactJson.getString("version"));
            imageGrypeJson.setType(artifactJson.getString("type"));
            imageGrypeJson.setCve(vulnerabilityJson.getString("id"));
            imageGrypeJson.setDatasource(vulnerabilityJson.getString("dataSource"));
            imageGrypeJson.setNamespace(vulnerabilityJson.getString("namespace"));
            imageGrypeJson.setSeverity(vulnerabilityJson.getString("severity"));
            imageGrypeJson.setDescription(vulnerabilityJson.getString("description"));
            imageGrypeJson.setResultId(result.getId());
            imageGrypeJsonMapper.insertSelective(imageGrypeJson);
        }

        //插入syftTables
        String syftTables[] = result.getSyftTable().split("\\r?\\n");
        for (String line : syftTables) {
            if (line.contains("NAME")) {continue;}//去除首行table title
            line = line.replaceAll(" +"," ");//将多个空格替换为一个
            String[] newStr = line.split(" ");//截取table
            ImageSyftTable imageSyftTable = new ImageSyftTable();
            for (int i = 0; i < newStr.length; i++) {
                String str = newStr[i];
                if(!str.isEmpty()) {//version 值可能为空
//                    NAME                    VERSION        TYPE
//                    US_export_policy                       java-archive
//                    alpine-baselayout       3.1.2-r0       apk
                    if (i == 0) {
                        imageSyftTable.setName(str);
                    } else if (i == 1 && newStr.length == 3) {
                        imageSyftTable.setVersion(str);
                    } else if (i == 1 && newStr.length == 2) {
                        imageSyftTable.setType(str);
                    } else if (i == 2) {
                        imageSyftTable.setType(str);
                    }
                }
            }
            imageSyftTable.setResultId(result.getId());
            imageSyftTableMapper.insertSelective(imageSyftTable);
        }

        //插入syftJsons
        JSONObject jsonS = JSONObject.parseObject(result.getSyftJson());
        JSONArray syftJsons = JSONArray.parseArray(jsonS.getString("artifacts"));
        for (Object obj : syftJsons) {
            ImageSyftJsonWithBLOBs imageSyftJson = new ImageSyftJsonWithBLOBs();
            JSONObject artifact = (JSONObject) obj;
            imageSyftJson.setName(artifact.getString("name"));
            imageSyftJson.setVersion(!artifact.getString("version").isEmpty()?artifact.getString("version"):"");
            imageSyftJson.setType(artifact.getString("type"));
            imageSyftJson.setFoundBy(artifact.getString("foundBy"));
            imageSyftJson.setLanguage(artifact.getString("language"));
            imageSyftJson.setPurl(artifact.getString("purl"));
            imageSyftJson.setMetadataType(artifact.getString("metadataType"));
            imageSyftJson.setLocations(artifact.getString("locations"));
            imageSyftJson.setLicenses(artifact.getString("licenses"));
            imageSyftJson.setCpes(artifact.getString("cpes"));
            imageSyftJson.setMetadata(artifact.getString("metadata"));
            imageSyftJson.setResultId(result.getId());
            imageSyftJsonMapper.insertSelective(imageSyftJson);
        }

        return Long.parseLong((grypeTables.length - 1) + "");
    }

    public String reScan(String id) throws Exception {
        ImageResultWithBLOBs result = imageResultMapper.selectByPrimaryKey(id);

        result.setUpdateTime(System.currentTimeMillis());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        result.setUserName(SessionUtils.getUser().getName());
        result.setScanType(CloudTaskConstants.IMAGE_TYPE.grype.name());
        imageResultMapper.updateByPrimaryKeySelective(result);

        this.reScanDeleteImageResult(id);

        saveImageResultLog(result.getId(), "i18n_restart_image_result", "", true);

        OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.RESCAN, "i18n_restart_image_result");

        historyService.updateHistoryImageTask(BeanUtils.copyBean(new HistoryImageTaskWithBLOBs(), result));

        return result.getId();
    }

    public void reScanDeleteImageResult(String id) throws Exception {

        ImageSyftTableExample imageSyftTableExample = new ImageSyftTableExample();
        imageSyftTableExample.createCriteria().andResultIdEqualTo(id);
        imageSyftTableMapper.deleteByExample(imageSyftTableExample);

        ImageSyftJsonExample imageSyftJsonExample = new ImageSyftJsonExample();
        imageSyftJsonExample.createCriteria().andResultIdEqualTo(id);
        imageSyftJsonMapper.deleteByExample(imageSyftJsonExample);

        ImageGrypeTableExample imageGrypeTableExample = new ImageGrypeTableExample();
        imageGrypeTableExample.createCriteria().andResultIdEqualTo(id);
        imageGrypeTableMapper.deleteByExample(imageGrypeTableExample);

        ImageGrypeJsonExample imageGrypeJsonExample = new ImageGrypeJsonExample();
        imageGrypeJsonExample.createCriteria().andResultIdEqualTo(id);
        imageGrypeJsonMapper.deleteByExample(imageGrypeJsonExample);
    }

    public void deleteImageResult(String id) throws Exception {

        ImageResultLogExample logExample = new ImageResultLogExample();
        logExample.createCriteria().andResultIdEqualTo(id);
        imageResultLogMapper.deleteByExample(logExample);

        imageResultMapper.deleteByPrimaryKey(id);
    }

    public void deleteResultByImageId(String id) throws Exception {
        ImageResultExample example = new ImageResultExample();
        example.createCriteria().andImageIdEqualTo(id).andScanTypeEqualTo(CloudTaskConstants.IMAGE_TYPE.grype.name());
        List<ImageResult> list = imageResultMapper.selectByExample(example);

        for (ImageResult result : list) {
            ImageResultLogExample logExample = new ImageResultLogExample();
            logExample.createCriteria().andResultIdEqualTo(result.getId());
            imageResultLogMapper.deleteByExample(logExample);

        }
        imageResultMapper.deleteByExample(example);
    }

    public String execute(Image image, String scanType, String outType) throws Exception {
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
                fileName = ImageConstants.DEFAULT_BASE_DIR + image.getPath();
            }
            String command = "";
            if (StringUtils.equalsIgnoreCase(scanType, ImageConstants.GRYPE)) {
                if (StringUtils.equalsIgnoreCase(outType, ImageConstants.JSON)) {
                    command = _proxy + dockerLogin + scanType + fileName + ImageConstants.SCOPE + ImageConstants.OUT + outType + ImageConstants._FILE +
                            ImageConstants.DEFAULT_BASE_DIR + ImageConstants.GRYPE_JSON_TXT;
                } else if (StringUtils.equalsIgnoreCase(outType, ImageConstants.TABLE)) {
                    command = _proxy + dockerLogin + scanType + fileName + ImageConstants.SCOPE + ImageConstants.OUT + outType + ImageConstants._FILE +
                            ImageConstants.DEFAULT_BASE_DIR + ImageConstants.GRYPE_TABLE_TXT;
                }
            } else if (StringUtils.equalsIgnoreCase(scanType, ImageConstants.SYFT)) {
                command = _proxy + dockerLogin + scanType + fileName + ImageConstants.SCOPE + ImageConstants.OUT + ImageConstants.SYFT_JSON + ImageConstants.DEFAULT_BASE_DIR + ImageConstants.SYFT_JSON_TXT +
                        ImageConstants.OUT + ImageConstants.SYFT_TABLE + ImageConstants.DEFAULT_BASE_DIR + ImageConstants.SYFT_TABLE_TXT;
            }
            LogUtil.info(image.getId() + " {image}[command]: " + image.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, ImageConstants.DEFAULT_BASE_DIR);
            if(resultStr.contains("ERROR") || resultStr.contains("error")) {
                throw new Exception(resultStr);
            }
            return resultStr;
        } catch (Exception e) {
            return "";
        }
    }

    public List<ImageResultWithBLOBsDTO> resultListWithBLOBs(ImageResultRequest request) {
        List<ImageResultWithBLOBsDTO> list = extImageResultMapper.resultListWithBLOBs(request);
        for (ImageResultWithBLOBsDTO imageResultWithBLOBsDTO : list) {
            imageResultWithBLOBsDTO.setGrypeJson(accountService.toJSONString(imageResultWithBLOBsDTO.getGrypeJson()!=null? imageResultWithBLOBsDTO.getGrypeJson():"{}"));
            imageResultWithBLOBsDTO.setSyftJson(accountService.toJSONString(imageResultWithBLOBsDTO.getSyftJson()!=null? imageResultWithBLOBsDTO.getSyftJson():"{}"));
        }
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

    public ImageResult getImageResult(String resultId) {
        ImageResult imageResult = imageResultMapper.selectByPrimaryKey(resultId);
        return imageResult;
    }

    public ImageResultWithBLOBs getImageResultWithBLOBs(String resultId) {
        ImageResultWithBLOBs imageResultWithBLOBs = imageResultMapper.selectByPrimaryKey(resultId);
        if(imageResultWithBLOBs!=null && !StringUtils.equalsIgnoreCase(imageResultWithBLOBs.getResultStatus(), TaskConstants.TASK_STATUS.APPROVED.toString())) {
            imageResultWithBLOBs.setGrypeJson(accountService.toJSONString(imageResultWithBLOBs.getGrypeJson()!=null? imageResultWithBLOBs.getGrypeJson():"{}"));
            imageResultWithBLOBs.setSyftJson(accountService.toJSONString(imageResultWithBLOBs.getSyftJson()!=null? imageResultWithBLOBs.getSyftJson():"{}"));
        }
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

    public List<ImageGrypeTable> resultItemList(ImageGrypeTable resourceRequest) {
        ImageGrypeTableExample example = new ImageGrypeTableExample();
        example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId());
        return imageGrypeTableMapper.selectByExample(example);
    }

}
