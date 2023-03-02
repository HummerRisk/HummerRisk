package com.hummer.system.api;


import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.dashboard.DashboardTarget;

import java.util.List;
import java.util.Map;

public interface ISystemProviderService {

    Integer insertScanHistory(AccountWithBLOBs account) throws Exception;

    void insertHistoryVulnTaskItem(HistoryVulnTaskItemWithBLOBs historyVulnTaskItem) throws Exception;

    void insertHistoryVulnTaskResource(HistoryVulnTaskResourceWithBLOBs historyVulnTaskItem) throws Exception;

    void updateHistoryVulnTaskItem(HistoryVulnTaskItemWithBLOBs historyVulnTaskItem) throws Exception;

    void insertHistoryVulnTask(HistoryVulnTask historyVulnTask) throws Exception;

    void updateHistoryVulnTask(HistoryVulnTask historyVulnTask) throws Exception;

    void updateHistoryVulnTaskResource(HistoryVulnTaskResourceWithBLOBs historyVulnTaskResource) throws Exception;

    String createMessageOrder(AccountWithBLOBs account) throws Exception;

    void createMessageOrderItem(String messageOrderId, CloudTask cloudTask) throws Exception;

    void insertScanTaskHistory(Object obj, Integer scanId, String accountId, String accountType) throws Exception;

    void deleteScanTaskHistory(Integer scanId) throws Exception;

    List<DashboardTarget> vulnTarget(Map<String, Object> params);

    List<Map<String, Object>> groupList(Map<String, Object> params);

    List<Map<String, Object>> reportList(Map<String, Object> params);

    List<Map<String, Object>> tagList(Map<String, Object> params);

    List<Map<String, Object>> resourceList(Map<String, Object> params);

    List<Map<String, Object>> historyList(Map<String, Object> params);

    List<Map<String, Object>> historyDiffList(Map<String, Object> params);

    HistoryVulnTask historyVulnTask(String id);

    void insertHistoryCloudTaskResource(HistoryCloudTaskResourceWithBLOBs historyCloudTaskResource) throws Exception;

    void updateHistoryCloudTaskResource(HistoryCloudTaskResourceWithBLOBs historyCloudTaskResource) throws Exception;

    void insertHistoryCloudTaskItem(HistoryCloudTaskItemWithBLOBs historyCloudTaskItemWithBLOBs) throws Exception;

    void updateHistoryCloudTaskItem(HistoryCloudTaskItemWithBLOBs historyCloudTaskItemWithBLOBs) throws Exception;

    void updateHistoryCloudTask(HistoryCloudTask historyCloudTask) throws Exception;

    void insertHistoryCloudTask(HistoryCloudTask historyCloudTask) throws Exception;

    HistoryCloudTask historyCloudTask(String id);

    void insertHistoryCloudTaskLog(HistoryCloudTaskLogWithBLOBs historyCloudTaskLog) throws Exception;

    void insertHistoryVulnTaskLog(HistoryVulnTaskLogWithBLOBs historyVulnTaskLog) throws Exception;

    void deleteHistoryScanTask(HistoryScanTaskExample historyScanTaskExample);

}
