package com.hummer.common.core.mapper.ext;

import com.hummer.common.core.domain.ImageResultItemWithBLOBs;
import com.hummer.common.core.domain.request.image.ImageResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtImageResultItemMapper {

    List<ImageResultItemWithBLOBs> resultItemListBySearch(@Param("request") ImageResultItemRequest request);


}
