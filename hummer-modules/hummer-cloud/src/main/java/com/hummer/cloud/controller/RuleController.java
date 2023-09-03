package com.hummer.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.cloud.service.RuleService;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.rule.*;
import com.hummer.common.core.dto.GroupDTO;
import com.hummer.common.core.dto.RuleDTO;
import com.hummer.common.core.dto.RuleGroupDTO;
import com.hummer.common.core.dto.RuleTagDTO;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 规则
 */
@Tag(name = "规则")
@RestController
@RequestMapping(value = "rule")
public class RuleController {
    @Autowired
    private RuleService ruleService;
    @Autowired
    private TokenService tokenService;
    @I18n
    @Operation(summary = "云账号规则列表")
    @PostMapping(value = "list/{goPage}/{pageSize}")
    public Pager<List<RuleDTO>> cloudList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CreateRuleRequest rule) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.cloudList(rule));
    }

    @I18n
    @Operation(summary = "云原生规则列表")
    @PostMapping(value = "k8s/list/{goPage}/{pageSize}")
    public Pager<List<RuleDTO>> k8sList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CreateRuleRequest rule) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.k8sList(rule));
    }

    @I18n
    @Hidden
    @GetMapping(value = "list/by/account/{accountId}")
    public List<Rule> listByAccountId(@PathVariable String accountId) {
        return ruleService.getRulesByAccountId(accountId);
    }

    @I18n
    @Operation(summary = "规则标签列表")
    @PostMapping(value = "rule/tag/list/{goPage}/{pageSize}")
    public Pager<List<RuleTag>> ruleTagList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleTagRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.ruleTagList(request));
    }

    @I18n
    @Operation(summary = "规则组列表")
    @PostMapping(value = "rule/group/list/{goPage}/{pageSize}")
    public Pager<List<RuleGroupDTO>> ruleGroupList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleGroupRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.ruleGroupList(request));
    }

    @I18n
    @Operation(summary = "所有规则组")
    @GetMapping(value = "all/rule/groups")
    public List<RuleGroupDTO> allRuleGroups() {
        return ruleService.ruleGroupList(new RuleGroupRequest());
    }

    @I18n
    @Operation(summary = "所有云检测规则组")
    @GetMapping(value = "all/cloud/rule/groups")
    public List<RuleGroupDTO> allCloudRuleGroups() {
        return ruleService.allCloudRuleGroups(new RuleGroupRequest());
    }

    @I18n
    @Operation(summary = "规则标签")
    @GetMapping(value = "rule/tags")
    public List<RuleTagDTO> getRuleTags() throws Exception {
        return ruleService.getRuleTags();
    }

    @I18n
    @Operation(summary = "添加规则")
    @PostMapping(value = "add")
    public Rule addRule(@RequestBody CreateRuleRequest createRuleRequest) {
        createRuleRequest.setId(null);
        return ruleService.saveRules(createRuleRequest, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "修改规则")
    @PostMapping(value = "update")
    public Rule updateRule(@RequestBody CreateRuleRequest createRuleRequest) {
        return ruleService.saveRules(createRuleRequest, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "复制规则")
    @PostMapping(value = "copy")
    public Rule copyRule(@RequestBody CreateRuleRequest createRuleRequest) {
        return ruleService.copyRule(createRuleRequest, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "运行规则")
    @PostMapping(value = "run")
    public CloudTask runRule(@RequestBody RuleDTO ruleDTO) {
        return ruleService.runRules(ruleDTO, tokenService.getLoginUser());
    }

    @Operation(summary = "测试运行规则")
    @PostMapping(value = "dry/run")
    public boolean dryRun(@RequestBody RuleDTO ruleDTO) throws Exception {
        return ruleService.dryRun(ruleDTO);
    }

    @Operation(summary = "删除规则")
    @GetMapping(value = "delete/{id}")
    public void deleteRule(@PathVariable String id) {
        ruleService.deleteRule(id, tokenService.getLoginUser());
    }

    @Operation(summary = "批量删除规则")
    @PostMapping("delete/rules")
    public void deleteRules(@RequestBody List<String> selectIds) throws Exception {
        ruleService.deleteRules(selectIds, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "规则详情")
    @GetMapping(value = "get/{ruleId}")
    public RuleDTO getRule(@PathVariable String ruleId) throws Exception {
        return ruleService.getRuleById(ruleId);
    }

    @I18n
    @Hidden
    @GetMapping(value = "get/rule/by/taskid/{taskId}")
    public RuleDTO getRuleByTaskId(@PathVariable String taskId) throws Exception {
        return ruleService.getRuleByTaskId(taskId);
    }

    @Operation(summary = "规则重复")
    @PostMapping(value = "get/rule/by/name")
    public boolean getRuleByName(@RequestBody CreateRuleRequest request) {
        return ruleService.getRuleByName(request);
    }

    @Operation(summary = "规则类型")
    @GetMapping(value = "all/resource/types")
    public List<Map<String, String>> getAllResourceTypes() {
        return ruleService.getAllResourceTypes();
    }

    @Operation(summary = "云检测规则类型")
    @GetMapping(value = "all/cloud/resource/types")
    public List<Map<String, String>> cloudResourceTypes() {
        return ruleService.cloudResourceTypes();
    }

    @I18n
    @Operation(summary = "规则组")
    @GetMapping(value = "rule/groups/{pluginId}")
    public List<RuleGroup> getRuleGroups(@PathVariable String pluginId) {
        return ruleService.getRuleGroups(pluginId);
    }

    @I18n
    @Operation(summary = "风险安全策略")
    @PostMapping(value = "rule/inspection/reports/{goPage}/{pageSize}")
    public Pager<List<RuleInspectionReport>> getRuleInspectionReports(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleInspectionReportRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.getRuleInspectionReportList(request));
    }

    @I18n
    @Hidden
    @GetMapping(value = "all/rule/inspection/report")
    public List<RuleInspectionReport> getRuleInspectionReport() {
        return ruleService.getRuleInspectionReportList(new RuleInspectionReportRequest());
    }

    @Hidden
    @GetMapping(value = "get/resource/types/by/id/{ruleId}")
    public String getResourceTypesById(@PathVariable String ruleId) {
        return ruleService.getResourceTypesById(ruleId);
    }

    @Operation(summary = "规则启用")
    @PostMapping(value = "change/status")
    public int changeStatus(@RequestBody Rule rule) {
        return ruleService.changeStatus(rule);
    }

    @Operation(summary = "批量重新检测")
    @GetMapping("rescans/{accountId}")
    public void reScans(@PathVariable String accountId) throws Exception {
        ruleService.reScans(accountId);
    }

    @Operation(summary = "批量重新检测")
    @GetMapping("rescans/k8s/{accountId}")
    public void reScansK8s(@PathVariable String accountId) throws Exception {
        ruleService.reScansK8s(accountId);
    }

    @Hidden
    @GetMapping("rescan/{taskId}/{accountId}")
    public void reScan(@PathVariable String taskId, @PathVariable String accountId) throws Exception {
        ruleService.reScan(taskId, accountId, tokenService.getLoginUser());
    }

    @Operation(summary = "执行检测")
    @PostMapping("scan")
    public void scan(@RequestBody ScanGroupRequest request) throws Exception {
        ruleService.scan(request, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "新增规则组")
    @RequestMapping(value = "group/save")
    public RuleGroup saveRuleGroup(@RequestBody RuleGroup ruleGroup) {
        return ruleService.saveRuleGroup(ruleGroup);
    }

    @I18n
    @Operation(summary = "修改规则组")
    @RequestMapping(value = "group/update")
    public RuleGroup updateRuleGroup(@RequestBody RuleGroup ruleGroup) {
        return ruleService.updateRuleGroup(ruleGroup);
    }

    @Operation(summary = "删除规则组")
    @GetMapping(value = "group/delete/{id}")
    public void deleteRuleGroup(@PathVariable Integer id) {
        ruleService.deleteRuleGroupById(id, tokenService.getLoginUser());
    }

    @Operation(summary = "批量删除规则组")
    @PostMapping("delete/groups")
    public void deleteGroups(@RequestBody List<Integer> selectIds) throws Exception {
        ruleService.deleteGroups(selectIds, tokenService.getLoginUser());
    }

    @I18n
    @Hidden
    @PostMapping("groups")
    public List<GroupDTO> groups(@RequestPart(value = "selectIds") List<String> ids) {
        return ruleService.groups(ids);
    }

    @I18n
    @Hidden
    @GetMapping("groups/by/account/{pluginId}")
    public List<RuleGroup> groupsByAccountId(@PathVariable String pluginId) {
        return ruleService.groupsByAccountId(pluginId);
    }

    @I18n
    @Operation(summary = "所有已绑定规则组的规则")
    @GetMapping("all/bind/list/{id}")
    public List<Rule> allBindList(@PathVariable String id) {
        return ruleService.allBindList(id);
    }

    @I18n
    @Operation(summary = "所有未绑定规则组的规则")
    @GetMapping("unbind/list/{id}")
    public List<Rule> unBindList(@PathVariable Integer id) {
        return ruleService.unBindList(id);
    }

    @I18n
    @Operation(summary = "规则组绑定规则")
    @PostMapping(value = "bind/rule")
    public void bindRule(@RequestBody BindRuleRequest request) throws Exception {
        ruleService.bindRule(request);
    }

    @I18n
    @Operation(summary = "规则组检测云账号")
    @GetMapping("scan/by/group/{groupId}/{accountId}")
    public void scanByGroup(@PathVariable String groupId, @PathVariable String accountId) {
        ruleService.scanByGroup(groupId, accountId, tokenService.getLoginUser());
    }
}
