package com.hummerrisk.controller.request.k8s;

import java.util.List;

/**
 * harris
 */
public class RiskTopology {

    private List<NameSpace> children;

    private String name;

    public List<NameSpace> getChildren() {
        return children;
    }

    public void setChildren(List<NameSpace> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
