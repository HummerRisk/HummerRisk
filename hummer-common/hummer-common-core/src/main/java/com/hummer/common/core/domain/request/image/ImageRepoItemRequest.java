package com.hummer.common.core.domain.request.image;

import com.hummerrisk.base.domain.ImageRepoItem;

import java.util.Map;

/**
 * harris
 */
public class ImageRepoItemRequest extends ImageRepoItem {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
