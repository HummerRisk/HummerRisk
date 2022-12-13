package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudNativeResultConfigItemWithBLOBs;
import com.hummerrisk.controller.request.k8s.K8sConfigResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtK8sResultConfigItemMapper {

    List<CloudNativeResultConfigItemWithBLOBs> resultConfigItemListBySearch(@Param("request") K8sConfigResultItemRequest request);
}
