package com.hummerrisk.controller.request.k8s.rbac;

import com.hummerrisk.controller.request.k8s.ResourceType;

import java.util.List;

/**
 * harris
 */
public class Nodes {

    private String id;

    private String name;

    private String value;

    private String symbolSize;

    private String category;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSymbolSize() {
        return symbolSize;
    }

    public void setSymbolSize(String symbolSize) {
        this.symbolSize = symbolSize;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
