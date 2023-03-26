package com.hummer.k8s.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.*;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.fs.FsRequest;
import com.hummer.common.core.domain.request.fs.FsResultItemRequest;
import com.hummer.common.core.domain.request.fs.FsResultRequest;
import com.hummer.common.core.domain.request.fs.FsRuleRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.*;
import com.hummer.common.security.service.TokenService;
import com.hummer.k8s.mapper.*;
import com.hummer.k8s.mapper.ext.ExtFileSystemMapper;
import com.hummer.k8s.mapper.ext.ExtFileSystemResultItemMapper;
import com.hummer.k8s.mapper.ext.ExtFileSystemResultMapper;
import com.hummer.k8s.mapper.ext.ExtFileSystemRuleMapper;
import com.hummer.system.api.IOperationLogService;
import com.hummer.system.api.ISystemProviderService;
import io.kubernetes.client.openapi.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.hummer.k8s.service.ImageService.changeFlowFormat;


/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileSystemService {

    @Autowired
    private ExtFileSystemMapper extFileSystemMapper;
    @Autowired
    private FileSystemMapper fileSystemMapper;
    @Autowired
    private ExtFileSystemRuleMapper extFileSystemRuleMapper;
    @Autowired
    private FileSystemRuleMapper fileSystemRuleMapper;
    @Autowired
    private ExtFileSystemResultMapper extFileSystemResultMapper;
    @Autowired
    private FileSystemResultMapper fileSystemResultMapper;
    @Autowired
    private FileSystemResultLogMapper fileSystemResultLogMapper;
    @Autowired
    private FileSystemResultItemMapper fileSystemResultItemMapper;
    @Autowired
    private ExtFileSystemResultItemMapper extFileSystemResultItemMapper;
    @Autowired
    private ProxyMapper proxyMapper;
    @Autowired
    private TokenService tokenService;
    @DubboReference
    private ISystemProviderService systemProviderService;
    @DubboReference
    private IOperationLogService operationLogService;
    @DubboReference
    private ICloudProviderService cloudProviderService;


    public List<FsDTO> fsList(FsRequest request) {
        return extFileSystemMapper.fsList(request);
    }

    public List<FileSystem> allList() {
        return fileSystemMapper.selectByExample(null);
    }

    public List<FileSystem> allBindList(String sbomVersionId) {
        FileSystemExample example = new FileSystemExample();
        example.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        return fileSystemMapper.selectByExample(example);
    }

    public List<FileSystem> unBindList() {
        return fileSystemMapper.selectByExample(null);
    }

    public FileSystem addFs(MultipartFile multipartFile, FileSystem fileSystem) throws Exception {
        String id = UUIDUtil.newUUID();
        fileSystem.setId(id);
        fileSystem.setCreator(tokenService.getLoginUser().getUserId());
        fileSystem.setCreateTime(System.currentTimeMillis());
        fileSystem.setUpdateTime(System.currentTimeMillis());
        fileSystem.setStatus("VALID");
        if (multipartFile != null) {
            fileSystem = upload(fileSystem, multipartFile, FileSystemConstants.DEFAULT_BASE_DIR);
            fileSystem.setSize(changeFlowFormat(multipartFile.getSize()));
        }

        operationLogService.log(tokenService.getLoginUser().getUser(), fileSystem.getId(), fileSystem.getName(), ResourceTypeConstants.FILE_SYSTEM.name(), ResourceOperation.CREATE, "i18n_create_fs");
        fileSystemMapper.insertSelective(fileSystem);
        return fileSystem;
    }

    public FileSystem updateFs(MultipartFile multipartFile, FileSystem fileSystem) throws Exception {
        fileSystem.setUpdateTime(System.currentTimeMillis());
        fileSystem.setCreator(tokenService.getLoginUser().getUserId());
        fileSystem.setStatus("VALID");
        if (multipartFile != null) {
            fileSystem = upload(fileSystem, multipartFile, FileSystemConstants.DEFAULT_BASE_DIR);
            fileSystem.setSize(changeFlowFormat(multipartFile.getSize()));
        }

        operationLogService.log(tokenService.getLoginUser().getUser(), fileSystem.getId(), fileSystem.getName(), ResourceTypeConstants.FILE_SYSTEM.name(), ResourceOperation.UPDATE, "i18n_update_fs");
        fileSystemMapper.updateByPrimaryKeySelective(fileSystem);
        return fileSystem;
    }

    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件名称
     * @throws Exception
     */
    public static final FileSystem upload(FileSystem fileSystem, MultipartFile file, String dir) throws IOException {
        try {
            String fileName = file.getOriginalFilename();
            String second = FileUploadUtils.uploadForFs(dir, file);
            String path = second + fileName;
            //压缩包解压
            if (fileName.endsWith(".tar")) {
                CommandUtils.extractTarGZ(new File(FileSystemConstants.DEFAULT_BASE_DIR + path), FileSystemConstants.DEFAULT_BASE_DIR + second);
                path = second;
            } else if (fileName.endsWith(".gz")) {
                CommandUtils.extractTarGZ(new File(FileSystemConstants.DEFAULT_BASE_DIR + path), FileSystemConstants.DEFAULT_BASE_DIR + second);
                path = second;
            } else if (fileName.endsWith(".zip")) {
                CommandUtils.extractZip(new File(FileSystemConstants.DEFAULT_BASE_DIR + path), FileSystemConstants.DEFAULT_BASE_DIR + second);
                path = second;
            }
            fileSystem.setFileName(fileName);
            fileSystem.setPath(path);
            fileSystem.setDir(FileSystemConstants.DEFAULT_BASE_DIR + second);
            return fileSystem;
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    public void deleteFs(String id) throws Exception {
        FileSystem fileSystem = fileSystemMapper.selectByPrimaryKey(id);
        fileSystemMapper.deleteByPrimaryKey(id);

        deleteResultByFsId(id);
        operationLogService.log(tokenService.getLoginUser().getUser(), id, fileSystem.getName(), ResourceTypeConstants.FILE_SYSTEM.name(), ResourceOperation.DELETE, "i18n_delete_fs");

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
        FileSystem fileSystem = fileSystemMapper.selectByPrimaryKey(id);
        //检验账号的有效性
        boolean valid = true;
        if (valid) {
            fileSystem.setStatus(CloudAccountConstants.Status.VALID.name());
        } else {
            fileSystem.setStatus(CloudAccountConstants.Status.INVALID.name());
        }
        fileSystemMapper.updateByPrimaryKeySelective(fileSystem);
        return valid;
    }

    public List<FsRuleDTO> ruleList(FsRuleRequest request) {
        return extFileSystemRuleMapper.ruleList(request);
    }

    public int addFsRule(FsRuleRequest request) throws Exception {
        FileSystemRule record = new FileSystemRule();
        BeanUtils.copyBean(record, request);
        record.setId(UUIDUtil.newUUID());
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        operationLogService.log(tokenService.getLoginUser().getUser(), record.getId(), record.getName(), ResourceTypeConstants.FILE_SYSTEM.name(), ResourceOperation.CREATE, "i18n_create_fs_rule");
        return fileSystemRuleMapper.insertSelective(record);
    }

    public void saveRuleTagMapping(String ruleId, String tagKey) {
        deleteRuleTag(null, ruleId);
        if (StringUtils.isNotEmpty(tagKey)) {
            RuleTagMapping sfRulesTagMapping = new RuleTagMapping();
            sfRulesTagMapping.setRuleId(ruleId);
            sfRulesTagMapping.setTagKey(tagKey);
            cloudProviderService.insertRuleTagMappings(sfRulesTagMapping);
        }
    }

    public void deleteRuleTag(String tagkey, String ruleId) {
        if (StringUtils.isNotBlank(tagkey)) {
            cloudProviderService.deleteRuleTag(tagkey);
        }
        if (StringUtils.isNotBlank(ruleId)) {
            RuleTagMappingExample ruleTagMappingExample = new RuleTagMappingExample();
            ruleTagMappingExample.createCriteria().andRuleIdEqualTo(ruleId);
            cloudProviderService.deleteRuleTagMappingByExample(ruleTagMappingExample);
        }
    }

    public int updateFsRule(FsRuleRequest request) throws Exception {
        FileSystemRule record = new FileSystemRule();
        BeanUtils.copyBean(record, request);
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        operationLogService.log(tokenService.getLoginUser().getUser(), record.getId(), record.getName(), ResourceTypeConstants.FILE_SYSTEM.name(), ResourceOperation.UPDATE, "i18n_update_fs_rule");
        return fileSystemRuleMapper.updateByPrimaryKeySelective(record);
    }

    public void deleteFsRule(String id) throws Exception {
        deleteRuleTag(null, id);
        fileSystemRuleMapper.deleteByPrimaryKey(id);
        operationLogService.log(tokenService.getLoginUser().getUser(), id, id, ResourceTypeConstants.FILE_SYSTEM.name(), ResourceOperation.DELETE, "i18n_delete_fs_rule");
    }

    public int changeStatus(FileSystemRule rule) throws Exception {
        return fileSystemRuleMapper.updateByPrimaryKeySelective(rule);
    }

    public List<FsResultDTO> resultList(FsResultRequest request) {
        List<FsResultDTO> list = extFileSystemResultMapper.resultList(request);
        return list;
    }

    public FsResultDTO getFsResult(String resultId) {
        FsResultDTO fsResult = extFileSystemResultMapper.getFsResult(resultId);
        return fsResult;
    }

    public List<FileSystemResultLog> getFsResultLog(String resultId) {
        FileSystemResultLogExample example = new FileSystemResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return fileSystemResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public void deleteRescanFsResult(String id) throws Exception {
        fileSystemResultMapper.deleteByPrimaryKey(id);
    }

    public void deleteFsResult(String id) throws Exception {
        FileSystemResultLogExample logExample = new FileSystemResultLogExample();
        logExample.createCriteria().andResultIdEqualTo(id);
        fileSystemResultLogMapper.deleteByExample(logExample);

        FileSystemResultItemExample itemExample = new FileSystemResultItemExample();
        itemExample.createCriteria().andResultIdEqualTo(id);
        fileSystemResultItemMapper.deleteByExample(itemExample);

        systemProviderService.deleteHistoryFsResult(id);
        fileSystemResultMapper.deleteByPrimaryKey(id);

        operationLogService.log(tokenService.getLoginUser().getUser(), id, id, ResourceTypeConstants.FILE_SYSTEM.name(), ResourceOperation.DELETE, "i18n_delete_fs_result");

    }

    public List<FileSystemResultItemWithBLOBs> resultItemList(FileSystemResultItem fileSystemResultItem) {
        FileSystemResultItemExample example = new FileSystemResultItemExample();
        if(fileSystemResultItem.getPkgName()!=null && !StringUtils.isBlank(fileSystemResultItem.getPkgName())) {
            example.createCriteria().andResultIdEqualTo(fileSystemResultItem.getResultId()).andPkgNameLike("%" + fileSystemResultItem.getPkgName() + "%");
        } else {
            example.createCriteria().andResultIdEqualTo(fileSystemResultItem.getResultId());
        }
        example.setOrderByClause("FIELD(`severity`, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'UNKNOWN')");
        return fileSystemResultItemMapper.selectByExampleWithBLOBs(example);
    }
    public List<FileSystemResultItemWithBLOBs> resultItemListBySearch(FsResultItemRequest request) {
        return extFileSystemResultItemMapper.resultItemListBySearch(request);
    }

    public void scan(String id) throws Exception {
        FileSystem fileSystem = fileSystemMapper.selectByPrimaryKey(id);
        Integer scanId = systemProviderService.insertScanHistory(fileSystem);
        if(StringUtils.equalsIgnoreCase(fileSystem.getStatus(), CloudAccountConstants.Status.VALID.name())) {
            List<FsRuleDTO> ruleList = ruleList(null);
            FileSystemResult result = new FileSystemResult();

            deleteRescanResultByFsId(id);

            for(FsRuleDTO dto : ruleList) {
                BeanUtils.copyBean(result, fileSystem);
                result.setId(UUIDUtil.newUUID());
                result.setFsId(id);
                result.setApplyUser(tokenService.getLoginUser().getUserId());
                result.setCreateTime(System.currentTimeMillis());
                result.setUpdateTime(System.currentTimeMillis());
                result.setRuleId(dto.getId());
                result.setRuleName(dto.getName());
                result.setRuleDesc(dto.getDescription());
                result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
                result.setSeverity(dto.getSeverity());
                result.setUserName(tokenService.getLoginUser().getUser().getName());
                fileSystemResultMapper.insertSelective(result);

                saveFsResultLog(result.getId(), "i18n_start_fs_result", "", true);
                operationLogService.log(tokenService.getLoginUser().getUser(), result.getId(), result.getName(), ResourceTypeConstants.FILE_SYSTEM.name(), ResourceOperation.SCAN, "i18n_start_fs_result");

                systemProviderService.insertScanTaskHistory(result, scanId, fileSystem.getId(), TaskEnum.fsAccount.getType());

                systemProviderService.insertHistoryFileSystemResult(BeanUtils.copyBean(new HistoryFileSystemResult(), result));
            }
        }
    }

    public String reScan(String id) throws Exception {
        FileSystemResult result = fileSystemResultMapper.selectByPrimaryKey(id);

        result.setUpdateTime(System.currentTimeMillis());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        result.setUserName(tokenService.getLoginUser().getUser().getName());
        fileSystemResultMapper.updateByPrimaryKeySelective(result);

        reScanDeleteFileSystemResult(id);

        saveFsResultLog(result.getId(), "i18n_restart_fs_result", "", true);

        operationLogService.log(tokenService.getLoginUser().getUser(), result.getId(), result.getName(), ResourceTypeConstants.FILE_SYSTEM.name(), ResourceOperation.RESCAN, "i18n_restart_fs_result");

        systemProviderService.updateHistoryFileSystemResult(BeanUtils.copyBean(new HistoryFileSystemResult(), result));

        return result.getId();
    }

    public void reScanDeleteFileSystemResult(String id) throws Exception {

        FileSystemResultItemExample example = new FileSystemResultItemExample();
        example.createCriteria().andResultIdEqualTo(id);
        fileSystemResultItemMapper.deleteByExample(example);

    }

    public void deleteRescanResultByFsId(String id) throws Exception {
        FileSystemResultExample example = new FileSystemResultExample();
        example.createCriteria().andFsIdEqualTo(id);
        fileSystemResultMapper.deleteByExample(example);
    }

    public void deleteResultByFsId(String id) throws Exception {
        FileSystemResultExample example = new FileSystemResultExample();
        example.createCriteria().andFsIdEqualTo(id);
        List<FileSystemResult> list = fileSystemResultMapper.selectByExample(example);
        for (FileSystemResult result : list) {
            FileSystemResultLogExample logExample = new FileSystemResultLogExample();
            logExample.createCriteria().andResultIdEqualTo(result.getId());
            fileSystemResultLogMapper.deleteByExample(logExample);

            FileSystemResultItemExample itemExample = new FileSystemResultItemExample();
            itemExample.createCriteria().andResultIdEqualTo(result.getId());
            fileSystemResultItemMapper.deleteByExample(itemExample);

            systemProviderService.deleteHistoryFsResult(result.getId());
        }
        fileSystemResultMapper.deleteByPrimaryKey(id);

        operationLogService.log(tokenService.getLoginUser().getUser(), id, id, ResourceTypeConstants.FILE_SYSTEM.name(), ResourceOperation.DELETE, "i18n_delete_fs_result");
    }

    public void saveFsResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        FileSystemResultLog log = new FileSystemResultLog();
        String operator = "system";
        try {
            if (tokenService.getLoginUser().getUser() != null) {
                operator = tokenService.getLoginUser().getUser().getId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        log.setOperator(operator);
        log.setResultId(resultId);
        log.setCreateTime(System.currentTimeMillis());
        log.setOperation(operation);
        log.setOutput(output);
        log.setResult(result);
        fileSystemResultLogMapper.insertSelective(log);

    }

    public void createScan (FileSystemResult result) throws Exception {
        try {
            FsRuleRequest request = new FsRuleRequest();
            request.setId(result.getRuleId());
            FsRuleDTO dto = ruleList(request).get(0);
            FileSystem fileSystem = fileSystemMapper.selectByPrimaryKey(result.getFsId());
            String script = dto.getScript();
            JSONArray jsonArray = JSON.parseArray(dto.getParameter());
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                String key = "${{" + jsonObject.getString("key") + "}}";
                if (script.contains(key)) {
                    script = script.replace(key, jsonObject.getString("defaultValue"));
                }
            }
            String returnJson = "";

            String command = execute(fileSystem).getCommand();
            result.setCommand(command);
            returnJson = ReadFileUtils.readToBuffer(fileSystem.getDir() + TrivyConstants.TRIVY_JSON);
            result.setUpdateTime(System.currentTimeMillis());
            result.setReturnJson(returnJson);
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());

            long count = saveResultItem(result);
            result.setReturnSum(count);
            fileSystemResultMapper.updateByPrimaryKeySelective(result);

            systemProviderService.createFsMessageOrder(result);
            saveFsResultLog(result.getId(), "i18n_end_fs_result", "", true);

            systemProviderService.updateHistoryFileSystemResult(BeanUtils.copyBean(new HistoryFileSystemResult(), result));
        } catch (Exception e) {
            LogUtil.error("create FileSystemResult: " + e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            fileSystemResultMapper.updateByPrimaryKeySelective(result);
            systemProviderService.updateHistoryFileSystemResult(BeanUtils.copyBean(new HistoryFileSystemResult(), result));
            saveFsResultLog(result.getId(), "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false);
        }
    }

    public ResultDTO execute(FileSystem fileSystem) throws Exception {
        try{
            Proxy proxy = new Proxy();
            if (fileSystem.getProxyId()!=null) {
                proxy = proxyMapper.selectByPrimaryKey(fileSystem.getProxyId());
            }
            ScanSetting scanSetting = new ScanSetting();
            String skipDbUpdate = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.SkipDbUpdate.getKey());
            String securityChecks = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.SecurityChecks.getKey());
            String ignoreUnfixed = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.IgnoreUnfixed.getKey());
            String offlineScan = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.OfflineScan.getKey());
            String severity = systemProviderService.getSystemParameterValue(ParamConstants.SCAN.Severity.getKey());
            scanSetting.setSkipDbUpdate(skipDbUpdate);
            scanSetting.setSecurityChecks(securityChecks);
            scanSetting.setIgnoreUnfixed(ignoreUnfixed);
            scanSetting.setOfflineScan(offlineScan);
            scanSetting.setSeverity(severity);
            ResultDTO resultDTO = execute(fileSystem, proxy, scanSetting);
            if (resultDTO.getResultStr().contains("ERROR") || resultDTO.getResultStr().contains("error")) {
                throw new Exception(resultDTO.getResultStr());
            }
            return resultDTO;
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ResultDTO execute(Object... obj) throws Exception {
        FileSystem fileSystem = (FileSystem) obj[0];
        try {
            String _proxy = "";
            if (fileSystem.getProxyId() != null) {
                Proxy proxy = (Proxy) obj[1];
                _proxy = ProxyUtil.isProxy(proxy);
            }
            ScanSetting scanSetting = (ScanSetting) obj[2];
            String str = "";
            if(scanSetting.getSkipDbUpdate() != null && StringUtils.equalsIgnoreCase(scanSetting.getSkipDbUpdate(), "true")) {
                str = str + TrivyConstants.SKIP_DB_UPDATE + TrivyConstants.SKIP_JAVA_DB_UPDATE;
            }
            if(scanSetting.getIgnoreUnfixed() != null && StringUtils.equalsIgnoreCase(scanSetting.getIgnoreUnfixed(), "true")) {
                str = str + TrivyConstants.UNFIXED;
            }
            if(scanSetting.getSecurityChecks() != null) {
                str = str + TrivyConstants.SECURITY_CHECKS + scanSetting.getSecurityChecks();
            } else {
                str = str + TrivyConstants.SECURITY_CHECKS_DEFAULT;
            }
            if(scanSetting.getOfflineScan() != null && StringUtils.equalsIgnoreCase(scanSetting.getOfflineScan(), "true")) {
                str = str + TrivyConstants.OFFLINE_SCAN;
            }
            CommandUtils.commonExecCmdWithResult(TrivyConstants.TRIVY_RM + TrivyConstants.TRIVY_JSON, fileSystem.getDir());
            String command = _proxy + TrivyConstants.TRIVY_FS + str + FileSystemConstants.DEFAULT_BASE_DIR + fileSystem.getPath() + TrivyConstants.TRIVY_TYPE + fileSystem.getDir() + TrivyConstants.TRIVY_JSON + TrivyConstants.TRIVY_SERVER;
            LogUtil.info(fileSystem.getId() + " {fileSystem scan}[command]: " + fileSystem.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, fileSystem.getDir());
            ResultDTO dto = new ResultDTO();
            dto.setCommand(command);
            dto.setResultStr(resultStr);
            return dto;
        } catch (Exception e) {
            throw e;
        }
    }

    long saveResultItem(FileSystemResult result) throws Exception {

        int i = 0;
        //插入returnJson
        JSONObject jsonG = JSONObject.parseObject(result.getReturnJson());
        if (jsonG != null) {
            JSONArray returnJson = JSONArray.parseArray(jsonG.getString("Results"));
            if(returnJson != null) {
                for (Object obj : returnJson) {
                    JSONObject jsonObject = (JSONObject) obj;
                    JSONArray vulnerabilities = JSONArray.parseArray(jsonObject.getString("Vulnerabilities"));
                    if(vulnerabilities == null) continue;
                    for (Object o : vulnerabilities) {
                        JSONObject resultObject = (JSONObject) o;
                        FileSystemResultItemWithBLOBs item = new FileSystemResultItemWithBLOBs();
                        item.setId(UUIDUtil.newUUID());
                        item.setResultId(result.getId());
                        item.setVulnerabilityId(resultObject.getString("VulnerabilityID"));
                        item.setPkgName(resultObject.getString("PkgName"));
                        item.setInstalledVersion(resultObject.getString("InstalledVersion"));
                        item.setFixedVersion(resultObject.getString("FixedVersion"));
                        item.setLayer(resultObject.getString("Layer"));
                        item.setSeveritySource(resultObject.getString("SeveritySource"));
                        item.setPrimaryUrl(resultObject.getString("PrimaryURL"));
                        item.setDataSource(resultObject.getString("DataSource"));
                        item.setTitle(resultObject.getString("Title"));
                        item.setDescription(resultObject.getString("Description"));
                        item.setSeverity(resultObject.getString("Severity"));
                        item.setCweIds(resultObject.getString("CweIDs"));
                        item.setCvss(resultObject.getString("CVSS"));
                        item.setReferences(resultObject.getString("References"));
                        item.setPublishedDate(resultObject.getString("PublishedDate"));
                        item.setLastModifiedDate(resultObject.getString("LastModifiedDate"));
                        item.setCreateTime(System.currentTimeMillis());
                        fileSystemResultItemMapper.insertSelective(item);
                        i++;
                    }

                }
            }
        }

        return i;

    }

    public Map<String, Object> topInfo(Map<String, Object> params) {
        return extFileSystemMapper.topInfo(params);
    }

    public List<Map<String, Object>> projectChart() {
        return extFileSystemMapper.projectChart();
    }

    public List<Map<String, Object>> severityChart() {
        return extFileSystemMapper.severityChart();
    }

    public String download(Map<String, Object> map) {
        HistoryFileSystemResult result = systemProviderService.fsResult(map.get("id").toString());
        String str = result.getReturnJson();
        return str;
    }

    public List<HistoryFsResultDTO> history(Map<String, Object> params) {
        List<HistoryFsResultDTO> historyList = systemProviderService.fsHistory(params);
        return historyList;
    }

    public List<FileSystemResultItemWithBLOBs> historyResultItemList(FileSystemResultItem item) {
        FileSystemResultItemExample example = new FileSystemResultItemExample();
        example.createCriteria().andResultIdEqualTo(item.getResultId());
        example.setOrderByClause("FIELD(`severity`, 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW', 'UNKNOWN')");
        return fileSystemResultItemMapper.selectByExampleWithBLOBs(example);
    }

    public void deleteHistoryFsResult(String id) throws Exception {
        systemProviderService.deleteHistoryFsResult(id);
    }

}
