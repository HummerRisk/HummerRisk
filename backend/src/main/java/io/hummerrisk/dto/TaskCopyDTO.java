package io.hummerrisk.dto;


import io.hummerrisk.base.domain.Rule;
import io.hummerrisk.base.domain.TaskItem;
import io.hummerrisk.commons.utils.SelectTag;

import java.util.LinkedList;
import java.util.List;

/**
 * @author harris
 */
public class TaskCopyDTO {
    private List<TaskItem> taskItemList;
    private Rule rule;
    private List<String> ruleTagMappingList;
    private List<SelectTag> SelectTags = new LinkedList<>();

    public List<TaskItem> getTaskItemList() {
        return taskItemList;
    }

    public void setTaskItemList(List<TaskItem> taskItemList) {
        this.taskItemList = taskItemList;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public List<SelectTag> getSelectTags() {
        return SelectTags;
    }

    public void setSelectTags(List<SelectTag> selectTags) {
        SelectTags = selectTags;
    }

    public List<String> getRuleTagMappingList() {
        return ruleTagMappingList;
    }

    public void setRuleTagMappingList(List<String> ruleTagMappingList) {
        this.ruleTagMappingList = ruleTagMappingList;
    }
}
