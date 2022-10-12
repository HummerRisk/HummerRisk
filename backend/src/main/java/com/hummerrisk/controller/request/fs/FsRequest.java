package com.hummerrisk.controller.request.fs;

import com.hummerrisk.base.domain.FileSystem;

import java.util.Map;

/**
 * harris
 */
public class FsRequest extends FileSystem {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
