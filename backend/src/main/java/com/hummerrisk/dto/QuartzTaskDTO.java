package com.hummerrisk.dto;


import com.hummerrisk.base.domain.Rule;
import com.hummerrisk.commons.utils.SelectTag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class QuartzTaskDTO extends Rule {

    private String cron;

    private String type;

    private String taskName;

    private List<String> tags = new ArrayList<>();

    private List<SelectTag> SelectTags = new LinkedList<>();

    private String AccountId;

    private String regions;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

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
        return AccountId;
    }

    public void setAccountId(String accountId) {
        AccountId = accountId;
    }

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }
}
