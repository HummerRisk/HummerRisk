package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudEventSyncLog;
import com.hummerrisk.controller.request.cloudEvent.CloudEventRequest;
import com.hummerrisk.controller.request.event.CloudEventSyncLogVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudEventSyncLogMapper {
    List<CloudEventSyncLogVo> getCloudEventSyncLog(@Param("request")CloudEventRequest request);

    int insertSelective(CloudEventSyncLog record);
}
