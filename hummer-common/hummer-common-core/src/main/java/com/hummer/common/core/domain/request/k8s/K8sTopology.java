package com.hummer.common.core.domain.request.k8s;

import java.util.List;

/**
 * harris
 */
public class K8sTopology {

    private List<K8s> children;

    private String name;

    public List<K8s> getChildren() {
        return children;
    }

    public void setChildren(List<K8s> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
