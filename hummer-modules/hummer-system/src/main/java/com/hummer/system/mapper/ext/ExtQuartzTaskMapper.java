package com.hummer.system.mapper.ext;

import com.hummer.common.core.domain.CloudAccountQuartzTask;
import com.hummer.common.core.domain.request.cloudTask.CloudQuartzRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExtQuartzTaskMapper {

    List<CloudAccountQuartzTask> selectQuartzTasks(@Param("request") CloudQuartzRequest request);

}
