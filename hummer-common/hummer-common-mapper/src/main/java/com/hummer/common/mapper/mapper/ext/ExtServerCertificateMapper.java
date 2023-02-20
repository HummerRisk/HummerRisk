package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.ServerCertificateDTO;
import com.hummer.common.mapper.domain.request.server.ServerCertificateRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtServerCertificateMapper {

    List<ServerCertificateDTO> certificateList(@Param("request") ServerCertificateRequest request);

}
