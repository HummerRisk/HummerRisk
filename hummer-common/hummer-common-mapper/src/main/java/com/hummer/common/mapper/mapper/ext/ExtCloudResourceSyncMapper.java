package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.CloudResourceSync;
import com.hummer.common.mapper.domain.request.cloudResource.CloudResourceSyncRequest;
import com.hummer.common.mapper.domain.request.sync.CloudTopology;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudResourceSyncMapper {

    List<CloudResourceSync> selectByRequest(@Param("request") CloudResourceSyncRequest request);

    CloudTopology cloudTopology();

}
