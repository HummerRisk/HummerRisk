package com.hummerrisk.controller.request.k8s;

/**
 * harris
 */
public class Resource {

    private Integer value;

    private Integer risk;

    private String name;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRisk() {
        return risk;
    }

    public void setRisk(Integer risk) {
        this.risk = risk;
    }
}
