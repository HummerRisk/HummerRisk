package com.hummer.system.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.domain.WebMsg;
import com.hummer.common.core.domain.request.webMsg.WebMsgRequest;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import com.hummer.system.service.WebMsgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "站内消息管理")
@RequestMapping("webmsg")
@RestController
public class WebMsgController {

    @Autowired
    private WebMsgService webMsgService;
    @Autowired
    private TokenService tokenService;

    @I18n
    @Operation(summary = "站内消息列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<WebMsg>> messages(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody WebMsgRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, webMsgService.queryGrid(request));
    }

    @Operation(summary = "查询未读数量")
    @PostMapping("unread/count")
    public Long unReadCount() {
        return webMsgService.queryCount();
    }

    @Operation(summary = "设置已读")
    @GetMapping("set/readed/{msgId}")
    public void setReaded(@PathVariable Long msgId) {
        webMsgService.setReaded(msgId, tokenService.getLoginUser());
    }


    @Operation(summary = "批量设置已读")
    @PostMapping("batch/read")
    public void batchRead(@RequestBody List<Long> msgIds) {
        webMsgService.setBatchReaded(msgIds, tokenService.getLoginUser());
    }

    @Operation(summary = "批量删除")
    @PostMapping("batch/delete")
    public void batchDelete(@RequestBody List<Long> msgIds) {
        webMsgService.batchDelete(msgIds);
    }

}
