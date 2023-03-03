package com.hummer.auth.controller;

import com.hummer.auth.form.LoginBody;
import com.hummer.auth.service.SysLoginService;
import com.hummer.common.core.constant.SsoMode;
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
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping
public class LoginController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    @Autowired
    private Environment env;

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
            return ResultHolder.success(userInfo);
        }catch (Exception e) {
            return ResultHolder.error(e.getMessage());
        }
    }

    @GetMapping(value = "isLogin")
    public ResultHolder isLogin() {
        String token = SecurityUtils.getToken();
        if (StringUtils.isNotEmpty(token)) {
            User user = tokenService.getLoginUser().getUser();
            if ((user!= null) && StringUtils.isBlank(user.getLanguage()))
                user.setLanguage(LocaleContextHolder.getLocale().toString());
            return ResultHolder.success(user);
        }
        String ssoMode = env.getProperty("sso.mode");
        if (ssoMode != null && org.apache.commons.lang3.StringUtils.equalsIgnoreCase(SsoMode.CAS.name(), ssoMode)) {
            return ResultHolder.error("sso");
        }
        return ResultHolder.error("");
    }

    @GetMapping(value = "/currentUser")
    public ResultHolder currentUser() {
        User user = tokenService.getLoginUser().getUser();
        return ResultHolder.success(user);
    }

    @DeleteMapping("signout")
    public R<?> logout(HttpServletRequest request) {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出日志
            sysLoginService.logout(username);
        }
        return R.ok();
    }

}
