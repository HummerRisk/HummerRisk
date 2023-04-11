package com.hummer.system.controller;

import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.system.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;

@Hidden
@RequestMapping("user/role")
@RestController
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @I18n
    @GetMapping("/all/{userId}")
    public List<Map<String, Object>> getUserRole(@PathVariable("userId") String userId) {
        return userRoleService.getUserRole(userId);
    }
}
