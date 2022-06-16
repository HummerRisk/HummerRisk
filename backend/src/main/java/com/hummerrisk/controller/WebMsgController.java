package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.WebMsg;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.service.WebMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "站内消息管理")
@RequestMapping("/webmsg")
@RestController
public class WebMsgController {

    @Resource
    private WebMsgService webMsgService;

    @ApiOperation("分页查询")
    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<WebMsg>> messages(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody WebMsg webMsg) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        Pager<List<WebMsg>> listPager = PageUtils.setPageInfo(page, webMsgService.queryGrid(webMsg));
        return listPager;
    }

    @ApiOperation("查询未读数量")
    @PostMapping("/unReadCount")
    public Long unReadCount(@RequestBody Map<String, String> request) {
        return webMsgService.queryCount();
    }

    @ApiOperation("设置已读")
    @PostMapping("/setReaded/{msgId}")
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
