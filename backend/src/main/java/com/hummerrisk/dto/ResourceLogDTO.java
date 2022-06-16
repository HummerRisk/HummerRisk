package com.hummerrisk.dto;


import com.hummerrisk.base.domain.Resource;

public class ResourceLogDTO extends TaskItemLogDTO{

    private Resource resource;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
