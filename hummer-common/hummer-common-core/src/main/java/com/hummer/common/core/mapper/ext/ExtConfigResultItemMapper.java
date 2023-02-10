package com.hummer.common.core.mapper.ext;

import com.hummer.common.core.domain.CloudNativeConfigResultItem;
import com.hummer.common.core.domain.request.config.ConfigResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtConfigResultItemMapper {

    List<CloudNativeConfigResultItem> resultItemListBySearch(@Param("request") ConfigResultItemRequest request);


}
