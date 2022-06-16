package com.hummerrisk.dto;

import com.hummerrisk.base.domain.CloudAccountQuartzTaskRelation;

import java.util.List;

public class ShowAccountQuartzTaskRelationDto extends CloudAccountQuartzTaskRelation {

    private List<TaskDTO> taskList;

    public List<TaskDTO> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskDTO> taskList) {
        this.taskList = taskList;
    }
}
