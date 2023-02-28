package com.hummer.common.core.dto;


import com.hummer.common.core.domain.CloudNativeRule;

import java.util.ArrayList;
import java.util.List;

/**
 * @author harris
 */
public class K8sRuleDTO extends CloudNativeRule {

    private String tagKey;

    private String tagName;

    private boolean isSaveParam;

    private List<String> tags = new ArrayList<>();

    private String pluginIcon;

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isSaveParam() {
        return isSaveParam;
    }

    public void setSaveParam(boolean saveParam) {
        isSaveParam = saveParam;
    }

    public String getPluginIcon() {
        return pluginIcon;
    }

    public void setPluginIcon(String pluginIcon) {
        this.pluginIcon = pluginIcon;
    }
}
