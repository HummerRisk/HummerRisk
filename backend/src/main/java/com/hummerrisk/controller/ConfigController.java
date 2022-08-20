package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.domain.Package;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.controller.request.cloudNative.CloudNativeRequest;
import com.hummerrisk.controller.request.cloudNative.CreateCloudNativeRequest;
import com.hummerrisk.controller.request.cloudNative.UpdateCloudNativeRequest;
import com.hummerrisk.controller.request.config.ConfigRequest;
import com.hummerrisk.controller.request.k8s.K8sResultRequest;
import com.hummerrisk.dto.CloudNativeConfigDTO;
import com.hummerrisk.dto.CloudNativeDTO;
import com.hummerrisk.service.CloudNativeConfigService;
import com.hummerrisk.service.K8sService;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

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


}
