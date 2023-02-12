package com.hummer.system.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.domain.request.chart.ChartData;
import com.hummer.common.core.domain.request.dashboard.AnslysisVo;
import com.hummer.common.core.domain.request.dashboard.DashboardTarget;
import com.hummer.common.core.domain.request.dashboard.HistoryScanVo;
import com.hummer.common.core.domain.request.dashboard.TaskCalendarVo;
import com.hummer.common.core.dto.ChartDTO;
import com.hummer.common.core.dto.HistoryScanDTO;
import com.hummer.common.core.dto.TopInfoDTO;
import com.hummer.common.core.dto.TopScanDTO;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.system.service.DashboardService;
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
    @ApiOperation(value = "漏洞合计统计")
    @PostMapping("vulnTotalPolicy")
    public List<Map<String, Object>> vulnTotalPolicy(@RequestBody Map<String, Object> params) {
        return dashboardService.vulnTotalPolicy(params);
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
    @ApiOperation(value = "首页TOP检测统计")
    @GetMapping("topScanInfo")
    public List<TopScanDTO> topScanInfo() {
        return dashboardService.topScanInfo();
    }

    @I18n
    @ApiOperation(value = "首页镜像检测统计")
    @PostMapping("imageChart")
    public ChartDTO imageChart(@RequestBody Map<String, Object> params) {
        return dashboardService.imageChart(params);
    }

    @I18n
    @ApiOperation(value = "首页源码检测统计")
    @PostMapping("codeChart")
    public ChartDTO codeChart(@RequestBody Map<String, Object> params) {
        return dashboardService.codeChart(params);
    }

    @I18n
    @ApiOperation(value = "首页云原生检测统计")
    @PostMapping("cloudNativeChart")
    public ChartDTO cloudNativeChart(@RequestBody Map<String, Object> params) {
        return dashboardService.cloudNativeChart(params);
    }

    @I18n
    @ApiOperation(value = "首页云原生检测统计")
    @PostMapping("configChart")
    public ChartDTO configChart(@RequestBody Map<String, Object> params) {
        return dashboardService.configChart(params);
    }

    @I18n
    @ApiOperation(value = "首页文件系统检测统计")
    @PostMapping("fsChart")
    public ChartDTO fsChart(@RequestBody Map<String, Object> params) {
        return dashboardService.fsChart(params);
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

    @I18n
    @ApiOperation(value = "保存统计分析")
    @PostMapping("saveAnalysis")
    public void saveAnalysis(@RequestBody AnslysisVo anslysisVo) {
        dashboardService.saveAnalysis(anslysisVo);
    }

    @ApiOperation(value = "查询统计分析")
    @GetMapping("queryAnalysis")
    public AnslysisVo queryAnalysis() {
        return dashboardService.queryAnalysis();
    }

    @I18n
    @ApiOperation(value = "检测统计分析")
    @PostMapping("analysisChart")
    public ChartDTO analysisChart() {
        return dashboardService.analysisChart();
    }

    @I18n
    @ApiOperation(value = "检测统计分析")
    @PostMapping("historyScanVo/{goPage}/{pageSize}")
    public Pager<List<HistoryScanVo>> historyScanVo(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody HistoryScanVo historyScanVo) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, dashboardService.historyScanVo(historyScanVo));
    }
}
