package com.hummer.common.core.domain.request.proxy;

import com.hummerrisk.base.domain.Proxy;

import java.util.Map;

/**
 * harris
 */
public class ProxyRequest extends Proxy {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
