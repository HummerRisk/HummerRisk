package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.CloudResourceItem;
import com.hummerrisk.base.domain.CloudResourceSummary;
import com.hummerrisk.base.domain.CloudResourceSync;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.controller.request.cloudResource.CloudResourceItemRequest;
import com.hummerrisk.controller.request.cloudResource.CloudResourceSyncRequest;
import com.hummerrisk.dto.CloudResourceItemDTO;
import com.hummerrisk.dto.ResourceRuleDTO;
import com.hummerrisk.service.CloudResourceService;
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
    @PostMapping(value = "list/{goPage}/{pageSize}")
    public Pager<List<CloudResourceItemDTO>> listResource(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudResourceItemRequest cloudResourceItemRequest) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudResourceService.getResources(cloudResourceItemRequest));
    }

    @I18n
    @GetMapping(value = "rule/list/{hummerId}")
    public List<ResourceRuleDTO> listResourceRule(@PathVariable String hummerId) {
        return cloudResourceService.getResourceRule(hummerId);
    }

}
