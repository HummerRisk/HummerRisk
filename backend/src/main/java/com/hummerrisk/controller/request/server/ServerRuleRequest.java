package com.hummerrisk.controller.request.server;


import com.hummerrisk.base.domain.ServerRule;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public class ServerRuleRequest extends ServerRule {

    private List<String> tags;

    private List<String> groups;

    private String tagKey;

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
}
