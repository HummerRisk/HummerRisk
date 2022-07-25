package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.commons.utils.ChartData;
import com.hummerrisk.commons.utils.DashboardTarget;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.controller.request.dashboard.TaskCalendarVo;
import com.hummerrisk.dto.HistoryScanDTO;
import com.hummerrisk.dto.ImageChartDTO;
import com.hummerrisk.dto.PackageChartDTO;
import com.hummerrisk.dto.TopInfoDTO;
import com.hummerrisk.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "首页")
@RestController
@RequestMapping("dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @I18n
    @ApiOperation(value = "不合规统计")
    @PostMapping("point/target/{goPage}/{pageSize}")
    public Pager<List<DashboardTarget>> target(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, dashboardService.target(params));
    }

    @I18n
    @ApiOperation(value = "风险等级统计")
    @PostMapping("point/severity")
    public List<Map<String, Object>> severityList(@RequestBody Map<String, Object> params) {
        return dashboardService.severityList(params);
    }

    @I18n
    @ApiOperation(value = "分组统计")
    @PostMapping("distribution")
    public List<ChartData> getDistribution(@RequestBody Map<String, Object> params) {
        return dashboardService.vulnDistribution(params);
    }

    @I18n
    @ApiOperation(value = "合计统计")
    @PostMapping("totalPolicy")
    public List<Map<String, Object>> totalPolicy(@RequestBody Map<String, Object> params) {
        return dashboardService.totalPolicy(params);
    }

    @I18n
    @ApiOperation(value = "云资源历史记录")
    @PostMapping("history/{goPage}/{pageSize}")
    public Pager<List<HistoryScanDTO>> history(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, dashboardService.history(params));
    }

    @I18n
    @ApiOperation(value = "漏洞检测历史记录")
    @PostMapping("vuln/history/{goPage}/{pageSize}")
    public Pager<List<HistoryScanDTO>> vulnHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, dashboardService.vulnHistory(params));
    }

    @I18n
    @ApiOperation(value = "首页TOP统计")
    @PostMapping("topInfo")
    public TopInfoDTO topInfo(@RequestBody Map<String, Object> params) {
        return dashboardService.topInfo(params);
    }

    @I18n
    @ApiOperation(value = "首页软件包检测统计")
    @PostMapping("packageChart")
    public PackageChartDTO packageChart(@RequestBody Map<String, Object> params) {
        return dashboardService.packageChart(params);
    }

    @I18n
    @ApiOperation(value = "首页镜像检测统计")
    @PostMapping("imageChart")
    public ImageChartDTO imageChart(@RequestBody Map<String, Object> params) {
        return dashboardService.imageChart(params);
    }

    @ApiOperation(value = "首页任务日历")
    @GetMapping("taskCalendar")
    public List<TaskCalendarVo> taskCalendar() {
        return dashboardService.taskCalendar();
    }

    @ApiOperation(value = "首页安全评分")
    @GetMapping("score")
    public Integer score() {
        return dashboardService.score();
    }

}
