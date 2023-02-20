package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.HistoryImageReportDTO;
import com.hummer.common.mapper.dto.HistoryImageResultDTO;
import com.hummer.common.mapper.dto.ImageResultDTO;
import com.hummer.common.mapper.dto.ImageResultWithBLOBsDTO;
import com.hummer.common.mapper.domain.request.image.ImageResultRequest;
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
