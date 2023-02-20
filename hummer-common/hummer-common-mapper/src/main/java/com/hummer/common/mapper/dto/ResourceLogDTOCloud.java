package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.Resource;

public class ResourceLogDTOCloud extends CloudTaskItemLogDTO {

    private Resource resource;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
