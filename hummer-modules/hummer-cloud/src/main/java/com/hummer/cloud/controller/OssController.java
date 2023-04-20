package com.hummer.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.cloud.oss.controller.request.OssBucketRequest;
import com.hummer.cloud.oss.controller.request.OssRequest;
import com.hummer.cloud.oss.dto.*;
import com.hummer.cloud.service.OssService;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.cloudTask.ManualRequest;
import com.hummer.common.core.domain.request.excel.ExcelExportRequest;
import com.hummer.common.core.domain.request.resource.ResourceRequest;
import com.hummer.common.core.domain.request.rule.RuleGroupRequest;
import com.hummer.common.core.dto.ResourceDTO;
import com.hummer.common.core.dto.RuleGroupDTO;
import com.hummer.common.core.dto.ValidateDTO;
import com.hummer.common.core.handler.ResultHolder;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Hidden;

import java.io.BufferedOutputStream;
import java.io.FilterInputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Tag(name = "对象存储")
@RestController
@RequestMapping(value = "oss")
public class OssController {
    @Autowired
    private OssService ossService;
    @Autowired
    private TokenService tokenService;

    @I18n
    @Operation(summary = "对象存储账号列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<OssDTO>> ossList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody OssRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ossService.ossList(request));
    }

    @I18n
    @Operation(summary = "所有对象存储账号")
    @GetMapping("allList")
    public List<OssWithBLOBs> allList() {
        return ossService.allList();
    }

    @I18n
    @Operation(summary = "云账号列表")
    @GetMapping("accounts")
    public List<AccountWithBLOBs> getCloudAccountList() {
        return ossService.getCloudAccountList();
    }

    @I18n
    @Operation(summary = "IAM策略信息")
    @GetMapping("iam/strategy/{accountId}")
    public String strategy(@PathVariable String accountId) throws Exception {
        return ossService.strategy(accountId);
    }

    @Operation(summary = "批量校验对象存储账号")
    @PostMapping("validate")
    public Boolean validate(@RequestBody List<String> selectIds) {
        return ossService.validate(selectIds);
    }

    @Operation(summary = "校验对象存储账号")
    @PostMapping("validate/{id}")
    public ValidateDTO validate(@PathVariable String id) {
        return ossService.validate(id);
    }

    @Operation(summary = "云账号详情")
    @GetMapping("changeAccount/{accountId}")
    public String getCredential(@PathVariable String accountId) {
        return ossService.getCredential(accountId);
    }

    @I18n
    @Operation(summary = "添加对象存储")
    @PostMapping("add")
    public OssWithBLOBs addOss(@RequestBody OssWithBLOBs request) throws Exception {
        return ossService.addOss(request, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "更新对象存储")
    @PostMapping("update")
    public OssWithBLOBs editOss(@RequestBody OssWithBLOBs request) throws Exception {
        return ossService.editOss(request, tokenService.getLoginUser());
    }

    @Operation(summary = "删除对象存储")
    @GetMapping(value = "delete/{ossId}")
    public void deleteOss(@PathVariable String ossId) {
        ossService.deleteOss(ossId);
    }

    @I18n
    @Operation(summary = "同步对象存储")
    @GetMapping("batch/sync/{id}")
    public void sync(@PathVariable String id) throws Exception {
        ossService.batch(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "对象存储同步日志")
    @GetMapping(value = "log/{ossId}")
    public List<OssLogWithBLOBs> getLogList(@PathVariable String ossId) {
        return ossService.getLogList(ossId);
    }

    @I18n
    @Operation(summary = "对象存储桶列表")
    @PostMapping("bucketList/{goPage}/{pageSize}")
    public Pager<List<OssBucketDTO>> ossBucketList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody OssBucketRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ossService.ossBucketList(request));
    }

    @I18n
    @Operation(summary = "文件目录列表")
    @GetMapping("objects/{bucketId}")
    public List<BucketObjectDTO> getObjects(@PathVariable String bucketId) throws Exception {
        return ossService.getObjects(bucketId, null);
    }

    @I18n
    @Operation(summary = "文件列表")
    @PostMapping("objects/{bucketId}")
    public List<BucketObjectDTO> getObjects(@PathVariable String bucketId, @RequestBody Map map) throws Exception {
        String path = map.get("path").toString();
        return ossService.getObjects(bucketId, path);
    }

