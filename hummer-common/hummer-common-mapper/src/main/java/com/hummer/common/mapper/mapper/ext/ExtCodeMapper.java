package com.hummer.common.mapper.mapper.ext;


import com.hummer.common.mapper.dto.CodeDTO;
import com.hummer.common.mapper.domain.request.code.CodeRequest;
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
