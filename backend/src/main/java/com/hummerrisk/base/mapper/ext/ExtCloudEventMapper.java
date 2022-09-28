package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudEvent;
import com.hummerrisk.controller.request.cloudEvent.CloudEventRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudEventMapper {

    List<CloudEvent> getCloudEventList(@Param("request") CloudEventRequest request);

    /**
     * 批量插入云事件
     * @param cloudEvents
     * @return
     */
    int batchCloudEvents(List<CloudEvent> cloudEvents);
}
