package com.hummerrisk.controller.request.log;

import com.hummerrisk.base.domain.OperationLog;

import java.util.Map;

/**
 * harris
 */
public class OperatorLogRequest extends OperationLog {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
