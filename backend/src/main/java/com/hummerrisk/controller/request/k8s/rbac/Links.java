package com.hummerrisk.controller.request.k8s.rbac;

/**
 * harris
 */
public class Links {

    private String source;

    private String target;

    private Relation relation;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }
}
