package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.WorkspaceRequest;
import com.hummerrisk.dto.WorkspaceDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtWorkspaceMapper {

    List<WorkspaceDTO> getWorkspaceWithOrg(@Param("request") WorkspaceRequest request);
}
