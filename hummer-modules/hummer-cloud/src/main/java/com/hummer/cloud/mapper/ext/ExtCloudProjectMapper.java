package com.hummer.cloud.mapper.ext;

import com.hummer.common.core.domain.CloudGroup;
import com.hummer.common.core.domain.CloudProcess;
import com.hummer.common.core.domain.CloudProject;
import com.hummer.common.core.domain.request.project.CloudGroupRequest;
import com.hummer.common.core.dto.CloudGroupDTO;
import com.hummer.common.core.dto.CloudProcessDTO;
import com.hummer.common.core.dto.CloudProjectDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudProjectMapper {

    List<CloudProjectDTO> getCloudProjectDTOs(@Param("request") CloudGroupRequest request);

    List<CloudGroupDTO> getCloudGroupDTOs(@Param("request") CloudGroupRequest request);

    CloudProcessDTO getCloudProcessDTO(@Param("request") CloudProcess request);

}
