package com.hummer.common.core.domain.request.code;


import com.hummer.common.core.domain.CodeResult;

import java.util.Map;

/**
 * @author harris
 */
public class CodeResultRequest extends CodeResult {

    private String name;

    private Map<String, Object> combine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
