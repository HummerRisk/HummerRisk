package com.hummerrisk.controller;

import com.hummerrisk.base.domain.UserKey;
import com.hummerrisk.commons.constants.RoleConstants;
import com.hummerrisk.commons.utils.SessionUtils;
import com.hummerrisk.security.ApiKeyHandler;
import com.hummerrisk.service.UserKeyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import java.util.List;
import java.util.Objects;

@Api(tags = "API Keys")
@RestController
@RequestMapping("user/key")
@RequiresRoles(value = {RoleConstants.ADMIN}, logical = Logical.OR)
public class UserKeysController {

    @Resource
    private UserKeyService userKeyService;

    @ApiOperation(value = "API Keys信息")
    @GetMapping("info")
    public List<UserKey> getUserKeysInfo() {
        String userId = Objects.requireNonNull(SessionUtils.getUser()).getId();
        return userKeyService.getUserKeysInfo(userId);
    }

    @ApiOperation(value = "校验API Keys")
    @GetMapping("validate")
    public String validate(ServletRequest request) throws Exception {
        return ApiKeyHandler.getUser(WebUtils.toHttp(request));
    }

    @ApiOperation(value = "生成API Keys")
    @GetMapping("generate")
    public void generateUserKey() {
        String userId = Objects.requireNonNull(SessionUtils.getUser()).getId();
        userKeyService.generateUserKey(userId);
    }

    @ApiOperation(value = "删除API Keys")
    @GetMapping("delete/{id}")
    public void deleteUserKey(@PathVariable String id) {
        userKeyService.deleteUserKey(id);
    }

    @ApiIgnore
    @GetMapping("active/{id}")
    public void activeUserKey(@PathVariable String id) {
        userKeyService.activeUserKey(id);
    }

    @ApiIgnore
    @GetMapping("disable/{id}")
    public void disabledUserKey(@PathVariable String id) {
        userKeyService.disableUserKey(id);
    }
}
