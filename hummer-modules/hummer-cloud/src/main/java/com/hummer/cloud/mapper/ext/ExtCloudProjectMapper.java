package com.hummer.cloud.mapper.ext;

import com.hummer.common.core.domain.CloudProject;
import com.hummer.common.core.dto.CloudProjectDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudProjectMapper {

    List<CloudProjectDTO> getCloudProjectDTOs(@Param("request") CloudProject request);

}
