package com.hummer.common.core.dto;

import com.hummer.common.core.domain.request.task.ProjectVo;

import java.util.List;

public class ProjectTreeDTO {

    private List<ProjectVo> projectVoList;

    public List<ProjectVo> getProjectVoList() {
        return projectVoList;
    }

    public void setProjectVoList(List<ProjectVo> projectVoList) {
        this.projectVoList = projectVoList;
    }
}
