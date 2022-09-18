package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.config.ConfigResultRequest;
import com.hummerrisk.dto.CloudNativeConfigResultDTO;
import com.hummerrisk.dto.MetricChartDTO;

import java.util.List;

public interface ExtCloudNativeConfigResultMapper {

    List<CloudNativeConfigResultDTO> resultList(ConfigResultRequest request);

    MetricChartDTO metricChart (String resultId);

}
