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
import com.hummerrisk.service.K8sService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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



}
