package com.hummerrisk.controller.request.task;

import com.hummerrisk.base.domain.Task;

import java.util.Map;

/**
 * harris
 */
public class TaskRequest extends Task {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
