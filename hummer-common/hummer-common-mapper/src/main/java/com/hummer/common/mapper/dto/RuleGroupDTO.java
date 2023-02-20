package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.RuleGroup;

/**
 * @author harris
 */
public class RuleGroupDTO extends RuleGroup {

    private String pluginName;

    private String pluginIcon;

    private Integer ruleSum;

    private Integer riskRuleSum;

    private String status;

    private Long createTime;

    private String state;

    private Integer riskyRegulation;

    private Integer totalRegulation;

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

    public Integer getRuleSum() {
        return ruleSum;
    }

    public void setRuleSum(Integer ruleSum) {
        this.ruleSum = ruleSum;
    }

    public Integer getRiskRuleSum() {
        return riskRuleSum;
    }

    public void setRiskRuleSum(Integer riskRuleSum) {
        this.riskRuleSum = riskRuleSum;
    }

    public Integer getRiskyRegulation() {
        return riskyRegulation;
    }

    public void setRiskyRegulation(Integer riskyRegulation) {
        this.riskyRegulation = riskyRegulation;
    }

    public Integer getTotalRegulation() {
        return totalRegulation;
    }

    public void setTotalRegulation(Integer totalRegulation) {
        this.totalRegulation = totalRegulation;
    }
}
