package com.hummer.common.core.domain.request.config;


import com.hummer.common.core.domain.CloudNativeConfigResultItem;

import java.util.Map;

/**
 * @author harris
 */
public class ConfigResultItemRequest extends CloudNativeConfigResultItem {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
