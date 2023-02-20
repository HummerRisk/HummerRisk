package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.CloudNativeResultItem;
import com.hummer.common.mapper.domain.request.k8s.K8sResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtK8sResultItemMapper {

    List<CloudNativeResultItem> resultItemListBySearch(@Param("request") K8sResultItemRequest request);

}
