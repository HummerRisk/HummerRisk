package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudNativeConfigResult;
import com.hummerrisk.controller.request.config.ConfigResultRequest;

import java.util.List;

public interface ExtCloudNativeConfigResultMapper {

    List<CloudNativeConfigResult> resultList(ConfigResultRequest request);

}
