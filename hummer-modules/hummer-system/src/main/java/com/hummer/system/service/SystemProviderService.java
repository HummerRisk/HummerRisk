package com.hummer.system.service;


import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.code.CodeResultRequest;
import com.hummer.common.core.domain.request.config.ConfigResultRequest;
import com.hummer.common.core.domain.request.fs.FsResultRequest;
import com.hummer.common.core.domain.request.image.ImageResultRequest;
import com.hummer.common.core.domain.request.k8s.K8sResultRequest;
import com.hummer.common.core.domain.request.server.ServerResultRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.LogUtil;
import com.hummer.common.core.utils.ReadFileUtils;
import com.hummer.system.api.ISystemProviderService;
import com.hummer.system.api.model.LoginUser;
import com.hummer.system.mapper.*;
import com.hummer.system.mapper.ext.ExtHistoryScanMapper;
import com.hummer.system.mapper.ext.ExtVulnMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
@DubboService
public class SystemProviderService implements ISystemProviderService {

    @Autowired
    private NoticeService noticeService;
    @Autowired
    private NoticeCreateService noticeCreateService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ExtVulnMapper extVulnMapper;
    @Autowired
    private HistoryCloudTaskMapper historyCloudTaskMapper;
    @Autowired
    private HistoryScanTaskMapper historyScanTaskMapper;
    @Autowired
    private HistoryCodeResultMapper historyCodeResultMapper;
    @Autowired
    private SystemParameterService systemParameterService;
    @Autowired
    private HistoryCloudNativeResultMapper historyCloudNativeResultMapper;
    @Autowired
    private HistoryCloudNativeConfigResultMapper historyCloudNativeConfigResultMapper;
    @Autowired
    private HistoryFileSystemResultMapper historyFileSystemResultMapper;
    @Autowired
    private HistoryImageResultMapper historyImageResultMapper;
    @Autowired
    private HistoryServerResultMapper historyServerResultMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ExtHistoryScanMapper extHistoryScanMapper;
    @Autowired
    private LicenseService licenseService;
    @Autowired
    private MessageOrderMapper messageOrderMapper;
    @Autowired
    private MessageOrderItemMapper messageOrderItemMapper;


    @Override
    public Integer insertScanHistory(Object obj, LoginUser loginUser) throws Exception {
        return historyService.insertScanHistory(obj, loginUser);
    }

    @Override
    public String createMessageOrder(AccountWithBLOBs account) throws Exception {
        return noticeService.createMessageOrder(account);
    }

    @Override
    public String createCloudMessageOrder(AccountWithBLOBs account, String projectId) throws Exception {
        return noticeService.createCloudMessageOrder(account, projectId);
    }

    @Override
    public String createK8sMessageOrder(CloudNative cloudNative) throws Exception {
        return noticeService.createK8sMessageOrder(cloudNative);
    }

    @Override
    public void createMessageOrderItem(String messageOrderId, CloudTask cloudTask) throws Exception {
        noticeService.createMessageOrderItem(messageOrderId, cloudTask);
    }

    @Override
    public void createK8sMessageOrderItem(String messageOrderId, CloudTask cloudTask) throws Exception {
        noticeService.createK8sMessageOrderItem(messageOrderId, cloudTask);
    }

    @Override
    public void insertScanTaskHistory(Object obj, Integer scanId, String accountId, String accountType) throws Exception {
        historyService.insertScanTaskHistory(obj, scanId, accountId, accountType);
    }

    @Override
    public void deleteScanTaskHistory(Integer scanId) throws Exception {
        historyService.deleteScanTaskHistory(scanId);
    }

    @Override
    public List<Map<String, Object>> groupList(Map<String, Object> params) {
        return extVulnMapper.groupList(params);
    }

    @Override
    public List<Map<String, Object>> reportList(Map<String, Object> params) {
        return extVulnMapper.reportList(params);
    }

    @Override
    public List<Map<String, Object>> tagList(Map<String, Object> params) {
        return extVulnMapper.tagList(params);
    }

    @Override
    public List<Map<String, Object>> resourceList(Map<String, Object> params) {
        return extVulnMapper.resourceList(params);
    }

    @Override
    public List<Map<String, Object>> historyList(Map<String, Object> params) {
        return extVulnMapper.historyList(params);
    }

    @Override
    public List<Map<String, Object>> historyDiffList(Map<String, Object> params) {
        return extVulnMapper.historyDiffList(params);
    }

    @Override
    public void insertHistoryCloudTaskResource(HistoryCloudTaskResourceWithBLOBs historyCloudTaskResource) throws Exception {
        historyService.insertHistoryCloudTaskResource(historyCloudTaskResource);
    }

    @Override
    public void updateHistoryCloudTaskResource(HistoryCloudTaskResourceWithBLOBs historyCloudTaskResource) throws Exception {
        historyService.updateHistoryCloudTaskResource(historyCloudTaskResource);
    }

