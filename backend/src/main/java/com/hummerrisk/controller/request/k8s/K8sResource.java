package com.hummerrisk.controller.request.k8s;

import java.util.List;

/**
 * harris
 */
public class K8sResource {

    private List<Node> children;

    private String name;

    private String k8sId;

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getK8sId() {
        return k8sId;
    }

    public void setK8sId(String k8sId) {
        this.k8sId = k8sId;
    }
}
