package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummerrisk.base.domain.Package;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtPackageMapper;
import com.hummerrisk.base.mapper.ext.ExtPackageResultMapper;
import com.hummerrisk.base.mapper.ext.ExtPackageRuleMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.packageSetting.PackageRequest;
import com.hummerrisk.controller.request.packageSetting.PackageResultRequest;
import com.hummerrisk.controller.request.packageSetting.PackageRuleRequest;
import com.hummerrisk.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PackageService {

    @Resource
    private ExtPackageMapper extPackageMapper;
    @Resource
    private PackageMapper packageMapper;
    @Resource
    private PackageRuleMapper packageRuleMapper;
    @Resource
    private ExtPackageRuleMapper extPackageRuleMapper;
    @Resource
    private RuleTagMapper ruleTagMapper;
    @Resource
    private RuleTagMappingMapper ruleTagMappingMapper;
    @Resource
    private PackageResultMapper packageResultMapper;
    @Resource
    private PackageResultLogMapper packageResultLogMapper;
    @Resource
    private ExtPackageResultMapper extPackageResultMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private NoticeService noticeService;
    @Resource
    private AccountService accountService;
    @Resource
    private HistoryService historyService;
    @Resource
    private PackageDependencyJsonMapper packageDependencyJsonMapper;

    public List<PackageDTO> packageList(PackageRequest request) {
        return extPackageMapper.packageList(request);
    }

    public Package addPackage(Package p) throws Exception {
        String id = UUIDUtil.newUUID();
        p.setId(id);
        p.setCreator(SessionUtils.getUserId());
        p.setCreateTime(System.currentTimeMillis());
        p.setUpdateTime(System.currentTimeMillis());
        p.setStatus("VALID");

        OperationLogService.log(SessionUtils.getUser(), p.getId(), p.getName(), ResourceTypeConstants.PACKAGE.name(), ResourceOperation.CREATE, "i18n_create_package");
        packageMapper.insertSelective(p);
        return p;
    }

    public Package editPackage(Package p) throws Exception {
        p.setUpdateTime(System.currentTimeMillis());
        p.setStatus("VALID");

        OperationLogService.log(SessionUtils.getUser(), p.getId(), p.getName(), ResourceTypeConstants.PACKAGE.name(), ResourceOperation.UPDATE, "i18n_update_package");
        packageMapper.updateByPrimaryKeySelective(p);
        return p;
    }

    public void deletePackage(String id) throws Exception {
        packageMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.PACKAGE.name(), ResourceOperation.DELETE, "i18n_delete_package");
    }

    public Package uploadImg(MultipartFile file, Package request) throws Exception {
        String filePath = upload(file, PackageConstants.DEFAULT_BASE_DIR);
        request.setPluginIcon("files/" + filePath);
        request.setUpdateTime(System.currentTimeMillis());
        request.setStatus("VALID");
        packageMapper.updateByPrimaryKeySelective(request);
        OperationLogService.log(SessionUtils.getUser(), request.getId(), request.getName(), ResourceTypeConstants.PACKAGE.name(), ResourceOperation.UPLOAD, "i18n_upload_package_img");
        return request;
    }

    public Package uploadPackage(MultipartFile file, Package request) throws Exception {
        String filePath = upload(file, PackageConstants.DEFAULT_BASE_DIR);
        request.setPackageName(file.getOriginalFilename());
        request.setPath(filePath);
        request.setUpdateTime(System.currentTimeMillis());
        request.setStatus("VALID");
        request.setSize(changeFlowFormat(file.getSize()));
        packageMapper.updateByPrimaryKeySelective(request);
        OperationLogService.log(SessionUtils.getUser(), request.getId(), request.getName(), ResourceTypeConstants.PACKAGE.name(), ResourceOperation.UPLOAD, "i18n_upload_package");
        return request;
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

    public void deleteImg(String id, PackageRequest p) throws Exception {
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(p.getPluginIcon()));
        Package request = BeanUtils.copyBean(new Package(), p);
        request.setPluginIcon("package.png");
        packageMapper.updateByPrimaryKeySelective(request);
    }

    public void deleteFile(String id, PackageRequest p) throws Exception {
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(p.getPath()));
        Package request = BeanUtils.copyBean(new Package(), p);
        request.setPackageName("");
        request.setPath("");
        request.setSize("");
        packageMapper.updateByPrimaryKeySelective(request);
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

    public List<PackageRuleDTO> ruleList(PackageRuleRequest request) {
        return extPackageRuleMapper.ruleList(request);
    }

    public int addPackageRule(PackageRuleRequest request) throws Exception {
        PackageRule record = new PackageRule();
        BeanUtils.copyBean(record, request);
        record.setId(UUIDUtil.newUUID());
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        OperationLogService.log(SessionUtils.getUser(), record.getId(), record.getName(), ResourceTypeConstants.PACKAGE.name(), ResourceOperation.CREATE, "i18n_create_package_rule");
        return packageRuleMapper.insertSelective(record);
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

    public int updatePackageRule(PackageRuleRequest request) throws Exception {
        PackageRule record = new PackageRule();
        BeanUtils.copyBean(record, request);
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        OperationLogService.log(SessionUtils.getUser(), record.getId(), record.getName(), ResourceTypeConstants.PACKAGE.name(), ResourceOperation.UPDATE, "i18n_update_package_rule");
        return packageRuleMapper.updateByPrimaryKeySelective(record);
    }

    public void deletePackageRule(String id) throws Exception {
        deleteRuleTag(null, id);
        packageRuleMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.PACKAGE.name(), ResourceOperation.DELETE, "i18n_delete_package_rule");
    }

    public int changeStatus(PackageRule rule) throws Exception {
        return packageRuleMapper.updateByPrimaryKeySelective(rule);
    }

    public void scan(String id) throws Exception{
        Package aPackage = packageMapper.selectByPrimaryKey(id);
        Integer scanId = historyService.insertScanHistory(aPackage);
        if(StringUtils.equalsIgnoreCase(aPackage.getStatus(), CloudAccountConstants.Status.VALID.name())) {
            List<PackageRuleDTO> ruleList = ruleList(null);
            PackageResultWithBLOBs result = new PackageResultWithBLOBs();

            deleteResultByPackageId(id);
            for(PackageRuleDTO dto : ruleList) {
                BeanUtils.copyBean(result, aPackage);
                result.setId(UUIDUtil.newUUID());
                result.setPackageId(id);
                result.setApplyUser(SessionUtils.getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setRuleId(dto.getId());
                result.setRuleName(dto.getName());
                result.setRuleDesc(dto.getDescription());
                result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(dto.getSeverity());
                result.setUserName(SessionUtils.getUser().getName());
                packageResultMapper.insertSelective(result);

                savePackageResultLog(result.getId(), "i18n_start_package_result", "", true);

                OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.PACKAGE.name(), ResourceOperation.CREATE, "i18n_start_package_result");

                historyService.insertScanTaskHistory(result, scanId, aPackage.getId(), TaskEnum.packageAccount.getType());

                historyService.insertHistoryPackageTask(BeanUtils.copyBean(new HistoryPackageTaskWithBLOBs(), result));
            }
        }
    }

    public void createScan (PackageResultWithBLOBs result) throws Exception {
        try {
            PackageRuleRequest request = new PackageRuleRequest();
            request.setId(result.getRuleId());
            PackageRuleDTO dto = ruleList(request).get(0);
            Package aPackage = packageMapper.selectByPrimaryKey(result.getPackageId());
            String script = dto.getScript();
            JSONArray jsonArray = JSON.parseArray(dto.getParameter());
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                String key = "${{" + jsonObject.getString("key") + "}}";
                if (script.contains(key)) {
                    script = script.replace(key, jsonObject.getString("defaultValue"));
                }
            }
            String resources = execute(aPackage);
            //检测包输出路径，去除后缀
            String suffix = (!aPackage.getPath().isEmpty()?aPackage.getPath().split("\\.")[0]:"") + "/";

            //路径：/opt/hummerrisk/file/20220801/4ba96f2abb2681c426c98d767795da6b/dependency-check-report.html
            String returnJson = !aPackage.getPath().isEmpty()? ReadFileUtils.readJsonFile(PackageConstants.DEFAULT_BASE_DIR + suffix, PackageConstants.JSON_DIR):"";
            String returnHtml = "files/" + suffix + PackageConstants.HTML_DIR;
            result.setResources(resources);
            result.setReturnJson(returnJson);
            result.setReturnHtml(returnHtml);
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());

            JSONArray jsonArr = JSON.parseObject(returnJson).getJSONArray("dependencies");
            result.setReturnSum(Long.parseLong(jsonArr.size() + ""));
            for (Object o : jsonArr) {
                JSONObject jsonObject = (JSONObject) o;
                saveResultItem(result, jsonObject);
            }
            packageResultMapper.updateByPrimaryKeySelective(result);

            noticeService.createPackageMessageOrder(result);
            savePackageResultLog(result.getId(), "i18n_end_package_result", "", true);

            historyService.updateHistoryPackageTask(BeanUtils.copyBean(new HistoryPackageTaskWithBLOBs(), result));
        } catch (Exception e) {
            LogUtil.error("create PackageResult: " + e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            packageResultMapper.updateByPrimaryKeySelective(result);
            historyService.updateHistoryPackageTask(BeanUtils.copyBean(new HistoryPackageTaskWithBLOBs(), result));
            savePackageResultLog(result.getId(), "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false);
        }
    }

    void saveResultItem(PackageResult result, JSONObject jsonObject) throws Exception {
        PackageDependencyJsonWithBLOBs packageResultItem = new PackageDependencyJsonWithBLOBs();
        packageResultItem.setIsVirtual(jsonObject.getString("isVirtual") != null?jsonObject.getString("isVirtual"):"");
        packageResultItem.setFileName(jsonObject.getString("fileName") != null?jsonObject.getString("fileName"):"");
        packageResultItem.setFilePath(jsonObject.getString("filePath") != null?jsonObject.getString("filePath"):"");
        packageResultItem.setMd5(jsonObject.getString("md5") != null?jsonObject.getString("md5"):"");
        packageResultItem.setSha1(jsonObject.getString("sha1") != null?jsonObject.getString("sha1"):"");
        packageResultItem.setSha256(jsonObject.getString("sha256") != null?jsonObject.getString("sha256"):"");
        packageResultItem.setEvidenceCollected(jsonObject.getString("evidenceCollected") != null?jsonObject.getString("evidenceCollected"):"{}");
        packageResultItem.setProjectreferences(jsonObject.getString("projectReferences") != null?jsonObject.getString("projectReferences"):"[]");
        packageResultItem.setPackages(jsonObject.getString("packages") != null?jsonObject.getString("packages"):"[]");
        packageResultItem.setVulnerabilities(jsonObject.getString("vulnerabilities") != null?jsonObject.getString("vulnerabilities"):"[]");
        packageResultItem.setResultId(result.getId());
        packageDependencyJsonMapper.insertSelective(packageResultItem);
    }

    public String reScan(String id) throws Exception {
        PackageResultWithBLOBs result = packageResultMapper.selectByPrimaryKey(id);

        result.setUpdateTime(System.currentTimeMillis());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        result.setUserName(SessionUtils.getUser().getName());
        packageResultMapper.updateByPrimaryKeySelective(result);

        this.reScanDeletePackageResult(id);

        savePackageResultLog(result.getId(), "i18n_restart_package_result", "", true);

        OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getName(), ResourceTypeConstants.PACKAGE.name(), ResourceOperation.CREATE, "i18n_restart_package_result");

        historyService.updateHistoryPackageTask(BeanUtils.copyBean(new HistoryPackageTaskWithBLOBs(), result));

        return result.getId();
    }

    public void reScanDeletePackageResult(String id) throws Exception {
        PackageResultWithBLOBs result = packageResultMapper.selectByPrimaryKey(id);
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml()));
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "json")));
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "xml")));
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "csv")));
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "sarif")));
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "xml").replace("dependency-check-report", "dependency-check-junit")));

        PackageDependencyJsonExample packageDependencyJsonExample = new PackageDependencyJsonExample();
        packageDependencyJsonExample.createCriteria().andResultIdEqualTo(id);
        packageDependencyJsonMapper.deleteByExample(packageDependencyJsonExample);
    }

    public void deletePackageResult(String id) throws Exception {
        PackageResultWithBLOBs result = packageResultMapper.selectByPrimaryKey(id);

        PackageResultLogExample logExample = new PackageResultLogExample();
        logExample.createCriteria().andResultIdEqualTo(id);
        packageResultLogMapper.deleteByExample(logExample);
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml()));
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "json")));
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "xml")));
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "csv")));
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "sarif")));
        FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "xml").replace("dependency-check-report", "dependency-check-junit")));
        packageResultMapper.deleteByPrimaryKey(id);
    }

    public void deleteResultByPackageId(String id) throws Exception {
        PackageResultExample example = new PackageResultExample();
        example.createCriteria().andPackageIdEqualTo(id);
        List<PackageResult> list = packageResultMapper.selectByExample(example);

        for (PackageResult result : list) {
            PackageResultLogExample logExample = new PackageResultLogExample();
            logExample.createCriteria().andResultIdEqualTo(result.getId());
            packageResultLogMapper.deleteByExample(logExample);
            FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml()));
            FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "json")));
            FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "xml")));
            FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "csv")));
            FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "sarif")));
            FileUploadUtils.delete(PackageConstants.DEFAULT_BASE_DIR + FileUploadUtils.trans(result.getReturnHtml().replace("html", "xml").replace("dependency-check-report", "dependency-check-junit")));

        }
        packageResultMapper.deleteByExample(example);
    }

    void savePackageResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        PackageResultLog packageResultLog = new PackageResultLog();
        String operator = "system";
        try {
            if (SessionUtils.getUser() != null) {
                operator = SessionUtils.getUser().getId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        packageResultLog.setOperator(operator);
        packageResultLog.setResultId(resultId);
        packageResultLog.setCreateTime(System.currentTimeMillis());
        packageResultLog.setOperation(operation);
        packageResultLog.setOutput(output);
        packageResultLog.setResult(result);
        packageResultLogMapper.insertSelective(packageResultLog);

        historyService.insertHistoryPackageTaskLog(BeanUtils.copyBean(new HistoryPackageTaskLog(), packageResultLog));
    }

    public String execute(Package aPackage) throws Exception {
        try {
            Proxy proxy;
            String _proxy = "";
            if(aPackage.getIsProxy()!=null && aPackage.getIsProxy()) {
                proxy = proxyMapper.selectByPrimaryKey(aPackage.getProxyId());
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

            //检测包输出路径，去除后缀
            String suffix = PackageConstants.DEFAULT_BASE_DIR + (!aPackage.getPath().isEmpty()?aPackage.getPath().split("\\.")[0]:"");
            //检测包路径，项目外
            String path = PackageConstants.DEFAULT_BASE_DIR + aPackage.getPath();
            String command = _proxy + PackageConstants.DEPENDENCY_CHECK + PackageConstants.NOUPDATE +
                        PackageConstants.SCAN + path + " " +
                        PackageConstants.FORMAT + PackageConstants.ALL + " " +
                        PackageConstants.OUT + suffix;
            LogUtil.info(aPackage.getId() + " {package}[command]: " + aPackage.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, PackageConstants.DEFAULT_BASE_DIR);
            if(resultStr.contains("ERROR")) {
                throw new Exception(resultStr);
            }
            return resultStr;
        } catch (Exception e) {
            return "";
        }
    }

    public List<PackageResultWithBLOBsDTO> resultListWithBLOBs(PackageResultRequest request) {
        List<PackageResultWithBLOBsDTO> list = extPackageResultMapper.resultListWithBLOBs(request);
        for (PackageResultWithBLOBsDTO packageResultWithBLOBsDTO : list) {
            packageResultWithBLOBsDTO.setReturnJson(accountService.toJSONString(packageResultWithBLOBsDTO.getReturnJson()!=null? packageResultWithBLOBsDTO.getReturnJson():"{}"));
        }
        return list;
    }

    public List<PackageResultDTO> resultList(PackageResultRequest request) {
        List<PackageResultDTO> list = extPackageResultMapper.resultList(request);
        return list;
    }

    public HistoryPackageReportDTO getPackageResultDto(String resultId) {
        HistoryPackageReportDTO dto = extPackageResultMapper.getPackageResultDto(resultId);
        return dto;
    }

    public PackageResult getPackageResult(String resultId) {
        PackageResult packageResult = packageResultMapper.selectByPrimaryKey(resultId);
        return packageResult;
    }

    public PackageResultWithBLOBs getPackageResultWithBLOBs(String resultId) {
        PackageResultWithBLOBs packageResultWithBLOBs = packageResultMapper.selectByPrimaryKey(resultId);
        if(packageResultWithBLOBs!=null && !StringUtils.equalsIgnoreCase(packageResultWithBLOBs.getResultStatus(), TaskConstants.TASK_STATUS.APPROVED.toString())) packageResultWithBLOBs.setReturnJson(accountService.toJSONString(packageResultWithBLOBs.getReturnJson()!=null? packageResultWithBLOBs.getReturnJson():"{}"));
        return packageResultWithBLOBs;
    }

    public List<PackageResultLog> getPackageResultLog(String resultId) {
        PackageResultLogExample example = new PackageResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return packageResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public List<PackageDependencyJsonWithBLOBs> resultItemList(PackageDependencyJson resourceRequest) {
        PackageDependencyJsonExample example = new PackageDependencyJsonExample();
        example.createCriteria().andResultIdEqualTo(resourceRequest.getResultId());
        return packageDependencyJsonMapper.selectByExampleWithBLOBs(example);
    }

}
