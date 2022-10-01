package com.hummerrisk.dto;

import java.util.List;
import java.util.Map;

public class K8sTopologyDTO {

    List<Map<String, Object>> k8sTopology;

    List<Map<String, Object>> edgesTopology;

    public List<Map<String, Object>> getK8sTopology() {
        return k8sTopology;
    }

    public void setK8sTopology(List<Map<String, Object>> k8sTopology) {
        this.k8sTopology = k8sTopology;
    }

    public List<Map<String, Object>> getEdgesTopology() {
        return edgesTopology;
    }

    public void setEdgesTopology(List<Map<String, Object>> edgesTopology) {
        this.edgesTopology = edgesTopology;
    }
}
