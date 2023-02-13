package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.core.domain.CloudNativeResultConfigItemWithBLOBs;
import com.hummer.common.core.domain.CloudNativeResultKubenchWithBLOBs;
import com.hummer.common.core.domain.request.k8s.K8sConfigResultItemRequest;
import com.hummer.common.core.domain.request.k8s.K8sKubenchResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtK8sResultConfigItemMapper {

    List<CloudNativeResultConfigItemWithBLOBs> resultConfigItemListBySearch(@Param("request") K8sConfigResultItemRequest request);

    List<CloudNativeResultKubenchWithBLOBs> resultKubenchItemListBySearch(@Param("request") K8sKubenchResultItemRequest request);
}
