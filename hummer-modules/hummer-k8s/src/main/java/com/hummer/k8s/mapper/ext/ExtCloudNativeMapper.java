package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.domain.request.cloudNative.CloudNativeRequest;
import com.hummer.common.core.dto.CloudNativeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudNativeMapper {

    List<CloudNativeDTO> getCloudNativeList(@Param("request") CloudNativeRequest request);

    Map<String, Object> topInfo(Map<String, Object> params);

    List<Map<String, Object>> k8sChart();

    List<Map<String, Object>> severityChart();

}
