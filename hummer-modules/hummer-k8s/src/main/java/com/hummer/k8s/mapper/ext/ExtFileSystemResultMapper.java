package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.domain.request.fs.FsResultRequest;
import com.hummer.common.core.dto.FsResultDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtFileSystemResultMapper {

    List<FsResultDTO> resultList(@Param("request") FsResultRequest request);

    FsResultDTO getFsResult(String resultId);

}
