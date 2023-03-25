package com.hummer.system.controller;

import com.hummer.common.core.domain.Role;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.system.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "角色")
@RequestMapping("role")
@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @I18n
    @ApiOperation(value = "获取角色")
    @GetMapping("/list/{sign}")
    public List<Role> getRoleList(@PathVariable String sign) {
        return roleService.getRoleList(sign);
    }

    @I18n
    @ApiOperation(value = "所有角色")
    @GetMapping("/all")
    public List<Role> getAllRole() {
        return roleService.getAllRole();
    }

}
