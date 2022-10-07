package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudEvent;
import com.hummerrisk.controller.request.cloudEvent.CloudEventRequest;
import com.hummerrisk.dto.CloudEventGroupDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudEventMapper {

    List<CloudEvent> getCloudEventList(@Param("request") CloudEventRequest request);

    List<CloudEventGroupDTO> selectEventGroup(@Param("request") CloudEventRequest request);
    /**
     * 批量插入云事件
     *
     * @param cloudEvents
     * @return
     */
    int batchCloudEvents(List<CloudEvent> cloudEvents);

    Map<String, Object> topInfo(Map<String, Object> params);
}
