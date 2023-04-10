package com.hummer.system.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.domain.UserKey;
import com.hummer.common.core.domain.request.user.UserKeyRequest;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import com.hummer.system.service.ApiKeyHandler;
import com.hummer.system.service.UserKeyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Objects;

@Tag(name = "API Keys")
@RestController
@RequestMapping("user/key")
public class UserKeysController {

    @Autowired
    private UserKeyService userKeyService;
    @Autowired
    private TokenService tokenService;

    @I18n
    @Operation(summary = "API Keys信息")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<UserKey>> getUserKeysInfo(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody UserKeyRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userKeyService.getUserKeysInfo(request, tokenService.getLoginUser()));
    }

    @I18n
    @Operation(summary = "校验API Keys")
    @GetMapping("validate")
    public String validate(ServletRequest request) throws Exception {
        return ApiKeyHandler.getUser((HttpServletRequest)request);
    }

    @Operation(summary = "生成API Keys")
    @GetMapping("generate")
    public void generateUserKey() throws Exception {
        String userId = Objects.requireNonNull(tokenService.getLoginUser().getUser()).getId();
        userKeyService.generateUserKey(userId, tokenService.getLoginUser());
    }

    @Operation(summary = "删除API Keys")
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
