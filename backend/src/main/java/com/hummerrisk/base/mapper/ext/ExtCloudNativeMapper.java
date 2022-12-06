package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.cloudNative.CloudNativeRequest;
import com.hummerrisk.dto.CloudNativeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudNativeMapper {

    List<CloudNativeDTO> getCloudNativeList(@Param("request") CloudNativeRequest request);

    Map<String, Object> topInfo(Map<String, Object> params);

    List<Map<String, Object>> k8sChart();

    List<Map<String, Object>> severityChart();

}
