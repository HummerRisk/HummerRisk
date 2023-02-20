package com.hummer.system.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.constant.RoleConstants;
import com.hummer.common.mapper.domain.Favorite;
import com.hummer.common.mapper.domain.Task;
import com.hummer.common.mapper.domain.TaskItem;
import com.hummer.common.mapper.domain.request.task.RuleVo;
import com.hummer.common.mapper.domain.request.task.TaskLogVo;
import com.hummer.common.mapper.domain.request.task.TaskRequest;
import com.hummer.common.mapper.domain.request.task.TaskVo;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.mapper.dto.*;
import com.hummer.common.mapper.handler.annotation.I18n;
import com.hummer.common.mapper.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "任务")
@RestController
@RequestMapping("task")
public class TaskController {

    @Resource
    private TaskService taskService;

    @I18n
    @ApiOperation(value = "收藏夹列表")
    @GetMapping(value = "favorite/list")
    public List<Favorite> listFavorites() throws Exception {
        return taskService.listFavorites();
    }

    @I18n
    @ApiOperation(value = "资源信息列表")
    @GetMapping(value = "account/list")
    public AccountTreeDTO listAccounts() throws Exception {
        return taskService.listAccounts();
    }

    @I18n
    @ApiOperation(value = "添加/取消收藏")
    @PostMapping(value = "addOrDelFavorite")
    public Favorite addOrDelFavorite(@RequestBody Favorite favorite) {
        return taskService.addOrDelFavorite(favorite);
    }

    @ApiOperation(value = "删除收藏")
    @GetMapping(value = "favorite/delete/{id}")
    public void deleteFavorite(@PathVariable String id) {
        taskService.deleteFavorite(id);
    }

    @I18n
    @ApiOperation(value = "全部检测列表")
    @PostMapping("allList/{goPage}/{pageSize}")
    public Pager<List<RuleVo>> allList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleVo ruleVo) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, taskService.allList(ruleVo));
    }

    @I18n
    @ApiOperation(value = "检测规则列表")
    @PostMapping("ruleList/{goPage}/{pageSize}")
    public Pager<List<RuleVo>> ruleList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleVo ruleVo) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, taskService.ruleList(ruleVo));
    }

    @I18n
    @ApiOperation(value = "检测规则标签列表")
    @PostMapping("ruleTagList/{goPage}/{pageSize}")
    public Pager<List<RuleVo>> ruleTagList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleVo ruleVo) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, taskService.ruleTagList(ruleVo));
    }

    @I18n
    @ApiOperation(value = "检测规则分组列表")
    @PostMapping("ruleGroupList/{goPage}/{pageSize}")
    public Pager<List<RuleVo>> ruleGroupList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleVo ruleVo) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, taskService.ruleGroupList(ruleVo));
    }

    @I18n
    @ApiOperation(value = "检测规则详情")
    @PostMapping(value = "detailRule")
    public TaskRuleDTO detailRule(@RequestBody RuleVo ruleVo) {
        return taskService.detailRule(ruleVo);
    }

    @I18n
    @ApiOperation(value = "检测规则标签详情")
    @PostMapping(value = "detailTag")
    public List<TaskTagGroupDTO> detailTag(@RequestBody RuleVo ruleVo) {
        return taskService.detailTag(ruleVo);
    }

    @I18n
    @ApiOperation(value = "检测规则分组详情")
    @PostMapping(value = "detailGroup")
    public List<TaskTagGroupDTO> detailGroup(@RequestBody RuleVo ruleVo) {
        return taskService.detailGroup(ruleVo);
    }

    @I18n
    @ApiOperation(value = "任务列表")
    @PostMapping("taskList/{goPage}/{pageSize}")
    public Pager<List<TaskVo>> taskList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody TaskRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, taskService.taskList(request));
    }

    @I18n
    @ApiOperation(value = "查询任务")
    @GetMapping(value = "getTask/{taskId}")
    public TaskVo getTask(@PathVariable String taskId) {
        return taskService.getTask(taskId);
    }

    @I18n
    @ApiOperation(value = "添加任务")
    @PostMapping(value = "addTask")
    public int addTask(@RequestBody TaskDTO taskDTO) throws Exception {
        return taskService.addTask(taskDTO);
    }

    @I18n
    @ApiOperation(value = "修改任务")
    @PostMapping(value = "updateTask")
    public int editTask(@RequestBody TaskDTO taskDTO) throws Exception {
        return taskService.editTask(taskDTO);
    }

    @ApiOperation(value = "删除任务")
    @GetMapping("/deleteTask/{taskId}")
    @RequiresRoles(RoleConstants.ADMIN)
    public void deleteTask(@PathVariable String taskId) throws Exception {
        taskService.deleteTask(taskId);
    }

    @I18n
    @ApiOperation(value = "子任务列表")
    @PostMapping("taskItemList")
    public List<TaskItem> taskItemList(@RequestBody TaskRequest request) {
        return taskService.taskItemList(request);
    }

    @ApiOperation(value = "执行任务")
    @GetMapping("execute/{id}")
    public void executeTask(@PathVariable String id) throws Exception {
        taskService.executeTask(id);
    }

    @ApiOperation(value = "重新执行任务")
    @GetMapping("reExecute/{id}")
    public void reExecute(@PathVariable String id) throws Exception {
        taskService.reExecute(id);
    }

    @I18n
    @ApiOperation(value = "查询执行任务日志")
    @PostMapping("taskLogList")
    public List<TaskLogVo> taskLogList(@RequestBody TaskRequest request) {
        return taskService.taskLogList(request);
    }

    @ApiOperation(value = "所有任务")
    @GetMapping("allTaskList")
    public List<Task> allTaskList() {
        return taskService.allTaskList();
    }

    @ApiOperation(value = "任务报告")
    @GetMapping("report/{id}")
    public TaskReportDTO report(@PathVariable String id) {
        return taskService.report(id);
    }
}
