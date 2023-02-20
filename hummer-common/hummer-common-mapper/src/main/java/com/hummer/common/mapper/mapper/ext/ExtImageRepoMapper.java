package com.hummer.common.mapper.mapper.ext;


import com.hummer.common.mapper.domain.ImageRepo;
import com.hummer.common.mapper.domain.request.image.ImageRepoRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author harris
 */
public interface ExtImageRepoMapper {

    List<ImageRepo> imageRepoList(@Param("request") ImageRepoRequest request);
}
