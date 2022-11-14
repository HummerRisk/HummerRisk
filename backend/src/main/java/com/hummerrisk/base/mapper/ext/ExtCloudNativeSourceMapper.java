package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.cloudNative.CloudNativeSourceRequest;
import com.hummerrisk.controller.request.k8s.*;
import com.hummerrisk.dto.CloudNativeSourceDTO;
import com.hummerrisk.dto.SituationDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudNativeSourceMapper {

    List<CloudNativeSourceDTO> getCloudNativeSourceList(@Param("request") CloudNativeSourceRequest request);

    SituationDTO situationInfo(Map<String, Object> params);

    K8sTopology k8sTopology();

    RiskTopology riskTopology(@Param("k8sId") String k8sId);

    K8sImage getImage(@Param("k8sId") String k8sId);

    NodeTopology nodeTopology();

    NameSpaceTopology namespaceTopology();


}
