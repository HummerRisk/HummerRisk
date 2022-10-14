package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.code.CodeResultRequest;
import com.hummerrisk.dto.CodeResultDTO;
import com.hummerrisk.dto.HistoryCodeResultDTO;

import java.util.List;
import java.util.Map;

public interface ExtCodeResultMapper {

    List<CodeResultDTO> resultListWithBLOBs(CodeResultRequest request);

    List<CodeResultDTO> resultList(CodeResultRequest request);

    CodeResultDTO getCodeResult(String resultId);

    List<HistoryCodeResultDTO> history(Map<String, Object> params);

}
