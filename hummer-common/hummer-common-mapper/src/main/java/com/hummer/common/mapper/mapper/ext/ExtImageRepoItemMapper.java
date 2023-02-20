package com.hummer.common.mapper.mapper.ext;


import com.hummer.common.mapper.dto.ImageRepoItemDTO;
import com.hummer.common.mapper.domain.request.image.ImageRepoItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author harris
 */
public interface ExtImageRepoItemMapper {

    List<ImageRepoItemDTO> repoItemList(@Param("request") ImageRepoItemRequest request);
}
