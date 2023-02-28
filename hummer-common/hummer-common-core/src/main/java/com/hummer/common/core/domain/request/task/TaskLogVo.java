package com.hummer.common.core.domain.request.task;

import com.hummer.common.core.domain.TaskItemResource;
import com.hummer.common.core.domain.TaskItemResourceLog;

import java.util.List;


public class TaskLogVo extends TaskItemResource {

    private List<TaskItemResourceLog> taskItemResourceLogList;

    private TaskResourceVo taskResourceVo;

    public List<TaskItemResourceLog> getTaskItemResourceLogList() {
        return taskItemResourceLogList;
    }

    public void setTaskItemResourceLogList(List<TaskItemResourceLog> taskItemResourceLogList) {
        this.taskItemResourceLogList = taskItemResourceLogList;
    }

    public TaskResourceVo getTaskResourceVo() {
        return taskResourceVo;
    }

    public void setTaskResourceVo(TaskResourceVo taskResourceVo) {
        this.taskResourceVo = taskResourceVo;
    }
}
