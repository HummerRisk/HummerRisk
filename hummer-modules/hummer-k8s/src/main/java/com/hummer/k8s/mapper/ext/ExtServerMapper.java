package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.domain.request.server.ServerRequest;
import com.hummer.common.core.dto.ServerDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtServerMapper {

    List<ServerDTO> getServerList(@Param("request") ServerRequest request);

    Map<String, Object> topInfo(Map<String, Object> params);

}
