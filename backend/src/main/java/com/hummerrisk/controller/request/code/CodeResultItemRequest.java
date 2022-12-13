package com.hummerrisk.controller.request.code;


import com.hummerrisk.base.domain.CodeResultItem;

import java.util.Map;

/**
 * @author harris
 */
public class CodeResultItemRequest extends CodeResultItem {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
