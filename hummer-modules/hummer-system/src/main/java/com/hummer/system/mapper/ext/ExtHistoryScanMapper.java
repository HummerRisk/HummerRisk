package com.hummer.system.mapper.ext;

import com.hummer.common.core.domain.HistoryScan;
import com.hummer.common.core.domain.request.code.CodeResultRequest;
import com.hummer.common.core.domain.request.config.ConfigResultRequest;
import com.hummer.common.core.domain.request.fs.FsResultRequest;
import com.hummer.common.core.domain.request.image.ImageResultRequest;
import com.hummer.common.core.domain.request.k8s.K8sResultRequest;
import com.hummer.common.core.domain.request.server.ServerResultRequest;
import com.hummer.common.core.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtHistoryScanMapper {

    Integer getScanId (String accountId);

    Integer updateByExampleSelective(HistoryScan record);

    HistoryImageReportDTO getImageResultDto(String resultId);

    List<HistoryImageResultDTO> imageHistory(@Param("request") ImageResultRequest request);

    List<HistoryCodeResultDTO> codeHistory(@Param("request") CodeResultRequest request);

    List<HistoryServerResultDTO> serverHistory(@Param("request") ServerResultRequest request);

    List<HistoryFsResultDTO> fsHistory(@Param("request") FsResultRequest request);

    List<HistoryCloudNativeResultDTO> k8sHistory(@Param("request") K8sResultRequest request);

    List<HistoryCloudNativeConfigResultDTO> configHistory(@Param("request") ConfigResultRequest request);

}
