package com.hummer.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.cloud.service.CloudProjectService;
import com.hummer.common.core.domain.CloudGroup;
import com.hummer.common.core.domain.CloudProcess;
import com.hummer.common.core.domain.request.project.CloudGroupRequest;
import com.hummer.common.core.domain.request.rule.ScanGroupRequest;
import com.hummer.common.core.dto.CloudGroupDTO;
import com.hummer.common.core.dto.CloudProcessDTO;
import com.hummer.common.core.dto.CloudProjectDTO;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "云资源检测历史")
@RestController
@RequestMapping(value = "cloud/project")
public class CloudProjectController {

    @Autowired
    private CloudProjectService cloudProjectService;
    @Autowired
    private TokenService tokenService;

    @Operation(summary = "项目列表")
    @I18n
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<CloudProjectDTO>> getCloudProjectDTOs(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudGroupRequest cloudGroupRequest) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudProjectService.getCloudProjectDTOs(cloudGroupRequest));
    }

    @Operation(summary = "项目详情")
    @I18n
    @GetMapping("projectById/{projectId}")
    public CloudProjectDTO projectById(@PathVariable String projectId) {
        return cloudProjectService.projectById(projectId);
    }

    @Operation(summary = "删除项目")
    @GetMapping("delete/{projectId}")
    public void deleteProject(@PathVariable String projectId) {
        cloudProjectService.deleteProject(projectId, tokenService.getLoginUser());
    }

    @Operation(summary = "批量删除项目")
    @PostMapping("deletes")
    public void deletes(@RequestBody List<String> selectIds) throws Exception {
        cloudProjectService.deletes(selectIds, tokenService.getLoginUser());
    }

    @Operation(summary = "项目规则组列表")
    @I18n
    @PostMapping("groupList/{goPage}/{pageSize}")
    public Pager<List<CloudGroupDTO>> getCloudGroupDTOs(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudGroupRequest cloudGroupRequest) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudProjectService.getCloudGroupDTOs(cloudGroupRequest));
    }

    @Operation(summary = "项目规则组详情")
    @I18n
    @GetMapping("groupById/{groupId}")
    public CloudGroupDTO groupById(@PathVariable String groupId) {
        return cloudProjectService.groupById(groupId);
    }

    @Operation(summary = "删除项目规则组")
    @GetMapping("deleteGroup/{groupId}")
    public void deleteGroup(@PathVariable String groupId) {
        cloudProjectService.deleteGroup(groupId, tokenService.getLoginUser());
    }

    @Operation(summary = "批量删除项目规则组")
    @PostMapping("deleteGroups")
    public void deleteGroups(@RequestBody List<String> selectIds) throws Exception {
        cloudProjectService.deleteGroups(selectIds, tokenService.getLoginUser());
    }

    @Operation(summary = "项目执行过程列表")
    @I18n
    @PostMapping("processList/{goPage}/{pageSize}")
    public Pager<List<CloudProcessDTO>> getCloudProcessDTOs(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudProcess cloudProcess) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudProjectService.getCloudProcessDTOs(cloudProcess));
    }

    @Operation(summary = "项目执行过程详情")
    @I18n
    @GetMapping("processById/{groupId}")
    public CloudProcessDTO processById(@PathVariable String processId) {
        return cloudProjectService.processById(processId);
    }

    @Operation(summary = "执行项目检测")
    @PostMapping("scan")
    public void scan(@RequestBody ScanGroupRequest request) throws Exception {
        cloudProjectService.scan(request, tokenService.getLoginUser());
    }

}
