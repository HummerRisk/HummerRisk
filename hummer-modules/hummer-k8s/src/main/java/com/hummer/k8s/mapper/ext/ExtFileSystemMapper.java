package com.hummer.k8s.mapper.ext;


import com.hummer.common.core.dto.FsDTO;
import com.hummer.common.core.domain.request.fs.FsRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public interface ExtFileSystemMapper {

    List<FsDTO> fsList(@Param("request") FsRequest request);

    Map<String, Object> topInfo(Map<String, Object> params);

    List<Map<String, Object>> projectChart();

    List<Map<String, Object>> severityChart();
}
