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

    List<TaskCalendarVo> taskCalendar();

    List<String> analysisChartX(AnslysisVo anslysisVo);

    List<Integer> analysisChartY(AnslysisVo anslysisVo);

    List<HistoryScanVo> historyScanVo(HistoryScanVo historyScanVo);

    Integer getClouds();

    Integer getAccounts();

    Integer getResources();

    List<PluginDTO> getPlugins();

}
