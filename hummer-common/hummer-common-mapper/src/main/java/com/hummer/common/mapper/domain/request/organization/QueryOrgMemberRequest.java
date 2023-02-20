package com.hummer.common.mapper.domain.request.organization;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryOrgMemberRequest {
    private String name;
    private String organizationId;
}
