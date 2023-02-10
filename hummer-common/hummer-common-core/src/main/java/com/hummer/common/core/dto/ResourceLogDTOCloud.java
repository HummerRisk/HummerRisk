package com.hummer.common.core.dto;


import com.hummer.common.core.domain.Resource;

public class ResourceLogDTOCloud extends CloudTaskItemLogDTO {

    private Resource resource;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
