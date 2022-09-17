package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.config.ConfigResultRequest;
import com.hummerrisk.dto.CloudNativeConfigResultDTO;

import java.util.List;

public interface ExtCloudNativeConfigResultMapper {

    List<CloudNativeConfigResultDTO> resultList(ConfigResultRequest request);

}
