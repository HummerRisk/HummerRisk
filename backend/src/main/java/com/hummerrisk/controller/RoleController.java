package com.hummerrisk.controller;

import com.hummerrisk.base.domain.Role;
import com.hummerrisk.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "角色")
@RequestMapping("role")
@RestController
public class RoleController {

    @Resource
    private RoleService roleService;

    @ApiOperation(value = "获取角色")
    @GetMapping("/list/{sign}")
    public List<Role> getRoleList(@PathVariable String sign) {
        return roleService.getRoleList(sign);
    }

    @ApiOperation(value = "所有角色")
    @GetMapping("/all")
    public List<Role> getAllRole() {
        return roleService.getAllRole();
    }

}
