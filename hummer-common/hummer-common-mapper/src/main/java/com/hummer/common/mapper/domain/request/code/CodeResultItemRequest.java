package com.hummer.common.mapper.domain.request.code;


import com.hummer.common.mapper.domain.CodeResultItem;

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
