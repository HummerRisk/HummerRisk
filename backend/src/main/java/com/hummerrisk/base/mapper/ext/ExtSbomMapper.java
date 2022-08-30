package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.base.domain.SbomVersion;
import com.hummerrisk.controller.request.sbom.SbomRequest;
import com.hummerrisk.controller.request.sbom.SbomVersionRequest;
import com.hummerrisk.dto.ApplicationDTO;
import com.hummerrisk.dto.SbomDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public interface ExtSbomMapper {

    List<SbomDTO> sbomList(@Param("request") SbomRequest request);

    List<SbomVersion> sbomVersionList(@Param("request") SbomVersionRequest request);

    List<ApplicationDTO> applications(@Param("request") SbomRequest request);

    Map<String, String> codeMetricChart(@Param("resultId") String resultId);

    Map<String, String> imageMetricChart(@Param("resultId") String resultId);

    Map<String, String> packageMetricChart(@Param("resultId") String resultId);
}
