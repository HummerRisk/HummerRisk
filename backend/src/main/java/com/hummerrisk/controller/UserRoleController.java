package com.hummerrisk.controller;

import com.hummerrisk.base.domain.Role;
import com.hummerrisk.commons.constants.RoleConstants;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.service.UserRoleService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@ApiIgnore
@RequestMapping("userrole")
@RestController
public class UserRoleController {

    @Resource
    private UserRoleService userRoleService;

    @I18n
    @GetMapping("/list/org/{orgId}/{userId}")
    @RequiresRoles(value = {RoleConstants.ADMIN}, logical = Logical.OR)
    public List<Role> getOrganizationMemberRoles(@PathVariable String orgId, @PathVariable String userId) {
        return userRoleService.getOrganizationMemberRoles(orgId, userId);
    }

    @I18n
    @GetMapping("/list/ws/{workspaceId}/{userId}")
    @RequiresRoles(value = {RoleConstants.ADMIN}, logical = Logical.OR)
    public List<Role> getWorkspaceMemberRoles(@PathVariable String workspaceId, @PathVariable String userId) {
        return userRoleService.getWorkspaceMemberRoles(workspaceId, userId);
    }

    @I18n
    @GetMapping("/all/{userId}")
    @RequiresRoles(RoleConstants.ADMIN)
    public List<Map<String, Object>> getUserRole(@PathVariable("userId") String userId) {
        return userRoleService.getUserRole(userId);
    }
}
