package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudAccountQuartzTask;
import com.hummerrisk.controller.request.cloudTask.CloudQuartzRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExtQuartzTaskMapper {

    List<CloudAccountQuartzTask> selectQuartzTasks(@Param("request") CloudQuartzRequest request);

}
