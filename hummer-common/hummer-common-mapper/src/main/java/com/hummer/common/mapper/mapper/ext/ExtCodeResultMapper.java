package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.CodeResultDTO;
import com.hummer.common.mapper.dto.HistoryCodeResultDTO;
import com.hummer.common.mapper.domain.request.code.CodeResultRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCodeResultMapper {

    List<CodeResultDTO> resultListWithBLOBs(@Param("request") CodeResultRequest request);

    List<CodeResultDTO> resultList(@Param("request") CodeResultRequest request);

    CodeResultDTO getCodeResult(String resultId);

    List<HistoryCodeResultDTO> history(Map<String, Object> params);

}
