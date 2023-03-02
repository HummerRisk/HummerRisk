package com.hummer.system.mapper.ext;

import com.hummer.common.core.dto.WorkspaceDTO;
import com.hummer.common.core.domain.request.WorkspaceRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtWorkspaceMapper {

    List<WorkspaceDTO> getWorkspaceWithOrg(@Param("request") WorkspaceRequest request);
}
