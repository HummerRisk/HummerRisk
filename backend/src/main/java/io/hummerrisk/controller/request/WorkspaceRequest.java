package io.hummerrisk.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceRequest {
    private String organizationId;
    private String name;

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
