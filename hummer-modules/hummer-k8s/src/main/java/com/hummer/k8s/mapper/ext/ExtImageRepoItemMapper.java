package com.hummer.k8s.mapper.ext;


import com.hummer.common.core.dto.ImageRepoItemDTO;
import com.hummer.common.core.domain.request.image.ImageRepoItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author harris
 */
public interface ExtImageRepoItemMapper {

    List<ImageRepoItemDTO> repoItemList(@Param("request") ImageRepoItemRequest request);
}
