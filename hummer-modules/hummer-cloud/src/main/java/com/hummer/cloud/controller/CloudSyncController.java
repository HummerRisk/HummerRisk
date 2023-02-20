package com.hummer.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.mapper.handler.annotation.I18n;
import com.hummer.common.mapper.service.CloudSyncService;
import com.hummer.common.mapper.domain.CloudResourceSync;
import com.hummer.common.mapper.domain.request.cloudResource.CloudResourceSyncRequest;
import com.hummer.common.mapper.domain.request.sync.CloudTopology;
import com.hummer.common.mapper.dto.CloudResourceSyncItemDTO;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "资源态势")
@RestController
@RequestMapping(value = "cloud/sync")
public class CloudSyncController {
    @Resource
    private CloudSyncService cloudSyncService;

    @I18n
    @ApiOperation(value = "云账号同步")
    @GetMapping(value = "sync/{accountId}")
    public void sync(@PathVariable String accountId) throws Exception {
        cloudSyncService.sync(accountId);
    }

    @I18n
    @GetMapping(value = "resourceType/list/{syncId}")
    public List<Map<String,Object>> listResourceType(@PathVariable String syncId) {
        return cloudSyncService.getResourceType(syncId);
    }

    @I18n
    @ApiOperation(value = "删除同步任务")
    @GetMapping(value = "delete/{id}")
    public void delete(@PathVariable String id) throws Exception {
        cloudSyncService.deleteSync(id);
    }

    @I18n
    @ApiOperation(value = "云账号同步日志列表")
    @PostMapping(value = "log/list/{goPage}/{pageSize}")
    public Pager<List<CloudResourceSync>> listResourceSync(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudResourceSyncRequest cloudResourceSyncRequest) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, cloudSyncService.getCloudResourceSyncLogs(cloudResourceSyncRequest));
    }

    @I18n
    @ApiOperation(value = "云账号同步日志详情")
    @GetMapping(value = "log/item/list/{syncId}")
    public List<CloudResourceSyncItemDTO> listResourceSyncItem(@PathVariable String syncId) {
        return cloudSyncService.getCloudResourceSyncItem(syncId);
    }

    @I18n
    @ApiOperation(value = "资源态势拓扑图")
    @GetMapping(value = "cloudTopology")
    public CloudTopology cloudTopology() {
        return cloudSyncService.cloudTopology();
    }

}
