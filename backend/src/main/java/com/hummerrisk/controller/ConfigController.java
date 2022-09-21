package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.CloudNativeConfig;
import com.hummerrisk.base.domain.CloudNativeConfigResult;
import com.hummerrisk.base.domain.CloudNativeConfigResultItem;
import com.hummerrisk.base.domain.CloudNativeConfigResultLog;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.controller.request.config.ConfigRequest;
import com.hummerrisk.controller.request.config.ConfigResultRequest;
import com.hummerrisk.dto.CloudNativeConfigDTO;
import com.hummerrisk.dto.CloudNativeConfigResultDTO;
import com.hummerrisk.dto.MetricChartDTO;
import com.hummerrisk.service.CloudNativeConfigService;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Api(tags = "Config")
@RestController
@RequestMapping(value = "config")
public class ConfigController {
    @Resource
    private CloudNativeConfigService cloudNativeConfigService;

    @I18n
    @ApiOperation(value = "云原生部署配置列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<CloudNativeConfigDTO>> getCloudNativeConfigList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody ConfigRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudNativeConfigService.getCloudNativeConfigList(request));
    }

    @ApiOperation(value = "批量校验云原生部署配置")
    @PostMapping("validate")
    public Boolean validate(@RequestBody List<String> selectIds) {
        return cloudNativeConfigService.validate(selectIds);
    }

    @ApiOperation(value = "校验云原生部署配置")
    @PostMapping("validate/{id}")
    public Boolean validate(@PathVariable String id) throws IOException, ApiException {
        return cloudNativeConfigService.validate(id);
    }

    @I18n
    @ApiOperation(value = "添加云原生部署配置")
    @PostMapping("add")
    public CloudNativeConfig addCloudNative(@RequestBody CloudNativeConfig request) {
        return cloudNativeConfigService.addCloudNativeConfig(request);
    }

    @I18n
    @ApiOperation(value = "更新云原生部署配置")
    @PostMapping("update")
    public CloudNativeConfig editCloudNativeConfig(@RequestBody CloudNativeConfig request) throws Exception {
        return cloudNativeConfigService.editCloudNativeConfig(request);
    }

    @ApiOperation(value = "删除云原生部署配置")
    @GetMapping(value = "delete/{accountId}")
    public void deleteAccount(@PathVariable String accountId) {
        cloudNativeConfigService.delete(accountId);
    }

    @ApiOperation(value = "上传YAML文件")
    @PostMapping(value = "uploadYaml", consumes = {"multipart/form-data"})
    public String uploadYaml(@RequestPart(value = "file", required = true) MultipartFile file) throws Exception {
        return cloudNativeConfigService.uploadYaml(file);
    }

    @I18n
    @ApiOperation(value = "云原生部署配置检测")
    @GetMapping("scan/{id}")
    public void scan(@PathVariable String id) throws Exception {
        cloudNativeConfigService.scan(id);
    }

    @ApiOperation(value = "重新云原生部署配置检测")
    @GetMapping("reScan/{id}")
    public void reScan(@PathVariable String id) throws Exception {
        cloudNativeConfigService.reScan(id);
    }

    @I18n
    @ApiOperation(value = "云原生部署配置检测结果列表")
    @PostMapping(value = "resultList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeConfigResultDTO>> resultList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ConfigResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudNativeConfigService.resultList(request));
    }

    @I18n
    @ApiOperation(value = "云原生部署配置检测结果详情列表")
    @PostMapping("resultItemList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeConfigResultItem>> resultItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ConfigResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudNativeConfigService.resultItemList(request));
    }

    @I18n
    @ApiOperation(value = "云原生部署配置检测结果详情")
    @GetMapping(value = "getCloudNativeConfigResult/{resultId}")
    public CloudNativeConfigResultDTO getCloudNativeConfigResult(@PathVariable String resultId) {
        return cloudNativeConfigService.getCloudNativeConfigResult(resultId);
    }

    @I18n
    @ApiOperation(value = "云原生部署配置检测日志")
    @GetMapping(value = "log/{resultId}")
    public List<CloudNativeConfigResultLog> getCloudNativeConfigResultLog(@PathVariable String resultId) {
        return cloudNativeConfigService.getCloudNativeConfigResultLog(resultId);
    }

    @ApiOperation(value = "删除云原生部署配置检测记录")
    @GetMapping("deleteCloudNativeConfigResult/{id}")
    public void deleteCloudNativeConfigResult(@PathVariable String id) throws Exception {
        cloudNativeConfigService.deleteCloudNativeConfigResult(id);
    }

    @I18n
    @ApiOperation(value = "风险数据信息")
    @GetMapping("metricChart/{resultId}")
    public MetricChartDTO metricChart(@PathVariable String resultId) {
        return cloudNativeConfigService.metricChart(resultId);
    }

}
