package com.hummerrisk.dto;

import com.hummerrisk.base.domain.RuleGroup;

/**
 * @author harris
 */
public class RuleGroupDTO extends RuleGroup {

    private String pluginName;

    private String pluginIcon;

    private String ruleSum;

    private String status;

    private Long createTime;

    private String state;

    private String riskyRegulation;

    private String totalRegulation;

    private Integer returnSum;

    private Integer resourcesSum;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRiskyRegulation() {
        return riskyRegulation;
    }

    public void setRiskyRegulation(String riskyRegulation) {
        this.riskyRegulation = riskyRegulation;
    }

    public String getTotalRegulation() {
        return totalRegulation;
    }

    public void setTotalRegulation(String totalRegulation) {
        this.totalRegulation = totalRegulation;
    }

    public Integer getReturnSum() {
        return returnSum;
    }

    public void setReturnSum(Integer returnSum) {
        this.returnSum = returnSum;
    }

    public Integer getResourcesSum() {
        return resourcesSum;
    }

    public void setResourcesSum(Integer resourcesSum) {
        this.resourcesSum = resourcesSum;
    }
}
