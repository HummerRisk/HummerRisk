package com.hummer.system.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.constant.ParamConstants;
import com.hummer.common.core.domain.SystemParameter;
import com.hummer.common.core.domain.Webhook;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import com.hummer.system.service.SystemParameterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "系统设置")
@RestController
@RequestMapping(value = "system")
public class SystemParameterController {
    @Autowired
    private SystemParameterService systemParameterService;
    @Autowired
    private TokenService tokenService;

    @Operation(summary = "编辑邮箱设置")
    @PostMapping("/edit/email")
    public void editMail(@RequestBody List<SystemParameter> systemParameter) {
        systemParameterService.editMail(systemParameter);
    }

    @Operation(summary = "编辑企业微信设置")
    @PostMapping("/edit/wechat")
    public void editWechat(@RequestBody List<SystemParameter> systemParameter) {
        systemParameterService.editWechat(systemParameter);
    }

    @Operation(summary = "编辑钉钉设置")
    @PostMapping("/edit/dingding")
    public void editDingding(@RequestBody List<SystemParameter> systemParameter) {
        systemParameterService.editDingding(systemParameter);
    }

    @Operation(summary = "编辑检测参数设置")
    @PostMapping("/edit/scan/setting")
    public void editScanSetting(@RequestBody List<SystemParameter> systemParameter) {
        systemParameterService.edit(systemParameter);
    }

    @Operation(summary = "测试连接")
    @PostMapping("/test/connection/{type}")
    public void testConnection(@PathVariable String type, @RequestBody Map<String, String> hashMap) throws Exception {
        switch (type) {
            case "email":
                systemParameterService.testEmailConnection(hashMap);
                break;
            case "wechat":
                systemParameterService.testWechatConnection(hashMap);
                break;
            case "dingding":
                systemParameterService.testDingtalkConnection(hashMap);
                break;
            default:
                break;
        }
    }

    @I18n
    @Operation(summary = "邮件设置")
    @GetMapping("/mail/info")
    public List<SystemParameter> mailInfo() {
        return systemParameterService.info(ParamConstants.Classify.MAIL.getValue());
    }

    @I18n
    @Operation(summary = "企业微信设置")
    @GetMapping("/wechat/info")
    public List<SystemParameter> wechatInfo() {
        return systemParameterService.wechatInfo(ParamConstants.Classify.WECHAT.getValue());
    }

    @Operation(summary = "钉钉设置")
    @GetMapping("/dingding/info")
    public List<SystemParameter> dingdingInfo() {
        return systemParameterService.dingdingInfo(ParamConstants.Classify.DINGDING.getValue());
    }

    @I18n
    @Operation(summary = "检测参数设置")
    @GetMapping("/scan/setting/info")
    public List<SystemParameter> scanSettingInfo() {
        return systemParameterService.scanSettingInfo(ParamConstants.Classify.SCAN.getValue());
    }

    @I18n
    @Operation(summary = "消息通知")
    @GetMapping("/message/info")
    public List<SystemParameter> messageInfo() {
        return systemParameterService.info(ParamConstants.Classify.MESSAGE.getValue());
    }

    @Operation(summary = "编辑消息通知")
    @PostMapping("/edit/message")
    public void editMessage(@RequestBody List<SystemParameter> systemParameter) {
        systemParameterService.edit(systemParameter);
    }

    @Operation(summary = "刷新系统参数信息")
    @GetMapping("/update/system")
    public void updateSystem() throws Exception {
        systemParameterService.updateSystem();
    }

    @I18n
    @Operation(summary = "查询系统参数信息")
    @GetMapping("/search/system")
    public List<SystemParameter> searchSystem() throws Exception {
        return systemParameterService.info(ParamConstants.Classify.SYSTEM.getValue());
    }

    @I18n
    @Operation(summary = "webhook列表")
    @PostMapping("webhook/list/{goPage}/{pageSize}")
    public Pager<List<Webhook>> getWebhookList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Webhook webhook) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, systemParameterService.getWebhookList(webhook));
    }

    @Operation(summary = "添加webhook")
    @PostMapping("add/webhook")
    public int addWebhook(@RequestBody Webhook webhook) {
        return systemParameterService.addWebhook(webhook, tokenService.getLoginUser());
    }

    @Operation(summary = "修改webhook")
    @PostMapping("edit/webhook")
    public int editWebhook(@RequestBody Webhook webhook) throws Exception {
        return systemParameterService.editWebhook(webhook, tokenService.getLoginUser());
    }

    @Operation(summary = "删除webhook")
    @GetMapping("delete/webhook/{id}")
    public void deleteWebhook(@PathVariable String id) throws Exception {
        systemParameterService.deleteWebhook(id, tokenService.getLoginUser());
    }

    @Operation(summary = "启用webhook")
    @PostMapping(value = "change/status")
    public int changeStatus(@RequestBody Webhook webhook) {
        return systemParameterService.changeStatus(webhook);
    }

}
