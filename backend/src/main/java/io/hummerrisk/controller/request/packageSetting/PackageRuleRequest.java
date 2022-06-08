package io.hummerrisk.controller.request.packageSetting;


import io.hummerrisk.base.domain.PackageRule;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
public class PackageRuleRequest extends PackageRule {

    private List<String> tags;

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
}
