package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudNativeResult;
import com.hummerrisk.controller.request.k8s.K8sResultRequest;

import java.util.List;

public interface ExtCloudNativeResultMapper {

    List<CloudNativeResult> resultList(K8sResultRequest request);

}
