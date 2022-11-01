package com.hummerrisk.controller.request.k8s;

import java.util.List;

/**
 * harris
 */
public class NameSpaceTopology {

    private List<K8sNameSpace> children;

    private String name;

    public List<K8sNameSpace> getChildren() {
        return children;
    }

    public void setChildren(List<K8sNameSpace> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
