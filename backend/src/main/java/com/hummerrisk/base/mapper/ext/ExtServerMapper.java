package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.server.ServerRequest;
import com.hummerrisk.dto.ServerDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtServerMapper {

    List<ServerDTO> getServerList(@Param("request") ServerRequest request);

}
