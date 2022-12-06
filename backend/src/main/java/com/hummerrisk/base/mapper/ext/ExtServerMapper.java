package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.server.ServerRequest;
import com.hummerrisk.dto.ServerDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtServerMapper {

    List<ServerDTO> getServerList(@Param("request") ServerRequest request);

    Map<String, Object> topInfo(Map<String, Object> params);

}
