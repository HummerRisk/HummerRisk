package com.hummer.cloud.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping
public class IndexController {

    @GetMapping(value = "healthz")
    public String healthz() {
        return "SUCCESS";
    }

}
