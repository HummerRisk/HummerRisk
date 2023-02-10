package com.hummer.common.core.domain.request.member;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddMemberRequest {

    private String workspaceId;
    private List<String> userIds;
    private List<String> roleIds;

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }
}
