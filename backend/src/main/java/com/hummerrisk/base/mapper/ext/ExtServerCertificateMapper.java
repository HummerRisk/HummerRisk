package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.server.ServerCertificateRequest;
import com.hummerrisk.dto.ServerCertificateDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtServerCertificateMapper {

    List<ServerCertificateDTO> certificateList(@Param("request") ServerCertificateRequest request);

}
