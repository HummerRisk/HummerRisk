package com.hummerrisk.controller.request.k8s;

import java.util.List;

/**
 * harris
 */
public class NodeTopology {

    private List<K8sResource> children;

    private String name;

    public List<K8sResource> getChildren() {
        return children;
    }

    public void setChildren(List<K8sResource> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
