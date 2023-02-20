package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.ScheduleDao;
import com.hummer.common.mapper.domain.request.QueryScheduleRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtScheduleMapper {
    List<ScheduleDao> list(@Param("request") QueryScheduleRequest request);
}
