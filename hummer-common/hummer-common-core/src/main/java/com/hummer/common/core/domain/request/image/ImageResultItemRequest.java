package com.hummer.common.core.domain.request.image;


import com.hummerrisk.base.domain.ImageResultItem;

import java.util.Map;

/**
 * @author harris
 */
public class ImageResultItemRequest extends ImageResultItem {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
