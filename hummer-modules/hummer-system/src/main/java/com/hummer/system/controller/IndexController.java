package com.hummer.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping
public class IndexController {

    @GetMapping(value = "healthz")
    public String healthz() {
        return "SUCCESS";
    }

}
