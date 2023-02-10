package com.hummer.common.core.dto;

import com.hummerrisk.base.domain.AccountWithBLOBs;
import com.hummerrisk.controller.request.k8s.rbac.Links;
import com.hummerrisk.controller.request.k8s.rbac.Nodes;

import java.util.List;


public class RbacDTO extends AccountWithBLOBs {

    private List<Nodes> nodes;

    private List<Links> links;

    public List<Nodes> getNodes() {
        return nodes;
    }

    public void setNodes(List<Nodes> nodes) {
        this.nodes = nodes;
    }

    public List<Links> getLinks() {
        return links;
    }

    public void setLinks(List<Links> links) {
        this.links = links;
    }
}
