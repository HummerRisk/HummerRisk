package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.config.ConfigResultRequest;
import com.hummerrisk.dto.CloudNativeConfigResultDTO;
import com.hummerrisk.dto.HistoryCloudNativeConfigResultDTO;
import com.hummerrisk.dto.HistoryCloudNativeResultDTO;
import com.hummerrisk.dto.MetricChartDTO;

import java.util.List;
import java.util.Map;

public interface ExtCloudNativeConfigResultMapper {

    List<CloudNativeConfigResultDTO> resultList(ConfigResultRequest request);

    MetricChartDTO metricChart (String resultId);

    CloudNativeConfigResultDTO getCloudNativeConfigResult(String resultId);

    List<HistoryCloudNativeConfigResultDTO> history(Map<String, Object> params);

}
