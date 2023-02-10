package com.hummer.common.core.dto;


import com.hummer.common.core.domain.CloudAccountQuartzTask;

import java.util.List;

public class ShowAccountQuartzTaskDTO extends CloudAccountQuartzTask {

   private List<ShowAccountQuartzTaskRelationDto> quartzTaskRelationDtos;

    public List<ShowAccountQuartzTaskRelationDto> getQuartzTaskRelationDtos() {
        return quartzTaskRelationDtos;
    }

    public void setQuartzTaskRelationDtos(List<ShowAccountQuartzTaskRelationDto> quartzTaskRelationDtos) {
        this.quartzTaskRelationDtos = quartzTaskRelationDtos;
    }
}
