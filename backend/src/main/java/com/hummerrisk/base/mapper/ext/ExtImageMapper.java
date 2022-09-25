package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.controller.request.image.ImageRequest;
import com.hummerrisk.dto.ImageDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public interface ExtImageMapper {

    List<ImageDTO> imageList(@Param("request") ImageRequest request);

    Map<String, Object> topInfo(Map<String, Object> params);

    List<Map<String, Object>> imageRepoChart();

    List<Map<String, Object>> severityChart();
}
