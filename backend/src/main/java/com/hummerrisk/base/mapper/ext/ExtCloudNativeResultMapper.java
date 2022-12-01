package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudNativeSourceSyncLogWithBLOBs;
import com.hummerrisk.controller.request.cloudNative.CloudNativeSyncLogRequest;
import com.hummerrisk.controller.request.image.ImageRequest;
import com.hummerrisk.controller.request.k8s.K8sResultRequest;
import com.hummerrisk.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudNativeResultMapper {

    List<CloudNativeResultDTO> resultList(K8sResultRequest request);

    List<ImageDTO> imageList(@Param("request") ImageRequest request);

    List<CloudNativeSourceSyncLogWithBLOBsDTO> syncList(@Param("request") CloudNativeSyncLogRequest request);

    MetricChartDTO metricChart (String resultId);

    MetricChartDTO metricConfigChart (String resultId);

    List<HistoryCloudNativeResultDTO> history(Map<String, Object> params);

    CloudNativeResultDTO getCloudNativeResult(String resultId);

}
