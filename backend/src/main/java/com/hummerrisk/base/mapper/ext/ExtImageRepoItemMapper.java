package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.controller.request.image.ImageRepoItemRequest;
import com.hummerrisk.dto.ImageRepoItemDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author harris
 */
public interface ExtImageRepoItemMapper {

    List<ImageRepoItemDTO> repoItemList(@Param("request") ImageRepoItemRequest request);
}
