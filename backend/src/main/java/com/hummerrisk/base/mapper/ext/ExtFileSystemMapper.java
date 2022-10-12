package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.controller.request.fs.FsRequest;
import com.hummerrisk.dto.FsDTO;
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
