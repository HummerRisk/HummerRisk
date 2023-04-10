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
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@ApiIgnore
@RestController
@RequestMapping(value = "history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @I18n
    @Operation(summary = "主机检测历史记录")
    @PostMapping("serverHistory/{goPage}/{pageSize}")
    public Pager<List<HistoryServerResultDTO>> serverHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ServerResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, historyService.serverHistory(request));
    }

    @I18n
    @Operation(summary = "删除主机检测历史记录")
    @GetMapping("serverDeleteHistoryResult/{id}")
    public void serverDeleteHistoryResult(@PathVariable String id) throws Exception {
        historyService.serverDeleteHistoryResult(id);
    }

    @I18n
    @Operation(summary = "源码检测历史记录")
    @PostMapping("codeHistory/{goPage}/{pageSize}")
    public Pager<List<HistoryCodeResultDTO>> codeHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CodeResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, historyService.codeHistory(request));
    }

    @Operation(summary = "删除源码检测历史记录")
    @GetMapping("codeDeleteHistoryResult/{id}")
    public void codeDeleteHistoryResult(@PathVariable String id) throws Exception {
        historyService.codeDeleteHistoryResult(id);
    }

    @I18n
    @Operation(summary = "部署检测历史记录")
    @PostMapping("configHistory/{goPage}/{pageSize}")
    public Pager<List<HistoryCloudNativeConfigResultDTO>> configHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ConfigResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, historyService.configHistory(request));
    }

    @Operation(summary = "删除检测历史记录")
    @GetMapping("configDeleteHistoryResult/{id}")
    public void configDeleteHistoryResult(@PathVariable String id) throws Exception {
        historyService.configDeleteHistoryResult(id);
    }

    @I18n
    @Operation(summary = "文件系统检测历史记录")
    @PostMapping("fsHistory/{goPage}/{pageSize}")
    public Pager<List<HistoryFsResultDTO>> fsHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody FsResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, historyService.fsHistory(request));
    }

    @Operation(summary = "删除文件系统检测历史记录")
    @GetMapping("fsDeleteHistoryResult/{id}")
    public void fsDeleteHistoryResult(@PathVariable String id) throws Exception {
        historyService.fsDeleteHistoryResult(id);
    }

    @I18n
    @Operation(summary = "镜像检测历史记录")
    @PostMapping("imageHistory/{goPage}/{pageSize}")
    public Pager<List<HistoryImageResultDTO>> imageHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, historyService.imageHistory(request));
    }

    @Operation(summary = "删除检测历史记录")
    @GetMapping("imageDeleteHistoryResult/{id}")
    public void imageDeleteHistoryResult(@PathVariable String id) throws Exception {
        historyService.imageDeleteHistoryResult(id);
    }

    @I18n
    @Operation(summary = "K8s 检测历史记录")
    @PostMapping("k8sHistory/{goPage}/{pageSize}")
    public Pager<List<HistoryCloudNativeResultDTO>> k8sHistory(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, historyService.k8sHistory(request));
    }

    @Operation(summary = "删除检测历史记录")
    @GetMapping("k8sDeleteHistoryResult/{id}")
    public void k8sDeleteHistoryResult(@PathVariable String id) throws Exception {
        historyService.k8sDeleteHistoryResult(id);
    }

}
