package com.hummer.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.cloud.service.AccountService;
import com.hummer.cloud.service.CloudProjectService;
import com.hummer.common.core.domain.CloudProject;
import com.hummer.common.core.dto.CloudProjectDTO;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
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

    @I18n
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<CloudProjectDTO>> getCloudProjectDTOs(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudProject cloudProject) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudProjectService.getCloudProjectDTOs(cloudProject));
    }

    @GetMapping("projectById/{projectId}")
    public CloudProjectDTO projectById(@PathVariable String projectId) {
        return cloudProjectService.projectById(projectId);
    }

    @GetMapping("delete/{projectId}")
    public void deleteProject(@PathVariable String projectId) {
        cloudProjectService.deleteProject(projectId, tokenService.getLoginUser());
    }

}
