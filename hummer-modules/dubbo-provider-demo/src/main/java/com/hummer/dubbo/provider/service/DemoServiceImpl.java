package com.hummer.dubbo.provider.service;

import com.hummer.dubbo.provider.api.IDemoService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class DemoServiceImpl implements IDemoService {

    @Override
    public String testDemo() {
        return "success";
    }
}
