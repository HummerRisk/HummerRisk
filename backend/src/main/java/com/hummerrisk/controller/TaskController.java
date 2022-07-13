package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.Favorite;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.request.task.RuleVo;
import com.hummerrisk.dto.AccountTreeDTO;
import com.hummerrisk.dto.TaskRuleDTO;
import com.hummerrisk.dto.TaskTagGroupDTO;
import com.hummerrisk.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "任务")
@RestController
@RequestMapping("task")
public class TaskController {

    @Resource
    private TaskService taskService;

    @ApiOperation(value = "收藏夹列表")
    @GetMapping(value = "favorite/list")
    public List<Favorite> listFavorites() throws Exception {
        return taskService.listFavorites();
    }

    @ApiOperation(value = "资源信息列表")
    @GetMapping(value = "account/list")
    public AccountTreeDTO listAccounts() throws Exception {
        return taskService.listAccounts();
    }

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

    @ApiOperation(value = "全部检测列表")
    @PostMapping("allList/{goPage}/{pageSize}")
    public Pager<List<RuleVo>> allList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleVo ruleVo) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, taskService.allList(ruleVo));
    }

    @ApiOperation(value = "检测规则列表")
    @PostMapping("ruleList/{goPage}/{pageSize}")
    public Pager<List<RuleVo>> ruleList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleVo ruleVo) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, taskService.ruleList(ruleVo));
    }

    @ApiOperation(value = "检测规则标签列表")
    @PostMapping("ruleTagList/{goPage}/{pageSize}")
    public Pager<List<RuleVo>> ruleTagList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleVo ruleVo) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, taskService.ruleTagList(ruleVo));
    }

    @ApiOperation(value = "检测规则分组列表")
    @PostMapping("ruleGroupList/{goPage}/{pageSize}")
    public Pager<List<RuleVo>> ruleGroupList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleVo ruleVo) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, taskService.ruleGroupList(ruleVo));
    }

    @ApiOperation(value = "检测规则详情")
    @PostMapping(value = "detailRule")
    public TaskRuleDTO detailRule(@RequestBody RuleVo ruleVo) {
        return taskService.detailRule(ruleVo);
    }

    @ApiOperation(value = "检测规则标签详情")
    @PostMapping(value = "detailTag")
    public List<TaskTagGroupDTO> detailTag(@RequestBody RuleVo ruleVo) {
        return taskService.detailTag(ruleVo);
    }

    @ApiOperation(value = "检测规则分组详情")
    @PostMapping(value = "detailGroup")
    public List<TaskTagGroupDTO> detailGroup(@RequestBody RuleVo ruleVo) {
        return taskService.detailGroup(ruleVo);
    }
}
