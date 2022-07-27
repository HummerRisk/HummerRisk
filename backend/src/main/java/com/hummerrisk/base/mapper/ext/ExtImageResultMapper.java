package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.image.ImageResultRequest;
import com.hummerrisk.dto.HistoryImageReportDTO;
import com.hummerrisk.dto.ImageResultDTO;
import com.hummerrisk.dto.ImageResultWithBLOBsDTO;

import java.util.List;

public interface ExtImageResultMapper {

    List<ImageResultWithBLOBsDTO> resultListWithBLOBs(ImageResultRequest request);

    List<ImageResultDTO> resultList(ImageResultRequest request);

    HistoryImageReportDTO getImageResultDto(String resultId);

}
