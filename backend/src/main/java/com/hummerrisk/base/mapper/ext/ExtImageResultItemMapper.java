package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.ImageResultItemWithBLOBs;
import com.hummerrisk.controller.request.image.ImageResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtImageResultItemMapper {

    List<ImageResultItemWithBLOBs> resultItemListBySearch(@Param("request") ImageResultItemRequest request);


}
