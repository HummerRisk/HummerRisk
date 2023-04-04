package com.hummer.system.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.domain.WebMsg;
import com.hummer.common.core.domain.request.webMsg.WebMsgRequest;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.system.service.WebMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "站内消息管理")
@RequestMapping("webmsg")
@RestController
public class WebMsgController {

    @Autowired
    private WebMsgService webMsgService;

    @I18n
    @ApiOperation("分页查询")
    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<WebMsg>> messages(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody WebMsgRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, webMsgService.queryGrid(request));
    }

    @ApiOperation("查询未读数量")
    @PostMapping("/unReadCount")
    public Long unReadCount() {
        return webMsgService.queryCount();
    }

    @ApiOperation("设置已读")
    @GetMapping("/setReaded/{msgId}")
    public void setReaded(@PathVariable Long msgId) {
        webMsgService.setReaded(msgId);
    }


    @ApiOperation("批量设置已读")
    @PostMapping("/batchRead")
    public void batchRead(@RequestBody List<Long> msgIds) {
        webMsgService.setBatchReaded(msgIds);
    }

    @ApiOperation("批量删除")
    @PostMapping("/batchDelete")
    public void batchDelete(@RequestBody List<Long> msgIds) {
        webMsgService.batchDelete(msgIds);
    }

}
