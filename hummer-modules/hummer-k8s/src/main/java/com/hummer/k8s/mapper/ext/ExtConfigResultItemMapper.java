package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.domain.CloudNativeConfigResultItemWithBLOBs;
import com.hummer.common.core.domain.request.config.ConfigResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtConfigResultItemMapper {

    List<CloudNativeConfigResultItemWithBLOBs> resultItemListBySearch(@Param("request") ConfigResultItemRequest request);


}
