package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.config.ConfigRequest;
import com.hummerrisk.dto.CloudNativeConfigDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudNativeConfigMapper {

    List<CloudNativeConfigDTO> getCloudNativeConfigList(@Param("request") ConfigRequest request);

    Map<String, Object> topInfo(Map<String, Object> params);

    List<Map<String, Object>> configChart();

    List<Map<String, Object>> severityChart();

}
