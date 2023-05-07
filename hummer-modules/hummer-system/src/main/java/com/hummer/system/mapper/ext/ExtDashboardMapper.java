package com.hummer.system.mapper.ext;


import com.hummer.common.core.domain.request.dashboard.AnslysisVo;
import com.hummer.common.core.domain.request.dashboard.AssetsInfo;
import com.hummer.common.core.domain.request.dashboard.HistoryScanVo;
import com.hummer.common.core.domain.request.dashboard.TaskCalendarVo;
import com.hummer.common.core.dto.PluginDTO;
import com.hummer.common.core.dto.TopInfoDTO;
import com.hummer.common.core.dto.TopScanDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public interface ExtDashboardMapper {

    TopInfoDTO topInfo(Map<String, Object> params);

    TopScanDTO topScanInfo(@Param("status") String status);

    List<String> imageChartX(Map<String, Object> params);

    List<Integer> imageChartY(Map<String, Object> params);

    List<String> codeChartX(Map<String, Object> params);

    List<Integer> codeChartY(Map<String, Object> params);

    List<String> cloudNativeChartX(Map<String, Object> params);

    List<Integer> cloudNativeChartY(Map<String, Object> params);

    List<String> configChartX(Map<String, Object> params);

    List<Integer> configChartY(Map<String, Object> params);

    List<String> fsChartX(Map<String, Object> params);

    List<Integer> fsChartY(Map<String, Object> params);

    List<TaskCalendarVo> taskCalendar();

    List<String> analysisChartX(AnslysisVo anslysisVo);

    List<Integer> analysisChartY(AnslysisVo anslysisVo);

    List<HistoryScanVo> historyScanVo(HistoryScanVo historyScanVo);

    Integer getClouds();

    Integer getAccounts();

    Integer getResources();

    List<PluginDTO> getPlugins();

    Integer getK8sClouds();

    Integer getK8sAccounts();

    Integer getK8sResources();

    List<PluginDTO> getK8sPlugins();

    AssetsInfo serverInfo();

    AssetsInfo imageInfo();

    AssetsInfo configInfo();

    AssetsInfo codeInfo();

    AssetsInfo fsInfo();

    List<Map<String, Object>> serverRiskChart(Map<String, Object> params);

}
