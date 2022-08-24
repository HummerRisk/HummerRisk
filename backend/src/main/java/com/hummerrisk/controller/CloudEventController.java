package com.hummerrisk.controller;

import com.hummerrisk.base.domain.CloudEvent;
import com.hummerrisk.service.CloudEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

@Api(tags = "云审计")
@RestController
@RequestMapping("cloud/event")
public class CloudEventController {
    @Resource
    private CloudEventService cloudEventService;

    @ApiOperation(value = "日志同步")
    @GetMapping("sync")
    public void syncEvents(String accountId,String region,String startTime,String endTime){
        cloudEventService.syncCloudEvents(accountId,region,startTime,endTime);
    }

    @ApiOperation(value = "日志查询")
    @GetMapping()
    public List<CloudEvent> getEvents(String accountId, String region, String startTime, String endTime){
        return cloudEventService.getCloudEvents(accountId,region,startTime,endTime);
    }
}
