package com.hummerrisk.dto;


import com.hummerrisk.base.domain.CloudTaskItem;
import com.hummerrisk.base.domain.CloudTaskItemLog;
import com.hummerrisk.base.domain.Rule;

import java.util.List;

public class TaskItemLogDTO {
    private CloudTaskItem cloudTaskItem;
    private List<CloudTaskItemLog> cloudTaskItemLogList;
    private Rule rule;

    public CloudTaskItem getTaskItem() {
        return cloudTaskItem;
    }

    public void setTaskItem(CloudTaskItem cloudTaskItem) {
        this.cloudTaskItem = cloudTaskItem;
    }

    public List<CloudTaskItemLog> getTaskItemLogList() {
        return cloudTaskItemLogList;
    }

    public void setTaskItemLogList(List<CloudTaskItemLog> cloudTaskItemLogList) {
        this.cloudTaskItemLogList = cloudTaskItemLogList;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}
