package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.config.ConfigRequest;
import com.hummerrisk.dto.CloudNativeConfigDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudNativeConfigMapper {

    List<CloudNativeConfigDTO> getCloudNativeConfigList(@Param("request") ConfigRequest request);

}
