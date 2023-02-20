package com.hummer.common.mapper.domain.request.server;

import com.hummer.common.mapper.domain.Server;

import java.util.Map;

/**
 * harris
 */
public class ServerRequest extends Server {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
