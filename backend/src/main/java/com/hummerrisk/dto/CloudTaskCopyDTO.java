package com.hummerrisk.dto;


import com.hummerrisk.base.domain.CloudTaskItem;
import com.hummerrisk.base.domain.Rule;
import com.hummerrisk.base.domain.SelectTag;

import java.util.LinkedList;
import java.util.List;

/**
 * @author harris
 */
public class CloudTaskCopyDTO {
    private List<CloudTaskItem> cloudTaskItemList;
    private Rule rule;
    private List<String> ruleTagMappingList;
    private List<SelectTag> SelectTags = new LinkedList<>();

    public List<CloudTaskItem> getCloudTaskItemList() {
        return cloudTaskItemList;
    }

    public void setCloudTaskItemList(List<CloudTaskItem> cloudTaskItemList) {
        this.cloudTaskItemList = cloudTaskItemList;
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
