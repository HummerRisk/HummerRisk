package com.hummerrisk.controller.request.sync;

import java.util.List;

/**
 * harris
 */
public class ResourceType {

    private List<Resource> children;

    private String name;

    private String resourceType;

    public List<Resource> getChildren() {
        return children;
    }

    public void setChildren(List<Resource> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
