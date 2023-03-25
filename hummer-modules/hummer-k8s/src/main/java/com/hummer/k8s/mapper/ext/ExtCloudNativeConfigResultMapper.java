package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.domain.request.config.ConfigResultRequest;
import com.hummer.common.core.dto.CloudNativeConfigResultDTO;
import com.hummer.common.core.dto.MetricChartDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudNativeConfigResultMapper {

    List<CloudNativeConfigResultDTO> resultList(@Param("request") ConfigResultRequest request);

    MetricChartDTO metricChart (String resultId);

    CloudNativeConfigResultDTO getCloudNativeConfigResult(String resultId);

}
