package com.hummer.common.core.dto;

import com.hummer.common.core.domain.ResourceItem;
import com.hummer.common.core.domain.ResourceWithBLOBs;

/**
 * @author harris
 */
public class ResourceDetailDTO extends ResourceItem {

    private String customData;
    private CloudTaskDTO cloudTaskDTO;
    private ResourceWithBLOBs resourceWithBLOBs;

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }


    public CloudTaskDTO getCloudTaskDTO() {
        return cloudTaskDTO;
    }

    public void setCloudTaskDTO(CloudTaskDTO cloudTaskDTO) {
        this.cloudTaskDTO = cloudTaskDTO;
    }

    public ResourceWithBLOBs getResourceWithBLOBs() {
        return resourceWithBLOBs;
    }

    public void setResourceWithBLOBs(ResourceWithBLOBs resourceWithBLOBs) {
        this.resourceWithBLOBs = resourceWithBLOBs;
    }
}