    @Override
    public void insertHistoryCloudTaskItem(HistoryCloudTaskItemWithBLOBs historyCloudTaskItemWithBLOBs) throws Exception {
        historyService.insertHistoryCloudTaskItem(historyCloudTaskItemWithBLOBs);
    }

    @Override
    public void updateHistoryCloudTaskItem(HistoryCloudTaskItemWithBLOBs historyCloudTaskItemWithBLOBs) throws Exception {
        historyService.updateHistoryCloudTaskItem(historyCloudTaskItemWithBLOBs);
    }

    @Override
    public void updateHistoryCloudTask(HistoryCloudTask historyCloudTask) throws Exception {
        historyService.updateHistoryCloudTask(historyCloudTask);
    }

    @Override
    public void insertHistoryCloudTask(HistoryCloudTask historyCloudTask) throws Exception {
        historyService.insertHistoryCloudTask(historyCloudTask);
    }

    @Override
    public HistoryCloudTask historyCloudTask(String id) {
        return historyCloudTaskMapper.selectByPrimaryKey(id);
    }

    @Override
    public void insertHistoryCloudTaskLog(HistoryCloudTaskLogWithBLOBs historyCloudTaskLog) throws Exception {
        historyService.insertHistoryCloudTaskLog(historyCloudTaskLog);
    }

    @Override
    public void deleteHistoryScanTask(HistoryScanTaskExample historyScanTaskExample) {
        historyScanTaskMapper.deleteByExample(historyScanTaskExample);
    }

    @Override
    public void insertHistoryCodeResult(HistoryCodeResult historyCodeResult) {
        historyService.insertHistoryCodeResult(historyCodeResult);
    }

    @Override
    public void updateHistoryCodeResult(HistoryCodeResult historyCodeResult) {
        historyService.updateHistoryCodeResult(historyCodeResult);
    }

    @Override
    public void createCodeMessageOrder(CodeResult result) {
        noticeService.createCodeMessageOrder(result);
    }

    @Override
    public void createImageMessageOrder(ImageResultWithBLOBs result) {
        noticeService.createImageMessageOrder(result);
    }

    @Override
    public String createServerMessageOrder(Server result) {
        return noticeService.createServerMessageOrder(result);
    }

    @Override
    public void createFsMessageOrder(FileSystemResult result) {
        noticeService.createFsMessageOrder(result);
    }

    @Override
    public void createCloudNativeConfigMessageOrder(CloudNativeConfigResult result) {
        noticeService.createCloudNativeConfigMessageOrder(result);
    }

    @Override
    public void createCloudNativeMessageOrder(CloudNativeResult result) {
        noticeService.createCloudNativeMessageOrder(result);
    }

    @Override
    public String getSystemParameterValue(String key) {
        return systemParameterService.getValue(key);
    }

