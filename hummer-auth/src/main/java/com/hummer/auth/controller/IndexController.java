package com.hummer.auth.controller;

import com.hummer.common.core.utils.SessionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
@RequestMapping
public class IndexController {

    @GetMapping(value = "/")
    public String index() {
        String s;
        s = "index.html";
        return s;
    }

    @GetMapping(value = "/login")
    public String login() {
        String s;
        if (SessionUtils.getUser() == null) {
            s = "login.html";
        } else {
            s = "redirect:/";
        }
        return s;
    }

    @GetMapping(value = "/sso/login")
    public String ossLogin() {
        String s;
        s = "redirect:/";
        return s;
    }

    @GetMapping(value = "/sso/logout")
    public void ossLogout() {
        SecurityUtils.getSubject().logout();
    }

}
