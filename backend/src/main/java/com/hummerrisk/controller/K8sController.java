package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.CloudNativeResult;
import com.hummerrisk.base.domain.CloudNativeResultItem;
import com.hummerrisk.base.domain.ImageGrypeTable;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.controller.request.k8s.K8sResultRequest;
import com.hummerrisk.service.K8sService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "K8s")
@RestController
@RequestMapping(value = "k8s")
public class K8sController {
    @Resource
    private K8sService k8sService;

    @I18n
    @ApiOperation(value = "云原生检测")
    @GetMapping("scan/{id}")
    public void scan(@PathVariable String id) throws Exception {
        k8sService.scan(id);
    }

    @ApiOperation(value = "重新云原生检测")
    @GetMapping("reScan/{id}")
    public void reScan(@PathVariable String id) throws Exception {
        k8sService.reScan(id);
    }

    @I18n
    @ApiOperation(value = "云原生检测结果列表")
    @PostMapping(value = "resultList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeResult>> resultList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.resultList(request));
    }

    @ApiOperation(value = "删除镜像检测记录")
    @GetMapping("deleteCloudNativeResult/{id}")
    public void deleteCloudNativeResult(@PathVariable String id) throws Exception {
        k8sService.deleteCloudNativeResult(id);
    }

    @I18n
    @ApiOperation(value = "云原生检测结果详情")
    @PostMapping("resultItemList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeResultItem>> resultItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.resultItemList(request));
    }


}
