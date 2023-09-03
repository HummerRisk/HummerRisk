package com.hummer.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.cloud.service.CloudEventService;
import com.hummer.common.core.domain.CloudEventRegionLog;
import com.hummer.common.core.domain.CloudEventSyncLog;
import com.hummer.common.core.domain.request.cloudEvent.CloudEventRequest;
import com.hummer.common.core.domain.request.event.CloudEventSyncLogVo;
import com.hummer.common.core.domain.request.event.CloudEventWithBLOBsVo;
import com.hummer.common.core.dto.ChartDTO;
import com.hummer.common.core.dto.CloudEventGroupDTO;
import com.hummer.common.core.dto.CloudEventSourceIpInsightDTO;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "云审计")
@RestController
@RequestMapping("event")
public class CloudEventController {

    @Autowired
    private CloudEventService cloudEventService;

    @Operation(summary = "同步任务列表")
    @PostMapping("sync/log/list/{goPage}/{pageSize}")
    public Pager<List<CloudEventSyncLogVo>> listSyncLogs(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudEventRequest cloudEventRequest) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudEventService.getCloudEventSyncLog(cloudEventRequest));
    }

    @Operation(summary = "区域同步日志")
    @PostMapping("sync/log/region/list/{logId}")
    public List<CloudEventRegionLog> listSyncRegionLogs(@PathVariable int logId) {
        return cloudEventService.getCloudEventRegionLog(logId);
    }

    @Operation(summary = "删除同步日志")
    @PostMapping("sync/log/delete/{id}")
    public void deleteSyncLog(@PathVariable int id) {
        cloudEventService.deleteCloudEventSyncLog(id);
    }


    @Operation(summary = "查询同步日志详情")
    @PostMapping("sync/log/detail/{id}")
    public CloudEventSyncLog getSyncLog(@PathVariable int id) {
        return cloudEventService.selectCloudEventSyncLog(id);
    }

    @Operation(summary = "删除云事件")
    @PostMapping("delete/{id}")
    public void deleteEvent(@PathVariable String id) {
        cloudEventService.deleteCloudEvent(id);
    }

    @Operation(summary = "云事件同步")
    @PostMapping("sync")
    public void syncEvents(@RequestBody CloudEventRequest cloudEventRequest) {
        cloudEventService.syncCloudEvents(cloudEventRequest.getAccountId(), cloudEventRequest.getRegions()
                , cloudEventRequest.getStartTime(), cloudEventRequest.getEndTime());
    }

    @Operation(summary = "云事件分析列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<CloudEventWithBLOBsVo>> listEvents(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudEventRequest cloudEventRequest) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudEventService.getCloudEvents(cloudEventRequest));
    }

    @Operation(summary = "云事件聚合列表")
    @PostMapping("group/list/{goPage}/{pageSize}")
    public Pager<List<CloudEventGroupDTO>> listEventsGroup(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudEventRequest cloudEventRequest) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudEventService.getCloudEventGroup(cloudEventRequest));
    }

    @Operation(summary = "源ip分析列表")
    @PostMapping("insight/list/{goPage}/{pageSize}")
    public Pager<List<CloudEventSourceIpInsightDTO>> listSourceIpInsight(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudEventRequest cloudEventRequest) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudEventService.getSourceIpInsight(cloudEventRequest));
    }
    @I18n
    @Operation(summary = "操作审计概览TOP统计")
    @PostMapping("top/info")
    public Map<String, Object> topInfo(@RequestBody Map<String, Object> params) {
        return cloudEventService.topInfo(params);
    }

    @I18n
    @Operation(summary = "云账号统计")
    @GetMapping("cloud/chart")
    public List<Map<String, Object>> cloudChart() {
        return cloudEventService.cloudChart();
    }

    @I18n
    @Operation(summary = "区域统计")
    @GetMapping("region/chart")
    public List<Map<String, Object>> regionChart() {
        return cloudEventService.regionChart();
    }

    @I18n
    @Operation(summary = "风险统计")
    @GetMapping("severity/chart")
    public List<Map<String, Object>> severityChart() {
        return cloudEventService.severityChart();
    }

    @I18n
    @Operation(summary = "IP 访问统计")
    @GetMapping("ip/access/chart/{ip}/{startDate}/{endDate}")
    public ChartDTO ipAccessChart(@PathVariable String ip,@PathVariable String startDate,@PathVariable String endDate){
        return cloudEventService.ipAccessChart(ip,startDate,endDate);
    }

    @Operation(summary = "批量删除云事件同步日志")
    @PostMapping("delete/logs")
    public void deleteLogs(@RequestBody List<Integer> selectIds) throws Exception {
        cloudEventService.deleteLogs(selectIds);
    }

}
