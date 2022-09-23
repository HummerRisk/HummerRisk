package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.controller.request.code.CodeRequest;
import com.hummerrisk.dto.CodeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public interface ExtCodeMapper {

    List<CodeDTO> codeList(@Param("request") CodeRequest request);

    Map<String, Object> topInfo(Map<String, Object> params);
}
