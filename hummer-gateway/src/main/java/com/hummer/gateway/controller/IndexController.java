package com.hummer.gateway.controller;

import com.hummer.common.core.constant.TokenConstants;
import com.hummer.common.core.utils.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping
public class IndexController {

    @GetMapping(value = "healthz")
    public String healthz() {
        return "SUCCESS";
    }

    @GetMapping(value = "/")
    public String index() {
        String s;
        s = "index.html";
        return s;
    }

    @GetMapping(value = "/login")
    public String login() {
        String s;
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
        // 从header获取token标识
        String token = servletRequestAttributes.getRequest().getHeader(TokenConstants.AUTHENTICATION);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, "");
        }
        if (StringUtils.isNotEmpty(token)) {
            s = "login.html";
        } else {
            s = "redirect:/";
        }
        return s;
    }
}
