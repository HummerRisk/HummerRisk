package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.CloudNativeConfigResultItem;
import com.hummer.common.mapper.domain.request.config.ConfigResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtConfigResultItemMapper {

    List<CloudNativeConfigResultItem> resultItemListBySearch(@Param("request") ConfigResultItemRequest request);


}
