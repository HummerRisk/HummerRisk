package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.CloudEvent;
import com.hummerrisk.base.domain.CloudEventSyncLog;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.request.cloudEvent.CloudEventRequest;
import com.hummerrisk.service.CloudEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "云审计")
@RestController
@RequestMapping("cloud/event")
public class CloudEventController {
    @Resource
    private CloudEventService cloudEventService;

    @ApiOperation(value = "同步日志查询")
    @PostMapping("sync/log/list/{goPage}/{pageSize}")
    public Pager<List<CloudEventSyncLog>> listSyncLogs(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudEventRequest cloudEventRequest){
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudEventService.getCloudEventSyncLog(cloudEventRequest.getAccountId(),cloudEventRequest.getRegion()));
    }

    @ApiOperation(value = "日志同步")
    @PostMapping("sync")
    public void syncEvents(@RequestBody CloudEventRequest cloudEventRequest){
        cloudEventService.syncCloudEvents(cloudEventRequest.getAccountId(),cloudEventRequest.getRegion()
                ,cloudEventRequest.getStartTime(),cloudEventRequest.getEndTime());
    }

    @ApiOperation(value = "日志查询")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<CloudEvent>> listEvents(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudEventRequest cloudEventRequest){
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudEventService.getCloudEvents(cloudEventRequest));
    }
}
