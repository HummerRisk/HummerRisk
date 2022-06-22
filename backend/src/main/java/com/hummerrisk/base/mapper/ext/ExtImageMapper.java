package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.base.domain.Image;
import com.hummerrisk.controller.request.image.ImageRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author harris
 */
public interface ExtImageMapper {

    List<Image> imageList(@Param("request") ImageRequest request);
}
