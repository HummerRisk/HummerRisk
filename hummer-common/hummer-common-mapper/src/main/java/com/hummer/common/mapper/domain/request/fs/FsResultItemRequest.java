package com.hummer.common.mapper.domain.request.fs;


import com.hummer.common.mapper.domain.FileSystemResultItem;

import java.util.Map;

/**
 * @author harris
 */
public class FsResultItemRequest extends FileSystemResultItem {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
