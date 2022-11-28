package com.hummerrisk.controller.request.user;

import com.hummerrisk.base.domain.UserKey;

import java.util.Map;

public class UserKeyRequest extends UserKey {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
