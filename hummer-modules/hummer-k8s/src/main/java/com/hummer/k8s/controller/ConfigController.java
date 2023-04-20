package com.hummer.k8s.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.domain.CloudNativeConfig;
import com.hummer.common.core.domain.CloudNativeConfigResultItem;
import com.hummer.common.core.domain.CloudNativeConfigResultItemWithBLOBs;
import com.hummer.common.core.domain.CloudNativeConfigResultLogWithBLOBs;
import com.hummer.common.core.domain.request.config.ConfigRequest;
import com.hummer.common.core.domain.request.config.ConfigResultItemRequest;
import com.hummer.common.core.domain.request.config.ConfigResultRequest;
import com.hummer.common.core.dto.CloudNativeConfigDTO;
import com.hummer.common.core.dto.CloudNativeConfigResultDTO;
import com.hummer.common.core.dto.MetricChartDTO;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import com.hummer.k8s.service.ConfigService;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Hidden;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "Config")
@RestController
@RequestMapping(value = "config")
public class ConfigController {
    @Autowired
    private ConfigService configService;
    @Autowired
    private TokenService tokenService;

    @I18n
    @Operation(summary = "云原生部署配置列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<CloudNativeConfigDTO>> getCloudNativeConfigList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody ConfigRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, configService.getCloudNativeConfigList(request));
    }

    @Operation(summary = "批量校验云原生部署配置")
    @PostMapping("validate")
    public Boolean validate(@RequestBody List<String> selectIds) {
        return configService.validate(selectIds);
    }

    @Operation(summary = "校验云原生部署配置")
    @PostMapping("validate/{id}")
    public Boolean validate(@PathVariable String id) throws IOException, ApiException {
        return configService.validate(id);
    }

    @I18n
    @Operation(summary = "添加云原生部署配置")
    @PostMapping("add")
    public CloudNativeConfig addCloudNative(@RequestBody CloudNativeConfig request) {
        return configService.addCloudNativeConfig(request, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "更新云原生部署配置")
    @PostMapping("update")
    public CloudNativeConfig editCloudNativeConfig(@RequestBody CloudNativeConfig request) throws Exception {
        return configService.editCloudNativeConfig(request, tokenService.getLoginUser());
    }

    @Operation(summary = "删除云原生部署配置")
    @GetMapping(value = "delete/{accountId}")
    public void deleteAccount(@PathVariable String accountId) throws Exception {
        configService.delete(accountId, tokenService.getLoginUser());
    }

    @Operation(summary = "上传YAML文件")
    @PostMapping(value = "uploadYaml", consumes = {"multipart/form-data"})
    public String uploadYaml(@RequestPart(value = "file", required = true) MultipartFile file) throws Exception {
        return configService.uploadYaml(file);
    }

    @I18n
    @Operation(summary = "云原生部署配置检测")
    @GetMapping("scan/{id}")
    public void scan(@PathVariable String id) throws Exception {
        configService.scan(id, tokenService.getLoginUser());
    }

    @Operation(summary = "重新云原生部署配置检测")
    @GetMapping("reScan/{id}")
    public void reScan(@PathVariable String id) throws Exception {
        configService.reScan(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "云原生部署配置检测结果列表")
    @PostMapping(value = "resultList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeConfigResultDTO>> resultList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ConfigResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, configService.resultList(request));
    }

    @I18n
    @Hidden
    @PostMapping("resultItemList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeConfigResultItem>> resultItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ConfigResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, configService.resultItemList(request));
    }

    @I18n
    @Operation(summary = "云原生部署配置检测结果详情列表")
    @PostMapping("resultItemListBySearch/{goPage}/{pageSize}")
    public Pager<List<CloudNativeConfigResultItemWithBLOBs>> resultItemListBySearch(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ConfigResultItemRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, configService.resultItemListBySearch(request));
    }

    @I18n
    @Operation(summary = "云原生部署配置检测结果详情")
    @GetMapping(value = "getCloudNativeConfigResult/{resultId}")
    public CloudNativeConfigResultDTO getCloudNativeConfigResult(@PathVariable String resultId) {
        return configService.getCloudNativeConfigResult(resultId);
    }

    @I18n
    @Operation(summary = "云原生部署配置检测日志")
    @GetMapping(value = "log/{resultId}")
    public List<CloudNativeConfigResultLogWithBLOBs> getCloudNativeConfigResultLog(@PathVariable String resultId) {
        return configService.getCloudNativeConfigResultLog(resultId);
    }

    @Operation(summary = "删除云原生部署配置检测记录")
    @GetMapping("deleteCloudNativeConfigResult/{id}")
    public void deleteCloudNativeConfigResult(@PathVariable String id) throws Exception {
        configService.deleteCloudNativeConfigResult(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "风险数据信息")
    @GetMapping("metricChart/{resultId}")
    public MetricChartDTO metricChart(@PathVariable String resultId) {
        return configService.metricChart(resultId);
    }

    @Operation(summary = "下载检测报告")
    @PostMapping("download")
    public String download(@RequestBody Map<String, Object> map) throws Exception {
        return configService.download(map);
    }

    @I18n
    @Operation(summary = "概览TOP统计")
    @PostMapping("topInfo")
    public Map<String, Object> topInfo(@RequestBody Map<String, Object> params) {
        return configService.topInfo(params);
    }

    @I18n
    @Operation(summary = "config 统计")
    @GetMapping("configChart")
    public List<Map<String, Object>> configChart() {
        return configService.configChart();
    }

    @I18n
    @Operation(summary = "风险统计")
    @GetMapping("severityChart")
    public List<Map<String, Object>> severityChart() {
        return configService.severityChart();
    }

    @I18n
    @Operation(summary = "所有部署配置")
    @GetMapping("allList")
    public List<CloudNativeConfig> allList() {
        return configService.allList();
    }



    @I18n
    @Operation(summary = "检测结果历史详情")
    @PostMapping("historyResultItemList")
    public List<CloudNativeConfigResultItemWithBLOBs> historyResultItemList(@RequestBody CloudNativeConfigResultItem request) {
        return configService.historyResultItemList(request);
    }

    @Operation(summary = "批量删除部署配置")
    @PostMapping("deleteConfigs")
    public void deleteConfigs(@RequestBody List<String> selectIds) throws Exception {
        configService.deleteConfigs(selectIds, tokenService.getLoginUser());
    }

}
