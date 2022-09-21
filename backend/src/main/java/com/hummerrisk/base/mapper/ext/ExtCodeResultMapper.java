package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.code.CodeResultRequest;
import com.hummerrisk.dto.CodeResultDTO;
import com.hummerrisk.dto.CodeResultWithBLOBsDTO;

import java.util.List;

public interface ExtCodeResultMapper {

    List<CodeResultWithBLOBsDTO> resultListWithBLOBs(CodeResultRequest request);

    List<CodeResultDTO> resultList(CodeResultRequest request);

    CodeResultDTO getCodeResult(String resultId);

}
