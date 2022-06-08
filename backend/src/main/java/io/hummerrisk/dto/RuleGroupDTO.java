package io.hummerrisk.dto;

import io.hummerrisk.base.domain.RuleGroup;

/**
 * @author harris
 */
public class RuleGroupDTO extends RuleGroup {

    private String pluginName;

    private String pluginIcon;

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginIcon() {
        return pluginIcon;
    }

    public void setPluginIcon(String pluginIcon) {
        this.pluginIcon = pluginIcon;
    }
}
