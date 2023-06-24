package com.hummer.cloud.mapper.ext;

import com.hummer.common.core.domain.CloudResourceSync;
import com.hummer.common.core.domain.request.cloudResource.CloudResourceSyncRequest;
import com.hummer.common.core.domain.request.sync.CloudTopology;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudResourceSyncMapper {

    List<CloudResourceSync> selectByRequest(@Param("request") CloudResourceSyncRequest request);

    CloudTopology cloudTopology(@Param("accountId")  String accountId);

}
