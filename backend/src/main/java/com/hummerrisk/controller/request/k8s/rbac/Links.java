package com.hummerrisk.controller.request.k8s.rbac;

import java.util.List;

/**
 * harris
 */
public class Links {

    private String id;

    private String name;

    private List<Relation> relation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Relation> getRelation() {
        return relation;
    }

    public void setRelation(List<Relation> relation) {
        this.relation = relation;
    }
}
