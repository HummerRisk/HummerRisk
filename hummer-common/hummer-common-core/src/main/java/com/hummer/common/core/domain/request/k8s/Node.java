package com.hummer.common.core.domain.request.k8s;

import java.util.List;

/**
 * harris
 */
public class Node {

    private List<Pod> children;

    private String name;

    private String k8sId;

    private String nodeId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pod> getChildren() {
        return children;
    }

    public void setChildren(List<Pod> children) {
        this.children = children;
    }

    public String getK8sId() {
        return k8sId;
    }

    public void setK8sId(String k8sId) {
        this.k8sId = k8sId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
