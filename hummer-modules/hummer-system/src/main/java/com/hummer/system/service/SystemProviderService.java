package com.hummer.system.service;


import com.hummer.common.core.constant.ParamConstants;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.dashboard.DashboardTarget;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.system.api.ISystemProviderService;
import com.hummer.system.mapper.*;
import com.hummer.system.mapper.ext.ExtVulnMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
@DubboService
public class SystemProviderService implements ISystemProviderService {

    @Resource
    private NoticeService noticeService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ExtVulnMapper extVulnMapper;
    @Resource
    private HistoryVulnTaskMapper historyVulnTaskMapper;
    @Resource
    private HistoryCloudTaskMapper historyCloudTaskMapper;
    @Resource
    private HistoryScanMapper historyScanMapper;
    @Resource
    private HistoryScanTaskMapper historyScanTaskMapper;
    @Resource
    private ProxyMapper proxyMapper;
    @Resource
    private HistoryCodeResultMapper historyCodeResultMapper;
    @Resource
    private SystemParameterService systemParameterService;
    @Resource
    private HistoryCloudNativeResultMapper historyCloudNativeResultMapper;
    @Resource
    private HistoryCloudNativeConfigResultMapper historyCloudNativeConfigResultMapper;
    @Resource
    private HistoryFileSystemResultMapper historyFileSystemResultMapper;
    @Resource
    private HistoryImageResultMapper historyImageResultMapper;
    @Resource
    private PluginMapper pluginMapper;
    @Resource
    private HistoryServerResultMapper historyServerResultMapper;

    @Override
    public Integer insertScanHistory(Object obj) throws Exception {
        return historyService.insertScanHistory(obj);
    }

    @Override
    public void insertHistoryVulnTaskItem(HistoryVulnTaskItemWithBLOBs historyVulnTaskItem) throws Exception {
        historyService.insertHistoryVulnTaskItem(historyVulnTaskItem);
    }

    @Override
    public void insertHistoryVulnTaskResource(HistoryVulnTaskResourceWithBLOBs historyVulnTaskItem) throws Exception {
        historyService.insertHistoryVulnTaskResource(historyVulnTaskItem);
    }


    @Override
    public void updateHistoryVulnTaskItem(HistoryVulnTaskItemWithBLOBs historyVulnTaskItem) throws Exception {
        historyService.updateHistoryVulnTaskItem(historyVulnTaskItem);
    }

    @Override
    public void insertHistoryVulnTask(HistoryVulnTask historyVulnTask) throws Exception {
        historyService.insertHistoryVulnTask(historyVulnTask);
    }

    @Override
    public void updateHistoryVulnTask(HistoryVulnTask historyVulnTask) throws Exception {
        historyService.updateHistoryVulnTask(historyVulnTask);
    }

    @Override
    public void updateHistoryVulnTaskResource(HistoryVulnTaskResourceWithBLOBs historyVulnTaskResource) throws Exception {
        historyService.updateHistoryVulnTaskResource(historyVulnTaskResource);
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
    public List<DashboardTarget> vulnTarget(Map<String, Object> params) {
        return extVulnMapper.vulnTarget(params);
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
    public HistoryVulnTask historyVulnTask(String id) {
        return historyVulnTaskMapper.selectByPrimaryKey(id);
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
    public void insertHistoryVulnTaskLog(HistoryVulnTaskLogWithBLOBs historyVulnTaskLog) throws Exception {
        historyService.insertHistoryVulnTaskLog(historyVulnTaskLog);
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


}
