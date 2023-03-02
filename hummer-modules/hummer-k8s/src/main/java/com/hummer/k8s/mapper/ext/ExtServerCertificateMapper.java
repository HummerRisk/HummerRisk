package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.dto.ServerCertificateDTO;
import com.hummer.common.core.domain.request.server.ServerCertificateRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtServerCertificateMapper {

    List<ServerCertificateDTO> certificateList(@Param("request") ServerCertificateRequest request);

}
