package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.core.domain.CloudEventSyncLog;
import com.hummer.common.core.domain.request.cloudEvent.CloudEventRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudEventSyncLogMapper {
    List<CloudEventSyncLog> getCloudEventSyncLog(@Param("request") CloudEventRequest request);

    int insertSelective(CloudEventSyncLog record);
}
