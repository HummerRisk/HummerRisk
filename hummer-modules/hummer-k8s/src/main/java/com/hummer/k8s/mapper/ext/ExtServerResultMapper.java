package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.domain.ServerLynisResultDetail;
import com.hummer.common.core.domain.request.server.ServerRequest;
import com.hummer.common.core.domain.request.server.ServerResultRequest;
import com.hummer.common.core.dto.ServerListDTO;
import com.hummer.common.core.dto.ServerLynisResultDetailDTO;
import com.hummer.common.core.dto.ServerResultDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtServerResultMapper {

    List<ServerResultDTO> resultList(@Param("request") ServerResultRequest request);

    ServerResultDTO result(@Param("resultId") String resultId);

    List<ServerListDTO> resultServerList(@Param("request") ServerRequest request);

    ServerListDTO resultServer(@Param("serverId") String serverId);

    List<Map<String, Object>> serverChart();

    List<Map<String, Object>> severityChart();

    List<String> serverChartX(Map<String, Object> params);

    List<Integer> serverChartY(Map<String, Object> params);

    List<ServerLynisResultDetailDTO> serverLynisResultDetailTitle (@Param("id") String id);

    List<ServerLynisResultDetailDTO> serverLynisWarnings (@Param("id") String id);

    List<ServerLynisResultDetailDTO> serverLynisSuggestions (@Param("id") String id);

    List<ServerLynisResultDetail> serverLynisResultDetails (@Param("id") String id, @Param("type") String type);

}
