package com.hummerrisk.controller.request.rule;


import com.hummerrisk.base.domain.RuleGroup;

import java.util.Map;

/**
 * @author harris
 */
public class RuleGroupRequest extends RuleGroup {

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
