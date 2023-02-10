package com.hummer.common.core.domain.request.rule;


import com.hummerrisk.base.domain.RuleTag;

import java.util.Map;

/**
 * @author harris
 */
public class RuleTagRequest extends RuleTag {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
