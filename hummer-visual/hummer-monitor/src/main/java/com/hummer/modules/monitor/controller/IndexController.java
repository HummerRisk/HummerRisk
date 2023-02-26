package com.hummer.modules.monitor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class IndexController {

    @GetMapping(value = "healthz")
    public String healthz() {
        return "SUCCESS";
    }

}
