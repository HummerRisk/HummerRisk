package com.hummer.system.controller;

import com.hummer.system.service.LicenseService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping
public class IndexController {

    @Autowired
    private LicenseService licenseService;

    @GetMapping(value = "healthz")
    public String healthz() {
        return "SUCCESS";
    }

    @GetMapping(value = "license")
    public boolean license() {
        return licenseService.license();
    }

}
