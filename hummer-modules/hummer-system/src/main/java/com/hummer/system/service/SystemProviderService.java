package com.hummer.system.service;


import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.dashboard.DashboardTarget;
import com.hummer.system.api.ISystemProviderService;
import com.hummer.system.mapper.HistoryCloudTaskMapper;
import com.hummer.system.mapper.HistoryScanMapper;
import com.hummer.system.mapper.HistoryScanTaskMapper;
import com.hummer.system.mapper.HistoryVulnTaskMapper;
import com.hummer.system.mapper.ext.ExtVulnMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.Lazy;

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

    @Override
    public Integer insertScanHistory(AccountWithBLOBs account) throws Exception {
        return historyService.insertScanHistory(account);
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


}
