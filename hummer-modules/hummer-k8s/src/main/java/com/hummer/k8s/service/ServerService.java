package com.hummer.k8s.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.*;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.rule.BindRuleRequest;
import com.hummer.common.core.domain.request.rule.ScanGroupRequest;
import com.hummer.common.core.domain.request.server.ServerCertificateRequest;
import com.hummer.common.core.domain.request.server.ServerRequest;
import com.hummer.common.core.domain.request.server.ServerResultRequest;
import com.hummer.common.core.domain.request.server.ServerRuleRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.proxy.server.SshUtil;
import com.hummer.common.core.proxy.server.WinRMHelper;
import com.hummer.common.core.utils.*;
import com.hummer.k8s.mapper.*;
import com.hummer.k8s.mapper.ext.ExtServerCertificateMapper;
import com.hummer.k8s.mapper.ext.ExtServerMapper;
import com.hummer.k8s.mapper.ext.ExtServerResultMapper;
import com.hummer.k8s.mapper.ext.ExtServerRuleMapper;
import com.hummer.system.api.IOperationLogService;
import com.hummer.system.api.ISystemProviderService;
import com.hummer.system.api.model.LoginUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private ExtServerMapper extServerMapper;
    @Autowired
    private ServerMapper serverMapper;
    @Autowired
    private ServerGroupMapper serverGroupMapper;
    @Autowired
    private ServerRuleMapper serverRuleMapper;
    @Autowired
    private ExtServerRuleMapper extServerRuleMapper;
    @Autowired
    private ExtServerResultMapper extServerResultMapper;
    @Autowired
    private ServerResultMapper serverResultMapper;
    @Autowired
    private ServerResultLogMapper serverResultLogMapper;
    @Autowired
    private ServerCertificateMapper serverCertificateMapper;
    @Autowired
    private ExtServerCertificateMapper extServerCertificateMapper;
    @Autowired
    private ProxyMapper proxyMapper;
    @DubboReference
    private ISystemProviderService systemProviderService;
    @DubboReference
    private IOperationLogService operationLogService;
    @DubboReference
    private ICloudProviderService cloudProviderService;

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

    public void scan(ScanGroupRequest request, LoginUser loginUser) throws Exception {
        Server server = serverMapper.selectByPrimaryKey(request.getAccountId());
        Integer scanId = systemProviderService.insertScanHistory(server);
        for (Integer groupId : request.getGroups()) {
            this.scanGroups(server, scanId, groupId.toString(), loginUser);
        }
    }

    public Boolean scan(String id, LoginUser loginUser) throws Exception {
        ServerRequest request = new ServerRequest();
        request.setId(id);//serverId
        Server server = BeanUtils.copyBean(new Server(), getServerList(request).get(0));
        if (StringUtils.equalsIgnoreCase(server.getStatus(), CloudAccountConstants.Status.UNLINK.name())) {
            //检验主机的有效性
            BeanUtils.copyBean(server, validateAccount(server).getServer());
            serverMapper.updateByPrimaryKeySelective(server);
        }
        Integer scanId = systemProviderService.insertScanHistory(server);
        this.messageOrderId = systemProviderService.createServerMessageOrder(server);
        if (StringUtils.equalsIgnoreCase(server.getStatus(), CloudAccountConstants.Status.VALID.name())) {

            deleteRescanServerResultById(id);

            ServerRuleRequest serverRuleRequest = new ServerRuleRequest();
            serverRuleRequest.setStatus(true);
            serverRuleRequest.setType(server.getType());
            List<ServerRuleDTO> ruleList = ruleList(serverRuleRequest);
            ServerResult result = new ServerResult();
            String serverGroupName = serverGroupMapper.selectByPrimaryKey(server.getServerGroupId()).getName();
            for (ServerRuleDTO dto : ruleList) {
                BeanUtils.copyBean(result, server);
                result.setId(UUIDUtil.newUUID());
                result.setServerId(id);
                result.setServerGroupId(server.getServerGroupId());
                result.setServerGroupName(serverGroupName);
                result.setApplyUser(loginUser.getUserId());
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

                saveServerResultLog(result.getId(), "i18n_start_server_result", "", true, loginUser);

                operationLogService.log(loginUser, result.getId(), result.getServerName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.SCAN, "i18n_start_server_result");

                systemProviderService.insertScanTaskHistory(result, scanId, server.getId(), TaskEnum.serverAccount.getType());

                systemProviderService.insertHistoryServerResult(BeanUtils.copyBean(new HistoryServerResult(), result));
            }
        }
        return true;
    }

    public void createScan(ServerResult result, LoginUser loginUser) throws Exception {
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

            systemProviderService.createServerMessageOrderItem(result, this.messageOrderId);

            saveServerResultLog(result.getId(), "i18n_end_server_result", returnLog, result.getIsSeverity(), loginUser);

            systemProviderService.updateHistoryServerResult(BeanUtils.copyBean(new HistoryServerResult(), result));
        } catch (Exception e) {
            LogUtil.error(result.getServerName() + "{}" + result.getIp() + "[error]: " + e.getMessage());
            result.setReturnLog(result.getServerName() + "{}" + result.getIp() + "[error]: " + e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            serverResultMapper.updateByPrimaryKeySelective(result);
            systemProviderService.updateHistoryServerResult(BeanUtils.copyBean(new HistoryServerResult(), result));
            systemProviderService.createServerMessageOrderItem(result, this.messageOrderId);
            saveServerResultLog(result.getId(), "i18n_operation_ex" + ": " + e.getMessage(), e.getMessage(), false, loginUser);
        }
    }

    public String rescan(String id, LoginUser loginUser) throws Exception {
        ServerResult result = serverResultMapper.selectByPrimaryKey(id);
        saveServerResultLog(result.getId(), "i18n_restart_server_result", "", true, loginUser);
        result.setUpdateTime(System.currentTimeMillis());
        result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
        serverResultMapper.updateByPrimaryKeySelective(result);
        operationLogService.log(loginUser, result.getId(), result.getServerName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.RESCAN, "i18n_restart_server_result");
        return result.getId();
    }

    public void deleteRescanServerResultById(String id) {
        ServerResultExample example = new ServerResultExample();
        example.createCriteria().andServerIdEqualTo(id);//serverId
        serverResultMapper.deleteByExample(example);
    }

    public void deleteServerResultById(String id, LoginUser loginUser) throws Exception {

        ServerResultExample example = new ServerResultExample();
        example.createCriteria().andServerIdEqualTo(id);//serverId
        List<ServerResult> list = serverResultMapper.selectByExample(example);

        for (ServerResult result : list) {
            ServerResultLogExample logExample = new ServerResultLogExample();
            logExample.createCriteria().andResultIdEqualTo(result.getId());
            serverResultLogMapper.deleteByExample(logExample);

            systemProviderService.deleteHistoryServerResult(result.getId());
        }
        serverResultMapper.deleteByExample(example);
        operationLogService.log(loginUser, id, id, ResourceTypeConstants.SERVER.name(), ResourceOperation.DELETE, "i18n_delete_server_result");

    }

    public void deleteServerResult(String id, LoginUser loginUser) throws Exception {
        ServerResultLogExample logExample = new ServerResultLogExample();
        logExample.createCriteria().andResultIdEqualTo(id);
        serverResultLogMapper.deleteByExample(logExample);

        systemProviderService.deleteHistoryServerResult(id);
        serverResultMapper.deleteByPrimaryKey(id);
        operationLogService.log(loginUser, id, id, ResourceTypeConstants.SERVER.name(), ResourceOperation.DELETE, "i18n_delete_server_result");
    }

    public void saveServerResultLog(String resultId, String operation, String output, boolean result, LoginUser loginUser) throws Exception {
        ServerResultLogWithBLOBs serverResultLog = new ServerResultLogWithBLOBs();
        String operator = "system";
        try {
            if (loginUser != null) {
                operator = loginUser.getUserId();
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
            serverValidateDTO= validateAccount(server, proxy);
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

    public int addServerGroup(ServerGroup serverGroup, LoginUser loginUser) {
        serverGroup.setId(UUIDUtil.newUUID());
        serverGroup.setCreator(loginUser.getUserId());
        serverGroup.setCreateTime(System.currentTimeMillis());
        serverGroup.setUpdateTime(System.currentTimeMillis());
        operationLogService.log(loginUser, serverGroup.getId(), serverGroup.getId(), ResourceTypeConstants.SERVER.name(), ResourceOperation.CREATE, "i18n_create_server_group");

        return serverGroupMapper.insertSelective(serverGroup);
    }

    public int editServerGroup(ServerGroup serverGroup, LoginUser loginUser) {
        serverGroup.setUpdateTime(System.currentTimeMillis());
        operationLogService.log(loginUser, serverGroup.getId(), serverGroup.getId(), ResourceTypeConstants.SERVER.name(), ResourceOperation.UPDATE, "i18n_update_server_group");
        return serverGroupMapper.updateByPrimaryKeySelective(serverGroup);
    }

    public void deleteServerGroup(ServerGroup serverGroup, LoginUser loginUser) {

        ServerExample example = new ServerExample();
        example.createCriteria().andServerGroupIdEqualTo(serverGroup.getId());
        List<Server> list = serverMapper.selectByExample(example);
        if (list.size() > 0) {
            HRException.throwException(Translator.get("i18n_ex_server_validate"));
        } else {
            serverGroupMapper.deleteByPrimaryKey(serverGroup.getId());
        }
        operationLogService.log(loginUser, serverGroup.getId(), serverGroup.getId(), ResourceTypeConstants.SERVER.name(), ResourceOperation.DELETE, "i18n_delete_server_group");

    }

    public ServerValidateDTO addServer(MultipartFile keyFile, Server server, LoginUser loginUser) throws Exception {
        String id = UUIDUtil.newUUID();
        server.setId(id);
        server.setCreator(loginUser.getUserId());
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

        operationLogService.log(loginUser, server.getId(), server.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.CREATE, "i18n_create_server");
        serverMapper.insertSelective(serverValidateDTO.getServer());
        return serverValidateDTO;
    }

    public ServerValidateDTO editServer(MultipartFile keyFile, Server server, LoginUser loginUser) throws Exception {
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

        operationLogService.log(loginUser, server.getId(), server.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.UPDATE, "i18n_update_server");
        serverMapper.updateByPrimaryKeySelective(serverValidateDTO.getServer());
        return serverValidateDTO;
    }

    public ServerValidateDTO copyServer(MultipartFile keyFile, Server server, LoginUser loginUser) throws Exception {
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

        operationLogService.log(loginUser, server.getId(), server.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.COPY, "i18n_copy_server");
        serverMapper.insertSelective(serverValidateDTO.getServer());
        return serverValidateDTO;
    }

    public void deleteServer(String id, LoginUser loginUser) throws Exception {
        serverMapper.deleteByPrimaryKey(id);
        deleteServerResultById(id, loginUser);
        operationLogService.log(loginUser, id, id, ResourceTypeConstants.SERVER.name(), ResourceOperation.DELETE, "i18n_delete_server");
    }

    public ServerValidateDTO validateAccount(Server server, Proxy proxy) throws Exception {
        ServerValidateDTO serverValidateDTO = new ServerValidateDTO();
        switch (server.getType()) {
            case "linux":
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
                    return SshUtil.executeSshd(SshUtil.loginSshd(server, proxy), "su - -c " + "\"" + cmd + "\"");
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

    public void saveRuleGroupMapping(String ruleId, List<String> ruleGroups) {
        deleteRuleGroupMapping(ruleId);
        if (ruleGroups.isEmpty()) {
            return;
        }
        for (String ruleGroup : ruleGroups) {
            RuleGroupMapping ruleGroupMapping = new RuleGroupMapping();
            ruleGroupMapping.setRuleId(ruleId);
            ruleGroupMapping.setGroupId(ruleGroup);
            cloudProviderService.insertRuleGroupMapping(ruleGroupMapping);
        }
    }

    public void deleteRuleGroupMapping(String ruleId) {
        if (StringUtils.isNotBlank(ruleId)) {
            RuleGroupMappingExample example = new RuleGroupMappingExample();
            example.createCriteria().andRuleIdEqualTo(ruleId);
            cloudProviderService.deleteRuleGroupMapping(example);
        }
    }

    public int updateServerRule(ServerRuleRequest request) throws Exception {
        ServerRule record = new ServerRule();
        BeanUtils.copyBean(record, request);
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(record.getId(), request.getTagKey());
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

    public int addCertificate(MultipartFile keyFile, ServerCertificate certificate, LoginUser loginUser) throws Exception {
        String id = UUIDUtil.newUUID();
        certificate.setId(id);
        certificate.setCreator(loginUser.getUserId());
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

        operationLogService.log(loginUser, certificate.getId(), certificate.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.CREATE, "i18n_create_server_certificate");
        return serverCertificateMapper.insertSelective(certificate);
    }

    public int editCertificate(MultipartFile keyFile, ServerCertificate certificate, LoginUser loginUser) throws Exception {
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

        operationLogService.log(loginUser, certificate.getId(), certificate.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.UPDATE, "i18n_update_server_certificate");
        return serverCertificateMapper.updateByPrimaryKeySelective(certificate);
    }

    public void deleteCertificate(String id, LoginUser loginUser) throws Exception {
        serverCertificateMapper.deleteByPrimaryKey(id);
        operationLogService.log(loginUser, id, id, ResourceTypeConstants.SERVER.name(), ResourceOperation.DELETE, "i18n_delete_server_certificate");
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

    public List<HistoryServerResultDTO> history(ServerResultRequest request) {
        List<HistoryServerResultDTO> historyList = systemProviderService.serverHistory(request);
        return historyList;
    }

    public void insertExperts(MultipartFile excelFile, Server request, LoginUser loginUser) throws Exception {
        if (excelFile==null|| excelFile.getSize()==0){
            LogUtil.error("文件上传错误，重新上传");
        }
        String filename = excelFile.getOriginalFilename();
        if (!(filename.endsWith(".xls")|| filename.endsWith(".xlsx"))){
            LogUtil.error("文件上传格式错误，请重新上传");
        }

        List<Server> list = new ArrayList<>();
        try {
            if (filename.endsWith(".xls")){
                list = readXLS(excelFile);
            }else {
                list = readXLSX(excelFile);
            }
        }catch (IOException e) {
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
        if(!newExpertList.isEmpty()){
            for (Server server : newExpertList) {
                server.setId(UUIDUtil.newUUID());
                server.setCreateTime(System.currentTimeMillis());
                server.setUpdateTime(System.currentTimeMillis());
                server.setCreator(loginUser.getUserId());
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
        List<Server> list =new ArrayList<>();

        InputStream inputStream = file.getInputStream();
        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(inputStream);
        HSSFWorkbook workbook = new HSSFWorkbook(poifsFileSystem);

        //读取第一张sheet
        HSSFSheet sheet = workbook.getSheetAt(0);
        //遍历每一行Excel获取内容
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            HSSFRow row = sheet.getRow(rowNum);
            if (row!=null){
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
                if(row.getCell(3) != null){
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
            if (row!=null){
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
                if(row.getCell(3) != null){
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

    public void deleteHistoryServerResult(String id) throws Exception {
        systemProviderService.deleteHistoryServerResult(id);
    }

    public List<ServerRule> allBindList(String id) {
        List<String> ids = new ArrayList<>();
        RuleGroupMappingExample example = new RuleGroupMappingExample();
        example.createCriteria().andGroupIdEqualTo(id);
        List<RuleGroupMapping> list = cloudProviderService.selectRuleGroupMappings(example);
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
        cloudProviderService.deleteRuleGroupMapping(example);
        for (String id : request.getCloudValue()) {
            RuleGroupMapping record = new RuleGroupMapping();
            record.setRuleId(id);
            record.setGroupId(groupId);
            cloudProviderService.insertRuleGroupMapping(record);
        }
    }

    private void scanGroups(Server server, Integer scanId, String groupId, LoginUser loginUser) {
        try {
            this.messageOrderId = systemProviderService.createServerMessageOrder(server);

            if (StringUtils.equalsIgnoreCase(server.getStatus(), CloudAccountConstants.Status.UNLINK.name())) {
                //检验主机的有效性
                BeanUtils.copyBean(server, validateAccount(server).getServer());
                serverMapper.updateByPrimaryKeySelective(server);
            }
            if (StringUtils.equalsIgnoreCase(server.getStatus(), CloudAccountConstants.Status.VALID.name())) {

                deleteRescanServerResultById(server.getId());

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
                    result.setApplyUser(loginUser.getUserId());
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

                    saveServerResultLog(result.getId(), "i18n_start_server_result", "", true, loginUser);

                    operationLogService.createOperationLog(loginUser, result.getId(), result.getServerName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.SCAN, "i18n_start_server_result", loginUser.getIpAddr());

                    systemProviderService.insertScanTaskHistory(result, scanId, server.getId(), TaskEnum.serverAccount.getType());

                    systemProviderService.insertHistoryServerResult(BeanUtils.copyBean(new HistoryServerResult(), result));
                }
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
    }

    public List<RuleGroup> getRuleGroups() {
        RuleGroupExample example = new RuleGroupExample();
        example.createCriteria().andPluginIdEqualTo("hummer-server-plugin").andTypeEqualTo("server");
        return cloudProviderService.ruleGroupList(example);
    }

    public void scanByGroup(String groupId, String serverId, LoginUser loginUser) throws Exception {
        Server server = serverMapper.selectByPrimaryKey(serverId);
        Integer scanId = systemProviderService.insertScanHistory(server);
        this.scanGroups(server, scanId, groupId, loginUser);
    }


}
