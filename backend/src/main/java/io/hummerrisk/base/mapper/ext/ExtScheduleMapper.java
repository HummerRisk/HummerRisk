package io.hummerrisk.base.mapper.ext;

import io.hummerrisk.controller.request.QueryScheduleRequest;
import io.hummerrisk.dto.ScheduleDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtScheduleMapper {
    List<ScheduleDao> list(@Param("request") QueryScheduleRequest request);
}
