package io.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.hummerrisk.base.domain.AccountWithBLOBs;
import io.hummerrisk.base.domain.RuleAccountParameter;
import io.hummerrisk.commons.constants.CloudAccountConstants;
import io.hummerrisk.commons.utils.PageUtils;
import io.hummerrisk.commons.utils.Pager;
import io.hummerrisk.controller.request.account.CloudAccountRequest;
import io.hummerrisk.controller.request.account.CreateCloudAccountRequest;
import io.hummerrisk.controller.request.account.UpdateCloudAccountRequest;
import io.hummerrisk.dto.AccountDTO;
import io.hummerrisk.dto.QuartzTaskDTO;
import io.hummerrisk.dto.RuleDTO;
import io.hummerrisk.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "云资源配置")
@RestController
@RequestMapping(value = "account")
public class AccountController {
    @Resource
    private AccountService accountService;

    @ApiOperation(value = "云账号列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<AccountDTO>> getCloudAccountList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudAccountRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.getCloudAccountList(request));
    }

    @ApiOperation(value = "所有云账号")
    @GetMapping("allList")
    public List<AccountDTO> getCloudAccountList() {
        CloudAccountRequest request = new CloudAccountRequest();
        request.setStatus(CloudAccountConstants.Status.VALID.name());
        return accountService.getCloudAccountList(request);
    }

    @ApiOperation(value = "云账号详情")
    @GetMapping("getAccount/{id}")
    public AccountWithBLOBs getAccount(@PathVariable String id) {
        return accountService.getAccount(id);
    }

    @ApiIgnore
    @GetMapping(value = "syncAll")
    public boolean syncAllCloudAccount() {
        try {
            accountService.syncRegions();
        } catch (Exception e){
            return false;
        }
        return true;
    }

    @ApiOperation(value = "批量校验云账号")
    @PostMapping("validate")
    public Boolean validate(@RequestBody List<String> selectIds) {
        return accountService.validate(selectIds);
    }

    @ApiOperation(value = "校验云账号")
    @PostMapping("validate/{id}")
    public Boolean validate(@PathVariable String id) {
        return accountService.validate(id);
    }

    @ApiOperation(value = "添加云账号")
    @PostMapping("add")
    public AccountWithBLOBs addAccount(@RequestBody CreateCloudAccountRequest request) {
        return accountService.addAccount(request);
    }

    @ApiOperation(value = "更新云账号")
    @PostMapping("update")
    public AccountWithBLOBs editAccount(@RequestBody UpdateCloudAccountRequest request) throws Exception {
        return accountService.editAccount(request);
    }

    @ApiOperation(value = "删除云账号")
    @PostMapping(value = "delete/{accountId}")
    public void deleteAccount(@PathVariable String accountId) {
        accountService.delete(accountId);
    }

    @ApiOperation(value = "获取云账号区域")
    @GetMapping("getRegions/{id}")
    public Object getRegions(@PathVariable String id) {
        return accountService.getRegions(id);
    }

    @ApiIgnore
    @PostMapping("string2PrettyFormat")
    public String string2PrettyFormat(@RequestBody AccountWithBLOBs accountWithBLOBs) {
        return accountService.string2PrettyFormat(accountWithBLOBs.getRegions());
    }

    @ApiOperation(value = "清除参数")
    @PostMapping("clean/parameter")
    public Object cleanParameter(@RequestBody List<RuleAccountParameter> list) {
        return accountService.cleanParameter(list);
    }

    @ApiOperation(value = "保存参数")
    @PostMapping("save/parameter")
    public Object saveParameter(@RequestBody List<QuartzTaskDTO> list) {
        return accountService.saveParameter(list);
    }

    @ApiOperation(value = "规则列表")
    @PostMapping(value = "rule/list/{goPage}/{pageSize}")
    public Pager<List<RuleDTO>> ruleList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QuartzTaskDTO dto) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.getRules(dto));
    }

    @ApiOperation(value = "规则组列表")
    @PostMapping("group/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> groupList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.groupList(params));
    }

    @ApiOperation(value = "等保条例列表")
    @PostMapping("report/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> reportList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.reportList(params));
    }

    @ApiOperation(value = "规则标签列表")
    @PostMapping("tag/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> tagList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.tagList(params));
    }

    @ApiOperation(value = "区域列表")
    @PostMapping("regions/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> regionsList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.regionsList(params));
    }

    @ApiOperation(value = "资源列表")
    @PostMapping("resource/list/{goPage}/{pageSize}")
    public Pager<List<Map<String, Object>>> resourceList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, accountService.resourceList(params));
    }

    @ApiOperation(value = "IAM策略信息")
    @GetMapping("iam/strategy/{type}")
    public String strategy(@PathVariable String type) throws Exception {
        return accountService.strategy(type);
    }

}
