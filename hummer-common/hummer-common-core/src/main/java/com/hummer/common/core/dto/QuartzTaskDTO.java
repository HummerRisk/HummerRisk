package com.hummer.common.core.dto;


import com.hummer.common.core.domain.Rule;
import com.hummer.common.core.domain.SelectTag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QuartzTaskDTO extends Rule {

    private String type;

    private String taskName;

    private List<String> tags = new ArrayList<>();

    private List<SelectTag> SelectTags = new LinkedList<>();

    private String accountId;

    private String regions;

    private String projectId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<SelectTag> getSelectTags() {
        return SelectTags;
    }

    public void setSelectTags(List<SelectTag> SelectTags) {
        this.SelectTags = SelectTags;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
