package com.hummer.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping("anonymous")
public class HelloController {
    @Autowired
    MessageSource messageSource;

    @GetMapping("hello")
    public String hello() {
        return messageSource.getMessage("i18n_operation_begin", null, "默认值", LocaleContextHolder.getLocale());
    }
}
