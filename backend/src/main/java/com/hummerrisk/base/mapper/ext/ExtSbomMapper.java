package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.base.domain.SbomVersion;
import com.hummerrisk.controller.request.sbom.SbomRequest;
import com.hummerrisk.controller.request.sbom.SbomVersionRequest;
import com.hummerrisk.dto.SbomDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author harris
 */
public interface ExtSbomMapper {

    List<SbomDTO> sbomList(@Param("request") SbomRequest request);

    List<SbomVersion> sbomVersionList(@Param("request") SbomVersionRequest request);
}
