package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.Role;
import com.hummerrisk.base.domain.User;
import com.hummerrisk.controller.request.member.QueryMemberRequest;
import com.hummerrisk.controller.request.organization.QueryOrgMemberRequest;
import com.hummerrisk.dto.UserRoleHelpDTO;
import com.hummerrisk.dto.OrganizationMemberDTO;
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
