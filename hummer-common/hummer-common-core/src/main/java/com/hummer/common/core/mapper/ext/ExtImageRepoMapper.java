package com.hummer.common.core.mapper.ext;


import com.hummer.common.core.domain.ImageRepo;
import com.hummer.common.core.domain.request.image.ImageRepoRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author harris
 */
public interface ExtImageRepoMapper {

    List<ImageRepo> imageRepoList(@Param("request") ImageRepoRequest request);
}
