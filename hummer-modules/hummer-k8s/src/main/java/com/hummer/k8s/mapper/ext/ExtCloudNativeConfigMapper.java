package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.domain.request.config.ConfigRequest;
import com.hummer.common.core.dto.CloudNativeConfigDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudNativeConfigMapper {

    List<CloudNativeConfigDTO> getCloudNativeConfigList(@Param("request") ConfigRequest request);

    Map<String, Object> topInfo(Map<String, Object> params);

    List<Map<String, Object>> configChart();

    List<Map<String, Object>> severityChart();

}
