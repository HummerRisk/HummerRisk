package com.hummer.system.mapper.ext;

import com.hummer.common.core.domain.HistoryScan;
import com.hummer.common.core.dto.*;

import java.util.List;
import java.util.Map;

public interface ExtHistoryScanMapper {

    Integer getScanId (String accountId);

    Integer updateByExampleSelective(HistoryScan record);

    HistoryImageReportDTO getImageResultDto(String resultId);

    List<HistoryImageResultDTO> imageHistory(Map<String, Object> params);

    List<HistoryCodeResultDTO> codeHistory(Map<String, Object> params);

    List<HistoryServerResultDTO> serverHistory(Map<String, Object> params);

    List<HistoryFsResultDTO> fsHistory(Map<String, Object> params);

    List<HistoryCloudNativeResultDTO> k8sHistory(Map<String, Object> params);

    List<HistoryCloudNativeConfigResultDTO> configHistory(Map<String, Object> params);

}
