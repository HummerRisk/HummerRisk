package com.hummer.system.controller;

import com.hummer.common.core.domain.HummerLicense;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.system.service.LicenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "License")
@RestController
@RequestMapping("license")
public class LicenseController {

    @Autowired
    private LicenseService licenseService;

    @I18n
    @Operation(summary = "获取license")
    @GetMapping(value = "getLicense")
    public HummerLicense getLicense() {
        return licenseService.getLicense();
    }

}
