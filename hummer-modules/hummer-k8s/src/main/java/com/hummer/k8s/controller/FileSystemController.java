package com.hummer.k8s.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.fs.FsRequest;
import com.hummer.common.core.domain.request.fs.FsResultItemRequest;
import com.hummer.common.core.domain.request.fs.FsResultRequest;
import com.hummer.common.core.domain.request.fs.FsRuleRequest;
import com.hummer.common.core.dto.FsDTO;
import com.hummer.common.core.dto.FsResultDTO;
import com.hummer.common.core.dto.FsRuleDTO;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import com.hummer.k8s.service.FileSystemService;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "文件系统")
@RestController
@RequestMapping(value = "fs")
public class FileSystemController {

    @Autowired
    private FileSystemService fileSystemService;
    @Autowired
    private TokenService tokenService;

    @I18n
    @Operation(summary = "文件系统列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<FsDTO>> codeList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody FsRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, fileSystemService.fsList(request));
    }

    @I18n
    @Operation(summary = "所有文件系统")
    @GetMapping("allList")
    public List<FileSystem> allList() {
        return fileSystemService.allList();
    }

    @I18n
    @Operation(summary = "所有已绑定文件系统")
    @GetMapping("allBindList/{sbomVersionId}")
    public List<FileSystem> allBindList(@PathVariable String sbomVersionId) {
        return fileSystemService.allBindList(sbomVersionId);
    }

    @I18n
    @Operation(summary = "所有未绑定文件系统")
    @GetMapping("unBindList")
    public List<FileSystem> unBindList() {
        return fileSystemService.unBindList();
    }

    @I18n
    @Operation(summary = "添加文件系统")
    @PostMapping(value = "addFs", consumes = {"multipart/form-data"})
    public FileSystem addFs(@RequestPart(value = "tarFile", required = false) MultipartFile multipartFile,
                            @RequestPart("request") FileSystem request) throws Exception {
        return fileSystemService.addFs(multipartFile, request, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "修改文件系统")
    @PostMapping(value = "updateFs", consumes = {"multipart/form-data"})
    public FileSystem updateFs(@RequestPart(value = "tarFile", required = false) MultipartFile multipartFile,
                               @RequestPart("request") FileSystem request) throws Exception {
        return fileSystemService.updateFs(multipartFile, request, tokenService.getLoginUser());
    }

    @Operation(summary = "删除文件系统")
    @GetMapping("deleteFs/{id}")
    public void deleteFs(@PathVariable String id) throws Exception {
        fileSystemService.deleteFs(id, tokenService.getLoginUser());
    }

    @Operation(summary = "批量校验文件系统")
    @PostMapping("validate")
    public Boolean validate(@RequestBody List<String> selectIds) {
        return fileSystemService.validate(selectIds);
    }

    @Operation(summary = "校验文件系统")
    @PostMapping("validate/{id}")
    public Boolean validate(@PathVariable String id) throws IOException, ApiException {
        return fileSystemService.validate(id);
    }

    @I18n
    @Operation(summary = "文件系统检测规则列表")
    @PostMapping(value = "ruleList/{goPage}/{pageSize}")
    public Pager<List<FsRuleDTO>> ruleList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody FsRuleRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, fileSystemService.ruleList(request));
    }

    @Operation(summary = "添加文件系统检测规则")
    @PostMapping(value = "addFsRule")
    public int addFsRule(@RequestBody FsRuleRequest request) throws Exception {
        return fileSystemService.addFsRule(request, tokenService.getLoginUser());
    }

    @Operation(summary = "修改文件系统检测规则")
    @PostMapping(value = "updateFsRule")
    public int updateFsRule(@RequestBody FsRuleRequest request) throws Exception {
        return fileSystemService.updateFsRule(request, tokenService.getLoginUser());
    }

    @Operation(summary = "删除文件系统检测规则")
    @GetMapping(value = "deleteFsRule/{id}")
    public void deleteFsRule(@PathVariable String id) throws Exception  {
        fileSystemService.deleteFsRule(id, tokenService.getLoginUser());
    }

    @Operation(summary = "文件系统检测规则启用")
    @PostMapping(value = "changeStatus")
    public int changeStatus(@RequestBody FileSystemRule rule) throws Exception {
        return fileSystemService.changeStatus(rule);
    }

    @Operation(summary = "文件系统检测规则")
    @GetMapping("scan/{id}")
    public void scan(@PathVariable String id) throws Exception {
        fileSystemService.scan(id, tokenService.getLoginUser());
    }

    @Operation(summary = "重新检测文件系统规则")
    @GetMapping("reScan/{id}")
    public void reScan(@PathVariable String id) throws Exception {
        fileSystemService.reScan(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "文件系统检测结果列表")
    @PostMapping(value = "resultList/{goPage}/{pageSize}")
    public Pager<List<FsResultDTO>> resultList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody FsResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, fileSystemService.resultList(request));
    }

    @I18n
    @Operation(summary = "文件系统检测结果详情")
    @GetMapping(value = "getFsResult/{resultId}")
    public FsResultDTO getFsResult(@PathVariable String resultId) {
        return fileSystemService.getFsResult(resultId);
    }

    @I18n
    @Operation(summary = "文件系统检测日志")
    @GetMapping(value = "log/{resultId}")
    public List<FileSystemResultLog> getFsResultLog (@PathVariable String resultId) {
        return fileSystemService.getFsResultLog(resultId);
    }

    @Operation(summary = "删除文件系统检测记录")
    @GetMapping("deleteFsResult/{id}")
    public void deleteFsResult(@PathVariable String id) throws Exception {
        fileSystemService.deleteFsResult(id, tokenService.getLoginUser());
    }

    @I18n
    @ApiIgnore
    @PostMapping("resultItemList/{goPage}/{pageSize}")
    public Pager<List<FileSystemResultItemWithBLOBs>> resultItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody FileSystemResultItem request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, fileSystemService.resultItemList(request));
    }

    @I18n
    @Operation(summary = "检测结果详情")
    @PostMapping("resultItemListBySearch/{goPage}/{pageSize}")
    public Pager<List<FileSystemResultItemWithBLOBs>> resultItemListBySearch(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody FsResultItemRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, fileSystemService.resultItemListBySearch(request));
    }

    @I18n
    @Operation(summary = "文件系统概览TOP统计")
    @PostMapping("topInfo")
    public Map<String, Object> topInfo(@RequestBody Map<String, Object> params) {
        return fileSystemService.topInfo(params);
    }

    @I18n
    @Operation(summary = "文件系统项目统计")
    @GetMapping("projectChart")
    public List<Map<String, Object>> projectChart() {
        return fileSystemService.projectChart();
    }

    @I18n
    @Operation(summary = "文件系统风险统计")
    @GetMapping("severityChart")
    public List<Map<String, Object>> severityChart() {
        return fileSystemService.severityChart();
    }

    @Operation(summary = "下载检测报告")
    @PostMapping("download")
    public String download(@RequestBody Map<String, Object> map) throws Exception {
        return fileSystemService.download(map);
    }

    @I18n
    @Operation(summary = "检测结果历史详情")
    @PostMapping("historyResultItemList")
    public List<FileSystemResultItemWithBLOBs> historyResultItemList(@RequestBody FileSystemResultItem request) {
        return fileSystemService.historyResultItemList(request);
    }
}
