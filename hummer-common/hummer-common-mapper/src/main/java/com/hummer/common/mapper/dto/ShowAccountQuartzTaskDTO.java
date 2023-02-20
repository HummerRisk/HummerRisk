package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.CloudAccountQuartzTask;

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
