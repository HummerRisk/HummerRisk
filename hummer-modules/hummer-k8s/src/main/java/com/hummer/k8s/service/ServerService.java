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
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hummer.common.core.proxy.server.SshUtil.encodingFilename;

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
    @Autowired
    private ServerLynisResultMapper serverLynisResultMapper;
    @Autowired
    private ServerLynisResultDetailMapper serverLynisResultDetailMapper;
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
        insertOrUpdateLynis(server);
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
                result.setIsSeverity("true");
            } else if (returnLog.contains(ServerConstants.HUMMER_ERROR)) {
                result.setIsSeverity("false");
            } else if (StringUtils.isBlank(returnLog)) {
                returnLog = ServerConstants.HUMMER_WARN + ": 没有获取到返回值";
                result.setIsSeverity("warn");
            }
            result.setCommand(script);
            result.setReturnLog(returnLog);
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());
            serverResultMapper.updateByPrimaryKeySelective(result);

            systemProviderService.createServerMessageOrderItem(result, this.messageOrderId);

            saveServerResultLog(result.getId(), "i18n_end_server_result", returnLog, StringUtils.equals(result.getIsSeverity(), "true") ? true : false, loginUser);

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

    public void rescanServer(String id, LoginUser loginUser) throws Exception {
        Server server = serverMapper.selectByPrimaryKey(id);
        insertOrUpdateLynis(server);

        ServerResultExample example = new ServerResultExample();
        example.createCriteria().andServerIdEqualTo(id);
        List<ServerResult> results = serverResultMapper.selectByExample(example);

        for (ServerResult result : results) {
            saveServerResultLog(result.getId(), "i18n_restart_server_result", "", true, loginUser);
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
            serverResultMapper.updateByPrimaryKeySelective(result);
            operationLogService.log(loginUser, result.getId(), result.getServerName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.RESCAN, "i18n_restart_server_result");
        }
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

    public void deleteServerResultByServerId(String id, LoginUser loginUser) throws Exception {
        deleteServerResultById(id, loginUser);
        deleteLynisResult(serverMapper.selectByPrimaryKey(id));
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

    public List<Server> allServerList(String serverType) {
        ServerExample example = new ServerExample();
        if (serverType != null) {
            example.createCriteria().andTypeEqualTo(serverType);
        }
        return serverMapper.selectByExampleWithBLOBs(example);
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
                    //先从本地生成文件
                    String dirPath = "/tmp/server";
                    String remotePath = "/tmp";
                    String fileName = DateUtils.datePath() + "-" + encodingFilename("server") + ".sh";
                    String filePath = dirPath + "/" + fileName;
                    CommandUtils.saveAsFile(cmd, dirPath, fileName, false);
                    //再scp到主机, 最后执行sudo sh检测
                    return SshUtil.executeScp(server, proxy, filePath, fileName, remotePath);
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
        String ruleId = UUIDUtil.newUUID();
        record.setId(UUIDUtil.newUUID());
        record.setLastModified(System.currentTimeMillis());
        saveRuleTagMapping(ruleId, request.getTagKey());
        saveRuleGroupMapping(ruleId, request.getGroups());
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

    public void deleteServerRule(String id, LoginUser loginUser) throws Exception {
        ServerRule serverRule = serverRuleMapper.selectByPrimaryKey(id);

        //内置规则不可以删除
        if(serverRule.getFlag()) return;

        deleteRuleTag(null, id);
        serverRuleMapper.deleteByPrimaryKey(id);
        operationLogService.log(loginUser, serverRule.getName(), id, ResourceTypeConstants.RULE.name(), ResourceOperation.DELETE, "i18n_delete_rule");
    }

    public int changeStatus(ServerRule rule) throws Exception {
        return serverRuleMapper.updateByPrimaryKeySelective(rule);
    }

    public List<ServerResultDTO> resultList(ServerResultRequest request) {
        return extServerResultMapper.resultList(request);
    }

    public List<ServerListDTO> resultServerList(ServerRequest request) throws Exception {
        List<ServerListDTO> list = extServerResultMapper.resultServerList(request);
        for (ServerListDTO serverListDTO : list) {
            ServerLynisResultExample serverLynisResultExample = new ServerLynisResultExample();
            serverLynisResultExample.createCriteria().andServerIdEqualTo(serverListDTO.getId());
            List<ServerLynisResultWithBLOBs> serverLynisResults = serverLynisResultMapper.selectByExampleWithBLOBs(serverLynisResultExample);
            if(serverLynisResults.size() > 0) {
                ServerLynisResultWithBLOBs serverLynisResultWithBLOBs = serverLynisResults.get(0);
                serverListDTO.setServerLynisResult(serverLynisResultWithBLOBs);

                List<ServerLynisResultDetailDTO> serverLynisResultDetailTitle = extServerResultMapper.serverLynisResultDetailTitle(serverLynisResultWithBLOBs.getId());
                List<ServerLynisResultDetailDTO> dtos = new LinkedList<>();
                for (ServerLynisResultDetail detail : serverLynisResultDetailTitle) {
                    ServerLynisResultDetailDTO dto = BeanUtils.copyBean(new ServerLynisResultDetailDTO(), detail);
                    List<ServerLynisResultDetail> serverLynisResultDetails = extServerResultMapper.serverLynisResultDetails(serverLynisResultWithBLOBs.getId(), detail.getType());
                    dto.setDetails(serverLynisResultDetails);
                    dtos.add(dto);
                }
                serverListDTO.setServerLynisResultDetails(dtos);

                List<ServerLynisResultDetailDTO> serverLynisWarnings = extServerResultMapper.serverLynisWarnings(serverLynisResultWithBLOBs.getId());
                List<ServerLynisResultDetailDTO> dtos2 = new LinkedList<>();
                for (ServerLynisResultDetail detail : serverLynisWarnings) {
                    ServerLynisResultDetailDTO dto = BeanUtils.copyBean(new ServerLynisResultDetailDTO(), detail);
                    List<ServerLynisResultDetail> serverLynisResultDetails = extServerResultMapper.serverLynisResultDetails(serverLynisResultWithBLOBs.getId(), detail.getType());
                    dto.setDetails(serverLynisResultDetails);
                    dtos2.add(dto);
                }
                serverListDTO.setServerLynisWarnings(dtos2);

                List<ServerLynisResultDetailDTO> serverLynisSuggestions = extServerResultMapper.serverLynisSuggestions(serverLynisResultWithBLOBs.getId());
                List<ServerLynisResultDetailDTO> dtos3 = new LinkedList<>();
                for (ServerLynisResultDetail detail : serverLynisSuggestions) {
                    ServerLynisResultDetailDTO dto = BeanUtils.copyBean(new ServerLynisResultDetailDTO(), detail);
                    List<ServerLynisResultDetail> serverLynisResultDetails = extServerResultMapper.serverLynisResultDetails(serverLynisResultWithBLOBs.getId(), detail.getType());
                    dto.setDetails(serverLynisResultDetails);
                    dtos3.add(dto);
                }
                serverListDTO.setServerLynisSuggestions(dtos3);
            }

        }
        return list;
    }

    public ServerListDTO resultServer(String serverId) {
        return extServerResultMapper.resultServer(serverId);
    }

    public ServerResultDTO getServerResult(String resultId) {
        return extServerResultMapper.result(resultId);
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

    public void insertExperts(MultipartFile excelFile, Server request, LoginUser loginUser) throws Exception {
        if (excelFile == null || excelFile.getSize() == 0) {
            LogUtil.error("文件上传错误，重新上传");
        }
        String filename = excelFile.getOriginalFilename();
        if (!(filename.endsWith(".xls") || filename.endsWith(".xlsx"))) {
            LogUtil.error("文件上传格式错误，请重新上传");
        }

        List<Server> list = new ArrayList<>();
        try {
            if (filename.endsWith(".xlsx")) {
                list = readXLSX(excelFile);
            } else {
                list = readXLS(excelFile);
            }
        } catch (Exception e) {
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
                if (row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getRawValue() !=null) {
                    expert.setName(row.getCell(0).getRawValue());
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
                if (row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue() != null) {
                    expert.setUserName(row.getCell(4).getStringCellValue());
                }
                // password
                if (row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getRawValue() != null) {
                    expert.setPassword(row.getCell(5).getRawValue());
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
        RuleGroupExample example = new RuleGroupExample();
        example.createCriteria().andIdEqualTo(Integer.valueOf(id));
        RuleGroup ruleGroup = cloudProviderService.ruleGroupList(example).get(0);
        ServerRuleExample ruleExample = new ServerRuleExample();
        ruleExample.createCriteria().andTypeEqualTo(ruleGroup.getServerType());
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

    public List<RuleGroup> getRuleGroups(String type) {
        RuleGroupExample example = new RuleGroupExample();
        if (type != null) {
            example.createCriteria().andPluginIdEqualTo("hummer-server-plugin").andTypeEqualTo("server").andServerTypeEqualTo(type);
        } else {
            example.createCriteria().andPluginIdEqualTo("hummer-server-plugin").andTypeEqualTo("server");
        }
        return cloudProviderService.ruleGroupList(example);
    }

    public void scanByGroup(String groupId, String serverId, LoginUser loginUser) throws Exception {
        Server server = serverMapper.selectByPrimaryKey(serverId);
        Integer scanId = systemProviderService.insertScanHistory(server);
        this.scanGroups(server, scanId, groupId, loginUser);
    }

    public void deleteServers(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteServer(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void deleteServerRules(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteServerRule(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void deleteCertificates(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteCertificate(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void deleteResults(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteServerResult(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void insertOrUpdateLynis(Server server) {
        ServerLynisResultExample example = new ServerLynisResultExample();
        example.createCriteria().andServerIdEqualTo(server.getId());
        List<ServerLynisResultWithBLOBs> list = serverLynisResultMapper.selectByExampleWithBLOBs(example);

        if (list.size() > 0) {
            ServerLynisResultWithBLOBs serverLynisResult = list.get(0);
            serverLynisResult.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
            serverLynisResultMapper.updateByPrimaryKey(serverLynisResult);
        } else {
            ServerLynisResultWithBLOBs serverLynisResultWithBLOBs = new ServerLynisResultWithBLOBs();
            serverLynisResultWithBLOBs.setId(UUIDUtil.newUUID());
            serverLynisResultWithBLOBs.setServerId(server.getId());
            serverLynisResultWithBLOBs.setServerName(server.getName());
            serverLynisResultWithBLOBs.setPluginIcon(server.getPluginIcon());
            serverLynisResultWithBLOBs.setCreateTime(System.currentTimeMillis());
            serverLynisResultWithBLOBs.setUpdateTime(System.currentTimeMillis());
            serverLynisResultWithBLOBs.setApplyUser("admin");
            serverLynisResultWithBLOBs.setResultStatus(CloudTaskConstants.TASK_STATUS.APPROVED.toString());
            serverLynisResultMapper.insertSelective(serverLynisResultWithBLOBs);
        }

    }

    public void scanLynis(ServerLynisResultWithBLOBs serverLynisResultWithBLOBs, LoginUser loginUser) throws Exception {
        LogUtil.info("start server lynis scan");
        Server server = serverMapper.selectByPrimaryKey(serverLynisResultWithBLOBs.getServerId());
        //删除旧数据
        deleteLynisResultDetail(serverLynisResultWithBLOBs);

        Proxy proxy = new Proxy();
        if (server.getIsProxy() != null && server.getIsProxy()) {
            proxy = proxyMapper.selectByPrimaryKey(server.getProxyId());
        }

        //先从本地生成文件
        String dirPath = "/tmp/server";
        String remotePath = "/tmp";
        String fileName1 = DateUtils.datePath() + "-" + encodingFilename("server") + ".sh";
        String filePath1 = dirPath + "/" + fileName1;
        String cmd1 = "if [ -d " + ServerConstants.LYNIS_VERSION + " ];then echo 存在;fi";
        CommandUtils.saveAsFile(cmd1, dirPath, fileName1, false);
        //再scp到主机, 最后执行sudo sh检测
        String isExist = SshUtil.executeScp(server, proxy, filePath1, fileName1, remotePath);

        if (!StringUtils.equals(isExist, "存在")) {
            String fileName2 = ServerConstants.LYNIS_TAR;
            String filePath2 = ServerConstants.LYNIS;
            //scp tar 包到主机
            SshUtil.executeScpLynis(server, proxy, filePath2, fileName2, remotePath);

            //解压
            String cmd3 = "tar -zxvf " + ServerConstants.SSH_LYNIS_DIR;
            String fileName3 = DateUtils.datePath() + "-" + encodingFilename("server") + ".sh";
            String filePath3 = dirPath + "/" + fileName3;
            CommandUtils.saveAsFile(cmd3, dirPath, fileName3, false);
            SshUtil.executeScp(server, proxy, filePath3, fileName3, remotePath);
        }

        String resultStr = executeLynis(server, proxy, dirPath, remotePath);

        //插入新数据
        executeLynisResultStr(resultStr, serverLynisResultWithBLOBs, loginUser);
    }

    public String executeLynis(Server server, Proxy proxy, String dirPath, String remotePath) throws Exception {
        //解压
        String cmd = "cd " + ServerConstants.LYNIS_VERSION + " && ./lynis audit system";
        String fileName = DateUtils.datePath() + "-" + encodingFilename("server") + ".sh";
        String filePath = dirPath + "/" + fileName;
        CommandUtils.saveAsFile(cmd, dirPath, fileName, false);
        return SshUtil.executeScp(server, proxy, filePath, fileName, remotePath);
    }

    public void deleteLynisResult(Server server) throws Exception {
        ServerLynisResultExample example = new ServerLynisResultExample();
        example.createCriteria().andServerIdEqualTo(server.getId());
        List<ServerLynisResult> list = serverLynisResultMapper.selectByExample(example);

        for (ServerLynisResult serverLynisResult : list) {
            ServerLynisResultDetailExample detailExample = new ServerLynisResultDetailExample();
            detailExample.createCriteria().andLynisIdEqualTo(serverLynisResult.getId());
            serverLynisResultDetailMapper.deleteByExample(detailExample);

            serverLynisResultMapper.deleteByPrimaryKey(serverLynisResult.getId());
        }
    }

    public void deleteLynisResultDetail(ServerLynisResultWithBLOBs serverLynisResultWithBLOBs) throws Exception {
        ServerLynisResultDetailExample detailExample = new ServerLynisResultDetailExample();
        detailExample.createCriteria().andLynisIdEqualTo(serverLynisResultWithBLOBs.getId());
        serverLynisResultDetailMapper.deleteByExample(detailExample);
    }

    public void executeLynisResultStr(String resultStr, ServerLynisResultWithBLOBs serverLynisResultWithBLOBs, LoginUser loginUser) throws Exception {

        try {
            if (StringUtils.isNotEmpty(resultStr)) {
                String lynisLog = resultStr;
                resultStr = resultStr.replaceAll("", "");
                resultStr = removeColors(resultStr);//先去掉颜色
                //将 "[2C" 替换成空格
                for (int i = 0; i < 50; i++) {
                    resultStr = resultStr.replaceAll("\\[" + i + "C", "");//间隔
                }
                String lynisId = serverLynisResultWithBLOBs.getId();
                long hardeningIndex = 0, pluginsEnabled= 0, testsPerformed = 0;
                String[] twoStr = resultStr.split("================================================================================");
                String str1 = twoStr[0];
                String str2 = twoStr[1];
                String str3 = twoStr[2];
                String[] results = str1.split("\n\n");
                String[] results2 = str2.split("\n\n");
                String[] results3 = str3.split("\n\n");

                for (String result : results) {
                    if (StringUtils.isEmpty(result)) continue;
                    if (StringUtils.contains(result, ServerConstants.SYSTEM_TOOLS)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SYSTEM_TOOLS, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.BOOT_AND_SERVICES)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.BOOT_AND_SERVICES, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.KERNEL)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.KERNEL, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.MEMORY_AND_PROCESSES)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.MEMORY_AND_PROCESSES, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.USERS_GROUPS_AND_AUTHENTICATION)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.USERS_GROUPS_AND_AUTHENTICATION, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.SHELLS)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SHELLS, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.FILE_SYSTEMS)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.FILE_SYSTEMS, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.USB_DEVICES)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.USB_DEVICES, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.STORAGE)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.STORAGE, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.NFS)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.NFS, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.NAME_SERVICES)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.NAME_SERVICES, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.PORTS_AND_PACKAGES)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.PORTS_AND_PACKAGES, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.NETWORKING)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.NETWORKING, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.PRINTERS_AND_SPOOLS)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.PRINTERS_AND_SPOOLS, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.SOFTWARE_EMAIL_AND_MESSAGING)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SOFTWARE_EMAIL_AND_MESSAGING, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.SOFTWARE_FIREWALLS)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SOFTWARE_FIREWALLS, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.SOFTWARE_WEBSERVER)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SOFTWARE_WEBSERVER, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.SSH_SUPPORT)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SSH_SUPPORT, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.SNMP_SUPPORT)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SNMP_SUPPORT, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.DATABASES)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.DATABASES, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.LDAP_SERVICES)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.LDAP_SERVICES, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.PHP)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.PHP, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.SQUID_SUPPORT)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SQUID_SUPPORT, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.LOGGING_AND_FILES)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.LOGGING_AND_FILES, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.INSECURE_SERVICES)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.INSECURE_SERVICES, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.BANNERS_AND_IDENTIFICATION)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.BANNERS_AND_IDENTIFICATION, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.SCHEDULED_TASKS)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SCHEDULED_TASKS, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.ACCOUNTING)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.ACCOUNTING, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.TIME_AND_SYNCHRONIZATION)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.TIME_AND_SYNCHRONIZATION, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.CRYPTOGRAPHY)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.CRYPTOGRAPHY, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.VIRTUALIZATION)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.VIRTUALIZATION, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.CONTAINERS)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.CONTAINERS, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.SECURITY_FRAMEWORKS)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SECURITY_FRAMEWORKS, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.SOFTWARE_FILE_INTEGRITY)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SOFTWARE_FILE_INTEGRITY, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.SOFTWARE_SYSTEM_TOOLING)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SOFTWARE_SYSTEM_TOOLING, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.SOFTWARE_MALWARE)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.SOFTWARE_MALWARE, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.FILE_PERMISSIONS)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.FILE_PERMISSIONS, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.HOME_DIRECTORIES)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.HOME_DIRECTORIES, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.KERNEL_HARDENING)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.KERNEL_HARDENING, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.HARDENING)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.HARDENING, loginUser);
                    } else if (StringUtils.contains(result, ServerConstants.CUSTOM_TESTS)) {
                        insertLynisResultDetail(result, lynisId, ServerConstants.CUSTOM_TESTS, loginUser);
                    }
                }

                long order = 1;
                for (String result : results2) {
                    if (StringUtils.isEmpty(result)) continue;
                    if (result.contains(ServerConstants.WARNINGS)) {
                        if (!result.contains(ServerConstants.NO_WARNING)) {
                            String strwar[] = result.split("----------------------------");
                            for (String s : strwar) {
                                insertLynisResultDetailSuggest(s, lynisId, ServerConstants.WARNINGS, order, loginUser);
                                order++;
                            }
                        }
                    } else if (result.contains(ServerConstants.SUGGESTIONS)) {
                        String strwar[] = result.split("----------------------------");
                        for (String s : strwar) {
                            insertLynisResultDetailSuggest(s, lynisId, ServerConstants.SUGGESTIONS, order, loginUser);
                            order++;
                        }
                    } else if (result.contains("*")) {
                        insertLynisResultDetailSuggest(result, lynisId, ServerConstants.SUGGESTIONS, order, loginUser);
                        order++;
                    }
                }

                for (String result : results3) {
                    if (StringUtils.isEmpty(result)) continue;
                    if (result.contains(ServerConstants.DETAILS)) {
                        String[] strs = result.split("\\R");
                        for (String line : strs) {
                            if (line.contains("Hardening index")) {
                                String right = line.split(" : ")[1];
                                if (right.contains("[")) {
                                    right = right.split("\\[")[0].replaceAll(" ", "");
                                }
                                hardeningIndex = Long.valueOf(right);
                            } else if (line.contains("Tests performed")) {
                                testsPerformed = Long.valueOf(line.split(" : ")[1]);
                            } else if (line.contains("Plugins enabled")) {
                                pluginsEnabled = Long.valueOf(line.split(" : ")[1]);
                            }
                        }
                    }
                }

                serverLynisResultWithBLOBs.setReturnLog(resultStr);
                serverLynisResultWithBLOBs.setLynisLog(lynisLog);
                serverLynisResultWithBLOBs.setHardeningIndex(hardeningIndex);
                serverLynisResultWithBLOBs.setPluginsEnabled(pluginsEnabled);
                serverLynisResultWithBLOBs.setTestsPerformed(testsPerformed);
                serverLynisResultWithBLOBs.setUpdateTime(System.currentTimeMillis());
                serverLynisResultWithBLOBs.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());
                serverLynisResultMapper.updateByPrimaryKey(serverLynisResultWithBLOBs);
            }
        } catch (Exception e) {
            serverLynisResultWithBLOBs.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            serverLynisResultMapper.updateByPrimaryKey(serverLynisResultWithBLOBs);
        }

    }


    public void insertLynisResultDetail(String result, String lynisId, String type, LoginUser loginUser) throws Exception {
        try {
            String[] strs = result.split("\\R");
            long order = 1;
            for (String line : strs) {
                if (line.contains("------")) continue;
                ServerLynisResultDetail detail = new ServerLynisResultDetail();
                detail.setLynisId(lynisId);
                detail.setCreateTime(System.currentTimeMillis());
                detail.setType(titleTrans(type.replace("[+] ", "")));
                detail.setOperator("admin");
                detail.setOrderIndex(order);
                line = line.replace("[+] ", "");
                if (line.contains("[")) {
                    String status = line.split("\\[")[1].replace("]", "");
                    detail.setStatus(statusTrans(status.trim()));
                    line = line.split("\\[")[0];
                }
                detail.setOutput(titleTrans(line.trim()));
                serverLynisResultDetailMapper.insertSelective(detail);
                order++;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String titleTrans (String title) {
        switch (title) {
            case "System tools":
                return "SECTION_SYSTEM_TOOLS";
            case "Boot and services":
                return "SECTION_BOOT_AND_SERVICES";
            case "Kernel":
                return "SECTION_KERNEL";
            case "Memory and Processes":
                return "SECTION_MEMORY_AND_PROCESSES";
            case "Users, Groups and Authentication":
                return "SECTION_USERS_GROUPS_AND_AUTHENTICATION";
            case "Shells":
                return "SECTION_SHELLS";
            case "File systems":
                return "SECTION_FILE_SYSTEMS";
            case "USB Devices":
                return "SECTION_USB_DEVICES";
            case "Storage":
                return "SECTION_STORAGE";
            case "NFS":
                return "SECTION_NFS";
            case "Name services":
                return "SECTION_NAME_SERVICES";
            case "Ports and packages":
                return "SECTION_PORTS_AND_PACKAGES";
            case "Networking":
                return "SECTION_NETWORKING";
            case "Printers and Spools":
                return "SECTION_PRINTERS_AND_SPOOLS";
            case "Software: e-mail and messaging":
                return "SECTION_EMAIL_AND_MESSAGING";
            case "Software: firewalls":
                return "SECTION_FIREWALLS";
            case "Software: webserver":
                return "SECTION_WEBSERVER";
            case "SSH Support":
                return "SECTION_SSH_SUPPORT";
            case "SNMP Support":
                return "SECTION_SNMP_SUPPORT";
            case "Databases":
                return "SECTION_DATABASES";
            case "LDAP Services":
                return "SECTION_LDAP_SERVICES";
            case "PHP":
                return "SECTION_PHP";
            case "Squid Support":
                return "SECTION_SQUID_SUPPORT";
            case "Logging and files":
                return "SECTION_LOGGING_AND_FILES";
            case "Insecure services":
                return "SECTION_INSECURE_SERVICES";
            case "Banners and identification":
                return "SECTION_BANNERS_AND_IDENTIFICATION";
            case "Scheduled tasks":
                return "SECTION_SCHEDULED_TASKS";
            case "Accounting":
                return "SECTION_ACCOUNTING";
            case "Time and Synchronization":
                return "SECTION_TIME_AND_SYNCHRONIZATION";
            case "Cryptography":
                return "SECTION_CRYPTOGRAPHY";
            case "Virtualization":
                return "SECTION_VIRTUALIZATION";
            case "Containers":
                return "SECTION_CONTAINERS";
            case "Security frameworks":
                return "SECTION_SECURITY_FRAMEWORKS";
            case "Software: file integrity":
                return "SECTION_FILE_INTEGRITY";
            case "Software: System tooling":
                return "SECTION_SYSTEM_TOOLING";
            case "Software: Malware":
                return "SECTION_MALWARE";
            case "File Permissions":
                return "SECTION_FILE_PERMISSIONS";
            case "Home directories":
                return "SECTION_HOME_DIRECTORIES";
            case "Kernel Hardening":
                return "SECTION_KERNEL_HARDENING";
            case "Hardening":
                return "SECTION_HARDENING";
            case "Custom tests":
                return "SECTION_CUSTOM_TESTS";
        }
        return title;
    }

    public String statusTrans (String status) {
        status = status.replaceAll(" ", "");
        switch (status) {
            case "ACTIVE":
                return "STATUS_ACTIVE";
            case "CHECK NEEDED":
                return "STATUS_CHECK_NEEDED";
            case "CHECKNEEDED":
                return "STATUS_CHECK_NEEDED";
            case "DEBUG":
                return "STATUS_DEBUG";
            case "DEFAULT":
                return "STATUS_DEFAULT";
            case "DIFFERENT":
                return "STATUS_DIFFERENT";
            case "DISABLED":
                return "STATUS_DISABLED";
            case "DONE":
                return "STATUS_DONE";
            case "ENABLED":
                return "STATUS_ENABLED";
            case "ERROR":
                return "STATUS_ERROR";
            case "EXPOSED":
                return "STATUS_EXPOSED";
            case "FAILED":
                return "STATUS_FAILED";
            case "FILES FOUND":
                return "STATUS_FILES_FOUND";
            case "FILESFOUND":
                return "STATUS_FILES_FOUND";
            case "FOUND":
                return "STATUS_FOUND";
            case "HARDENED":
                return "STATUS_HARDENED";
            case "INSTALLED":
                return "STATUS_INSTALLED";
            case "LOCAL ONLY":
                return "STATUS_LOCAL_ONLY";
            case "LOCALONLY":
                return "STATUS_LOCAL_ONLY";
            case "MEDIUM":
                return "STATUS_MEDIUM";
            case "NO":
                return "STATUS_NO";
            case "NO UPDATE":
                return "STATUS_NO_UPDATE";
            case "NOUPDATE":
                return "STATUS_NO_UPDATE";
            case "NON DEFAULT":
                return "STATUS_NON_DEFAULT";
            case "NONDEFAULT":
                return "STATUS_NON_DEFAULT";
            case "NONE":
                return "STATUS_NONE";
            case "NOT CONFIGURED":
                return "STATUS_NOT_CONFIGURED";
            case "NOT DISABLED":
                return "STATUS_NOT_DISABLED";
            case "NOTDISABLED":
                return "STATUS_NOT_DISABLED";
            case "NOT ENABLED":
                return "STATUS_NOT_ENABLED";
            case "NOTENABLED":
                return "STATUS_NOT_ENABLED";
            case "NOT FOUND":
                return "STATUS_NOT_FOUND";
            case "NOTFOUND":
                return "STATUS_NOT_FOUND";
            case "NOT RUNNING":
                return "STATUS_NOT_RUNNING";
            case "NOTRUNNING":
                return "STATUS_NOT_RUNNING";
            case "OFF":
                return "STATUS_OFF";
            case "OK":
                return "STATUS_OK";
            case "ON":
                return "STATUS_ON";
            case "PARTIALLY HARDENED":
                return "STATUS_PARTIALLY_HARDENED";
            case "PROTECTED":
                return "STATUS_PROTECTED";
            case "RUNNING":
                return "STATUS_RUNNING";
            case "SKIPPED":
                return "STATUS_SKIPPED";
            case "SUGGESTION":
                return "STATUS_SUGGESTION";
            case "UNKNOWN":
                return "STATUS_UNKNOWN";
            case "UNSAFE":
                return "STATUS_UNSAFE";
            case "UPDATE AVAILABLE":
                return "STATUS_UPDATE_AVAILABLE";
            case "UPDATEAVAILABLE":
                return "STATUS_UPDATE_AVAILABLE";
            case "WARNING":
                return "STATUS_WARNING";
            case "WEAK":
                return "STATUS_WEAK";
            case "YES":
                return "STATUS_YES";
        }
        return status;
    }

    public void insertLynisResultDetailSuggest(String result, String lynisId, String type, long order, LoginUser loginUser) throws Exception {
        ServerLynisResultDetail detail = new ServerLynisResultDetail();
        detail.setLynisId(lynisId);
        detail.setCreateTime(System.currentTimeMillis());
        detail.setOutput(result.replaceAll("https://cisofy.com/lynis/", "https://hummerrisk.com/"));
        detail.setType(type);
        detail.setOperator("admin");
        detail.setOrderIndex(order);
        serverLynisResultDetailMapper.insertSelective(detail);
    }

    public String removeColors(String text) {
        // 带颜色属性的文本
        // 移除颜色属性
        Pattern pattern = Pattern.compile("\u001B\\[[;\\d]*m");
        Matcher matcher = pattern.matcher(text);
        String resultStr = matcher.replaceAll("");
        List<String> colors = Arrays.asList("\\[0;30m", "\\[1;30m", "\\[0;34m", "\\[1;34m", "\\[0;32m", "\\[1;32m", "\\[0;36m", "\\[0;31m", "\\[1;31m", "\\[0;35m", "\\[1;35m", "\\[0;33m", "\\[1;33m", "\\[0;37m", "\\[1;37m", "\\[30;43m",
                "\\[0m", "\\[1m", "\\[4m", "\\[5m", "\\[7m", "\\[8m", "\\[0;44m", "\\[0;94m");
        for (String color : colors) {
            resultStr = resultStr.replaceAll(color, "");//python颜色
        }
        return resultStr;
    }

}
