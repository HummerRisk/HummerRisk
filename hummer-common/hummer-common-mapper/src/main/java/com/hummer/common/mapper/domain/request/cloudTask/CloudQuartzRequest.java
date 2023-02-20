package com.hummer.common.mapper.domain.request.cloudTask;


import com.hummer.common.mapper.domain.CloudAccountQuartzTask;

import java.util.Map;

public class CloudQuartzRequest extends CloudAccountQuartzTask {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
