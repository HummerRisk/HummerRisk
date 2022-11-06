package com.hummerrisk.oss.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.oss.controller.request.OssRequest;
import com.hummerrisk.oss.dto.OssDTO;
import com.hummerrisk.oss.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "对象存储")
@RestController
@RequestMapping(value = "oss")
public class OssController {
    @Resource
    private OssService ossService;

    @I18n
    @ApiOperation(value = "对象存储列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<OssDTO>> ossList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody OssRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ossService.ossList(request));
    }

    @I18n
    @ApiOperation(value = "同步对象存储")
    @GetMapping("batch/sync/{id}")
    public void sync(@PathVariable String id) throws Exception {
        ossService.syncBatch(id);
    }

}
