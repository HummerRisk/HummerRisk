package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.controller.request.dashboard.AnslysisVo;
import com.hummerrisk.controller.request.dashboard.HistoryScanVo;
import com.hummerrisk.controller.request.dashboard.TaskCalendarVo;
import com.hummerrisk.dto.TopInfoDTO;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public interface ExtDashboardMapper {

    TopInfoDTO topInfo(Map<String, Object> params);

    List<String> packageChartX(Map<String, Object> params);

    List<Integer> packageChartY(Map<String, Object> params);

    List<String> imageChartX(Map<String, Object> params);

    List<Integer> imageChartY(Map<String, Object> params);

    List<String> codeChartX(Map<String, Object> params);

    List<Integer> codeChartY(Map<String, Object> params);

    List<String> cloudNativeChartX(Map<String, Object> params);

    List<Integer> cloudNativeChartY(Map<String, Object> params);

    List<TaskCalendarVo> taskCalendar();

    List<String> analysisChartX(AnslysisVo anslysisVo);

    List<Integer> analysisChartY(AnslysisVo anslysisVo);

    List<HistoryScanVo> historyScanVo(HistoryScanVo historyScanVo);

}
