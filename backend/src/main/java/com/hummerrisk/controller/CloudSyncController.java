package com.hummerrisk.controller;

import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.service.CloudSyncService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

@ApiIgnore
@RestController
@RequestMapping(value = "cloud/sync")
public class CloudSyncController {
    @Resource
    private CloudSyncService cloudSyncService;

    @I18n
    @GetMapping(value = "sync/{accountId}")
    public void sync(@PathVariable String accountId) throws Exception {
        cloudSyncService.sync(accountId);
    }


}
