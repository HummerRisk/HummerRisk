package com.hummer.common.core.domain.request.image;


import com.hummer.common.core.domain.ImageResult;

import java.util.Map;

/**
 * @author harris
 */
public class ImageResultRequest extends ImageResult {

    private String name;

    private String imageUrl;

    private Map<String, Object> combine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
