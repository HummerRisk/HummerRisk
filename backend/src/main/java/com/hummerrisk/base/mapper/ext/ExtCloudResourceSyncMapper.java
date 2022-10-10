package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudResourceSync;
import com.hummerrisk.controller.request.cloudResource.CloudResourceSyncRequest;
import com.hummerrisk.controller.request.sync.D3;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudResourceSyncMapper {

    List<CloudResourceSync> selectByRequest(@Param("request")CloudResourceSyncRequest cloudResourceSyncRequest);

    D3 d3();

}
