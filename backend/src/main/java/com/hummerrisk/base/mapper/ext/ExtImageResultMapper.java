package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.image.ImageResultRequest;
import com.hummerrisk.dto.HistoryImageReportDTO;
import com.hummerrisk.dto.HistoryImageResultDTO;
import com.hummerrisk.dto.ImageResultDTO;
import com.hummerrisk.dto.ImageResultWithBLOBsDTO;
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
