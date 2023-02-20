package com.hummer.common.mapper.domain.request.user;

import com.hummer.common.mapper.domain.UserKey;

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
