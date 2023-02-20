package com.hummer.common.mapper.domain.request.k8s;

import java.util.List;

/**
 * harris
 */
public class NameSpace {

    private List<Resource> children;

    private String name;

    private String k8sId;

    private String namespaceId;

    private String riskType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Resource> getChildren() {
        return children;
    }

    public void setChildren(List<Resource> children) {
        this.children = children;
    }

    public String getK8sId() {
        return k8sId;
    }

    public void setK8sId(String k8sId) {
        this.k8sId = k8sId;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }
}
