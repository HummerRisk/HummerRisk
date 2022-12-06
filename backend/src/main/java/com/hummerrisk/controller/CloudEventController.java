package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.controller.request.cloudEvent.CloudEventRequest;
import com.hummerrisk.dto.CloudEventGroupDTO;
import com.hummerrisk.service.CloudEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "云审计")
@RestController
@RequestMapping("cloud/event")
public class CloudEventController {
    @Resource
    private CloudEventService cloudEventService;

    @ApiOperation(value = "同步任务列表")
    @PostMapping("sync/log/list/{goPage}/{pageSize}")
    public Pager<List<CloudEventSyncLog>> listSyncLogs(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudEventRequest cloudEventRequest) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudEventService.getCloudEventSyncLog(cloudEventRequest));
    }

    @ApiOperation(value = "区域同步日志")
    @PostMapping("sync/log/region/list/{logId}")
    public List<CloudEventRegionLog> listSyncRegionLogs(@PathVariable int logId) {
        return cloudEventService.getCloudEventRegionLog(logId);
    }

    @ApiOperation(value = "删除同步日志")
    @PostMapping("sync/log/delete/{id}")
    public void deleteSyncLog(@PathVariable int id) {
        cloudEventService.deleteCloudEventSyncLog(id);
    }


    @ApiOperation(value = "查询同步日志详情")
    @PostMapping("sync/log/detail/{id}")
    public CloudEventSyncLog getSyncLog(@PathVariable int id) {
        return cloudEventService.selectCloudEventSyncLog(id);
    }

    @ApiOperation(value = "删除云事件")
    @PostMapping("delete/{id}")
    public void deleteEvent(@PathVariable String id) {
        cloudEventService.deleteCloudEvent(id);
    }

    @ApiOperation(value = "云事件同步")
    @PostMapping("sync")
    public void syncEvents(@RequestBody CloudEventRequest cloudEventRequest) {
        cloudEventService.syncCloudEvents(cloudEventRequest.getAccountId(), cloudEventRequest.getRegions()
                , cloudEventRequest.getStartTime(), cloudEventRequest.getEndTime());
    }

    @ApiOperation(value = "云事件分析列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<CloudEventWithBLOBs>> listEvents(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudEventRequest cloudEventRequest) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudEventService.getCloudEvents(cloudEventRequest));
    }

    @ApiOperation(value = "云事件聚合列表")
    @PostMapping("group/list/{goPage}/{pageSize}")
    public Pager<List<CloudEventGroupDTO>> listEventsGroup(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudEventRequest cloudEventRequest) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudEventService.getCloudEventGroup(cloudEventRequest));
    }
    @I18n
    @ApiOperation(value = "操作审计概览TOP统计")
    @PostMapping("topInfo")
    public Map<String, Object> topInfo(@RequestBody Map<String, Object> params) {
        return cloudEventService.topInfo(params);
    }

    @I18n
    @ApiOperation(value = "云账号统计")
    @GetMapping("cloudChart")
    public List<Map<String, Object>> cloudChart() {
        return cloudEventService.cloudChart();
    }

    @I18n
    @ApiOperation(value = "区域统计")
    @GetMapping("regionChart")
    public List<Map<String, Object>> regionChart() {
        return cloudEventService.regionChart();
    }

    @I18n
    @ApiOperation(value = "风险统计")
    @GetMapping("severityChart")
    public List<Map<String, Object>> severityChart() {
        return cloudEventService.severityChart();
    }
}
