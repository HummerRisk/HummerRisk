package com.hummer.dubbo.customer.service;

import com.hummer.dubbo.provider.api.IDemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @DubboReference
    private IDemoService demoService;

    public String testDubbo(){
        return demoService.testDemo();
    }
}
