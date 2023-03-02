package com.hummer.customer.controller;

import com.hummer.customer.service.TestService;
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
