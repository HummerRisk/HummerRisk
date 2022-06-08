package io.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.hummerrisk.base.domain.OperationLog;
import io.hummerrisk.commons.utils.PageUtils;
import io.hummerrisk.commons.utils.Pager;
import io.hummerrisk.controller.request.log.OperayionLogRequest;
import io.hummerrisk.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "日志")
@RestController
@RequestMapping("log/operation")
public class OperationLogController {
    @Resource
    private OperationLogService operationLogService;

    @ApiOperation(value = "日志列表")
    @PostMapping("query/resource/{goPage}/{pageSize}")
    public Pager<List<OperationLog>> queryOperationLog(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody OperayionLogRequest dto) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, operationLogService.selectOperationLog(dto));
    }

    @ApiOperation(value = "资源日志列表")
    @GetMapping("query/resource/{resourceId}/{goPage}/{pageSize}")
    public Pager<List<OperationLog>> queryResourceOperationLog(@PathVariable String resourceId, @PathVariable int goPage, @PathVariable int pageSize) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, operationLogService.selectRersourceOperationLog(resourceId));
    }

}
