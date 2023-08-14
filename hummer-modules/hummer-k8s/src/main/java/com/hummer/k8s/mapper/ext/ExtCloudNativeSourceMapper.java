package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.domain.request.cloudNative.CloudNativeSourceRequest;
import com.hummer.common.core.domain.request.cloudNative.CloudNativeSourceVo;
import com.hummer.common.core.domain.request.k8s.*;
import com.hummer.common.core.dto.CloudNativeSourceDTO;
import com.hummer.common.core.dto.SituationDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudNativeSourceMapper {

    List<CloudNativeSourceDTO> getCloudNativeSourceList(@Param("request") CloudNativeSourceRequest request);

    SituationDTO situationInfo(Map<String, Object> params);

    K8sTopology k8sTopology();

    RiskTopology riskTopology(@Param("request") RiskRequest request);

    K8sImage getImage(@Param("request") RiskRequest request);

    NodeTopology nodeTopology();

    NameSpaceTopology namespaceTopology();

    List<Map<String, String>> namespaces();

    List<CloudNativeSourceVo> allCloudNativeSource2YamlList();


}
