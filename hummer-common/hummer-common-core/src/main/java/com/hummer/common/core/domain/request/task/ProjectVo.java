package com.hummer.common.core.domain.request.task;

import com.hummer.common.core.domain.CloudProject;


public class ProjectVo extends CloudProject {

    private Boolean favour;

    private String type;

    private String name;

    public Boolean getFavour() {
        return favour;
    }

    public void setFavour(Boolean favour) {
        this.favour = favour;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
