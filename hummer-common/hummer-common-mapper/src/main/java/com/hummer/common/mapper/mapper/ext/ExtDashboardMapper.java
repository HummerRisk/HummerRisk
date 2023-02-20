package com.hummer.common.mapper.mapper.ext;


import com.hummer.common.mapper.dto.TopInfoDTO;
import com.hummer.common.mapper.dto.TopScanDTO;
import com.hummer.common.mapper.domain.request.dashboard.AnslysisVo;
import com.hummer.common.mapper.domain.request.dashboard.HistoryScanVo;
import com.hummer.common.mapper.domain.request.dashboard.TaskCalendarVo;
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

}
