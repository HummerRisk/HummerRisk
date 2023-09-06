package com.hummer.system.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.domain.request.code.CodeResultRequest;
import com.hummer.common.core.domain.request.config.ConfigResultRequest;
import com.hummer.common.core.domain.request.fs.FsResultRequest;
import com.hummer.common.core.domain.request.image.ImageResultRequest;
import com.hummer.common.core.domain.request.k8s.K8sResultRequest;
import com.hummer.common.core.domain.request.server.ServerResultRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.system.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

@Hidden
@RestController
@RequestMapping(value = "history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @I18n
    @Operation(summary = "主机检测历史记录")
    @PostMapping("server/history/{goPage}/{pageSize}")
    public Pager<List<HistoryServerResultDTO>> serverHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ServerResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, historyService.serverHistory(request));
    }

    @I18n
    @Operation(summary = "删除主机检测历史记录")
    @GetMapping("server/delete/history/result/{id}")
    public void serverDeleteHistoryResult(@PathVariable String id) throws Exception {
        historyService.serverDeleteHistoryResult(id);
    }

    @Operation(summary = "批量删除主机检测历史记录")
    @PostMapping("delete/server/history/results")
    public void deleteServerHistoryResults(@RequestBody List<String> selectIds) throws Exception {
        historyService.deleteServerHistoryResults(selectIds);
    }

    @I18n
    @Operation(summary = "源码检测历史记录")
    @PostMapping("code/history/{goPage}/{pageSize}")
    public Pager<List<HistoryCodeResultDTO>> codeHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CodeResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, historyService.codeHistory(request));
    }

    @Operation(summary = "删除源码检测历史记录")
    @GetMapping("code/delete/history/result/{id}")
    public void codeDeleteHistoryResult(@PathVariable String id) throws Exception {
        historyService.codeDeleteHistoryResult(id);
    }

    @Operation(summary = "批量删除部署检测历史记录")
    @PostMapping("delete/code/history/results")
    public void deleteCodeHistoryResults(@RequestBody List<String> selectIds) throws Exception {
        historyService.deleteCodeHistoryResults(selectIds);
    }

    @I18n
    @Operation(summary = "部署检测历史记录")
    @PostMapping("config/history/{goPage}/{pageSize}")
    public Pager<List<HistoryCloudNativeConfigResultDTO>> configHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ConfigResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, historyService.configHistory(request));
    }

    @Operation(summary = "删除部署检测历史记录")
    @GetMapping("config/delete/history/result/{id}")
    public void configDeleteHistoryResult(@PathVariable String id) throws Exception {
        historyService.configDeleteHistoryResult(id);
    }

    @Operation(summary = "批量删除部署检测历史记录")
    @PostMapping("delete/config/history/results")
    public void deleteConfigHistoryResults(@RequestBody List<String> selectIds) throws Exception {
        historyService.deleteConfigHistoryResults(selectIds);
    }

    @I18n
    @Operation(summary = "文件系统检测历史记录")
    @PostMapping("fs/history/{goPage}/{pageSize}")
    public Pager<List<HistoryFsResultDTO>> fsHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody FsResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, historyService.fsHistory(request));
    }

    @Operation(summary = "删除文件系统检测历史记录")
    @GetMapping("fs/delete/history/result/{id}")
    public void fsDeleteHistoryResult(@PathVariable String id) throws Exception {
        historyService.fsDeleteHistoryResult(id);
    }

    @Operation(summary = "批量删除文件系统检测历史记录")
    @PostMapping("delete/fs/history/results")
    public void deleteFsHistoryResults(@RequestBody List<String> selectIds) throws Exception {
        historyService.deleteFsHistoryResults(selectIds);
    }

    @I18n
    @Operation(summary = "镜像检测历史记录")
    @PostMapping("image/history/{goPage}/{pageSize}")
    public Pager<List<HistoryImageResultDTO>> imageHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, historyService.imageHistory(request));
    }

    @Operation(summary = "删除镜像检测历史记录")
    @GetMapping("image/delete/history/result/{id}")
    public void imageDeleteHistoryResult(@PathVariable String id) throws Exception {
        historyService.imageDeleteHistoryResult(id);
    }

    @Operation(summary = "批量删除镜像检测历史记录")
    @PostMapping("delete/image/history/results")
    public void deleteImageHistoryResults(@RequestBody List<String> selectIds) throws Exception {
        historyService.deleteImageHistoryResults(selectIds);
    }

    @I18n
    @Operation(summary = "K8s 检测历史记录")
    @PostMapping("k8s/history/{goPage}/{pageSize}")
    public Pager<List<HistoryCloudNativeResultDTO>> k8sHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, historyService.k8sHistory(request));
    }

    @Operation(summary = "删除K8s检测历史记录")
    @GetMapping("k8s/delete/history/result/{id}")
    public void k8sDeleteHistoryResult(@PathVariable String id) throws Exception {
        historyService.k8sDeleteHistoryResult(id);
    }

    @Operation(summary = "批量删除K8s检测历史记录")
    @PostMapping("delete/k8s/history/results")
    public void deleteK8sHistoryResults(@RequestBody List<String> selectIds) throws Exception {
        historyService.deleteK8sHistoryResults(selectIds);
    }

}
