package com.hummerrisk.dto;

import com.hummerrisk.base.domain.CodeResult;

/**
 * @author harris
 */
public class CodeResultWithBLOBsDTO extends CodeResult {

    private String name;

    private String tagKey;

    private String tagName;

    private String user;

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

}
