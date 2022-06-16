package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.dto.ServerResultDTO;
import com.hummerrisk.controller.request.server.ServerResultRequest;

import java.util.List;

public interface ExtServerResultMapper {

    List<ServerResultDTO> resultList(ServerResultRequest request);

}
