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
import com.hummerrisk.controller.request.server.ServerCertificateRequest;
import com.hummerrisk.controller.request.server.ServerRequest;
import com.hummerrisk.controller.request.server.ServerResultRequest;
import com.hummerrisk.controller.request.server.ServerRuleRequest;
import com.hummerrisk.dto.*;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.proxy.server.SshUtil;
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

    public boolean validate(List<String> ids) {
        ids.forEach(id -> {
            try {
                boolean validate = validate(id);
                if(!validate) throw new HRException(Translator.get("failed_server"));
            } catch (Exception e) {
                LogUtil.error(e.getMessage());
                throw new HRException(e.getMessage());
            }
        });
        return true;
    }


    public boolean validate(String id) {
        try {
            Server server = serverMapper.selectByPrimaryKey(id);
            //检验虚拟机的有效性
            boolean valid = validateAccount(server);
            if (valid) {
                server.setStatus(CloudAccountConstants.Status.VALID.name());
            } else {
                server.setStatus(CloudAccountConstants.Status.INVALID.name());
            }
            serverMapper.updateByPrimaryKeySelective(server);
            return valid;
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            return false;
        }

    }

    public Boolean scan(List<String> ids) {
        ids.forEach(id -> {
            try {
                scan(id);
            } catch (Exception e) {
                LogUtil.error(e.getMessage());
                throw new HRException(e.getMessage());
            }
        });
        return true;
    }

    public Boolean scan(String id) throws Exception{
            ServerRequest request = new ServerRequest();
            request.setId(id);//serverId
            Server server = BeanUtils.copyBean(new Server(), getServerList(request).get(0));
            Integer scanId = historyService.insertScanHistory(server);
            if(StringUtils.equalsIgnoreCase(server.getStatus(), CloudAccountConstants.Status.VALID.name())) {
                deleteServerResult(id);
                List<ServerRuleDTO> ruleList = ruleList(null);
                ServerResult result = new ServerResult();
                String serverGroupName = serverGroupMapper.selectByPrimaryKey(server.getServerGroupId()).getName();
                for(ServerRuleDTO dto : ruleList) {
                    BeanUtils.copyBean(result, server);
                    result.setId(UUIDUtil.newUUID());
                    result.setServerId(id);
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
                    serverResultMapper.insertSelective(result);

                    saveServerResultLog(result.getId(), "i18n_start_server_result", "", true);

                    OperationLogService.log(SessionUtils.getUser(), result.getId(), result.getServerName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.SCAN, "i18n_start_server_result");

                    historyService.insertScanTaskHistory(result, scanId, server.getId(), TaskEnum.serverAccount.getType());

                    historyService.insertHistoryServerTask(BeanUtils.copyBean(new HistoryServerTask(), result));
                }
            }
        return true;
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
            if(server.getIsProxy()!=null && server.getIsProxy()) {
                proxy = proxyMapper.selectByPrimaryKey(server.getProxyId());
            }
            LogUtil.info(server.getId() + " {server}[command]: " + server.getName() + "   "  + script);
            String returnLog = execute(server, script, proxy);
            result.setReturnLog(returnLog);
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.FINISHED.toString());
            serverResultMapper.updateByPrimaryKeySelective(result);

            noticeService.createServerMessageOrder(result);

            saveServerResultLog(result.getId(), "i18n_end_server_result", returnLog, true);

            historyService.updateHistoryServerTask(BeanUtils.copyBean(new HistoryServerTask(), result));
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            result.setUpdateTime(System.currentTimeMillis());
            result.setResultStatus(CloudTaskConstants.TASK_STATUS.ERROR.toString());
            serverResultMapper.updateByPrimaryKeySelective(result);
            historyService.updateHistoryServerTask(BeanUtils.copyBean(new HistoryServerTask(), result));
            saveServerResultLog(result.getId(), "i18n_operation_ex" + ": " + StringUtils.substring(e.getMessage(), 0, 900) + "...", e.getMessage(), false);
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

    public void deleteServerResult(String id) {
        ServerResultExample example = new ServerResultExample();
        example.createCriteria().andServerIdEqualTo(id);//serverId
        List<ServerResult> list = serverResultMapper.selectByExample(example);

        for (ServerResult result : list) {
            ServerResultLogExample logExample = new ServerResultLogExample();
            logExample.createCriteria().andResultIdEqualTo(result.getRuleId());
            serverResultLogMapper.deleteByExample(logExample);
        }
        serverResultMapper.deleteByExample(example);
    }

    public void saveServerResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        ServerResultLog serverResultLog = new ServerResultLog();
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

        historyService.insertHistoryServerTaskLog(BeanUtils.copyBean(new HistoryServerTaskLog(), serverResultLog));
    }

    private boolean validateAccount(Server server) {
        try {
            Proxy proxy = new Proxy();
            if(server.getIsProxy()!=null && server.getIsProxy()) {
                proxy = proxyMapper.selectByPrimaryKey(server.getProxyId());
            }
            return StringUtils.equalsIgnoreCase(login(server, proxy).getStatus(),  CloudAccountConstants.Status.VALID.name());
        } catch (Exception e) {
            LogUtil.error(String.format("HRException in verifying server, server: [%s], ip: [%s], error information:%s", server.getName(), server.getIp(), e.getMessage()), e);
            return false;
        }
    }

    public List<ServerGroup> getServerGroupList() {
        return serverGroupMapper.selectByExample(null);
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

    public int addServer(MultipartFile keyFile, Server server) throws Exception {
        String id = UUIDUtil.newUUID();
        server.setId(id);
        server.setCreator(SessionUtils.getUserId());
        server.setCreateTime(System.currentTimeMillis());
        server.setUpdateTime(System.currentTimeMillis());
        Proxy proxy = new Proxy();
        if(server.getIsProxy()!=null && server.getIsProxy()) {
            proxy = proxyMapper.selectByPrimaryKey(server.getProxyId());
        }
        if(!server.getIsCertificate()) {
            if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "file")) {
                String keyFilePath = upload(keyFile, ServerConstants.DEFAULT_BASE_DIR);
                String publicKey = ReadFileUtils.readToBuffer(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
                server.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
                server.setPublicKey(publicKey);
            } else if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "str")) {
                String uuid = UUIDUtil.newUUID();
                CommandUtils.saveAsFile(server.getPublicKey(), ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/", ServerConstants.HUMMER_RSA);
                server.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/" + ServerConstants.HUMMER_RSA);
            }
        }

        server = login(server, proxy);

        OperationLogService.log(SessionUtils.getUser(), server.getId(), server.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.CREATE, "i18n_create_server");
        return serverMapper.insertSelective(server);
    }

    public int editServer(MultipartFile keyFile, Server server) throws Exception {
        server.setUpdateTime(System.currentTimeMillis());
        Proxy proxy = new Proxy();
        if(server.getIsProxy()!=null && server.getIsProxy()) {
            proxy = proxyMapper.selectByPrimaryKey(server.getProxyId());
        }

        if(!server.getIsCertificate()) {
            if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "file")) {
                String keyFilePath = upload(keyFile, ServerConstants.DEFAULT_BASE_DIR);
                String publicKey = ReadFileUtils.readToBuffer(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
                server.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR + keyFilePath);
                server.setPublicKey(publicKey);
            } else if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "str")) {
                String uuid = UUIDUtil.newUUID();
                CommandUtils.saveAsFile(server.getPublicKey(), ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/", ServerConstants.HUMMER_RSA);
                server.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/" + ServerConstants.HUMMER_RSA);
            }
        }

        server = login(server, proxy);

        OperationLogService.log(SessionUtils.getUser(), server.getId(), server.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.UPDATE, "i18n_update_server");
        return serverMapper.updateByPrimaryKeySelective(server);
    }

    public void deleteServer(String id) throws Exception {
        serverMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.SERVER.name(), ResourceOperation.DELETE, "i18n_delete_server");
    }


    public Server login(Server server, Proxy proxy) throws Exception {
        try {
            SshUtil.login(server, proxy);
            server.setStatus(CloudAccountConstants.Status.VALID.name());
            server.setAuthType("ssh2");
        } catch (Exception e) {
            try {
                SshUtil.loginSshd(server, proxy);
                server.setStatus(CloudAccountConstants.Status.VALID.name());
                server.setAuthType("sshd");
            } catch (Exception ex) {
                server.setStatus(CloudAccountConstants.Status.INVALID.name());
                server.setAuthType("sshd");
            }
        }
        return server;
    }

    public String execute(Server server, String cmd, Proxy proxy) throws Exception {
        try {
            if (StringUtils.equalsIgnoreCase(server.getAuthType(), "ssh2")) {
                return SshUtil.execute(SshUtil.login(server, proxy), cmd);
            } else {
                return SshUtil.executeSshd(SshUtil.loginExecute(server, proxy), cmd);
            }
        } catch (Exception e) {
            return "";
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

    public ServerResultDTO getServerResult(String resultId) {
        ServerResultRequest request = new ServerResultRequest();
        request.setId(resultId);
        return extServerResultMapper.resultList(request) !=null?extServerResultMapper.resultList(request).get(0):new ServerResultDTO();
    }

    public List<ServerResultLog> getServerResultLog(String resultId) {
        ServerResultLogExample example = new ServerResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return serverResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public List<ServerCertificate> allCertificateList() {
        return serverCertificateMapper.selectByExample(null);
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
            CommandUtils.saveAsFile(certificate.getPublicKey(), ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/", ServerConstants.HUMMER_RSA);
            certificate.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/" + ServerConstants.HUMMER_RSA);
        }

        OperationLogService.log(SessionUtils.getUser(), certificate.getId(), certificate.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.CREATE, "i18n_create_server");
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
            CommandUtils.saveAsFile(certificate.getPublicKey(), ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/", ServerConstants.HUMMER_RSA);
            certificate.setPublicKeyPath(ServerConstants.DEFAULT_BASE_DIR_KEY + uuid + "/" + ServerConstants.HUMMER_RSA);
        }

        OperationLogService.log(SessionUtils.getUser(), certificate.getId(), certificate.getName(), ResourceTypeConstants.SERVER.name(), ResourceOperation.UPDATE, "i18n_update_server");
        return serverCertificateMapper.updateByPrimaryKeySelective(certificate);
    }

    public void deleteCertificate(String id) throws Exception {
        serverCertificateMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.SERVER.name(), ResourceOperation.DELETE, "i18n_delete_server");
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
            return FileUploadUtils.uploadCertificate(dir, file, extension.contains(".")?"." + extension:"");
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

}
