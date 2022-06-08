package io.hummerrisk.base.mapper.ext;

import io.hummerrisk.controller.request.WorkspaceRequest;
import io.hummerrisk.dto.WorkspaceDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtWorkspaceMapper {

    List<WorkspaceDTO> getWorkspaceWithOrg(@Param("request") WorkspaceRequest request);
}
