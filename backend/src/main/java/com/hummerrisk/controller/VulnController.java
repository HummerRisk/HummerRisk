package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.request.account.CloudAccountRequest;
import com.hummerrisk.controller.request.rule.CreateRuleRequest;
import com.hummerrisk.dto.AccountDTO;
import com.hummerrisk.dto.RuleDTO;
import com.hummerrisk.service.VulnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "漏洞扫描配置")
@RestController
@RequestMapping(value = "vuln")
public class VulnController {

    @Resource
    private VulnService vulnService;

    @ApiOperation(value = "漏洞配置列表")
    @PostMapping("vulnList/{goPage}/{pageSize}")
    public Pager<List<AccountDTO>> getVulnList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudAccountRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, vulnService.getVulnList(request));
    }

    @ApiOperation(value = "漏洞规则列表")
    @PostMapping(value = "vulnRuleList/{goPage}/{pageSize}")
    public Pager<List<RuleDTO>> vulnRuleList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CreateRuleRequest rule) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, vulnService.vulnList(rule));
    }
}
