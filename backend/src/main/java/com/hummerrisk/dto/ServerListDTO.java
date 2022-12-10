package com.hummerrisk.dto;

import com.hummerrisk.base.domain.Server;

import java.util.List;

/**
 * @author harris
 */
public class ServerListDTO extends Server {

    private String tagKey;

    private String tagName;

    private String user;

    private String groupName;

    private List<ServerResultDTO> serverResultDTOS;

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

    public List<ServerResultDTO> getServerResultDTOS() {
        return serverResultDTOS;
    }

    public void setServerResultDTOS(List<ServerResultDTO> serverResultDTOS) {
        this.serverResultDTOS = serverResultDTOS;
    }
}
