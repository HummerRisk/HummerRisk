package com.hummerrisk.dto;

import com.hummerrisk.base.domain.RuleGroup;

/**
 * @author harris
 */
public class RuleGroupDTO extends RuleGroup {

    private String pluginName;

    private String pluginIcon;

    private String ruleSum;



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

    public String getRuleSum() {
        return ruleSum;
    }

    public void setRuleSum(String ruleSum) {
        this.ruleSum = ruleSum;
    }
}
