package com.hummerrisk.listener;

import com.hummerrisk.service.TaskService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InitListener implements ApplicationRunner {

    @Resource
    private TaskService taskService;

    @Override
    public void run(ApplicationArguments args) {
        taskService.reAddQuartzOnStart();
    }


}
