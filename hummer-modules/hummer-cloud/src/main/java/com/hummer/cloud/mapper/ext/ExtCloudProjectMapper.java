package com.hummer.cloud.mapper.ext;

import com.hummer.common.core.domain.CloudProject;
import com.hummer.common.core.dto.CloudProjectDTO;
import com.hummer.common.core.dto.QuartzTaskDTO;
import com.hummer.common.core.dto.RuleDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ExtCloudProjectMapper {

    List<CloudProjectDTO> getCloudProjectDTOs(@Param("request") CloudProject request);

    CloudProjectDTO projectById(String projectId);

    List<Map<String, Object>> groupList(Map<String, Object> params);


}
