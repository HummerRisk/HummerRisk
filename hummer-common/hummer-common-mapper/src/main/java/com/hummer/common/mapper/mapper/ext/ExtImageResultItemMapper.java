package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.ImageResultItemWithBLOBs;
import com.hummer.common.mapper.domain.request.image.ImageResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtImageResultItemMapper {

    List<ImageResultItemWithBLOBs> resultItemListBySearch(@Param("request") ImageResultItemRequest request);


}
