package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudEventWithBLOBs;
import com.hummerrisk.controller.request.cloudEvent.CloudEventRequest;
import com.hummerrisk.controller.request.event.CloudEventWithBLOBsVo;
import com.hummerrisk.dto.CloudEventGroupDTO;
import com.hummerrisk.dto.CloudEventSourceIpInsightDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudEventMapper {

    List<CloudEventWithBLOBsVo> getCloudEventList(@Param("request") CloudEventRequest request);

    List<CloudEventGroupDTO> selectEventGroup(@Param("request") CloudEventRequest request);
    /**
     * 批量插入云事件
     *
     * @param cloudEvents
     * @return
     */
    int batchCloudEvents(List<CloudEventWithBLOBs> cloudEvents);

    Map<String, Object> topInfo(Map<String, Object> params);

    List<Map<String, Object>> cloudChart();

    List<Map<String, Object>> regionChart();

    List<Map<String, Object>> severityChart();
    List<CloudEventSourceIpInsightDto> selectSourceIpInsight(@Param("request") CloudEventRequest request);
    List<Map<String,Object>> selectIpAccessTimesGroupByDate(@Param("ip")String ip,@Param("startDate")String startDate,@Param("endDate")String endDate);
}
