package com.hummerrisk.controller.request.task;

import com.hummerrisk.base.domain.Image;


public class ImageVo extends Image {

    private Boolean favour;

    private String type;

    public Boolean getFavour() {
        return favour;
    }

    public void setFavour(Boolean favour) {
        this.favour = favour;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}
