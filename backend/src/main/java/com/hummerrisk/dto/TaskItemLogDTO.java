package com.hummerrisk.dto;


import com.hummerrisk.base.domain.Rule;
import com.hummerrisk.base.domain.TaskItem;
import com.hummerrisk.base.domain.TaskItemLog;

import java.util.List;

public class TaskItemLogDTO {
    private TaskItem taskItem;
    private List<TaskItemLog> taskItemLogList;
    private Rule rule;

    public TaskItem getTaskItem() {
        return taskItem;
    }

    public void setTaskItem(TaskItem taskItem) {
        this.taskItem = taskItem;
    }

    public List<TaskItemLog> getTaskItemLogList() {
        return taskItemLogList;
    }

    public void setTaskItemLogList(List<TaskItemLog> taskItemLogList) {
        this.taskItemLogList = taskItemLogList;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}
