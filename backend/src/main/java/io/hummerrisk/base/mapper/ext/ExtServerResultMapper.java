package io.hummerrisk.base.mapper.ext;

import io.hummerrisk.controller.request.server.ServerResultRequest;
import io.hummerrisk.dto.ServerResultDTO;

import java.util.List;

public interface ExtServerResultMapper {

    List<ServerResultDTO> resultList(ServerResultRequest request);

}
