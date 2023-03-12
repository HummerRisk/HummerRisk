package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtServerCertificateMapper;
import com.hummerrisk.base.mapper.ext.ExtServerMapper;
import com.hummerrisk.base.mapper.ext.ExtServerResultMapper;
import com.hummerrisk.base.mapper.ext.ExtServerRuleMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.rule.BindRuleRequest;
import com.hummerrisk.controller.request.rule.ScanGroupRequest;
import com.hummerrisk.controller.request.server.ServerCertificateRequest;
import com.hummerrisk.controller.request.server.ServerRequest;
import com.hummerrisk.controller.request.server.ServerResultRequest;
import com.hummerrisk.controller.request.server.ServerRuleRequest;
import com.hummerrisk.dto.*;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.proxy.server.SshUtil;
import com.hummerrisk.proxy.server.WinRMHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ServerService {

    @Resource
    private ExtServerMapper extServerMapper;
    @Resource
    private ServerMapper serverMapper;
    @Resource
    private ServerGroupMapper serverGroupMapper;
    @Resource
    private ServerRuleMapper serverRuleMapper;
    @Resource
    private ExtServerRuleMapper extServerRuleMapper;
    @Resource
    private RuleTagMapper ruleTagMapper;
    @Resource
    private RuleTagMappingMapper ruleTagMappingMapper;
    @Resource
    private ExtServerResultMapper extServerResultMapper;
    @Resource
    private ServerResultMapper serverResultMapper;
    @Resource
    private ServerResultLogMapper serverResultLogMapper;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private NoticeService noticeService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ServerCertificateMapper serverCertificateMapper;
    @Resource
    private ExtServerCertificateMapper extServerCertificateMapper;
    @Resource
    private HistoryServerResultMapper historyServerResultMapper;
    @Resource
    private RuleGroupMappingMapper ruleGroupMappingMapper;
    @Resource
    private RuleGroupMapper ruleGroupMapper;

    private String messageOrderId = "";

    public List<ServerValidateDTO> validate(List<String> ids) {
        List<ServerValidateDTO> list = new ArrayList<>();
        ids.forEach(id -> {
            try {
                Proxy proxy = new Proxy();
                Server server = serverMapper.selectByPrimaryKey(id);
                if (server.getIsProxy() != null && server.getIsProxy()) {
                    proxy = proxyMapper.selectByPrimaryKey(server.getProxyId());
                }
                BeanUtils.copyBean(server, validateAccount(server, proxy).getServer());
                serverMapper.updateByPrimaryKeySelective(server);
            } catch (Exception e) {
                throw new HRException(Translator.get("failed_server") + e.getMessage());
            }
        });
        return list;
    }


    public ServerValidateDTO validate(String id) throws Exception {
        Server server = serverMapper.selectByPrimaryKey(id);
        //检验主机的有效性
        ServerValidateDTO valid = validateAccount(server);
        BeanUtils.copyBean(server, valid.getServer());
        serverMapper.updateByPrimaryKeySelective(server);
        return valid;
    }

    public void scan(ScanGroupRequest request) throws Exception {
        Server server = serverMapper.selectByPrimaryKey(request.getAccountId());
        Integer scanId = historyService.insertScanHistory(server);
        for (Integer groupId : request.getGroups()) {
            this.scanGroups(server, scanId, groupId.toString());
        }
    }

    public void createScan(ServerResult result) throws Exception {
        ServerRuleRequest request = new ServerRuleRequest();
        request.setId(result.getRuleId());
        ServerRuleDTO dto = ruleList(request).get(0);
        Server server = serverMapper.selectByPrimaryKey(result.getServerId());
        try {
            String script = dto.getScript();
            JSONArray jsonArray = JSON.parseArray(dto.getParameter());
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                String key = "${{" + jsonObject.getString("key") + "}}";
                if (script.contains(key)) {
                    script = script.replace(key, jsonObject.getString("defaultValue"));
                }
            }
            Proxy proxy = new Proxy();
            if (server.getIsProxy() != null && server.getIsProxy()) {
                proxy = proxyMapper.selectByPrimaryKey(server.getProxyId());
            }
            String returnLog = execute(server, script, proxy, jsonArray);
            if (returnLog.contains(ServerConstants.HUMMER_SUCCESS)) {
                result.setIsSeverity(true);
            } else if (returnLog.contains(ServerConstants.HUMMER_ERROR)) {
                result.setIsSeverity(false);
            } else if (StringUtils.isBlank(returnLog)) {
                returnLog = ServerConstants.HUMMER_ERROR + ": 没有获取到返回值";
                result.setIsSeverity(false);
            }
            result.setCommand(script);
            result.setReturnLog(returnLog);
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());
            serverResultMapper.updateByPrimaryKeySelective(result);

            noticeService.createServerMessageOrderItem(result, this.messageOrderId);

            saveServerResultLog(result.getId(), "i18n_end_server_result", returnLog, result.getIsSeverity());

            historyService.updateHistoryServerResult(BeanUtils.copyBean(new HistoryServerResult(), result));
        } catch (Exception e) {
            LogUtil.error(result.getServerName() + "{}" + result.getIp() + "[error]: " + e.getMessage());
            result.setReturnLog(result.getServerName() + "{}" + result.getIp() + "[error]: " + e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            serverResultMapper.updateByPrimaryKeySelective(result);
            historyService.updateHistoryServerResult(BeanUtils.copyBean(new HistoryServerResult(), result));
            noticeService.createServerMessageOrderItem(result, this.messageOrderId);
            saveServerResultLog(result.getId(), "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false);
        }
    }

    public String rescan(String id) throws Exception {
        ServerResult result = serverResultMapper.selectByPrimaryKey(id);
        saveServerResultLog(result.getId(), "i18n_restart_server_result", "", true);
        result.setUpdateTime(System.currentTimeMillis());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        serverResultMapper.updateByPrimaryKeySelective(result);
        OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getServerName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.RESCAN, "i18n_restart_server_result");
        return result.getId();
    }

    public void deleteServerResultById(String id) {
        ServerResultExample example = new ServerResultExample();
        example.createCriteria().andServerIdEqualTo(id);//serverId
        serverResultMapper.deleteByExample(example);
    }

    public void deleteServerResult(String id) {
        serverResultMapper.deleteByPrimaryKey(id);
    }

    public void saveServerResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        ServerResultLogWithBLOBs serverResultLog = new ServerResultLogWithBLOBs();
        String operator = "system";
        try {
            if (SessionUtils.getUser() != null) {
                operator = SessionUtils.getUser().getId();
            }
        } catch (Exception e) {
            //防止单元测试无session
        }
        serverResultLog.setOperator(operator);
        serverResultLog.setResultId(resultId);
        serverResultLog.setCreateTime(System.currentTimeMillis());
        serverResultLog.setOperation(operation);
        serverResultLog.setOutput(output);
        serverResultLog.setResult(result);
        serverResultLogMapper.insertSelective(serverResultLog);

    }

    private ServerValidateDTO validateAccount(Server server) {
        ServerValidateDTO serverValidateDTO = new ServerValidateDTO();
        try {
            Proxy proxy = new Proxy();
            if (server.getIsProxy() != null && server.getIsProxy()) {
                proxy = proxyMapper.selectByPrimaryKey(server.getProxyId());
            }
            serverValidateDTO = validateAccount(server, proxy);
            return serverValidateDTO;
        } catch (Exception e) {
            LogUtil.error(String.format("HRException in verifying server, server: [%s], ip: [%s], error information:%s", server.getName(), server.getIp(), e.getMessage()), e);
            serverValidateDTO.setFlag(false);
            serverValidateDTO.setMessage(String.format("HRException in verifying server, server: [%s], ip: [%s], error information:%s", server.getName(), server.getIp(), e.getMessage()));
            serverValidateDTO.setServer(server);
            return serverValidateDTO;
        }
    }

    public List<ServerGroup> getServerGroupList() {
        return serverGroupMapper.selectByExample(null);
    }

    public List<Server> allServerList() {
        return serverMapper.selectByExampleWithBLOBs(null);
    }

    public List<ServerDTO> getServerList(ServerRequest server) {
        return extServerMapper.getServerList(server);
    }

    public Server getServer(String id) {
        return serverMapper.selectByPrimaryKey(id);
    }

    public int addServerGroup(ServerGroup serverGroup) {
        serverGroup.setId(UUIDUtil.newUUID());
        serverGroup.setCreator(SessionUtils.getUserId());
        serverGroup.setCreateTime(System.currentTimeMillis());
        serverGroup.setUpdateTime(System.currentTimeMillis());
        OperationLogService.log(SessionUtils.getUser(), serverGroup.getId(), serverGroup.getId(), ResourceTypeConstants.SERVER.name(), ResourceOperation.CREATE, "i18n_create_server_group");

        return serverGroupMapper.insertSelective(serverGroup);
    }

    public int editServerGroup(ServerGroup serverGroup) {
        serverGroup.setUpdateTime(System.currentTimeMillis());
        OperationLogService.log(SessionUtils.getUser(), serverGroup.getId(), serverGroup.getId(), ResourceTypeConstants.SERVER.name(), ResourceOperation.UPDATE, "i18n_update_server_group");
        return serverGroupMapper.updateByPrimaryKeySelective(serverGroup);
    }

    public void deleteServerGroup(ServerGroup serverGroup) {

        ServerExample example = new ServerExample();
        example.createCriteria().andServerGroupIdEqualTo(serverGroup.getId());
        List<Server> list = serverMapper.selectByExample(example);
        if (list.size() > 0) {
            HRException.throwException(Translator.get("i18n_ex_server_validate"));
        } else {
            serverGroupMapper.deleteByPrimaryKey(serverGroup.getId());
        }
        OperationLogService.log(SessionUtils.getUser(), serverGroup.getId(), serverGroup.getId(), ResourceTypeConstants.SERVER.name(), ResourceOperation.DELETE, "i18n_delete_server_group");

    }

    public ServerValidateDTO addServer(MultipartFile keyFile, Server server) throws Exception {
        String id = UUIDUtil.newUUID();
        server.setId(id);
        server.setCreator(SessionUtils.getUserId());
        server.setCreateTime(System.currentTimeMillis());
        server.setUpdateTime(System.currentTimeMillis());
        Proxy proxy = new Proxy();
        if (server.getIsProxy() != null && server.getIsProxy()) {
            proxy = proxyMapper.selectByPrimaryKey(server.getProxyId());
        }
        if (server.getIsCertificate() != null) {
            if (!server.getIsCertificate()) {
                if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "file")) {
                    String keyFilePath = upload(keyFile, ServerConstants.DEFAULT_BASE_DIR);
                    String publicKey = ReadFileUtils.readToBuffer(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
                    server.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
                    server.setPublicKey(publicKey);
                } else if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "str")) {
                    String uuid = UUIDUtil.newUUID();
                    CommandUtils.saveAsFile(server.getPublicKey(), ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/", ServerConstants.HUMMER_RSA, false);
                    server.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/" + ServerConstants.HUMMER_RSA);
                }
            } else {
                ServerCertificate serverCertificate = serverCertificateMapper.selectByPrimaryKey(server.getCertificateId());
                server.setIsPublicKey(serverCertificate.getIsPublicKey());
                if (StringUtils.equalsIgnoreCase(serverCertificate.getIsPublicKey(), "no")) {
                    server.setPassword(serverCertificate.getPassword());
                } else {
                    server.setPublicKey(serverCertificate.getPublicKey());
                    server.setPublicKeyPath(serverCertificate.getPublicKeyPath());
                }
            }
        } else {
            server.setIsCertificate(false);
        }

        ServerValidateDTO serverValidateDTO = validateAccount(server, proxy);

        OperationLogService.log(SessionUtils.getUser(), server.getId(), server.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.CREATE, "i18n_create_server");
        serverMapper.insertSelective(serverValidateDTO.getServer());
        return serverValidateDTO;
    }

    public ServerValidateDTO editServer(MultipartFile keyFile, Server server) throws Exception {
        server.setUpdateTime(System.currentTimeMillis());
        Proxy proxy = new Proxy();
        if (server.getIsProxy() != null && server.getIsProxy()) {
            proxy = proxyMapper.selectByPrimaryKey(server.getProxyId());
        }

        if (server.getIsCertificate() != null) {
            if (!server.getIsCertificate()) {
                if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "file")) {
                    String keyFilePath = upload(keyFile, ServerConstants.DEFAULT_BASE_DIR);
                    String publicKey = ReadFileUtils.readToBuffer(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
                    server.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
                    server.setPublicKey(publicKey);
                } else if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "str")) {
                    String uuid = UUIDUtil.newUUID();
                    CommandUtils.saveAsFile(server.getPublicKey(), ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/", ServerConstants.HUMMER_RSA, false);
                    server.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/" + ServerConstants.HUMMER_RSA);
                }
            } else {
                ServerCertificate serverCertificate = serverCertificateMapper.selectByPrimaryKey(server.getCertificateId());
                server.setIsPublicKey(serverCertificate.getIsPublicKey());
                if (StringUtils.equalsIgnoreCase(serverCertificate.getIsPublicKey(), "no")) {
                    server.setPassword(serverCertificate.getPassword());
                } else {
                    server.setPublicKey(serverCertificate.getPublicKey());
                    server.setPublicKeyPath(serverCertificate.getPublicKeyPath());
                }
            }
        } else {
            server.setIsCertificate(false);
        }

        ServerValidateDTO serverValidateDTO = validateAccount(server, proxy);

        OperationLogService.log(SessionUtils.getUser(), server.getId(), server.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.UPDATE, "i18n_update_server");
        serverMapper.updateByPrimaryKeySelective(serverValidateDTO.getServer());
        return serverValidateDTO;
    }

    public ServerValidateDTO copyServer(MultipartFile keyFile, Server server) throws Exception {
        String id = UUIDUtil.newUUID();
        server.setId(id);
        server.setUpdateTime(System.currentTimeMillis());
        Proxy proxy = new Proxy();
        if (server.getIsProxy() != null && server.getIsProxy()) {
            proxy = proxyMapper.selectByPrimaryKey(server.getProxyId());
        }

        if (server.getIsCertificate() != null) {
            if (!server.getIsCertificate()) {
                if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "file")) {
                    String keyFilePath = upload(keyFile, ServerConstants.DEFAULT_BASE_DIR);
                    String publicKey = ReadFileUtils.readToBuffer(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
                    server.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
                    server.setPublicKey(publicKey);
                } else if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "str")) {
                    String uuid = UUIDUtil.newUUID();
                    CommandUtils.saveAsFile(server.getPublicKey(), ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/", ServerConstants.HUMMER_RSA, false);
                    server.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/" + ServerConstants.HUMMER_RSA);
                }
            } else {
                ServerCertificate serverCertificate = serverCertificateMapper.selectByPrimaryKey(server.getCertificateId());
                server.setIsPublicKey(serverCertificate.getIsPublicKey());
                if (StringUtils.equalsIgnoreCase(serverCertificate.getIsPublicKey(), "no")) {
                    server.setPassword(serverCertificate.getPassword());
                } else {
                    server.setPublicKey(serverCertificate.getPublicKey());
                    server.setPublicKeyPath(serverCertificate.getPublicKeyPath());
                }
            }
        } else {
            server.setIsCertificate(false);
        }

        ServerValidateDTO serverValidateDTO = validateAccount(server, proxy);

        OperationLogService.log(SessionUtils.getUser(), server.getId(), server.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.COPY, "i18n_copy_server");
        serverMapper.insertSelective(serverValidateDTO.getServer());
        return serverValidateDTO;
    }

    public void deleteServer(String id) throws Exception {
        serverMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.SERVER.name(), ResourceOperation.DELETE, "i18n_delete_server");
    }

    public ServerValidateDTO validateAccount(Server server, Proxy proxy) throws Exception {
        ServerValidateDTO serverValidateDTO = new ServerValidateDTO();
        switch (server.getType()) {
            case "linux":
                try {
                    SshUtil.validateSsh2(server, proxy);
                    server.setStatus(CloudAccountConstants.Status.VALID.name());
                    server.setAuthType("ssh2");
                    serverValidateDTO.setFlag(true);
                    serverValidateDTO.setMessage("Verification succeeded!");
                } catch (Exception e) {
                    try {
                        SshUtil.validateSshd(server, proxy);
                        server.setStatus(CloudAccountConstants.Status.VALID.name());
                        server.setAuthType("sshd");
                        serverValidateDTO.setFlag(true);
                        serverValidateDTO.setMessage("Verification succeeded!");
                    } catch (Exception ex) {
                        server.setStatus(CloudAccountConstants.Status.INVALID.name());
                        server.setAuthType("sshd");
                        serverValidateDTO.setFlag(false);
                        serverValidateDTO.setMessage(String.format("HRException in verifying server, server: [%s], ip: [%s], error information:%s", server.getName(), server.getIp(), ex.getMessage()));
                    }
                }
                break;
            case "windows":
                try {
                    WinRMHelper.validateWindows(server, proxy);
                    server.setStatus(CloudAccountConstants.Status.VALID.name());
                    server.setAuthType("winrm");
                    serverValidateDTO.setFlag(true);
                    serverValidateDTO.setMessage("Verification succeeded!");
                } catch (Exception e) {
                    server.setStatus(CloudAccountConstants.Status.INVALID.name());
                    server.setAuthType("winrm");
                    serverValidateDTO.setFlag(false);
                    serverValidateDTO.setMessage(String.format("HRException in verifying server, server: [%s], ip: [%s], error information:%s", server.getName(), server.getIp(), e.getMessage()));
                }
                break;
        }
        serverValidateDTO.setServer(server);
        return serverValidateDTO;
    }

    public String execute(Server server, String cmd, Proxy proxy, JSONArray jsonArray) throws Exception {
        try {
            switch (server.getType()) {
                case "linux":
                    if (StringUtils.equalsIgnoreCase(server.getAuthType(), "ssh2")) {
                        return SshUtil.executeSsh2(SshUtil.loginSsh2(server, proxy), cmd);
                    } else {
                        return SshUtil.executeSshd(SshUtil.loginSshd(server, proxy), cmd);
                    }
                case "windows":
                    String result = WinRMHelper.execute(server, cmd);
                    String hummerSuccess = "", hummerError = "";
                    for (Object o : jsonArray) {
                        JSONObject jsonObject = (JSONObject) o;
                        String key = jsonObject.getString("key");
                        if (StringUtils.equalsIgnoreCase(key, "HummerSuccess")) {
                            hummerSuccess = jsonObject.getString("defaultValue");
                        } else if (StringUtils.equalsIgnoreCase(key, "HummerError")) {
                            hummerError = jsonObject.getString("defaultValue");
                        }
                    }
                    if (result.contains(ServerConstants.HUMMER_SUCCESS)) {
                        result = result.replace("HummerSuccess", hummerSuccess);
                    } else if (result.contains(ServerConstants.HUMMER_ERROR)) {
                        result = result.replace("HummerError", hummerError);
                    }
                    return result;
                default:
                    return "Unexpected value: type";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public List<ServerRuleDTO> ruleList(ServerRuleRequest request) {
        return extServerRuleMapper.ruleList(request);
    }

    public int addServerRule(ServerRuleRequest request) throws Exception {
        ServerRule record = new ServerRule();
        BeanUtils.copyBean(record, request);
        record.setId(UUIDUtil.newUUID());
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        saveRuleGroupMapping(request.getId(), request.getGroups());
        return serverRuleMapper.insertSelective(record);
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

    public void saveRuleGroupMapping(String ruleId, List<String> ruleGroups) {
        deleteRuleGroupMapping(ruleId);
        if (ruleGroups.isEmpty()) {
            return;
        }
        for (String ruleGroup : ruleGroups) {
            RuleGroupMapping ruleGroupMapping = new RuleGroupMapping();
            ruleGroupMapping.setRuleId(ruleId);
            ruleGroupMapping.setGroupId(ruleGroup);
            ruleGroupMappingMapper.insertSelective(ruleGroupMapping);
        }
    }

    public void deleteRuleGroupMapping(String ruleId) {
        if (StringUtils.isNotBlank(ruleId)) {
            RuleGroupMappingExample example = new RuleGroupMappingExample();
            example.createCriteria().andRuleIdEqualTo(ruleId);
            ruleGroupMappingMapper.deleteByExample(example);
        }
    }

    public void deleteRuleTag(String tagkey, String ruleId) {
        if (StringUtils.isNotBlank(tagkey)) {
            ruleTagMapper.deleteByPrimaryKey(tagkey);
        }
        if (StringUtils.isNotBlank(ruleId)) {
            RuleTagMappingExample ruleTagMappingExample = new RuleTagMappingExample();
            ruleTagMappingExample.createCriteria().andRuleIdEqualTo(ruleId);
            ruleTagMappingMapper.deleteByExample(ruleTagMappingExample);
        }
    }

    public int updateServerRule(ServerRuleRequest request) throws Exception {
        ServerRule record = new ServerRule();
        BeanUtils.copyBean(record, request);
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
        saveRuleGroupMapping(request.getId(), request.getGroups());
        return serverRuleMapper.updateByPrimaryKeySelective(record);
    }

    public void deleteServerRule(String id) throws Exception {
        deleteRuleTag(null, id);
        serverRuleMapper.deleteByPrimaryKey(id);
    }

    public int changeStatus(ServerRule rule) throws Exception {
        return serverRuleMapper.updateByPrimaryKeySelective(rule);
    }

    public List<ServerResultDTO> resultList(ServerResultRequest request) {
        return extServerResultMapper.resultList(request);
    }

    public List<ServerListDTO> resultServerList(ServerRequest request) {
        return extServerResultMapper.resultServerList(request);
    }

    public ServerResultDTO getServerResult(String resultId) {
        ServerResultRequest request = new ServerResultRequest();
        request.setId(resultId);
        return extServerResultMapper.resultList(request) != null ? extServerResultMapper.resultList(request).get(0) : new ServerResultDTO();
    }

    public List<ServerResultLogWithBLOBs> getServerResultLog(String resultId) {
        ServerResultLogExample example = new ServerResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return serverResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public List<ServerCertificate> allCertificateList() {
        return serverCertificateMapper.selectByExampleWithBLOBs(null);
    }

    public List<ServerCertificateDTO> certificateList(ServerCertificateRequest request) {
        return extServerCertificateMapper.certificateList(request);
    }

    public int addCertificate(MultipartFile keyFile, ServerCertificate certificate) throws Exception {
        String id = UUIDUtil.newUUID();
        certificate.setId(id);
        certificate.setCreator(SessionUtils.getUserId());
        certificate.setLastModified(System.currentTimeMillis());

        if (StringUtils.equalsIgnoreCase(certificate.getIsPublicKey(), "file")) {
            String keyFilePath = upload(keyFile, ServerConstants.DEFAULT_BASE_DIR);
            String publicKey = ReadFileUtils.readToBuffer(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
            certificate.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
            certificate.setPublicKey(publicKey);
        } else if (StringUtils.equalsIgnoreCase(certificate.getIsPublicKey(), "str")) {
            String uuid = UUIDUtil.newUUID();
            CommandUtils.saveAsFile(certificate.getPublicKey(), ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/", ServerConstants.HUMMER_RSA, false);
            certificate.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/" + ServerConstants.HUMMER_RSA);
        }

        OperationLogService.log(SessionUtils.getUser(), certificate.getId(), certificate.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.CREATE, "i18n_create_server_certificate");
        return serverCertificateMapper.insertSelective(certificate);
    }

    public int editCertificate(MultipartFile keyFile, ServerCertificate certificate) throws Exception {
        certificate.setLastModified(System.currentTimeMillis());
        if (StringUtils.equalsIgnoreCase(certificate.getIsPublicKey(), "file")) {
            String keyFilePath = upload(keyFile, ServerConstants.DEFAULT_BASE_DIR);
            String publicKey = ReadFileUtils.readToBuffer(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
            certificate.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
            certificate.setPublicKey(publicKey);
        } else if (StringUtils.equalsIgnoreCase(certificate.getIsPublicKey(), "str")) {
            String uuid = UUIDUtil.newUUID();
            CommandUtils.saveAsFile(certificate.getPublicKey(), ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/", ServerConstants.HUMMER_RSA, false);
            certificate.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/" + ServerConstants.HUMMER_RSA);
        }

        OperationLogService.log(SessionUtils.getUser(), certificate.getId(), certificate.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.UPDATE, "i18n_update_server_certificate");
        return serverCertificateMapper.updateByPrimaryKeySelective(certificate);
    }

    public void deleteCertificate(String id) throws Exception {
        serverCertificateMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.SERVER.name(), ResourceOperation.DELETE, "i18n_delete_server_certificate");
    }

    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件名称
     * @throws Exception
     */
    public static final String upload(MultipartFile file, String dir) throws IOException {
        try {
            String fileName = file.getOriginalFilename();
            String extension = StringUtils.isNotBlank(fileName) && fileName.contains(".") ? fileName.split("\\.")[fileName.split("\\.").length - 1] : "";
            //png、html等小文件存放路径，页面需要显示，项目内目录
            //jar包等大文件存放路径，项目外目录
            return FileUploadUtils.uploadCertificate(dir, file, extension.contains(".") ? "." + extension : "");
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    public Map<String, Object> topInfo(Map<String, Object> params) {
        return extServerMapper.topInfo(params);
    }

    public List<Map<String, Object>> serverChart() {
        return extServerResultMapper.serverChart();
    }

    public List<Map<String, Object>> severityChart() {
        return extServerResultMapper.severityChart();
    }

    public ChartDTO serverLineChart(Map<String, Object> params) {
        ChartDTO codeChartDTO = new ChartDTO();
        List<String> xAxis = extServerResultMapper.serverChartX(params);
        List<Integer> yAxis = extServerResultMapper.serverChartY(params);
        codeChartDTO.setXAxis(xAxis);
        codeChartDTO.setYAxis(yAxis);
        return codeChartDTO;
    }

    public List<HistoryServerResultDTO> history(Map<String, Object> params) {
        List<HistoryServerResultDTO> historyList = extServerResultMapper.history(params);
        return historyList;
    }

    public void deleteHistoryServerResult(String id) throws Exception {
        historyServerResultMapper.deleteByPrimaryKey(id);
    }

    public void insertExperts(MultipartFile excelFile, Server request) throws Exception {
        if (excelFile == null || excelFile.getSize() == 0) {
            LogUtil.error("文件上传错误，重新上传");
        }
        String filename = excelFile.getOriginalFilename();
        if (!(filename.endsWith(".xls") || filename.endsWith(".xlsx"))) {
            LogUtil.error("文件上传格式错误，请重新上传");
        }

        List<Server> list = new ArrayList<>();
        try {
            if (filename.endsWith(".xls")) {
                list = readXLS(excelFile);
            } else {
                list = readXLSX(excelFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.error("文件内容读取失败，请重试");
        }
        //查询主机库数据
        List<Server> professorListModelList = serverMapper.selectByExample(null);
        List<Server> excelList = list.stream().filter(e -> e.getIp() != null).collect(Collectors.toList());
        //过滤出主机IP不存在的主机
        List<Server> newExpertList = excelList.stream().filter(new Predicate<Server>() {
            @Override
            public boolean test(Server server) {
                for (Server professorListModel : professorListModelList) {
                    if (server.getIp().equals(professorListModel.getIp())) {
                        return false;
                    }
                }
                return true;
            }
        }).collect(Collectors.toList());
        if (!newExpertList.isEmpty()) {
            for (Server server : newExpertList) {
                server.setId(UUIDUtil.newUUID());
                server.setCreateTime(System.currentTimeMillis());
                server.setUpdateTime(System.currentTimeMillis());
                server.setCreator(SessionUtils.getUserId());
                server.setIsCertificate(false);
                server.setIsPublicKey("no");
                server.setIsProxy(false);
                server.setPluginIcon("server.png");
                server.setServerGroupId(request.getServerGroupId());
                server.setStatus(CloudAccountConstants.Status.UNLINK.name());
                serverMapper.insertSelective(server);
            }
        }
    }

    public List<Server> readXLS(MultipartFile file) throws IOException {
        List<Server> list = new ArrayList<>();

        InputStream inputStream = file.getInputStream();
        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(inputStream);
        HSSFWorkbook workbook = new HSSFWorkbook(poifsFileSystem);

        //读取第一张sheet
        HSSFSheet sheet = workbook.getSheetAt(0);
        //遍历每一行Excel获取内容
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            HSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                Server expert = new Server();
                // 名称
                // Row.MissingCellPolicy.CREATE_NULL_AS_BLANK 获取的数据位null时 替换成""
                if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                    expert.setName(row.getCell(0).getStringCellValue());
                }
                // type
                if (!row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                    expert.setType(row.getCell(1).getStringCellValue());
                }
                // IP
                if (!row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                    expert.setIp(row.getCell(2).getStringCellValue());
                }
                // port
                if (row.getCell(3) != null) {
                    int i = (int) row.getCell(3).getNumericCellValue();
                    expert.setPort(String.valueOf(i));
                }
                // username
                if (!row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                    expert.setUserName(row.getCell(4).getStringCellValue());
                }
                // password
                if (!row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                    expert.setPassword(row.getCell(5).getStringCellValue());
                }
                list.add(expert);
            }
        }
        return list;
    }


    public List<Server> readXLSX(MultipartFile file) throws IOException {
        ArrayList<Server> list = new ArrayList<>();

        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        for (int rowNum = 1; rowNum <= lastRowNum; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row != null) {
                Server expert = new Server();
                // 名称
                if (!row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                    expert.setName(row.getCell(0).getStringCellValue());
                }
                // type
                if (!row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                    expert.setType(row.getCell(1).getStringCellValue());
                }
                // IP
                if (!row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                    expert.setIp(row.getCell(2).getStringCellValue());
                }
                // port
                if (row.getCell(3) != null) {
                    int i = (int) row.getCell(3).getNumericCellValue();
                    expert.setPort(String.valueOf(i));
                }
                // username
                if (!row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                    expert.setUserName(row.getCell(4).getStringCellValue());
                }
                // password
                if (!row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                    expert.setPassword(row.getCell(5).getStringCellValue());
                }
                list.add(expert);
            }
        }
        return list;
    }

    public List<ServerRule> allBindList(String id) {
        List<String> ids = new ArrayList<>();
        RuleGroupMappingExample example = new RuleGroupMappingExample();
        example.createCriteria().andGroupIdEqualTo(id);
        List<RuleGroupMapping> list = ruleGroupMappingMapper.selectByExample(example);
        for (RuleGroupMapping groupMapping : list) {
            ids.add(groupMapping.getRuleId());
        }
        ServerRuleExample ruleExample = new ServerRuleExample();
        if (ids.size() > 0) {
            ruleExample.createCriteria().andIdIn(ids);
            ruleExample.setOrderByClause("name");
            return serverRuleMapper.selectByExample(ruleExample);
        }
        return new ArrayList<>();
    }

    public List<ServerRule> unBindList(String id) {
        ServerRuleExample ruleExample = new ServerRuleExample();
        ruleExample.setOrderByClause("name");
        return serverRuleMapper.selectByExample(ruleExample);
    }

    public void bindRule(BindRuleRequest request) throws Exception {
        String groupId = request.getGroupId();
        RuleGroupMappingExample example = new RuleGroupMappingExample();
        example.createCriteria().andGroupIdEqualTo(groupId);
        ruleGroupMappingMapper.deleteByExample(example);
        for (String id : request.getCloudValue()) {
            RuleGroupMapping record = new RuleGroupMapping();
            record.setRuleId(id);
            record.setGroupId(groupId);
            ruleGroupMappingMapper.insertSelective(record);
        }
    }

    private void scanGroups(Server server, Integer scanId, String groupId) {
        try {
            this.messageOrderId = noticeService.createServerMessageOrder(server);

            if (StringUtils.equalsIgnoreCase(server.getStatus(), CloudAccountConstants.Status.UNLINK.name())) {
                //检验主机的有效性
                BeanUtils.copyBean(server, validateAccount(server).getServer());
                serverMapper.updateByPrimaryKeySelective(server);
            }
            if (StringUtils.equalsIgnoreCase(server.getStatus(), CloudAccountConstants.Status.VALID.name())) {
                deleteServerResultById(server.getId());
                ServerRuleRequest serverRuleRequest = new ServerRuleRequest();
                serverRuleRequest.setStatus(true);
                serverRuleRequest.setType(server.getType());
                serverRuleRequest.setRuleGroupId(groupId);
                List<ServerRuleDTO> ruleList = ruleList(serverRuleRequest);
                ServerResult result = new ServerResult();
                String serverGroupName = serverGroupMapper.selectByPrimaryKey(server.getServerGroupId()).getName();
                for (ServerRuleDTO dto : ruleList) {
                    BeanUtils.copyBean(result, server);
                    result.setId(UUIDUtil.newUUID());
                    result.setServerId(server.getId());
                    result.setServerGroupId(server.getServerGroupId());
                    result.setServerGroupName(serverGroupName);
                    result.setApplyUser(SessionUtils.getUserId());
                    result.setCreateTime(System.currentTimeMillis());
                    result.setUpdateTime(System.currentTimeMillis());
                    result.setServerName(server.getName());
                    result.setRuleId(dto.getId());
                    result.setRuleName(dto.getName());
                    result.setRuleDesc(dto.getDescription());
                    result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
                    result.setSeverity(dto.getSeverity());
                    result.setType(dto.getType());
                    serverResultMapper.insertSelective(result);

                    saveServerResultLog(result.getId(), "i18n_start_server_result", "", true);

                    OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getServerName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.SCAN, "i18n_start_server_result");

                    historyService.insertScanTaskHistory(result, scanId, server.getId(), TaskEnum.serverAccount.getType());

                    historyService.insertHistoryServerResult(BeanUtils.copyBean(new HistoryServerResult(), result));
                }
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
    }

    public List<RuleGroup> getRuleGroups() {
        RuleGroupExample example = new RuleGroupExample();
        example.createCriteria().andPluginIdEqualTo("hummer-server-plugin").andTypeEqualTo("server");
        return ruleGroupMapper.selectByExample(example);
    }


}
