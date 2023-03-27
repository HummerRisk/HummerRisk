package com.hummer.k8s.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.sbom.DownloadRequest;
import com.hummer.common.core.domain.request.sbom.SbomRequest;
import com.hummer.common.core.domain.request.sbom.SbomVersionRequest;
import com.hummer.common.core.domain.request.sbom.SettingVersionRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.k8s.service.SbomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "SBOM管理")
@RestController
@RequestMapping(value = "sbom")
public class SbomController {

    @Autowired
    private SbomService sbomService;

    //@I18n
    @ApiOperation(value = "SBOM项目列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<SbomDTO>> sbomList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody SbomRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, sbomService.sbomList(request));
    }

    //@I18n
    @ApiOperation(value = "添加SBOM项目")
    @PostMapping(value = "addSbom")
    public Sbom addSbom(@RequestBody Sbom request) throws Exception {
        return sbomService.addSbom(request);
    }

    //@I18n
    @ApiOperation(value = "修改SBOM项目")
    @PostMapping(value = "updateSbom")
    public Sbom updateSbom(@RequestBody Sbom request) throws Exception {
        return sbomService.updateSbom(request);
    }

    @ApiOperation(value = "删除SBOM项目")
    @GetMapping("deleteSbom/{id}")
    public void deleteSbom(@PathVariable String id) throws Exception {
        sbomService.deleteSbom(id);
    }

    @ApiOperation(value = "执行SBOM检测")
    @GetMapping("scan/{id}")
    public void scan(@PathVariable String id) throws Exception {
        sbomService.scan(id);
    }

    //@I18n
    @ApiOperation(value = "SBOM项目版本列表")
    @PostMapping("sbomVersionList/{goPage}/{pageSize}")
    public Pager<List<SbomVersion>> sbomVersionList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody SbomVersionRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, sbomService.sbomVersionList(request));
    }

    //@I18n
    @ApiOperation(value = "添加SBOM项目版本")
    @PostMapping(value = "addSbomVersion")
    public SbomVersion addSbomVersion(@RequestBody SbomVersion request) throws Exception {
        return sbomService.addSbomVersion(request);
    }

    //@I18n
    @ApiOperation(value = "修改SBOM项目版本")
    @PostMapping(value = "updateSbomVersion")
    public SbomVersion updateSbomVersion(@RequestBody SbomVersion request) throws Exception {
        return sbomService.updateSbomVersion(request);
    }

    @ApiOperation(value = "删除SBOM项目版本")
    @GetMapping("deleteSbomVersion/{id}")
    public void deleteSbomVersion(@PathVariable String id) throws Exception {
        sbomService.deleteSbomVersion(id);
    }

    //@I18n
    @ApiOperation(value = "项目版本检测配置")
    @PostMapping(value = "settingVersion")
    public void settingVersion(@RequestBody SettingVersionRequest request) throws Exception {
        sbomService.settingVersion(request);
    }

    //@I18n
    @ApiOperation(value = "项目应用列表")
    @PostMapping(value = "applications")
    public List<ApplicationDTO> applications(@RequestBody SbomRequest request) throws Exception {
        return sbomService.applications(request);
    }

    //@I18n
    @ApiOperation(value = "所有已绑定项目的源码的检测结果")
    @GetMapping("codeResult/{sbomVersionId}")
    public List<CodeResultDTO> codeResult(@PathVariable String sbomVersionId) {
        return sbomService.codeResult(sbomVersionId);
    }

    //@I18n
    @ApiOperation(value = "所有已绑定项目的镜像的检测结果")
    @GetMapping("imageResult/{sbomVersionId}")
    public List<ImageResultDTO> imageResult(@PathVariable String sbomVersionId) throws Exception {
        return sbomService.imageResult(sbomVersionId);
    }

    //@I18n
    @ApiOperation(value = "所有已绑定项目的文件系统的检测结果")
    @GetMapping("fsResult/{sbomVersionId}")
    public List<FsResultDTO> fsResult(@PathVariable String sbomVersionId) throws Exception {
        return sbomService.fsResult(sbomVersionId);
    }

    //@I18n
    @ApiOperation(value = "所有已绑定项目的源码的历史检测结果")
    @GetMapping("historyCodeResult/{sbomVersionId}")
    public List<HistoryCodeResult> historyCodeResult(@PathVariable String sbomVersionId) {
        return sbomService.historyCodeResult(sbomVersionId);
    }

    //@I18n
    @ApiOperation(value = "所有已绑定项目的镜像的历史检测结果")
    @GetMapping("historyImageResult/{sbomVersionId}")
    public List<HistoryImageResultDTO> historyImageResult(@PathVariable String sbomVersionId) throws Exception {
        return sbomService.historyImageResult(sbomVersionId);
    }

    //@I18n
    @ApiOperation(value = "所有已绑定项目的文件系统的历史检测结果")
    @GetMapping("historyFsResult/{sbomVersionId}")
    public List<HistoryFsResultDTO> historyFsResult(@PathVariable String sbomVersionId) throws Exception {
        return sbomService.historyFsResult(sbomVersionId);
    }

    //@I18n
    @ApiOperation(value = "源码检测历史日志")
    @GetMapping(value = "codeLog/{resultId}")
    public List<CodeResultLogWithBLOBs> getCodeResultLog(@PathVariable String resultId) {
        return sbomService.getCodeResultLog(resultId);
    }

    //@I18n
    @ApiOperation(value = "源码检测结果详情")
    @GetMapping(value = "getCodeResult/{resultId}")
    public HistoryCodeResult getCodeResult(@PathVariable String resultId) {
        return sbomService.getCodeResult(resultId);
    }

    //@I18n
    @ApiOperation(value = "镜像检测历史日志")
    @GetMapping(value = "imageLog/{resultId}")
    public List<ImageResultLogWithBLOBs> getImageResultLog(@PathVariable String resultId) {
        return sbomService.getImageResultLog(resultId);
    }

    //@I18n
    @ApiOperation(value = "源码风险数据信息")
    @GetMapping("codeMetricChart/{resultId}")
    public MetricChartDTO codeMetricChart(@PathVariable String resultId) {
        return sbomService.codeMetricChart(resultId);
    }

    //@I18n
    @ApiOperation(value = "镜像风险数据信息")
    @GetMapping("imageMetricChart/{resultId}")
    public MetricChartDTO imageMetricChart(@PathVariable String resultId) {
        return sbomService.imageMetricChart(resultId);
    }

    //@I18n
    @ApiOperation(value = "文件系统风险数据信息")
    @GetMapping("fsMetricChart/{resultId}")
    public MetricChartDTO fsMetricChart(@PathVariable String resultId) {
        return sbomService.fsMetricChart(resultId);
    }

    //@I18n
    @ApiOperation(value = "所有SBOM项目")
    @PostMapping("allSbomList")
    public List<SbomDTO> allSbomList(@RequestBody SbomRequest request) {
        return sbomService.sbomList(request);
    }

    //@I18n
    @ApiOperation(value = "所有SBOM项目版本")
    @PostMapping("allSbomVersionList")
    public List<SbomVersion> allSbomVersionList(@RequestBody SbomVersionRequest request) {
        return sbomService.sbomVersionList(request);
    }

    @ApiOperation(value = "下载SBOM检测报告")
    @PostMapping("download")
    public String download(@RequestBody DownloadRequest request) throws Exception {
        return sbomService.download(request);
    }

}
