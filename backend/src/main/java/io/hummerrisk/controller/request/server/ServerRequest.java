package io.hummerrisk.controller.request.server;

import io.hummerrisk.base.domain.Server;

import java.util.Map;

/**
 * harris
 */
public class ServerRequest extends Server {

    private Map<String, Object> combine;

    private String groupId;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
