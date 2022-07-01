package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.commons.utils.ChartData;
import com.hummerrisk.commons.utils.DashboardTarget;
import com.hummerrisk.dto.CloudScanHistoryDTO;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public interface ExtVulnMapper {

    List<ChartData> overall(Map<String, Object> params);

    List<ChartData> ruleGroup(Map<String, Object> params);

    List<ChartData> report(Map<String, Object> params);

    List<ChartData> ruleList(Map<String, Object> params);

    List<ChartData> accountList(Map<String, Object> params);

    List<ChartData> vulnList(Map<String, Object> params);

    List<ChartData> regionsList(Map<String, Object> params);

    List<Map<String, Object>> severityList(Map<String, Object> params);

    List<Map<String, Object>> totalPolicy(Map<String, Object> params);

    List<DashboardTarget> target(Map<String, Object> params);

    List<CloudScanHistoryDTO> history(Map<String, Object> params);

    List<CloudScanHistoryDTO> vulnHistory(Map<String, Object> params);

}
