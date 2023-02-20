package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.HistoryServerResult;

/**
 * @author harris
 */
public class HistoryServerResultDTO extends HistoryServerResult {

    private String name;

    private String tagKey;

    private String tagName;

    private String user;

    private String groupName;

    private String rule;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
