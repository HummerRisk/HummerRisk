package io.hummerrisk.controller.request.server;


import io.hummerrisk.base.domain.ServerResult;

import java.util.Map;

/**
 * @author harris
 */
public class ServerResultRequest extends ServerResult {

    private String name;

    private Map<String, Object> combine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
