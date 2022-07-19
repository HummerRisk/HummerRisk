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
import com.hummerrisk.dto.ImageDTO;
import com.hummerrisk.dto.ImageResultDTO;
import com.hummerrisk.dto.ImageResultWithBLOBsDTO;
import com.hummerrisk.dto.ImageRuleDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    private ImageResultItemMapper imageResultItemMapper;
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
                result.setUserName(userMapper.selectByPrimaryKey(SessionUtils.getUserId()).getName());
                imageResultMapper.insertSelective(result);

                saveImageResultLog(result.getId(), "i18n_start_image_result", "", true);
                OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_start_image_result");

                historyService.insertScanTaskHistory(result, scanId);
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

            String lines[] = grypeTable.split("\\r?\\n");
            result.setReturnSum(Long.parseLong(lines.length+""));
            for (String line : lines) {
                saveResultItem(result, line);
            }
            imageResultMapper.updateByPrimaryKeySelective(result);

            noticeService.createImageMessageOrder(result);
            saveImageResultLog(result.getId(), "i18n_end_image_result", "", true);
        } catch (Exception e) {
            LogUtil.error("create ImageResult: " + e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            imageResultMapper.updateByPrimaryKeySelective(result);
            saveImageResultLog(result.getId(), "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false);
            throw e;
        }
    }

    void saveResultItem(ImageResult result, String line) {
        if(line.contains("NAME INSTALLED FIXED-IN TYPE VULNERABILITY SEVERITY")) {return;}
        ImageResultItem imageResultItem = new ImageResultItem();
        imageResultItem.setId(UUIDUtil.newUUID());
        imageResultItem.setSeverity(result.getSeverity());
        imageResultItem.setName(result.getName());
        imageResultItem.setResultId(result.getId());
        imageResultItem.setCreateTime(System.currentTimeMillis());
        imageResultItem.setUpdateTime(System.currentTimeMillis());

        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        String json = "{\n" +
                "  \"id\": " + "\"" + UUIDUtil.newUUID() + "\"" + ",\n" +
                "  \"CreatedTime\": " + "\"" + sdf.format(date) + "\"" + ",\n" +
                "  \"Name\": " + "\"" + result.getName() + "\"" + ",\n" +
                "  \"Result\": " + "\"" + line + "\"" + "\n" +
                "}";
        imageResultItem.setResource(json);
        imageResultItemMapper.insertSelective(imageResultItem);
    }

    public String reScan(String id) throws Exception {
        ImageResultWithBLOBs result = imageResultMapper.selectByPrimaryKey(id);
        ImageRule rule = imageRuleMapper.selectByPrimaryKey(result.getRuleId());
        Image image = imageMapper.selectByPrimaryKey(result.getImageId());
        ImageRuleDTO dto = BeanUtils.copyBean(new ImageRuleDTO(), rule);

        deleteImageResult(id);

        BeanUtils.copyBean(result, image);
        result.setId(UUIDUtil.newUUID());
        result.setImageId(image.getId());
        result.setApplyUser(SessionUtils.getUserId());
        result.setCreateTime(System.currentTimeMillis());
        result.setUpdateTime(System.currentTimeMillis());
        result.setRuleId(dto.getId());
        result.setRuleName(dto.getName());
        result.setRuleDesc(dto.getDescription());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        result.setSeverity(dto.getSeverity());
        result.setUserName(userMapper.selectByPrimaryKey(SessionUtils.getUserId()).getName());
        imageResultMapper.insertSelective(result);

        saveImageResultLog(result.getId(), "i18n_restart_image_result", "", true);

        OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "i18n_restart_image_result");
        return result.getId();
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
                            ImageConstants.DEFAULT_BASE_DIR + ImageConstants.GRYPE_JSON_TXT + ImageConstants.DISTRO + ImageConstants._DISTRO;
                } else if (StringUtils.equalsIgnoreCase(outType, ImageConstants.TABLE)) {
                    command = _proxy + dockerLogin + scanType + fileName + ImageConstants.SCOPE + ImageConstants.OUT + outType + ImageConstants._FILE +
                            ImageConstants.DEFAULT_BASE_DIR + ImageConstants.GRYPE_TABLE_TXT + ImageConstants.DISTRO + ImageConstants._DISTRO;
                }
            } else if (StringUtils.equalsIgnoreCase(scanType, ImageConstants.SYFT)) {
                command = _proxy + dockerLogin + scanType + fileName + ImageConstants.SCOPE + ImageConstants.OUT + ImageConstants.SYFT_JSON + ImageConstants.DEFAULT_BASE_DIR + ImageConstants.SYFT_JSON_TXT +
                        ImageConstants.OUT + ImageConstants.SYFT_TABLE + ImageConstants.DEFAULT_BASE_DIR + ImageConstants.SYFT_TABLE_TXT;
            }
            LogUtil.info(image.getId() + " {image}[command]: " + image.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, ImageConstants.DEFAULT_BASE_DIR);
            if(resultStr.contains("ERROR")) {
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

    public ImageResultWithBLOBs getImageResult(String resultId) {
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

    void saveImageResultLog(String resultId, String operation, String output, boolean result) {
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
    }

    public List<ImageResultItem> resultItemList(ImageResultItem resourceRequest) {
        ImageResultItemExample example = new ImageResultItemExample();
        example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId());
        return imageResultItemMapper.selectByExampleWithBLOBs(example);
    }

}
