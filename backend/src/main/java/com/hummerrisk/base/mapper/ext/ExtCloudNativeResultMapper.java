package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudNativeResult;
import com.hummerrisk.base.domain.CloudNativeSourceSyncLog;
import com.hummerrisk.controller.request.cloudNative.CloudNativeSyncLogRequest;
import com.hummerrisk.controller.request.image.ImageRequest;
import com.hummerrisk.controller.request.k8s.K8sResultRequest;
import com.hummerrisk.dto.ImageDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudNativeResultMapper {

    List<CloudNativeResult> resultList(K8sResultRequest request);

    List<ImageDTO> imageList(@Param("request") ImageRequest request);

    List<CloudNativeSourceSyncLog> syncList(CloudNativeSyncLogRequest request);

}
