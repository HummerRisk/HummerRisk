package com.hummer.common.mapper.domain.request.cloudTask;



import com.hummer.common.mapper.domain.CloudTask;
import com.hummer.common.mapper.domain.CloudTaskItemWithBLOBs;

import java.util.List;

public class CloudTaskRequest {
    private CloudTask cloudTask;
    private String taskId;
    private String remarks;
    private List<CloudTaskItemWithBLOBs> cloudTaskItemWithBLOBsList;


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

    public CloudTask getCloudTask() {
        return cloudTask;
    }

    public void setCloudTask(CloudTask cloudTask) {
        this.cloudTask = cloudTask;
    }

    public List<CloudTaskItemWithBLOBs> getCloudTaskItemWithBLOBsList() {
        return cloudTaskItemWithBLOBsList;
    }

    public void setCloudTaskItemWithBLOBsList(List<CloudTaskItemWithBLOBs> cloudTaskItemWithBLOBsList) {
        this.cloudTaskItemWithBLOBsList = cloudTaskItemWithBLOBsList;
    }
}
