package com.hummer.common.core.domain.request.task;

import com.hummer.common.core.domain.CloudNativeConfig;


public class ConfigVo extends CloudNativeConfig {

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
