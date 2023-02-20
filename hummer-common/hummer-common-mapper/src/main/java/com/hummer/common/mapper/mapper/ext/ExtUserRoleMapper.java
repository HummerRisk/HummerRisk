package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.core.domain.Role;
import com.hummer.common.core.domain.User;
import com.hummer.common.mapper.dto.OrganizationMemberDTO;
import com.hummer.common.mapper.dto.UserRoleHelpDTO;
import com.hummer.common.mapper.domain.request.member.QueryMemberRequest;
import com.hummer.common.mapper.domain.request.organization.QueryOrgMemberRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtUserRoleMapper {

    List<UserRoleHelpDTO> getUserRoleHelpList(@Param("userId") String userId);

    List<User> getMemberList(@Param("member") QueryMemberRequest request);

    List<User> getOrgMemberList(@Param("orgMember") QueryOrgMemberRequest request);

    List<OrganizationMemberDTO> getOrganizationMemberDTO(@Param("orgMember") QueryOrgMemberRequest request);

    List<Role> getOrganizationMemberRoles(@Param("orgId") String orgId, @Param("userId") String userId);

    List<Role> getWorkspaceMemberRoles(@Param("workspaceId") String workspaceId, @Param("userId") String userId);

    List<User> getBesideOrgMemberList(@Param("orgId") String orgId);


    List<User> getWorkspaceAdminAndUserList(@Param("request") QueryMemberRequest request);
}
