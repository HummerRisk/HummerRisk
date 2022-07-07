package com.hummerrisk.controller;

import com.hummerrisk.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "任务")
@RestController
@RequestMapping("task")
public class TaskController {

    @Resource
    private TaskService taskService;

    @ApiOperation(value = "收藏夹列表")
    @GetMapping(value = "favorite/list")
    public Object listFavorites() throws Exception {
        return taskService.listFavorites();
    }

    @ApiOperation(value = "资源信息列表")
    @GetMapping(value = "account/list")
    public Object listAccounts() throws Exception {
        return taskService.listAccounts();
    }


}
