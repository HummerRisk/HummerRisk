package com.hummer.system.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.domain.request.chart.ChartData;
import com.hummer.common.core.domain.request.dashboard.*;
import com.hummer.common.core.dto.ChartDTO;
import com.hummer.common.core.dto.HistoryScanDTO;
import com.hummer.common.core.dto.TopInfoDTO;
import com.hummer.common.core.dto.TopScanDTO;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.system.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "首页")
@RestController
@RequestMapping("dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @I18n
    @Operation(summary = "不合规统计")
    @PostMapping("point/target/{goPage}/{pageSize}")
    public Pager<List<DashboardTarget>> target(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, dashboardService.target(params));
    }

    @I18n
    @Operation(summary = "风险等级统计")
    @PostMapping("point/severity")
    public List<Map<String, Object>> severityList(@RequestBody Map<String, Object> params) {
        return dashboardService.severityList(params);
    }

    @I18n
    @Operation(summary = "分组统计")
    @PostMapping("distribution")
    public List<ChartData> getDistribution(@RequestBody Map<String, Object> params) {
        return dashboardService.vulnDistribution(params);
    }

    @I18n
    @Operation(summary = "合计统计")
    @PostMapping("totalPolicy")
    public List<Map<String, Object>> totalPolicy(@RequestBody Map<String, Object> params) {
        return dashboardService.totalPolicy(params);
    }

    @I18n
    @Operation(summary = "云资源历史记录")
    @PostMapping("history/{goPage}/{pageSize}")
    public Pager<List<HistoryScanDTO>> history(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, dashboardService.history(params));
    }

    @I18n
    @Operation(summary = "首页TOP统计")
    @PostMapping("topInfo")
    public TopInfoDTO topInfo(@RequestBody Map<String, Object> params) {
        return dashboardService.topInfo(params);
    }

    @I18n
    @Operation(summary = "首页TOP检测统计")
    @GetMapping("topScanInfo")
    public List<TopScanDTO> topScanInfo() {
        return dashboardService.topScanInfo();
    }

    @I18n
    @Operation(summary = "首页镜像检测统计")
    @PostMapping("imageChart")
    public ChartDTO imageChart(@RequestBody Map<String, Object> params) {
        return dashboardService.imageChart(params);
    }

    @I18n
    @Operation(summary = "首页源码检测统计")
    @PostMapping("codeChart")
    public ChartDTO codeChart(@RequestBody Map<String, Object> params) {
        return dashboardService.codeChart(params);
    }

    @I18n
    @Operation(summary = "首页云原生检测统计")
    @PostMapping("cloudNativeChart")
    public ChartDTO cloudNativeChart(@RequestBody Map<String, Object> params) {
        return dashboardService.cloudNativeChart(params);
    }

    @I18n
    @Operation(summary = "首页云原生检测统计")
    @PostMapping("configChart")
    public ChartDTO configChart(@RequestBody Map<String, Object> params) {
        return dashboardService.configChart(params);
    }

    @I18n
    @Operation(summary = "首页文件系统检测统计")
    @PostMapping("fsChart")
    public ChartDTO fsChart(@RequestBody Map<String, Object> params) {
        return dashboardService.fsChart(params);
    }

    @Operation(summary = "首页任务日历")
    @GetMapping("taskCalendar")
    public List<TaskCalendarVo> taskCalendar() {
        return dashboardService.taskCalendar();
    }

    @Operation(summary = "首页安全评分")
    @GetMapping("score")
    public Integer score() {
        return dashboardService.score();
    }

    @I18n
    @Operation(summary = "保存统计分析")
    @PostMapping("saveAnalysis")
    public void saveAnalysis(@RequestBody AnslysisVo anslysisVo) {
        dashboardService.saveAnalysis(anslysisVo);
    }

    @Operation(summary = "查询统计分析")
    @GetMapping("queryAnalysis")
    public AnslysisVo queryAnalysis() {
        return dashboardService.queryAnalysis();
    }

    @I18n
    @Operation(summary = "检测统计分析")
    @PostMapping("analysisChart")
    public ChartDTO analysisChart() {
        return dashboardService.analysisChart();
    }

    @I18n
    @Operation(summary = "检测统计分析")
    @PostMapping("historyScanVo/{goPage}/{pageSize}")
    public Pager<List<HistoryScanVo>> historyScanVo(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody HistoryScanVo historyScanVo) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, dashboardService.historyScanVo(historyScanVo));
    }

    @I18n
    @Operation(summary = "首页Cloud统计")
    @PostMapping("cloudInfo")
    public CloudInfo cloudInfo() {
        return dashboardService.cloudInfo();
    }

    @I18n
    @Operation(summary = "首页K8s统计")
    @PostMapping("k8sInfo")
    public CloudInfo k8sInfo() {
        return dashboardService.k8sInfo();
    }

    @I18n
    @Operation(summary = "首页Server统计")
    @PostMapping("serverInfo")
    public AssetsInfo serverInfo() {
        return dashboardService.serverInfo();
    }

    @I18n
    @Operation(summary = "首页Image统计")
    @PostMapping("imageInfo")
    public AssetsInfo imageInfo() {
        return dashboardService.imageInfo();
    }

    @I18n
    @Operation(summary = "首页Config统计")
    @PostMapping("configInfo")
    public AssetsInfo configInfo() {
        return dashboardService.configInfo();
    }

    @I18n
    @Operation(summary = "首页Code统计")
    @PostMapping("codeInfo")
    public AssetsInfo codeInfo() {
        return dashboardService.codeInfo();
    }

    @I18n
    @Operation(summary = "首页FileSystem统计")
    @PostMapping("fsInfo")
    public AssetsInfo fsInfo() {
        return dashboardService.fsInfo();
    }

}
