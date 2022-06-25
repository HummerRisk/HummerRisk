package com.hummerrisk.listener;

import com.hummerrisk.service.CloudTaskService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InitListener implements ApplicationRunner {

    @Resource
    private CloudTaskService cloudTaskService;

    @Override
    public void run(ApplicationArguments args) {
        cloudTaskService.reAddQuartzOnStart();
    }


}
