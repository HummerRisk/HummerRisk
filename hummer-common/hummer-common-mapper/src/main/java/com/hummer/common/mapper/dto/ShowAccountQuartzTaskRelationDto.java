package com.hummer.common.mapper.dto;

import com.hummer.common.mapper.domain.CloudAccountQuartzTaskRelation;

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
