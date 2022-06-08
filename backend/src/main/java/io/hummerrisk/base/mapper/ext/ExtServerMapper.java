package io.hummerrisk.base.mapper.ext;

import io.hummerrisk.controller.request.server.ServerRequest;
import io.hummerrisk.dto.ServerDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtServerMapper {

    List<ServerDTO> getServerList(@Param("request") ServerRequest request);

}
