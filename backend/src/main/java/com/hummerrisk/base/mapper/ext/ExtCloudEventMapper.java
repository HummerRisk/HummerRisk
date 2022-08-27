package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudEvent;

import java.util.List;

public interface ExtCloudEventMapper {
    /**
     * 批量插入云事件
     * @param cloudEvents
     * @return
     */
    int batchCloudEvents(List<CloudEvent> cloudEvents);
}
