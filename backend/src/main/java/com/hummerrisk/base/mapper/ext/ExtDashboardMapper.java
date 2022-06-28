package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.commons.utils.ChartData;
import com.hummerrisk.commons.utils.DashboardTarget;
import com.hummerrisk.dto.ScanHistoryDTO;
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

}
