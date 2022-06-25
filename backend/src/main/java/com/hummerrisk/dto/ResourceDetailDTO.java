package com.hummerrisk.dto;

import com.hummerrisk.base.domain.ResourceItem;
import com.hummerrisk.base.domain.ResourceWithBLOBs;

/**
 * @author harris
 */
public class ResourceDetailDTO extends ResourceItem {

    private String customData;
    private TaskDTO taskDTO;
    private ResourceWithBLOBs resourceWithBLOBs;

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public TaskDTO getTaskDTO() {
        return taskDTO;
    }

    public void setTaskDTO(TaskDTO taskDTO) {
        this.taskDTO = taskDTO;
    }

    public ResourceWithBLOBs getResourceWithBLOBs() {
        return resourceWithBLOBs;
    }

    public void setResourceWithBLOBs(ResourceWithBLOBs resourceWithBLOBs) {
        this.resourceWithBLOBs = resourceWithBLOBs;
    }
}
