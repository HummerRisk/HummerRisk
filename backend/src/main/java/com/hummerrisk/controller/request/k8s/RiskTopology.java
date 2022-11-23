package com.hummerrisk.controller.request.k8s;

import java.util.List;

/**
 * harris
 */
public class RiskTopology {

    private List<NameSpace> children;

    private String name;

    private String k8sId;

    private String riskType;

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

    public String getK8sId() {
        return k8sId;
    }

    public void setK8sId(String k8sId) {
        this.k8sId = k8sId;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }
}
