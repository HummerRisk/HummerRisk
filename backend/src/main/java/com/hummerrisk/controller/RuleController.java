package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.controller.request.rule.RuleTagRequest;
import com.hummerrisk.dto.RuleTagDTO;
import com.hummerrisk.base.domain.Rule;
import com.hummerrisk.base.domain.RuleGroup;
import com.hummerrisk.base.domain.RuleInspectionReport;
import com.hummerrisk.base.domain.RuleTag;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.request.rule.CreateRuleRequest;
import com.hummerrisk.controller.request.rule.RuleGroupRequest;
import com.hummerrisk.dto.GroupDTO;
import com.hummerrisk.dto.RuleDTO;
import com.hummerrisk.dto.RuleGroupDTO;
import com.hummerrisk.service.RuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 规则
 */
@Api(tags = "规则")
@RestController
@RequestMapping(value = "rule")
public class RuleController {
    @Resource
    private RuleService ruleService;

    @ApiOperation(value = "云账号规则列表")
    @PostMapping(value = "list/{goPage}/{pageSize}")
    public Pager<List<RuleDTO>> cloudList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CreateRuleRequest rule) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.cloudList(rule));
    }

    @ApiIgnore
    @GetMapping(value = "listByAccountId/{accountId}")
    public List<Rule> listByAccountId(@PathVariable String accountId) {
        return ruleService.getRulesByAccountId(accountId);
    }

    @ApiOperation(value = "规则标签列表")
    @PostMapping(value = "ruleTag/list/{goPage}/{pageSize}")
    public Pager<List<RuleTag>> ruleTagList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleTagRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.ruleTagList(request));
    }

    @ApiOperation(value = "规则组列表")
    @PostMapping(value = "ruleGroup/list/{goPage}/{pageSize}")
    public Pager<List<RuleGroupDTO>> ruleGroupList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleGroupRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.ruleGroupList(request));
    }

    @ApiOperation(value = "规则标签")
    @GetMapping(value = "ruleTags")
    public List<RuleTagDTO> getRuleTags() throws Exception {
        return ruleService.getRuleTags();
    }

    @ApiOperation(value = "添加规则")
    @PostMapping(value = "add")
    public Rule addRule(@RequestBody CreateRuleRequest createRuleRequest) {
        createRuleRequest.setId(null);
        return ruleService.saveRules(createRuleRequest);
    }

    @ApiOperation(value = "修改规则")
    @PostMapping(value = "update")
    public Rule updateRule(@RequestBody CreateRuleRequest createRuleRequest) {
        return ruleService.saveRules(createRuleRequest);
    }

    @ApiOperation(value = "复制规则")
    @PostMapping(value = "copy")
    public Rule copyRule(@RequestBody CreateRuleRequest createRuleRequest) {
        return ruleService.copyRule(createRuleRequest);
    }

    @ApiOperation(value = "运行规则")
    @PostMapping(value = "run")
    public Object runRule(@RequestBody RuleDTO ruleDTO) {
        return ruleService.runRules(ruleDTO);
    }

    @ApiOperation(value = "测试运行规则")
    @PostMapping(value = "dryRun")
    public Object dryRun(@RequestBody RuleDTO ruleDTO) throws Exception {
        return ruleService.dryRun(ruleDTO);
    }

    @ApiOperation(value = "删除规则")
    @GetMapping(value = "delete/{id}")
    public void deleteRule(@PathVariable String id) {
        ruleService.deleteRule(id);
    }

    @ApiOperation(value = "规则详情")
    @GetMapping(value = "get/{ruleId}")
    public RuleDTO getRule(@PathVariable String ruleId) throws Exception {
        return ruleService.getRuleById(ruleId);
    }

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

    @ApiOperation(value = "规则组")
    @GetMapping(value = "ruleGroups/{pluginId}")
    public List<RuleGroup> getRuleGroups(@PathVariable String pluginId) {
        return ruleService.getRuleGroups(pluginId);
    }

    @ApiOperation(value = "规则条例")
    @PostMapping(value = "ruleInspectionReports/{goPage}/{pageSize}")
    public Pager<List<RuleInspectionReport>> getRuleInspectionReports(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleInspectionReport ruleInspectionReport) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ruleService.getRuleInspectionReport(ruleInspectionReport));
    }

    @ApiIgnore
    @GetMapping(value = "all/ruleInspectionReport")
    public List<RuleInspectionReport> getRuleInspectionReport() {
        return ruleService.getRuleInspectionReport(new RuleInspectionReport());
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
    public void reScans(@PathVariable String accountId) {
        ruleService.reScans(accountId);
    }

    @ApiIgnore
    @GetMapping("reScan/{taskId}/{accountId}")
    public void reScan(@PathVariable String taskId, @PathVariable String accountId) throws Exception {
        ruleService.reScan(taskId, accountId);
    }

    @ApiOperation(value = "批量检测")
    @PostMapping("scan")
    public void scan(@RequestPart(value = "scanCheckedGroups") List<String> scanCheckedGroups) {
        ruleService.scan(scanCheckedGroups);
    }

    @ApiOperation(value = "历史检测")
    @GetMapping("insertScanHistory/{accountId}")
    public void insertScanHistory(@PathVariable String accountId) {
        ruleService.insertScanHistory(accountId);
    }

    @ApiOperation(value = "新增规则组")
    @RequestMapping(value = "group/save")
    public RuleGroup saveRuleGroup(@RequestBody RuleGroup ruleGroup) {
        return ruleService.saveRuleGroup(ruleGroup);
    }

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

    @ApiIgnore
    @PostMapping("groups")
    public List<GroupDTO> groups(@RequestPart(value = "selectIds") List<String> ids) {
        return ruleService.groups(ids);
    }
}
