package com.hummer.common.core.dto;

import com.hummer.common.core.domain.Task;
import com.hummer.common.core.domain.TaskItem;

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
