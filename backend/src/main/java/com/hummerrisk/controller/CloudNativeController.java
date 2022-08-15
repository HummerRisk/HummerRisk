package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.CloudNative;
import com.hummerrisk.commons.constants.CloudAccountConstants;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.controller.request.cloudNative.CloudNativeRequest;
import com.hummerrisk.controller.request.cloudNative.CloudNativeSourceRequest;
import com.hummerrisk.controller.request.cloudNative.CreateCloudNativeRequest;
import com.hummerrisk.controller.request.cloudNative.UpdateCloudNativeRequest;
import com.hummerrisk.dto.CloudNativeDTO;
import com.hummerrisk.dto.CloudNativeSourceDTO;
import com.hummerrisk.dto.SituationDTO;
import com.hummerrisk.service.CloudNativeService;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(tags = "云原生")
@RestController
@RequestMapping(value = "cloud/native")
public class CloudNativeController {
    @Resource
    private CloudNativeService cloudNativeService;

    @I18n
    @ApiOperation(value = "云原生账号列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<CloudNativeDTO>> getCloudNativeList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudNativeRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudNativeService.getCloudNativeList(request));
    }

    @I18n
    @ApiOperation(value = "所有云原生账号")
    @GetMapping("allCloudNativeList")
    public List<CloudNativeDTO> allCloudNativeList() {
        CloudNativeRequest request = new CloudNativeRequest();
        request.setStatus(CloudAccountConstants.Status.VALID.name());
        return cloudNativeService.allCloudNativeList(request);
    }

    @I18n
    @ApiOperation(value = "云原生账号详情")
    @GetMapping("getCloudNative/{id}")
    public CloudNative getCloudNative(@PathVariable String id) {
        return cloudNativeService.getCloudNative(id);
    }

    @ApiOperation(value = "批量校验云原生账号")
    @PostMapping("validate")
    public Boolean validate(@RequestBody List<String> selectIds) {
        return cloudNativeService.validate(selectIds);
    }

    @ApiOperation(value = "校验云原生账号")
    @PostMapping("validate/{id}")
    public Boolean validate(@PathVariable String id) throws IOException, ApiException {
        return cloudNativeService.validate(id);
    }

    @I18n
    @ApiOperation(value = "添加云原生账号")
    @PostMapping("add")
    public CloudNative addCloudNative(@RequestBody CreateCloudNativeRequest request) {
        return cloudNativeService.addCloudNative(request);
    }

    @I18n
    @ApiOperation(value = "更新云原生账号")
    @PostMapping("update")
    public CloudNative editCloudNative(@RequestBody UpdateCloudNativeRequest request) throws Exception {
        return cloudNativeService.editCloudNative(request);
    }

    @ApiOperation(value = "删除云原生账号")
    @PostMapping(value = "delete/{accountId}")
    public void deleteAccount(@PathVariable String accountId) {
        cloudNativeService.delete(accountId);
    }

    @I18n
    @ApiOperation(value = "资源态势列表")
    @PostMapping("cloudNativeSourceList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeSourceDTO>> getCloudNativeSourceList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudNativeSourceRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudNativeService.getCloudNativeSourceList(request));
    }

    @I18n
    @ApiOperation(value = "资源态势")
    @PostMapping("situation")
    public SituationDTO situationInfo(@RequestBody Map<String, Object> params) {
        return cloudNativeService.situationInfo(params);
    }

}
