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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * 规则
 */
@Api(tags = "规则")
@RestController
@RequestMapping(value = "rule")
public class RuleController {
    @Autowired
    private RuleService ruleService;

    //@I18n
    @ApiOperation(value = "云账号规则列表")
    @PostMapping(value = "list/{goPage}/{pageSize}")
    public Pager<List<RuleDTO>> cloudList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CreateRuleRequest rule) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.cloudList(rule));
    }

    //@I18n
    @ApiIgnore
    @GetMapping(value = "listByAccountId/{accountId}")
    public List<Rule> listByAccountId(@PathVariable String accountId) {
        return ruleService.getRulesByAccountId(accountId);
    }

    //@I18n
    @ApiOperation(value = "规则标签列表")
    @PostMapping(value = "ruleTag/list/{goPage}/{pageSize}")
    public Pager<List<RuleTag>> ruleTagList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleTagRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.ruleTagList(request));
    }

    //@I18n
    @ApiOperation(value = "规则组列表")
    @PostMapping(value = "ruleGroup/list/{goPage}/{pageSize}")
    public Pager<List<RuleGroupDTO>> ruleGroupList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleGroupRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.ruleGroupList(request));
    }

    //@I18n
    @ApiOperation(value = "所有规则组")
    @GetMapping(value = "allRuleGroups")
    public List<RuleGroupDTO> allRuleGroups() {
        return ruleService.ruleGroupList(new RuleGroupRequest());
    }

    //@I18n
    @ApiOperation(value = "所有云检测规则组")
    @GetMapping(value = "allCloudRuleGroups")
    public List<RuleGroupDTO> allCloudRuleGroups() {
        return ruleService.allCloudRuleGroups(new RuleGroupRequest());
    }

    //@I18n
    @ApiOperation(value = "规则标签")
    @GetMapping(value = "ruleTags")
    public List<RuleTagDTO> getRuleTags() throws Exception {
        return ruleService.getRuleTags();
    }

    //@I18n
    @ApiOperation(value = "添加规则")
    @PostMapping(value = "add")
    public Rule addRule(@RequestBody CreateRuleRequest createRuleRequest) {
        createRuleRequest.setId(null);
        return ruleService.saveRules(createRuleRequest);
    }

    //@I18n
    @ApiOperation(value = "修改规则")
    @PostMapping(value = "update")
    public Rule updateRule(@RequestBody CreateRuleRequest createRuleRequest) {
        return ruleService.saveRules(createRuleRequest);
    }

    //@I18n
    @ApiOperation(value = "复制规则")
    @PostMapping(value = "copy")
    public Rule copyRule(@RequestBody CreateRuleRequest createRuleRequest) {
        return ruleService.copyRule(createRuleRequest);
    }

    //@I18n
    @ApiOperation(value = "运行规则")
    @PostMapping(value = "run")
    public CloudTask runRule(@RequestBody RuleDTO ruleDTO) {
        return ruleService.runRules(ruleDTO);
    }

    @ApiOperation(value = "测试运行规则")
    @PostMapping(value = "dryRun")
    public boolean dryRun(@RequestBody RuleDTO ruleDTO) throws Exception {
        return ruleService.dryRun(ruleDTO);
    }

    @ApiOperation(value = "删除规则")
    @GetMapping(value = "delete/{id}")
    public void deleteRule(@PathVariable String id) {
        ruleService.deleteRule(id);
    }

    //@I18n
    @ApiOperation(value = "规则详情")
    @GetMapping(value = "get/{ruleId}")
    public RuleDTO getRule(@PathVariable String ruleId) throws Exception {
        return ruleService.getRuleById(ruleId);
    }

    //@I18n
    @ApiIgnore
    @GetMapping(value = "getRuleByTaskId/{taskId}")
    public RuleDTO getRuleByTaskId(@PathVariable String taskId) throws Exception {
        return ruleService.getRuleByTaskId(taskId);
    }

    @ApiOperation(value = "规则重复")
    @PostMapping(value = "getRuleByName")
    public boolean getRuleByName(@RequestBody CreateRuleRequest request) {
        return ruleService.getRuleByName(request);
    }

    @ApiOperation(value = "规则类型")
    @GetMapping(value = "all/resourceTypes")
    public List<Map<String, String>> getAllResourceTypes() {
        return ruleService.getAllResourceTypes();
    }

    @ApiOperation(value = "云检测规则类型")
    @GetMapping(value = "all/cloudResourceTypes")
    public List<Map<String, String>> cloudResourceTypes() {
        return ruleService.cloudResourceTypes();
    }

    //@I18n
    @ApiOperation(value = "规则组")
    @GetMapping(value = "ruleGroups/{pluginId}")
    public List<RuleGroup> getRuleGroups(@PathVariable String pluginId) {
        return ruleService.getRuleGroups(pluginId);
    }

    //@I18n
    @ApiOperation(value = "规则条例")
    @PostMapping(value = "ruleInspectionReports/{goPage}/{pageSize}")
    public Pager<List<RuleInspectionReport>> getRuleInspectionReports(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleInspectionReportRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.getRuleInspectionReportList(request));
    }

    //@I18n
    @ApiIgnore
    @GetMapping(value = "all/ruleInspectionReport")
    public List<RuleInspectionReport> getRuleInspectionReport() {
        return ruleService.getRuleInspectionReportList(new RuleInspectionReportRequest());
    }

    @ApiIgnore
    @GetMapping(value = "getResourceTypesById/{ruleId}")
    public String getResourceTypesById(@PathVariable String ruleId) {
        return ruleService.getResourceTypesById(ruleId);
    }

    @ApiOperation(value = "规则启用")
    @PostMapping(value = "changeStatus")
    public int changeStatus(@RequestBody Rule rule) {
        return ruleService.changeStatus(rule);
    }

    @ApiOperation(value = "批量重新检测")
    @GetMapping("reScans/{accountId}")
    public void reScans(@PathVariable String accountId) throws Exception {
        ruleService.reScans(accountId);
    }

