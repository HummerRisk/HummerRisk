package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.fs.FsResultRequest;
import com.hummerrisk.dto.FsResultDTO;
import com.hummerrisk.dto.HistoryFsResultDTO;

import java.util.List;
import java.util.Map;

public interface ExtFileSystemResultMapper {

    List<FsResultDTO> resultList(FsResultRequest request);

    FsResultDTO getFsResult(String resultId);

    List<HistoryFsResultDTO> history(Map<String, Object> params);

}
