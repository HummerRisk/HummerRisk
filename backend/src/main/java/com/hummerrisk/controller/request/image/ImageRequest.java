package com.hummerrisk.controller.request.image;

import com.hummerrisk.base.domain.Image;

import java.util.Map;

/**
 * harris
 */
public class ImageRequest extends Image {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