    @I18n
    @Operation(summary = "下载文件")
    @PostMapping("downloadObject/{bucketId}")
    public void downloadObject(@PathVariable String bucketId, @RequestBody Map map, HttpServletResponse response) throws Exception {
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        String objectId = map.get("objectId").toString();
        FilterInputStream in = ossService.downloadObject(bucketId, objectId);
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(objectId, "utf-8"));
        byte[] car = new byte[102400];
        int L = 0;
        while ((L = in.read(car)) != -1) {
            out.write(car, 0, L);
        }
        if (out != null) {
            out.flush();
            out.close();
        }
        if (in != null) {
            in.close();
        }
    }

    @I18n
    @Operation(summary = "创建存储桶")
    @PostMapping("create")
    public void create(@RequestBody OssBucket bucket) throws Exception {
        ossService.create(bucket, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "批量删除存储桶")
    @PostMapping("deleteByBatch")
    public ResultHolder deleteByBatch(@RequestBody List<String> ids) {
        return ossService.delete(ids, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "删除存储桶")
    @GetMapping("deleteBucket/{bucketId}")
    public ResultHolder deleteBucket(@PathVariable String bucketId) {
        return ossService.delete(Arrays.asList(bucketId), tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "创建目录")
    @PostMapping("createDir/{bucketId}")
    public void createDir(@PathVariable String bucketId, @RequestBody Map map) throws Exception{
        ossService.createDir(bucketId, map.get("dir").toString());
    }

    @I18n
    @Operation(summary = "删除对象")
    @PostMapping("deleteObject/{bucketId}")
    public void deleteObject(@PathVariable String bucketId, @RequestBody Map map) throws Exception{
        ossService.deleteObject(bucketId, map.get("objectId").toString());
    }

    @I18n
    @Operation(summary = "批量删除对象")
    @PostMapping("deleteObjects/{bucketId}")
    public void deleteObjects(@PathVariable String bucketId, @RequestBody List<String> objectIds) throws Exception{
        ossService.deleteObjects(bucketId, objectIds);
    }

    @I18n
    @Hidden
    @GetMapping("support/bucketAddforOssId/{ossId}")
    public BucketKeyValueItem bucketAddforOssId(@PathVariable String ossId) throws Exception {
        return ossService.bucketAddforOssId(ossId);
    }

    @I18n
    @Operation(summary = "获取对象存储区域")
    @GetMapping("support/regions/{ossId}")
    public List<OssRegion> getRegions(@PathVariable String ossId) throws Exception {
        return ossService.getOssRegions(ossId);
    }

    @I18n
    @Operation(summary = "获取对象存储类型")
    @GetMapping("support/{ossId}/params/{type}")
    public List<KeyValueItem> getParams(@PathVariable String ossId, @PathVariable String type) throws Exception{
        return ossService.getParamList(ossId, type);
    }

    @I18n
    @Hidden
    @GetMapping("groups/{pluginId}")
    public List<RuleGroup> groups(@PathVariable String pluginId) {
        return ossService.groups(pluginId);
    }

    @I18n
    @Operation(summary = "合规报告规则组列表")
    @PostMapping(value = "ruleGroup/list/{goPage}/{pageSize}")
    public Pager<List<RuleGroupDTO>> ruleGroupList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleGroupRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ossService.ruleGroupList(request));
    }

    @Operation(summary = "导出规则组检测报告")
    @PostMapping("groupExport")
    public ResponseEntity<byte[]> exportGroupReport(@RequestBody ExcelExportRequest request) throws Exception {
        byte[] bytes = ossService.exportGroupReport(request, tokenService.getLoginUser());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "对象存储不合规资源检测报告.xlsx");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .body(bytes);
    }

    @I18n
    @Hidden
    @PostMapping("manual/list/{goPage}/{pageSize}")
    public Pager<List<CloudTask>> getManualTasks(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ManualRequest request) throws Exception {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        request.setType("manual");
        return PageUtils.setPageInfo(page, ossService.selectManualTasks(request));
    }

    @I18n
    @Hidden
    @PostMapping("resource/list/{goPage}/{pageSize}")
    public Pager<List<ResourceDTO>> resourceList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ResourceRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ossService.resourceList(request));
    }

    @I18n
    @Operation(summary = "上传文件")
    @PostMapping(value = "uploadFile/{bucketId}", consumes = {"multipart/form-data"})
    public void uploadFile(@PathVariable String bucketId, @RequestPart(value = "request") Path request, @RequestPart(value = "objectFile", required = false) MultipartFile objectFile) throws Exception {
        String path = request.getPath();
        String objectId;
        if(path.equalsIgnoreCase("/")){
            objectId = objectFile.getOriginalFilename();
        }else {
            objectId = path.endsWith("/") ? path + objectFile.getOriginalFilename() : path + "/" + objectFile.getOriginalFilename();
        }
        ossService.uploadObject(bucketId , objectId, objectFile, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "对象存储概览TOP统计")
    @PostMapping("topInfo")
    public Map<String, Object> topInfo(@RequestBody Map<String, Object> params) {
        return ossService.topInfo(params);
    }

    @I18n
    @Operation(summary = "对象存储统计")
    @GetMapping("ossChart")
    public List<Map<String, Object>> ossChart() {
        return ossService.ossChart();
    }

    @Operation(summary = "存储桶统计")
    @GetMapping("bucketChart")
    public List<Map<String, Object>> bucketChart() {
        return ossService.bucketChart();
    }

    @I18n
    @Operation(summary = "对象存储风险统计")
    @GetMapping("severityChart")
    public List<Map<String, Object>> severityChart() {
        return ossService.severityChart();
    }

    @Operation(summary = "批量删除对象存储账号")
    @PostMapping("deleteAccounts")
    public void deleteAccounts(@RequestBody List<String> selectIds) throws Exception {
        ossService.deleteAccounts(selectIds);
    }

}
