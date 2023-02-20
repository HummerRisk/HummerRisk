package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.WorkspaceDTO;
import com.hummer.common.mapper.domain.request.WorkspaceRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtWorkspaceMapper {

    List<WorkspaceDTO> getWorkspaceWithOrg(@Param("request") WorkspaceRequest request);
}
