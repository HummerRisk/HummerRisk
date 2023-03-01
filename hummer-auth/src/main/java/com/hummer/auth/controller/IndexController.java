package com.hummer.auth.controller;

import com.hummer.common.security.service.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

@ApiIgnore
@Controller
@RequestMapping
public class IndexController {

    @Resource
    private TokenService tokenService;

    @GetMapping(value = "/")
    public String index() {
        return "index.html";
    }

    @GetMapping(value = "/login")
    public String login() {
        String s;
        if (tokenService.getLoginUser().getUser() == null) {
            s = "login.html";
        } else {
            s = "redirect:/";
        }
        return s;
    }

}
