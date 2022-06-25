package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.image.ImageResultRequest;
import com.hummerrisk.dto.ImageResultDTO;

import java.util.List;

public interface ExtImageResultMapper {

    List<ImageResultDTO> resultList(ImageResultRequest request);

}
