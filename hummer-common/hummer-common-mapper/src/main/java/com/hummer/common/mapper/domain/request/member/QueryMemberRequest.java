package com.hummer.common.mapper.domain.request.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryMemberRequest {
    private String name;
    private String workspaceId;
}
