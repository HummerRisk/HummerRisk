package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.dto.HistoryImageReportDTO;
import com.hummer.common.core.dto.HistoryImageResultDTO;
import com.hummer.common.core.dto.ImageResultDTO;
import com.hummer.common.core.dto.ImageResultWithBLOBsDTO;
import com.hummer.common.core.domain.request.image.ImageResultRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtImageResultMapper {

    List<ImageResultWithBLOBsDTO> resultListWithBLOBs(@Param("request") ImageResultRequest request);

    List<ImageResultDTO> resultList(@Param("request") ImageResultRequest request);

    HistoryImageReportDTO getImageResultDto(String resultId);

    ImageResultDTO getImageResult(String resultId);

    List<HistoryImageResultDTO> history(Map<String, Object> params);

}
