package com.hummer.common.mapper.mapper.ext;


import com.hummer.common.mapper.dto.ApplicationDTO;
import com.hummer.common.mapper.dto.MetricChartDTO;
import com.hummer.common.mapper.dto.SbomDTO;
import com.hummer.common.mapper.domain.SbomVersion;
import com.hummer.common.mapper.domain.request.sbom.SbomRequest;
import com.hummer.common.mapper.domain.request.sbom.SbomVersionRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author harris
 */
public interface ExtSbomMapper {

    List<SbomDTO> sbomList(@Param("request") SbomRequest request);

    List<SbomVersion> sbomVersionList(@Param("request") SbomVersionRequest request);

    List<ApplicationDTO> applications(@Param("request") SbomRequest request);

    MetricChartDTO codeMetricChart(@Param("resultId") String resultId);

    MetricChartDTO imageMetricChart(@Param("resultId") String resultId);

    MetricChartDTO fsMetricChart(@Param("resultId") String resultId);

}
