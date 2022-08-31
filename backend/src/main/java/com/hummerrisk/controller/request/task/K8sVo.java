package com.hummerrisk.controller.request.task;

import com.hummerrisk.base.domain.CloudNative;

public class K8sVo extends CloudNative {

    private Boolean favour;

    private String type;

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
}
