package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.CloudNativeConfigResultDTO;
import com.hummer.common.mapper.dto.HistoryCloudNativeConfigResultDTO;
import com.hummer.common.mapper.dto.MetricChartDTO;
import com.hummer.common.mapper.domain.request.config.ConfigResultRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudNativeConfigResultMapper {

    List<CloudNativeConfigResultDTO> resultList(@Param("request") ConfigResultRequest request);

    MetricChartDTO metricChart (String resultId);

    CloudNativeConfigResultDTO getCloudNativeConfigResult(String resultId);

    List<HistoryCloudNativeConfigResultDTO> history(Map<String, Object> params);

}
