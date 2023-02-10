package com.hummer.common.core.domain.request.image;

import com.hummerrisk.base.domain.ImageRepo;

import java.util.Map;

/**
 * harris
 */
public class ImageRepoRequest extends ImageRepo {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
