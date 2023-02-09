package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.controller.request.chart.ChartData;
import com.hummerrisk.commons.utils.DashboardTarget;
import com.hummerrisk.dto.HistoryScanDTO;

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

    List<ChartData> vulnRuleList(Map<String, Object> params);

    List<ChartData> vulnOverall(Map<String, Object> params);

    List<ChartData> vulnRuleGroup(Map<String, Object> params);

    List<ChartData> regionsList(Map<String, Object> params);

    List<ChartData> countList(Map<String, Object> params);

    List<Map<String, Object>> severityList(Map<String, Object> params);

    List<Map<String, Object>> totalPolicy(Map<String, Object> params);

    List<Map<String, Object>> vulnTotalPolicy(Map<String, Object> params);

    List<DashboardTarget> target(Map<String, Object> params);

    List<HistoryScanDTO> history(Map<String, Object> params);

    List<HistoryScanDTO> vulnHistory(Map<String, Object> params);

    List<DashboardTarget> vulnTarget(Map<String, Object> params);

    List<Map<String, Object>> groupList(Map<String, Object> params);

    List<Map<String, Object>> reportList(Map<String, Object> params);

    List<Map<String, Object>> tagList(Map<String, Object> params);

    List<Map<String, Object>> resourceList(Map<String, Object> params);

    List<Map<String, Object>> historyList(Map<String, Object> params);

    List<Map<String, Object>> historyDiffList(Map<String, Object> params);

}
