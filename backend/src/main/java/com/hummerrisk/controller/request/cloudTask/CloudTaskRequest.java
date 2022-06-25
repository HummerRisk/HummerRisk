package com.hummerrisk.controller.request.cloudTask;



import com.hummerrisk.base.domain.CloudTask;
import com.hummerrisk.base.domain.CloudTaskItemWithBLOBs;

import java.util.List;

public class CloudTaskRequest {
    private CloudTask cloudTask;
    private String taskId;
    private String remarks;
    private List<CloudTaskItemWithBLOBs> taskItemWithBLOBsList;

    public CloudTask getTask() {
        return cloudTask;
    }

    public void setTask(CloudTask cloudTask) {
        this.cloudTask = cloudTask;
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

    public List<CloudTaskItemWithBLOBs> getTaskItems() {
        return taskItemWithBLOBsList;
    }

    public void setTaskItems(List<CloudTaskItemWithBLOBs> taskItemWithBLOBsList) {
        this.taskItemWithBLOBsList = taskItemWithBLOBsList;
    }
}
