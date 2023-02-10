package com.hummer.common.core.mapper.ext;

import com.hummer.common.core.domain.request.fs.FsResultRequest;
import com.hummer.common.core.dto.FsResultDTO;
import com.hummer.common.core.dto.HistoryFsResultDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtFileSystemResultMapper {

    List<FsResultDTO> resultList(@Param("request") FsResultRequest request);

    FsResultDTO getFsResult(String resultId);

    List<HistoryFsResultDTO> history(Map<String, Object> params);

}
