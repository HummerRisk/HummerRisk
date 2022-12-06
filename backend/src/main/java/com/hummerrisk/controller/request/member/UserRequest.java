package com.hummerrisk.controller.request.member;

import com.hummerrisk.base.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserRequest extends User {

    private List<Map<String, Object>> roles = new ArrayList<>();

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

    public List<Map<String, Object>> getRoles() {
        return roles;
    }

    public void setRoles(List<Map<String, Object>> roles) {
        this.roles = roles;
    }
}
