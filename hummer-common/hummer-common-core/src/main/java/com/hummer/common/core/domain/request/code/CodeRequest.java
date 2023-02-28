package com.hummer.common.core.domain.request.code;

import com.hummer.common.core.domain.Code;

import java.util.Map;

/**
 * harris
 */
public class CodeRequest extends Code {

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
