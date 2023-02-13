package com.hummer.common.mapper.mapper.ext;


import com.hummer.common.core.domain.request.code.CodeRequest;
import com.hummer.common.core.dto.CodeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public interface ExtCodeMapper {

    List<CodeDTO> codeList(@Param("request") CodeRequest request);

    Map<String, Object> topInfo(Map<String, Object> params);

    List<Map<String, Object>> projectChart();

    List<Map<String, Object>> severityChart();
}
