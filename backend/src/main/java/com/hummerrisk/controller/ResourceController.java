package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.ResourceWithBLOBs;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.request.excel.ExcelExportRequest;
import com.hummerrisk.controller.request.resource.JsonRequest;
import com.hummerrisk.controller.request.resource.ResourceRequest;
import com.hummerrisk.dto.*;
import com.hummerrisk.service.ResourceService;
import com.hummerrisk.base.domain.TaskItem;
import com.hummerrisk.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
@Api(tags = "资源")
@RestController
@RequestMapping("resource")
public class ResourceController {
    @Resource
    private ResourceService resourceService;

    @ApiOperation(value = "云账号资源")
    @GetMapping("source/{accountId}")
    public SourceDTO sourceDTO(@PathVariable String accountId) {
        return resourceService.source(accountId);
    }

    @ApiOperation(value = "资源列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<ResourceDTO>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ResourceRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, resourceService.search(request));
    }

    @ApiOperation(value = "合规报告列表")
    @PostMapping("reportList/{goPage}/{pageSize}")
    public Pager<List<ReportDTO>> reportList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ResourceRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, resourceService.reportList(request));
    }

    @ApiOperation(value = "资源详情")
    @GetMapping("{id}")
    public ResourceDetailDTO getResource(@PathVariable String id) throws Exception {
        return resourceService.getResource(id);
    }

    @ApiIgnore
    @PostMapping("getElement")
    public String getElement(@RequestBody String element) {
        String result;
        try{
            result = resourceService.toJSONString2(element);
        }catch (Exception e) {
            result = resourceService.toJSONString(element);
        }
        return result;
    }

    @ApiIgnore
    @GetMapping("fix/{id}")
    public ResourceWithBLOBs fixResource(@PathVariable String id) throws Exception {
        return resourceService.operatingResource(id, "fix");
    }

    @ApiOperation(value = "重新扫描")
    @GetMapping("restart/{id}")
    public ResourceWithBLOBs restartResource(@PathVariable String id) throws Exception {
        return resourceService.operatingResource(id, "restart");
    }

    @ApiOperation(value = "扫描日志")
    @GetMapping(value = "log/resourceId/{resourceId}")
    public List<ResourceLogDTO> getResourceLog(@PathVariable String resourceId) {
        return resourceService.getResourceLog(resourceId);
    }

    @ApiOperation(value = "导出扫描报告")
    @PostMapping("export")
    public ResponseEntity<byte[]> exportReport(@RequestBody ExcelExportRequest request) throws Exception {
        byte[] bytes = resourceService.export(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "不合规资源扫描报告.xlsx");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .body(bytes);
    }

    @ApiIgnore
    @PostMapping("string2PrettyFormat")
    public String string2PrettyFormat(@RequestBody JsonRequest jsonRequest) {
        try {
            return resourceService.toJSONString(jsonRequest.getJson());
        } catch (Exception e) {
            return resourceService.toJSONString2(jsonRequest.getJson());
        }
    }

    @ApiOperation(value = "删除扫描记录")
    @GetMapping("account/delete/{id}")
    public void deleteResourceByAccountId(@PathVariable String id) throws Exception {
        resourceService.deleteResourceByAccountId(id);
    }

    @ApiOperation(value = "规则条例信息")
    @GetMapping("report/iso/{accountId}/{groupId}")
    public Map<String, String> reportIso(@PathVariable String accountId, @PathVariable String groupId) {
        return resourceService.reportIso(accountId, groupId);
    }

    @ApiOperation(value = "规则组详情")
    @PostMapping("rule/groups")
    public List<Map<String, String>> groups(@RequestBody Map<String, Object> params) {
        return resourceService.groups(params);
    }

    @ApiOperation(value = "资源日志")
    @PostMapping("resourceLog")
    public ResourceWithBLOBs resource(@RequestBody TaskItem taskItem) {
        return resourceService.resource(taskItem);
    }
}
