package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.domain.request.cloudNative.CloudNativeSyncLogRequest;
import com.hummer.common.core.domain.request.image.ImageRequest;
import com.hummer.common.core.domain.request.k8s.K8sResultRequest;
import com.hummer.common.core.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudNativeResultMapper {

    List<CloudNativeResultDTO> resultList(K8sResultRequest request);

    List<ImageDTO> imageList(@Param("request") ImageRequest request);

    List<CloudNativeSourceSyncLogWithBLOBsDTO> syncList(@Param("request") CloudNativeSyncLogRequest request);

    MetricChartDTO metricChart(String resultId);

    MetricChartDTO metricConfigChart(String resultId);

    KubenchChartDTO kubenchChart(String resultId);

    CloudNativeResultDTO getCloudNativeResult(String resultId);

}
