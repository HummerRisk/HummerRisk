package com.hummer.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.cloud.service.AccountService;
import com.hummer.common.core.constant.CloudAccountConstants;
import com.hummer.common.core.domain.Account;
import com.hummer.common.core.domain.AccountWithBLOBs;
import com.hummer.common.core.domain.RuleAccountParameter;
import com.hummer.common.core.domain.request.account.CloudAccountRequest;
import com.hummer.common.core.domain.request.account.CreateCloudAccountRequest;
import com.hummer.common.core.domain.request.account.UpdateCloudAccountRequest;
import com.hummer.common.core.dto.AccountDTO;
import com.hummer.common.core.dto.QuartzTaskDTO;
import com.hummer.common.core.dto.RuleDTO;
import com.hummer.common.core.dto.ValidateDTO;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;

@Tag(name = "云资源配置")
@RestController
@RequestMapping(value = "account")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private TokenService tokenService;

    @I18n
    @Operation(summary = "云账号列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<AccountDTO>> getCloudAccountList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudAccountRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.getCloudAccountList(request));
    }

    @I18n
    @Operation(summary = "所有云账号")
    @GetMapping("allList")
    public List<AccountDTO> getCloudAccountList() {
        CloudAccountRequest request = new CloudAccountRequest();
        request.setStatus(CloudAccountConstants.Status.VALID.name());
        return accountService.getCloudAccountList(request);
    }

    @I18n
    @Operation(summary = "规则组获取云账号")
    @GetMapping("listByGroup/{pluginId}")
    public List<Account> listByGroup(@PathVariable String pluginId) {
        return accountService.listByGroup(pluginId);
    }

    @I18n
    @Operation(summary = "云账号详情")
    @GetMapping("getAccount/{id}")
    public AccountWithBLOBs getAccount(@PathVariable String id) {
        return accountService.getAccount(id);
    }

    @Hidden
    @GetMapping(value = "syncAll")
    public boolean syncAllCloudAccount() {
        try {
            accountService.syncRegions();
        } catch (Exception e){
            return false;
        }
        return true;
    }

    @Operation(summary = "批量校验云账号")
    @PostMapping("validate")
    public List<ValidateDTO> validate(@RequestBody List<String> selectIds) {
        return accountService.validate(selectIds);
    }

    @Operation(summary = "校验云账号")
    @PostMapping("validate/{id}")
    public ValidateDTO validate(@PathVariable String id) {
        return accountService.validate(id);
    }

    @I18n
    @Operation(summary = "添加云账号")
    @PostMapping("add")
    public AccountWithBLOBs addAccount(@RequestBody CreateCloudAccountRequest request) throws Exception {
        return accountService.addAccount(request, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "更新云账号")
    @PostMapping("update")
    public AccountWithBLOBs editAccount(@RequestBody UpdateCloudAccountRequest request) throws Exception {
        return accountService.editAccount(request, tokenService.getLoginUser());
    }

    @Operation(summary = "删除云账号")
    @PostMapping(value = "delete/{accountId}")
    public void deleteAccount(@PathVariable String accountId) {
        accountService.delete(accountId, tokenService.getLoginUser());
    }

    @Operation(summary = "批量删除云账号")
    @PostMapping("deletes")
    public void deletes(@RequestBody List<String> selectIds) throws Exception {
        accountService.deletes(selectIds, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "获取云账号区域")
    @GetMapping("getRegions/{id}")
    public Object getRegions(@PathVariable String id) {
        return accountService.getRegions(id);
    }

    @I18n
    @Hidden
    @PostMapping("string2PrettyFormat")
    public String string2PrettyFormat(@RequestBody AccountWithBLOBs accountWithBLOBs) {
        return accountService.string2PrettyFormat(accountWithBLOBs.getRegions());
    }

    @Operation(summary = "清除参数")
    @PostMapping("clean/parameter")
    public boolean cleanParameter(@RequestBody List<RuleAccountParameter> list) {
        return accountService.cleanParameter(list, tokenService.getLoginUser());
    }

    @Operation(summary = "保存参数")
    @PostMapping("save/parameter")
    public boolean saveParameter(@RequestBody List<QuartzTaskDTO> list) {
        return accountService.saveParameter(list, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "规则列表")
    @PostMapping(value = "rule/list/{goPage}/{pageSize}")
    public Pager<List<RuleDTO>> ruleList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QuartzTaskDTO dto) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.getRules(dto));
    }

    @I18n
    @Operation(summary = "规则组列表")
    @PostMapping("group/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> groupList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.groupList(params));
    }

    @I18n
    @Operation(summary = "等保条例列表")
    @PostMapping("report/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> reportList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.reportList(params));
    }

    @I18n
    @Operation(summary = "规则标签列表")
    @PostMapping("tag/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> tagList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.tagList(params));
    }

    @I18n
    @Operation(summary = "区域列表")
    @PostMapping("regions/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> regionsList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.regionsList(params));
    }

    @I18n
    @Operation(summary = "资源列表")
    @PostMapping("resource/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> resourceList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.resourceList(params));
    }

    @I18n
    @Operation(summary = "IAM策略信息")
    @GetMapping("iam/strategy/{type}")
    public String strategy(@PathVariable String type) throws Exception {
        return accountService.strategy(type);
    }

    @I18n
    @Operation(summary = "历史数据")
    @PostMapping("historyList/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> historyList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.historyList(params));
    }

    @I18n
    @Operation(summary = "对比历史数据")
    @PostMapping("historyDiffList/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> historyDiffList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.historyDiffList(params));
    }

}
