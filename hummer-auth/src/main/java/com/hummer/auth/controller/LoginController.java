package com.hummer.auth.controller;

import com.hummer.auth.service.SysLoginService;
import com.hummer.common.core.domain.R;
import com.hummer.common.core.domain.request.LoginRequest;
import com.hummer.common.core.text.ResultHolder;
import com.hummer.common.core.utils.JwtUtils;
import com.hummer.common.core.utils.StringUtils;
import com.hummer.common.security.auth.AuthUtil;
import com.hummer.common.security.service.TokenService;
import com.hummer.common.security.utils.SecurityUtils;
import com.hummer.system.api.domain.User;
import com.hummer.system.api.model.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@ApiIgnore
@RestController
@RequestMapping
public class LoginController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    @GetMapping(value = "healthz")
    public String healthz() {
        return "SUCCESS";
    }

    @PostMapping("signin")
    public ResultHolder login(@RequestBody LoginRequest request) throws Exception {
        try {
            // 用户登录
            LoginUser userInfo = sysLoginService.login(request.getUsername(), request.getPassword());
            // 获取登录token
            Map<String, Object> tokenInfo = tokenService.createToken(userInfo);
            userInfo.setToken((String) tokenInfo.get("access_token"));
            return ResultHolder.success(userInfo);
        } catch (Exception e) {
            return ResultHolder.error(e.getMessage());
        }
    }

    @GetMapping(value = "isLogin")
    public ResultHolder isLogin() {
        String token = SecurityUtils.getToken();
        if (StringUtils.isNotEmpty(token)) {
            LoginUser loginUser = tokenService.getLoginUser();
            if (loginUser == null) return ResultHolder.error("");
            User user = loginUser.getUser();
            if ((user != null) && StringUtils.isBlank(user.getLanguage()))
                user.setLanguage(LocaleContextHolder.getLocale().toString());
            return ResultHolder.success(user);
        }
        return ResultHolder.error("");
    }

    @GetMapping("signout")
    public ResultHolder logout(HttpServletRequest request) {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String userId = JwtUtils.getUserId(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出日志
            sysLoginService.logout(userId);
        }
        return ResultHolder.success("");
    }

}
