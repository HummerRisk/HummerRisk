package com.hummer.common.mapper.domain.request.server;


import com.hummer.common.mapper.domain.ServerResult;

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
