package io.hummerrisk.controller.request.rule;


import io.hummerrisk.base.domain.Rule;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public class CreateRuleRequest extends Rule {

    private List<String> tags;

    private List<String> ruleSets;

    private List<String> inspectionSeports;

    private List<String> types;

    private String tagKey;

    private String resourceType;

    private String type;

    private Map<String, Object> combine;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getRuleSets() {
        return ruleSets;
    }

    public void setRuleSets(List<String> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public List<String> getInspectionSeports() {
        return inspectionSeports;
    }

    public void setInspectionSeports(List<String> inspectionSeports) {
        this.inspectionSeports = inspectionSeports;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
