package com.hummer.system.dto;

import com.hummer.common.core.domain.CloudProject;
import com.hummer.common.core.domain.CloudTask;

import java.util.List;

public class ProjectDTO {

    private CloudProject cloudProject;

    private List<CloudTask> cloudTaskList;

    public CloudProject getCloudProject() {
        return cloudProject;
    }

    public void setCloudProject(CloudProject cloudProject) {
        this.cloudProject = cloudProject;
    }

    public List<CloudTask> getCloudTaskList() {
        return cloudTaskList;
    }

    public void setCloudTaskList(List<CloudTask> cloudTaskList) {
        this.cloudTaskList = cloudTaskList;
    }
}
