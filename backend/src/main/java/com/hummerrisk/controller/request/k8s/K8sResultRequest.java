package com.hummerrisk.controller.request.k8s;


import com.hummerrisk.base.domain.CloudNativeResult;

import java.util.Map;

/**
 * @author harris
 */
public class K8sResultRequest extends CloudNativeResult {

    private String name;

    private String userName;

    private String resultId;

    private Map<String, Object> combine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
