package com.hummer.system.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.constant.ParamConstants;
import com.hummer.common.core.domain.SystemParameter;
import com.hummer.common.core.domain.Webhook;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.system.service.SystemParameterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "系统设置")
@RestController
@RequestMapping(value = "/system")
public class SystemParameterController {
    @Autowired
    private SystemParameterService systemParameterService;

    @ApiOperation(value = "编辑邮箱设置")
    @PostMapping("/edit/email")
    public void editMail(@RequestBody List<SystemParameter> systemParameter) {
        systemParameterService.editMail(systemParameter);
    }

    @ApiOperation(value = "编辑企业微信设置")
    @PostMapping("/edit/wechat")
    public void editWechat(@RequestBody List<SystemParameter> systemParameter) {
        systemParameterService.editWechat(systemParameter);
    }

    @ApiOperation(value = "编辑钉钉设置")
    @PostMapping("/edit/dingding")
    public void editDingding(@RequestBody List<SystemParameter> systemParameter) {
        systemParameterService.editDingding(systemParameter);
    }

    @ApiOperation(value = "编辑检测参数设置")
    @PostMapping("/edit/scanSetting")
    public void editScanSetting(@RequestBody List<SystemParameter> systemParameter) {
        systemParameterService.edit(systemParameter);
    }

    @ApiOperation(value = "测试连接")
    @PostMapping("/testConnection/{type}")
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
    @ApiOperation(value = "邮件设置")
    @GetMapping("/mail/info")
    public List<SystemParameter> mailInfo() {
        return systemParameterService.info(ParamConstants.Classify.MAIL.getValue());
    }

    @I18n
    @ApiOperation(value = "企业微信设置")
    @GetMapping("/wechat/info")
    public List<SystemParameter> wechatInfo() {
        return systemParameterService.wechatInfo(ParamConstants.Classify.WECHAT.getValue());
    }

    @ApiOperation(value = "钉钉设置")
    @GetMapping("/dingding/info")
    public List<SystemParameter> dingdingInfo() {
        return systemParameterService.dingdingInfo(ParamConstants.Classify.DINGDING.getValue());
    }

    @I18n
    @ApiOperation(value = "检测参数设置")
    @GetMapping("/scanSetting/info")
    public List<SystemParameter> scanSettingInfo() {
        return systemParameterService.scanSettingInfo(ParamConstants.Classify.SCAN.getValue());
    }

    @I18n
    @ApiOperation(value = "消息通知")
    @GetMapping("/message/info")
    public List<SystemParameter> messageInfo() {
        return systemParameterService.info(ParamConstants.Classify.MESSAGE.getValue());
    }

    @ApiOperation(value = "编辑消息通知")
    @PostMapping("/edit/message")
    public void editMessage(@RequestBody List<SystemParameter> systemParameter) {
        systemParameterService.edit(systemParameter);
    }

    @ApiOperation(value = "刷新系统参数信息")
    @GetMapping("/updateSystem")
    public void updateSystem() throws Exception {
        systemParameterService.updateSystem();
    }

    @I18n
    @ApiOperation(value = "查询系统参数信息")
    @GetMapping("/searchSystem")
    public List<SystemParameter> searchSystem() throws Exception {
        return systemParameterService.info(ParamConstants.Classify.SYSTEM.getValue());
    }

    @I18n
    @ApiOperation(value = "在线更新漏洞库")
    @GetMapping("/updateVulnDb")
    public void updateVulnDb() throws Exception {
        systemParameterService.updateVulnDb();
    }

    @I18n
    @ApiOperation(value = "webhook列表")
    @PostMapping("webhookList/{goPage}/{pageSize}")
    public Pager<List<Webhook>> getWebhookList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Webhook webhook) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, systemParameterService.getWebhookList(webhook));
    }

    @ApiOperation(value = "添加webhook")
    @PostMapping("add/webhook")
    public int addWebhook(@RequestBody Webhook webhook) {
        return systemParameterService.addWebhook(webhook);
    }

    @ApiOperation(value = "修改webhook")
    @PostMapping("edit/webhook")
    public int editWebhook(@RequestBody Webhook webhook) throws Exception {
        return systemParameterService.editWebhook(webhook);
    }

    @ApiOperation(value = "删除webhook")
    @GetMapping("delete/webhook/{id}")
    public void deleteWebhook(@PathVariable String id) throws Exception {
        systemParameterService.deleteWebhook(id);
    }

    @ApiOperation(value = "启用webhook")
    @PostMapping(value = "changeStatus")
    public int changeStatus(@RequestBody Webhook webhook) {
        return systemParameterService.changeStatus(webhook);
    }

}
