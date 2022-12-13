package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudNativeConfigResultItem;
import com.hummerrisk.controller.request.config.ConfigResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtConfigResultItemMapper {

    List<CloudNativeConfigResultItem> resultItemListBySearch(@Param("request") ConfigResultItemRequest request);


}
