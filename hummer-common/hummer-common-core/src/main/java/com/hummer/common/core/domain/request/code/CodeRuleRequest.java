package com.hummer.common.core.domain.request.code;


import com.hummer.common.core.domain.CodeRule;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public class CodeRuleRequest extends CodeRule {

    private List<String> tags;

    private List<String> groups;

    private String tagKey;

    private String ruleGroupId;

    private Map<String, Object> combine;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(String ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }
}
