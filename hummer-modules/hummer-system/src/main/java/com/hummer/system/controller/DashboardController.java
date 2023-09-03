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
    @Operation(summary = "混合云不合规资产风险等级统计")
    @PostMapping("point/risk/list")
    public List<Map<String, Object>> riskList(@RequestBody Map<String, Object> params) {
        return dashboardService.riskList(params);
    }

    @I18n
    @Operation(summary = "分组统计")
    @PostMapping("distribution")
    public List<ChartData> getDistribution(@RequestBody Map<String, Object> params) {
        return dashboardService.vulnDistribution(params);
    }

    @I18n
    @Operation(summary = "合计统计")
    @PostMapping("total/policy")
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
    @PostMapping("top/info")
    public TopInfoDTO topInfo(@RequestBody Map<String, Object> params) {
        return dashboardService.topInfo(params);
    }

    @I18n
    @Operation(summary = "首页TOP检测统计")
    @GetMapping("top/scan/info")
    public List<TopScanDTO> topScanInfo() {
        return dashboardService.topScanInfo();
    }

    @I18n
    @Operation(summary = "首页镜像检测统计")
    @PostMapping("image/chart")
    public ChartDTO imageChart(@RequestBody Map<String, Object> params) {
        return dashboardService.imageChart(params);
    }

    @I18n
    @Operation(summary = "首页源码检测统计")
    @PostMapping("code/chart")
    public ChartDTO codeChart(@RequestBody Map<String, Object> params) {
        return dashboardService.codeChart(params);
    }

    @I18n
    @Operation(summary = "首页云原生检测统计")
    @PostMapping("cloudnative/chart")
    public ChartDTO cloudNativeChart(@RequestBody Map<String, Object> params) {
        return dashboardService.cloudNativeChart(params);
    }

    @I18n
    @Operation(summary = "首页云原生检测统计")
    @PostMapping("config/chart")
    public ChartDTO configChart(@RequestBody Map<String, Object> params) {
        return dashboardService.configChart(params);
    }

    @I18n
    @Operation(summary = "首页文件系统检测统计")
    @PostMapping("fs/chart")
    public ChartDTO fsChart(@RequestBody Map<String, Object> params) {
        return dashboardService.fsChart(params);
    }

    @Operation(summary = "首页任务日历")
    @GetMapping("task/calendar")
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
    @PostMapping("save/analysis")
    public void saveAnalysis(@RequestBody AnslysisVo anslysisVo) {
        dashboardService.saveAnalysis(anslysisVo);
    }

    @Operation(summary = "查询统计分析")
    @GetMapping("query/analysis")
    public AnslysisVo queryAnalysis() {
        return dashboardService.queryAnalysis();
    }

    @I18n
    @Operation(summary = "检测统计分析")
    @PostMapping("analysis/chart")
    public ChartDTO analysisChart() {
        return dashboardService.analysisChart();
    }

    @I18n
    @Operation(summary = "检测统计分析")
    @PostMapping("history/scan/vo/{goPage}/{pageSize}")
    public Pager<List<HistoryScanVo>> historyScanVo(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody HistoryScanVo historyScanVo) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, dashboardService.historyScanVo(historyScanVo));
    }

    @I18n
    @Operation(summary = "首页Cloud统计")
    @PostMapping("cloud/info")
    public CloudInfo cloudInfo() {
        return dashboardService.cloudInfo();
    }

    @I18n
    @Operation(summary = "首页K8s统计")
    @PostMapping("k8s/info")
    public CloudInfo k8sInfo() {
        return dashboardService.k8sInfo();
    }

    @I18n
    @Operation(summary = "首页Server统计")
    @PostMapping("server/info")
    public AssetsInfo serverInfo() {
        return dashboardService.serverInfo();
    }

    @I18n
    @Operation(summary = "首页Image统计")
    @PostMapping("image/info")
    public AssetsInfo imageInfo() {
        return dashboardService.imageInfo();
    }

    @I18n
    @Operation(summary = "首页Config统计")
    @PostMapping("config/info")
    public AssetsInfo configInfo() {
        return dashboardService.configInfo();
    }

    @I18n
    @Operation(summary = "首页Code统计")
    @PostMapping("code/info")
    public AssetsInfo codeInfo() {
        return dashboardService.codeInfo();
    }

    @I18n
    @Operation(summary = "首页FileSystem统计")
    @PostMapping("fs/info")
    public AssetsInfo fsInfo() {
        return dashboardService.fsInfo();
    }

    @I18n
    @Operation(summary = "主机不合规风险统计")
    @PostMapping("server/risk/chart")
    public List<Map<String, Object>> serverRiskChart(@RequestBody Map<String, Object> params) {
        return dashboardService.serverRiskChart(params);
    }

    @I18n
    @Operation(summary = "镜像检测风险统计")
    @PostMapping("image/risk/chart")
    public List<Map<String, Object>> imageRiskChart(@RequestBody Map<String, Object> params) {
        return dashboardService.imageRiskChart(params);
    }

    @I18n
    @Operation(summary = "K8s漏洞检测风险统计")
    @PostMapping("k8s/vuln/risk/chart")
    public List<Map<String, Object>> k8sVulnRiskChart(@RequestBody Map<String, Object> params) {
        return dashboardService.k8sVulnRiskChart(params);
    }

    @I18n
    @Operation(summary = "K8s配置审计风险统计")
    @PostMapping("k8s/config/risk/chart")
    public List<Map<String, Object>> k8sConfigRiskChart(@RequestBody Map<String, Object> params) {
        return dashboardService.k8sConfigRiskChart(params);
    }

    @I18n
    @Operation(summary = "K8s Benchmark 风险统计")
    @PostMapping("k8s/kubench/risk/chart")
    public List<Map<String, Object>> k8sKubenchRiskChart(@RequestBody Map<String, Object> params) {
        return dashboardService.k8sKubenchRiskChart(params);
    }

    @I18n
    @Operation(summary = "K8s合规检测风险统计")
    @PostMapping("k8s/scan/risk/chart")
    public List<Map<String, Object>> k8sScanRiskChart(@RequestBody Map<String, Object> params) {
        return dashboardService.k8sScanRiskChart(params);
    }

}
