package com.hummer.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.cloud.service.CloudResourceService;
import com.hummer.common.mapper.domain.CloudResourceSummary;
import com.hummer.common.mapper.domain.CloudTask;
import com.hummer.common.mapper.domain.request.cloudResource.CloudResourceItemRequest;
import com.hummer.common.mapper.dto.CloudResourceItemDTO;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.mapper.handler.annotation.I18n;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

@ApiIgnore
@RestController
@RequestMapping(value = "cloud/resource")
public class CloudResourceController {

    @Resource
    private CloudResourceService cloudResourceService;

    @I18n
    @GetMapping(value = "summary")
    public List<CloudResourceSummary> summary(@RequestParam(required = false) String accountId) throws Exception {
        return cloudResourceService.getSummary(accountId);
    }

    @I18n
    @GetMapping(value = "summary/{accountId}")
    public List<CloudResourceSummary> summaryByAccountId(@PathVariable String accountId) throws Exception {
        return cloudResourceService.getSummary(accountId);
    }

    @I18n
    @PostMapping(value = "list/{goPage}/{pageSize}")
    public Pager<List<CloudResourceItemDTO>> listResource(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudResourceItemRequest cloudResourceItemRequest) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudResourceService.getResources(cloudResourceItemRequest));
    }

    @I18n
    @GetMapping(value = "risk/list/{regionId}/{hummerId}")
    public List<CloudTask> listResourceRisk(@PathVariable String regionId, @PathVariable String hummerId) {
        return cloudResourceService.getCloudTaskByHummerId(hummerId,regionId);
    }

    @I18n
    @GetMapping(value = "task/count/{accountId}/{regionId}/{resourceType}")
    public Integer countResourceTask(@PathVariable String accountId,@PathVariable String regionId,@PathVariable String resourceType) {
        return cloudResourceService.countResourceTask(accountId,regionId,resourceType);
    }

}
