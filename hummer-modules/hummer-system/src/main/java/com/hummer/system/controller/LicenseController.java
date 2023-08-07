package com.hummer.system.controller;

import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.security.service.TokenService;
import com.hummer.system.service.LicenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "License")
@RestController
@RequestMapping("license")
public class LicenseController {

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private TokenService tokenService;

    @I18n
    @Operation(summary = "获取license")
    @GetMapping(value = "getLicense")
    public HummerLicenseWithBLOBs getLicense() {
        return licenseService.getLicense();
    }

    @I18n
    @Operation(summary = "上传校验license")
    @PostMapping(value = "updateLicense", consumes = {"multipart/form-data"})
    public void validateLicense(@RequestPart(value = "licenseFile", required = false) MultipartFile licenseFile) throws Exception {
        licenseService.validateLicense(licenseFile, tokenService.getLoginUser());
    }

    @Operation(summary = "是否存在有效license")
    @GetMapping(value = "isLicense")
    public boolean isLicense() {
        return licenseService.license();
    }

}
