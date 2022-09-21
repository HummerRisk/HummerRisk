package com.hummerrisk.controller.request.resource;

import com.hummerrisk.base.domain.Resource;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * @author harris
 */
public class ResourceRequest extends Resource {

    private String taskId;
    private String tagKey;
    private String severity;
    private String pluginName;
    private String taskName;
    private String ruleId;
    private String ruleName;
    private String project;
    private String itemSortFirstLevel;
    private String itemSortSecondLevel;
    private String name;
    private String groupId;

    @ApiModelProperty(hidden = true)
    private String sort;

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getItemSortFirstLevel() {
        return itemSortFirstLevel;
    }

    public void setItemSortFirstLevel(String itemSortFirstLevel) {
        this.itemSortFirstLevel = itemSortFirstLevel;
    }

    public String getItemSortSecondLevel() {
        return itemSortSecondLevel;
    }

    public void setItemSortSecondLevel(String itemSortSecondLevel) {
        this.itemSortSecondLevel = itemSortSecondLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
