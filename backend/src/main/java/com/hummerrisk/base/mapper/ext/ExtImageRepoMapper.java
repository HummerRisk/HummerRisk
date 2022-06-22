package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.base.domain.ImageRepo;
import com.hummerrisk.controller.request.image.ImageRepoRequest;
import com.hummerrisk.controller.request.packageSetting.PackageRequest;
import com.hummerrisk.dto.PackageDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author harris
 */
public interface ExtImageRepoMapper {

    List<ImageRepo> imageRepoList(@Param("request") ImageRepoRequest request);
}
