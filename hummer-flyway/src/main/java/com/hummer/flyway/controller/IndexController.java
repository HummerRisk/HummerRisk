package com.hummer.flyway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
@RequestMapping
public class IndexController {

    @GetMapping(value = "/healthz")
    public String index() {
        return "index";
    }

}
