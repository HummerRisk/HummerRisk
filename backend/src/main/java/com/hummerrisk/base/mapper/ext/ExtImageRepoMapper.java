package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.base.domain.ImageRepo;
import com.hummerrisk.controller.request.image.ImageRepoRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author harris
 */
public interface ExtImageRepoMapper {

    List<ImageRepo> imageRepoList(@Param("request") ImageRepoRequest request);
}
