package com.hummer.dubbo.customer.controller;

import com.hummer.dubbo.customer.service.TestService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("test")
    public String test(){
        return testService.testDubbo();
    }
}
