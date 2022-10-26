package com.hummerrisk.controller.request.sync;

import java.util.List;

/**
 * harris
 */
public class CloudTopology {

    private List<Cloud> children;

    private String name;

    public List<Cloud> getChildren() {
        return children;
    }

    public void setChildren(List<Cloud> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
