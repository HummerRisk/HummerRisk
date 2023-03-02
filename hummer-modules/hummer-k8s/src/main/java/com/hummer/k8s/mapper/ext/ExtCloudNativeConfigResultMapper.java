package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.dto.CloudNativeConfigResultDTO;
import com.hummer.common.core.dto.HistoryCloudNativeConfigResultDTO;
import com.hummer.common.core.dto.MetricChartDTO;
import com.hummer.common.core.domain.request.config.ConfigResultRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudNativeConfigResultMapper {

    List<CloudNativeConfigResultDTO> resultList(@Param("request") ConfigResultRequest request);

    MetricChartDTO metricChart (String resultId);

    CloudNativeConfigResultDTO getCloudNativeConfigResult(String resultId);

    List<HistoryCloudNativeConfigResultDTO> history(Map<String, Object> params);

}
