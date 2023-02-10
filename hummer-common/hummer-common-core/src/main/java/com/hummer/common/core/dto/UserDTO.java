package com.hummer.common.core.dto;


import com.hummer.common.core.domain.Role;
import com.hummer.common.core.domain.User;
import com.hummer.common.core.domain.UserRole;

import java.util.ArrayList;
import java.util.List;

public class UserDTO extends User {

    private List<Role> roles = new ArrayList<>();

    private List<UserRole> userRoles = new ArrayList<>();

    private static final long serialVersionUID = 1L;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
}
