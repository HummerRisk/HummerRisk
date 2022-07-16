package com.hummerrisk.controller.request.task;

import com.hummerrisk.base.domain.TaskItemLog;
import com.hummerrisk.base.domain.TaskItemResource;

import java.util.List;


public class TaskLogVo extends TaskItemResource {

    private List<TaskItemLog> taskItemLogList;

    private TaskResourceVo taskResourceVo;

    public List<TaskItemLog> getTaskItemLogList() {
        return taskItemLogList;
    }

    public void setTaskItemLogList(List<TaskItemLog> taskItemLogList) {
        this.taskItemLogList = taskItemLogList;
    }

    public TaskResourceVo getTaskResourceVo() {
        return taskResourceVo;
    }

    public void setTaskResourceVo(TaskResourceVo taskResourceVo) {
        this.taskResourceVo = taskResourceVo;
    }
}
