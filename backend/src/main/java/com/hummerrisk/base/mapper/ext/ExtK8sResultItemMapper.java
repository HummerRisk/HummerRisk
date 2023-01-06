package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudNativeResultItem;
import com.hummerrisk.controller.request.k8s.K8sResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtK8sResultItemMapper {

    List<CloudNativeResultItem> resultItemListBySearch(@Param("request") K8sResultItemRequest request);

}
