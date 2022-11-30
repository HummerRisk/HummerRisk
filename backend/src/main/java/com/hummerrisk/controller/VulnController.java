package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.CloudTask;
import com.hummerrisk.commons.utils.DashboardTarget;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.controller.request.account.CloudAccountRequest;
import com.hummerrisk.controller.request.cloudTask.ManualRequest;
import com.hummerrisk.controller.request.rule.CreateRuleRequest;
import com.hummerrisk.dto.AccountDTO;
import com.hummerrisk.dto.RuleDTO;
import com.hummerrisk.service.VulnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "漏洞检测配置")
@RestController
@RequestMapping(value = "vuln")
public class VulnController {

    @Resource
    private VulnService vulnService;

    @I18n
    @ApiOperation(value = "漏洞配置列表")
    @PostMapping("vulnList/{goPage}/{pageSize}")
    public Pager<List<AccountDTO>> getVulnList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudAccountRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, vulnService.getVulnList(request));
    }

    @I18n
    @ApiOperation(value = "漏洞规则列表")
    @PostMapping(value = "vulnRuleList/{goPage}/{pageSize}")
    public Pager<List<RuleDTO>> vulnRuleList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CreateRuleRequest rule) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, vulnService.vulnList(rule));
    }

    @I18n
    @ApiOperation(value = "漏洞结果列表")
    @PostMapping("manual/list/{goPage}/{pageSize}")
    public Pager<List<CloudTask>> getManualTasks(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ManualRequest request) throws Exception {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        request.setType("manual");
        return PageUtils.setPageInfo(page, vulnService.selectManualTasks(request));
    }

    @I18n
    @ApiOperation(value = "不合规统计")
    @PostMapping("point/target/{goPage}/{pageSize}")
    public Pager<List<DashboardTarget>> target(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, vulnService.target(params));
    }

    @I18n
    @ApiOperation(value = "规则组列表")
    @PostMapping("group/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> groupList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, vulnService.groupList(params));
    }

    @I18n
    @ApiOperation(value = "等保条例列表")
    @PostMapping("report/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> reportList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, vulnService.reportList(params));
    }

    @I18n
    @ApiOperation(value = "规则标签列表")
    @PostMapping("tag/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> tagList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, vulnService.tagList(params));
    }

    @I18n
    @ApiOperation(value = "资源列表")
    @PostMapping("resource/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> resourceList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, vulnService.resourceList(params));
    }

    @I18n
    @ApiOperation(value = "历史数据")
    @PostMapping("historyList/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> historyList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, vulnService.historyList(params));
    }

    @I18n
    @ApiOperation(value = "对比历史数据")
    @PostMapping("historyDiffList/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> historyDiffList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, vulnService.historyDiffList(params));
    }

}
