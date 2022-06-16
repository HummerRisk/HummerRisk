package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.QueryScheduleRequest;
import com.hummerrisk.dto.ScheduleDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtScheduleMapper {
    List<ScheduleDao> list(@Param("request") QueryScheduleRequest request);
}
