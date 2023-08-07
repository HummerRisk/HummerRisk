package com.hummer.system.mapper.ext;

import com.hummer.common.core.domain.MessageOrder;
import com.hummer.common.core.dto.MetricChartDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExtNoticeMapper {

    MetricChartDTO metricChartCloud(@Param("request") MessageOrder request);

}
