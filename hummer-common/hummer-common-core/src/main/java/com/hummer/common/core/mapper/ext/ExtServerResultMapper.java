package com.hummer.common.core.mapper.ext;

import com.hummer.common.core.domain.request.server.ServerRequest;
import com.hummer.common.core.domain.request.server.ServerResultRequest;
import com.hummer.common.core.dto.HistoryServerResultDTO;
import com.hummer.common.core.dto.ServerListDTO;
import com.hummer.common.core.dto.ServerResultDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtServerResultMapper {

    List<ServerResultDTO> resultList(@Param("request")  ServerResultRequest request);

    List<ServerListDTO> resultServerList(@Param("request") ServerRequest request);

    List<Map<String, Object>> serverChart();

    List<Map<String, Object>> severityChart();

    List<String> serverChartX(Map<String, Object> params);

    List<Integer> serverChartY(Map<String, Object> params);

    List<HistoryServerResultDTO> history(Map<String, Object> params);

}
