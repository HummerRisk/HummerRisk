package com.hummer.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.cloud.service.ResourceService;
import com.hummer.common.core.domain.CloudTaskItem;
import com.hummer.common.core.domain.ResourceWithBLOBs;
import com.hummer.common.core.domain.RuleInspectionReport;
import com.hummer.common.core.domain.request.excel.ExcelExportRequest;
import com.hummer.common.core.domain.request.resource.JsonRequest;
import com.hummer.common.core.domain.request.resource.ResourceRequest;
import com.hummer.common.core.domain.request.rule.RuleGroupRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
@Tag(name = "资源")
@RestController
@RequestMapping("resource")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private TokenService tokenService;
    @I18n
    @Operation(summary = "云账号资源")
    @GetMapping("source/{accountId}")
    public SourceDTO sourceDTO(@PathVariable String accountId) {
        return resourceService.source(accountId);
    }

    @I18n
    @Operation(summary = "云检测资源")
    @GetMapping("source/by/projectid/{projectId}")
    public SourceDTO sourceByProjectId(@PathVariable String projectId) {
        return resourceService.sourceByProjectId(projectId);
    }

    @I18n
    @Operation(summary = "云原生资源")
    @GetMapping("k8s/source/{accountId}")
    public SourceDTO k8sSource(@PathVariable String accountId) {
        return resourceService.k8sSource(accountId);
    }

    @I18n
    @Operation(summary = "资源列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<ResourceDTO>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ResourceRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, resourceService.search(request));
    }

    @I18n
    @Operation(summary = "合规报告列表")
    @PostMapping("report/list/{goPage}/{pageSize}")
    public Pager<List<ReportDTO>> reportList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ResourceRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, resourceService.reportList(request));
    }

    @I18n
    @Operation(summary = "合规报告列表")
    @PostMapping("report/by/project/list/{goPage}/{pageSize}")
    public Pager<List<ReportDTO>> reportByProjectList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ResourceRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, resourceService.reportByProjectList(request));
    }

    @I18n
    @Operation(summary = "资源详情")
    @GetMapping("{id}")
    public ResourceDetailDTO getResource(@PathVariable String id) throws Exception {
        return resourceService.getResource(id);
    }

    @Hidden
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

    @I18n
    @Hidden
    @GetMapping("fix/{id}")
    public ResourceWithBLOBs fixResource(@PathVariable String id) throws Exception {
        return resourceService.operatingResource(id, "fix", tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "重新检测")
    @GetMapping("restart/{id}")
    public ResourceWithBLOBs restartResource(@PathVariable String id) throws Exception {
        return resourceService.operatingResource(id, "restart", tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "检测日志")
    @GetMapping(value = "log/resourceId/{resourceId}")
    public List<ResourceLogDTOCloud> getResourceLog(@PathVariable String resourceId) {
        return resourceService.getResourceLog(resourceId);
    }

    @Operation(summary = "导出整个云检测报告")
    @PostMapping("export")
    public ResponseEntity<byte[]> exportReport(@RequestBody ExcelExportRequest request) throws Exception {
        byte[] bytes = resourceService.export(request, tokenService.getLoginUser());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "不合规资源检测报告.xlsx");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .body(bytes);
    }

    @Operation(summary = "导出规则组检测报告")
    @PostMapping("group/export")
    public ResponseEntity<byte[]> exportGroupReport(@RequestBody ExcelExportRequest request) throws Exception {
        byte[] bytes = resourceService.exportGroupReport(request, tokenService.getLoginUser());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "不合规资源检测报告.xlsx");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .body(bytes);
    }

    @Hidden
    @PostMapping("string/2/pretty/format")
    public String string2PrettyFormat(@RequestBody JsonRequest jsonRequest) {
        try {
            return resourceService.toJSONString(jsonRequest.getJson());
        } catch (Exception e) {
            return resourceService.toJSONString2(jsonRequest.getJson());
        }
    }

    @Operation(summary = "删除检测记录")
    @GetMapping("account/delete/{id}")
    public void deleteResourceByAccountId(@PathVariable String id) throws Exception {
        resourceService.deleteResourceByAccountId(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "风险安全策略信息")
    @GetMapping("report/iso/{accountId}/{groupId}")
    public Map<String, String> reportIso(@PathVariable String accountId, @PathVariable String groupId) {
        return resourceService.reportIso(accountId, groupId);
    }

    @I18n
    @Operation(summary = "风险安全策略信息")
    @GetMapping("report/iso/list/{projectId}/{groupId}")
    public Map<String, String> reportIsoList(@PathVariable String projectId, @PathVariable String groupId) {
        return resourceService.reportIsoList(projectId, groupId);
    }

    @I18n
    @Operation(summary = "规则组详情")
    @PostMapping("rule/groups")
    public List<Map<String, String>> groups(@RequestBody Map<String, Object> params) {
        return resourceService.groups(params);
    }

    @I18n
    @Operation(summary = "资源日志")
    @PostMapping("resource/log")
    public ResourceWithBLOBs resource(@RequestBody CloudTaskItem cloudTaskItem) {
        return resourceService.resource(cloudTaskItem);
    }

    @I18n
    @Operation(summary = "区域统计")
    @PostMapping("region/data")
    public List<Map<String, Object>> regionData(@RequestBody Map<String, Object> map) {
        return resourceService.regionData(map);
    }

    @I18n
    @Operation(summary = "风险统计")
    @PostMapping("severity/data")
    public List<Map<String, Object>> severityData(@RequestBody Map<String, Object> map) {
        return resourceService.severityData(map);
    }

    @I18n
    @Operation(summary = "资源类型统计")
    @PostMapping("resource/type/data")
    public List<Map<String, Object>> resourceTypeData(@RequestBody Map<String, Object> map) {
        return resourceService.resourceTypeData(map);
    }

    @I18n
    @Operation(summary = "规则统计")
    @PostMapping("rule/data")
    public List<Map<String, Object>> ruleData(@RequestBody Map<String, Object> map) {
        return resourceService.ruleData(map);
    }

    @I18n
    @Operation(summary = "风险安全策略")
    @GetMapping("regulation/{ruleId}")
    public List<RuleInspectionReport> regulation(@PathVariable String ruleId) {
        return resourceService.regulation(ruleId);
    }

    @I18n
    @Operation(summary = "合规报告规则组列表")
    @PostMapping(value = "rule/group/list/{goPage}/{pageSize}")
    public Pager<List<RuleGroupDTO>> ruleGroupList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleGroupRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, resourceService.ruleGroupList(request));
    }

    @I18n
    @Operation(summary = "合规报告规则组列表")
    @PostMapping(value = "rule/group/by/project/list/{goPage}/{pageSize}")
    public Pager<List<RuleGroupDTO>> ruleGroupByProjectList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleGroupRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, resourceService.ruleGroupByProjectList(request));
    }

    @I18n
    @Operation(summary = "合规报告资源列表")
    @PostMapping(value = "resource/list/{goPage}/{pageSize}")
    public Pager<List<ResourceDTO>> resourceList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ResourceRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, resourceService.resourceList(request));
    }

    @I18n
    @Operation(summary = "云资源检测规则组结果")
    @PostMapping("resouce/groups")
    public List<Map<String, Object>> resouceGroups(@RequestBody Map<String, Object> params) {
        return resourceService.resouceGroups(params);
    }

    @I18n
    @Operation(summary = "云资源项目检测规则组结果")
    @PostMapping("resouce/groups/by/project")
    public List<Map<String, Object>> resouceGroupsByProjectId(@RequestBody Map<String, Object> params) {
        return resourceService.resouceGroupsByProjectId(params);
    }
}
