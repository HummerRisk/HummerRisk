package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.ServerDTO;
import com.hummer.common.mapper.domain.request.server.ServerRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtServerMapper {

    List<ServerDTO> getServerList(@Param("request") ServerRequest request);

    Map<String, Object> topInfo(Map<String, Object> params);

}
