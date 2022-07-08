package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.Favorite;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.request.rule.CreateRuleRequest;
import com.hummerrisk.controller.request.server.ServerRequest;
import com.hummerrisk.controller.request.task.RuleVo;
import com.hummerrisk.dto.AccountTreeDTO;
import com.hummerrisk.dto.RuleDTO;
import com.hummerrisk.dto.ServerDTO;
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

    @ApiOperation(value = "检测规则列表")
    @PostMapping("ruleList/{goPage}/{pageSize}")
    public Pager<List<RuleVo>> ruleList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody RuleVo ruleVo) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, taskService.ruleList(ruleVo));
    }
}
