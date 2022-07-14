package com.hummerrisk.dto;

import com.hummerrisk.base.domain.Task;
import com.hummerrisk.base.domain.TaskItem;

import java.util.List;


public class TaskDTO extends Task {

    private List<TaskItem> taskItemList;

    public List<TaskItem> getTaskItemList() {
        return taskItemList;
    }

    public void setTaskItemList(List<TaskItem> taskItemList) {
        this.taskItemList = taskItemList;
    }
}
