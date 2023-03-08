package com.hummer.gateway.controller;

import com.hummer.common.core.utils.StringUtils;
import com.hummer.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
@RequestMapping
public class LoginController {

    @GetMapping(value = "/")
    public String index() {
        String s;
        s = "index.html";
        return s;
    }

    @GetMapping(value = "/login")
    public String login() {
        String s;
        String token = SecurityUtils.getToken();
        if (StringUtils.isNotEmpty(token)) {
            s = "login.html";
        } else {
            s = "redirect:/";
        }
        return s;
    }

}
