package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.FsResultDTO;
import com.hummer.common.mapper.dto.HistoryFsResultDTO;
import com.hummer.common.mapper.domain.request.fs.FsResultRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtFileSystemResultMapper {

    List<FsResultDTO> resultList(@Param("request") FsResultRequest request);

    FsResultDTO getFsResult(String resultId);

    List<HistoryFsResultDTO> history(Map<String, Object> params);

}
