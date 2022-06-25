package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.server.ServerResultRequest;
import com.hummerrisk.dto.ServerResultDTO;

import java.util.List;

public interface ExtServerResultMapper {

    List<ServerResultDTO> resultList(ServerResultRequest request);

}