    @Override
    public HistoryCodeResult codeResult(String id) {
        return historyCodeResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public HistoryServerResult serverResult(String id) {
        return historyServerResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public HistoryFileSystemResult fsResult(String id) {
        return historyFileSystemResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public HistoryImageResultWithBLOBs imageResult(String id) {
        return historyImageResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public HistoryCloudNativeResultWithBLOBs k8sResult(String id) {
        return historyCloudNativeResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public HistoryCloudNativeConfigResult configResult(String id) {
        return historyCloudNativeConfigResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public void insertHistoryFileSystemResult(HistoryFileSystemResult result) {
        historyService.insertHistoryFileSystemResult(result);
    }

    @Override
    public void updateHistoryFileSystemResult(HistoryFileSystemResult result) {
        historyService.updateHistoryFileSystemResult(result);
    }

    @Override
    public void insertHistoryServerResult(HistoryServerResult result) {
        historyService.insertHistoryServerResult(result);
    }

    @Override
    public void updateHistoryServerResult(HistoryServerResult result) {
        historyService.updateHistoryServerResult(result);
    }

    @Override
    public void insertHistoryImageResult(HistoryImageResultWithBLOBs result) {
        historyService.insertHistoryImageResult(result);
    }

    @Override
    public void updateHistoryImageResult(HistoryImageResultWithBLOBs result) {
        historyService.updateHistoryImageResult(result);
    }

    @Override
    public void insertHistoryCloudNativeConfigResult(HistoryCloudNativeConfigResult result) {
        historyService.insertHistoryCloudNativeConfigResult(result);
    }

    @Override
    public void updateHistoryCloudNativeConfigResult(HistoryCloudNativeConfigResult result) {
        historyService.updateHistoryCloudNativeConfigResult(result);
    }

    @Override
    public void insertHistoryCloudNativeResult(HistoryCloudNativeResultWithBLOBs result) {
        historyService.insertHistoryCloudNativeResult(result);
    }

    @Override
    public void updateHistoryCloudNativeResult(HistoryCloudNativeResultWithBLOBs result) {
        historyService.updateHistoryCloudNativeResult(result);
    }

    @Override
    public List<HistoryCodeResult> historyCodeResultByExample(HistoryCodeResultExample example) {
        return historyCodeResultMapper.selectByExample(example);
    }

    @Override
    public List<HistoryImageResult> historyImageResultByExample(HistoryImageResultExample example) {
        return historyImageResultMapper.selectByExample(example);
    }

    @Override
    public void createServerMessageOrderItem(ServerResult result, String messageOrderId) {
        noticeService.createServerMessageOrderItem(result, messageOrderId);
    }

    @Override
    public void deleteHistoryCodeResult(String id) throws Exception {
        historyCodeResultMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteHistoryCloudNativeConfigResult(String id) throws Exception {
        historyCloudNativeConfigResultMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteHistoryFsResult(String id) throws Exception {
        historyFileSystemResultMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteHistoryImageResult(String id) throws Exception {
        historyImageResultMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteHistoryK8sResult(String id) throws Exception {
        historyCloudNativeResultMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteHistoryServerResult(String id) throws Exception {
        historyServerResultMapper.deleteByPrimaryKey(id);
    }

    @Override
    public LoginUser getLoginUserByName(String id) throws Exception {
        return userService.getLoginUserByName(id);
    }

    @Override
    public HistoryImageReportDTO getImageResultDto(String resultId) {
        HistoryImageReportDTO imageResult = extHistoryScanMapper.getImageResultDto(resultId);
        return imageResult;
    }

    @Override
    public List<HistoryImageResultDTO> imageHistory(ImageResultRequest request) {
        List<HistoryImageResultDTO> historyList = extHistoryScanMapper.imageHistory(request);
        return historyList;
    }

    @Override
    public List<HistoryCodeResultDTO> codeHistory(CodeResultRequest request) {
        List<HistoryCodeResultDTO> historyList = extHistoryScanMapper.codeHistory(request);
        return historyList;
    }

    @Override
    public List<HistoryServerResultDTO> serverHistory(ServerResultRequest request) {
        List<HistoryServerResultDTO> historyList = extHistoryScanMapper.serverHistory(request);
        return historyList;
    }

    @Override
    public List<HistoryFsResultDTO> fsHistory(FsResultRequest request) {
        List<HistoryFsResultDTO> historyList = extHistoryScanMapper.fsHistory(request);
        return historyList;
    }

    @Override
    public List<HistoryCloudNativeResultDTO> k8sHistory(K8sResultRequest request) {
        List<HistoryCloudNativeResultDTO> historyList = extHistoryScanMapper.k8sHistory(request);
        return historyList;
    }

    @Override
    public List<HistoryCloudNativeConfigResultDTO> configHistory(ConfigResultRequest request) {
        List<HistoryCloudNativeConfigResultDTO> historyList = extHistoryScanMapper.configHistory(request);
        return historyList;
    }

    @Override
    public String getCodeCredential() {
        String BASE_CREDENTIAL_DIC = "support/credential/";
        String JSON_EXTENSION = ".json";
        String pluginId = "hummer-code-plugin";
        try {
            return ReadFileUtils.readConfigFile(BASE_CREDENTIAL_DIC, pluginId, JSON_EXTENSION);
        } catch (Exception e) {
            LogUtil.error("Error getting credential parameters: " + pluginId, e);
            HRException.throwException(Translator.get("i18n_ex_plugin_get"));
        }
        return Translator.get("i18n_ex_plugin_get");
    }

    @Override
    public boolean license() {
        return licenseService.license();
    }

    @Override
    public void sendTask(MessageOrder messageOrder) throws Exception {
        noticeCreateService.sendTask(messageOrder);
    }

    @Override
    public void updateMessageOrderItem(MessageOrderItem messageOrderItem) throws Exception {
        messageOrderItemMapper.updateByPrimaryKeySelective(messageOrderItem);
    }

    @Override
    public void updateMessageOrder(MessageOrder messageOrder) throws Exception {
        messageOrderMapper.updateByPrimaryKeySelective(messageOrder);
    }

    @Override
    public List<MessageOrderItem> messageOrderItemList(MessageOrderItem messageOrderItem) throws Exception {
        MessageOrderItemExample messageOrderItemExample = new MessageOrderItemExample();
        messageOrderItemExample.createCriteria().andMessageOrderIdEqualTo(messageOrderItem.getMessageOrderId()).andTaskIdEqualTo(messageOrderItem.getTaskId());
        List<MessageOrderItem> messageOrderItemList = messageOrderItemMapper.selectByExample(messageOrderItemExample);
        return messageOrderItemList;
    }

    @Override
    public List<MessageOrder> messageOrderList(MessageOrder messageOrder) throws Exception {
        MessageOrderExample messageOrderExample = new MessageOrderExample();
        messageOrderExample.createCriteria().andAccountIdEqualTo(messageOrder.getAccountId());
        List<MessageOrder> messageOrderList = messageOrderMapper.selectByExample(messageOrderExample);
        return messageOrderList;
    }


}
