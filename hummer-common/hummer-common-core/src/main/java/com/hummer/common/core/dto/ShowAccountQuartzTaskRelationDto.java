package com.hummer.common.core.dto;

import com.hummerrisk.base.domain.CloudAccountQuartzTaskRelation;

import java.util.List;

public class ShowAccountQuartzTaskRelationDto extends CloudAccountQuartzTaskRelation {

    private List<CloudTaskDTO> cloudTaskDTOList;

    public List<CloudTaskDTO> getCloudTaskDTOList() {
        return cloudTaskDTOList;
    }

    public void setCloudTaskDTOList(List<CloudTaskDTO> cloudTaskDTOList) {
        this.cloudTaskDTOList = cloudTaskDTOList;
    }
}