//    @ApiIgnore
//    @GetMapping("reScan/{taskId}/{accountId}")
//    public void reScan(@PathVariable String taskId, @PathVariable String accountId) throws Exception {
//        ruleService.reScan(taskId, accountId);
//    }

//    @ApiOperation(value = "执行检测")
//    @PostMapping("scan")
//    public void scan(@RequestBody ScanGroupRequest request) throws Exception {
//        ruleService.scan(request);
//    }

    //@I18n
    @ApiOperation(value = "新增规则组")
    @RequestMapping(value = "group/save")
    public RuleGroup saveRuleGroup(@RequestBody RuleGroup ruleGroup) {
        return ruleService.saveRuleGroup(ruleGroup);
    }

    //@I18n
    @ApiOperation(value = "修改规则组")
    @RequestMapping(value = "group/update")
    public RuleGroup updateRuleGroup(@RequestBody RuleGroup ruleGroup) {
        return ruleService.updateRuleGroup(ruleGroup);
    }

    @ApiOperation(value = "删除规则组")
    @GetMapping(value = "group/delete/{id}")
    public int deleteRuleGroup(@PathVariable Integer id) {
        return ruleService.deleteRuleGroupById(id);
    }

    //@I18n
    @ApiIgnore
    @PostMapping("groups")
    public List<GroupDTO> groups(@RequestPart(value = "selectIds") List<String> ids) {
        return ruleService.groups(ids);
    }

    //@I18n
    @ApiIgnore
    @GetMapping("groupsByAccountId/{pluginId}")
    public List<RuleGroup> groupsByAccountId(@PathVariable String pluginId) {
        return ruleService.groupsByAccountId(pluginId);
    }

    //@I18n
    @ApiOperation(value = "所有已绑定规则组的规则")
    @GetMapping("allBindList/{id}")
    public List<Rule> allBindList(@PathVariable String id) {
        return ruleService.allBindList(id);
    }

    //@I18n
    @ApiOperation(value = "所有未绑定规则组的规则")
    @GetMapping("unBindList/{id}")
    public List<Rule> unBindList(@PathVariable Integer id) {
        return ruleService.unBindList(id);
    }

    //@I18n
    @ApiOperation(value = "规则组绑定规则")
    @PostMapping(value = "bindRule")
    public void bindRule(@RequestBody BindRuleRequest request) throws Exception {
        ruleService.bindRule(request);
    }

//    //@I18n
//    @ApiOperation(value = "规则组检测云账号")
//    @GetMapping("scanByGroup/{groupId}/{accountId}")
//    public void scanByGroup(@PathVariable String groupId, @PathVariable String accountId) {
//        ruleService.scanByGroup(groupId, accountId);
//    }
}
