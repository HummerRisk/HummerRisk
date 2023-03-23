package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.domain.request.image.ImageResultRequest;
import com.hummer.common.core.dto.ImageResultDTO;
import com.hummer.common.core.dto.ImageResultWithBLOBsDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtImageResultMapper {

    List<ImageResultWithBLOBsDTO> resultListWithBLOBs(@Param("request") ImageResultRequest request);

    List<ImageResultDTO> resultList(@Param("request") ImageResultRequest request);

    ImageResultDTO getImageResult(String resultId);


}
