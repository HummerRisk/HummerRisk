package com.hummer.gateway.controller;

import com.hummer.common.core.constant.TokenConstants;
import com.hummer.common.core.utils.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Controller
@RequestMapping
public class LoginController {

    @GetMapping(value = "/")
    public String index() {
        return "index.html";
    }

    @GetMapping(value = "/login")
    public String login() throws Exception {
        try {
            String s;
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
                if (servletRequestAttributes != null) {
                    // 从header获取token标识
                    String token = servletRequestAttributes.getRequest().getHeader(TokenConstants.AUTHENTICATION);
                    // 如果前端设置了令牌前缀，则裁剪掉前缀
                    if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
                        token = token.replaceFirst(TokenConstants.PREFIX, "");
                    }
                    if (StringUtils.isEmpty(token)) {
                        s = "login.html";
                    } else {
                        s = "redirect:/";
                    }
                } else {
                    s = "login.html";
                }
            } else {
                s = "login.html";
            }

            return s;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

}
