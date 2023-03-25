package com.hummer.cloud.mapper.ext;

import com.hummer.common.core.domain.CloudEventSyncLog;
import com.hummer.common.core.domain.request.cloudEvent.CloudEventRequest;
import com.hummer.common.core.domain.request.event.CloudEventSyncLogVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudEventSyncLogMapper {
    List<CloudEventSyncLogVo> getCloudEventSyncLog(@Param("request") CloudEventRequest request);

    int insertSelective(CloudEventSyncLog record);
}
