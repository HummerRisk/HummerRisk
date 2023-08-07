package com.hummer.system.service;


import com.hummer.common.core.domain.*;
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
    private HistoryService historyService;
    @Autowired
    private ExtVulnMapper extVulnMapper;
    @Autowired
    private HistoryCloudTaskMapper historyCloudTaskMapper;
    @Autowired
    private HistoryScanTaskMapper historyScanTaskMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ExtHistoryScanMapper extHistoryScanMapper;
    @Autowired
    private SystemParameterService systemParameterService;
    @Autowired
    private LicenseService licenseService;


    @Override
    public Integer insertScanHistory(Object obj) throws Exception {
        return historyService.insertScanHistory(obj);
    }

    @Override
    public String createMessageOrder(AccountWithBLOBs account) throws Exception {
        return noticeService.createMessageOrder(account);
    }

    @Override
    public void createMessageOrderItem(String messageOrderId, CloudTask cloudTask) throws Exception {
        noticeService.createMessageOrderItem(messageOrderId, cloudTask);
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
    public String getSystemParameterValue(String key) {
        return systemParameterService.getValue(key);
    }

    @Override
    public LoginUser getLoginUserByName(String id) throws Exception {
        return userService.getLoginUserByName(id);
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


}
