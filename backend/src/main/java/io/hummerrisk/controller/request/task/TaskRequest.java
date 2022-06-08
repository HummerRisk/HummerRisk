package io.hummerrisk.controller.request.task;



import io.hummerrisk.base.domain.Task;
import io.hummerrisk.base.domain.TaskItemWithBLOBs;

import java.util.List;

public class TaskRequest {
    private Task task;
    private String taskId;
    private String remarks;
    private List<TaskItemWithBLOBs> taskItemWithBLOBsList;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<TaskItemWithBLOBs> getTaskItems() {
        return taskItemWithBLOBsList;
    }

    public void setTaskItems(List<TaskItemWithBLOBs> taskItemWithBLOBsList) {
        this.taskItemWithBLOBsList = taskItemWithBLOBsList;
    }
}
