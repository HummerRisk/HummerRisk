package com.hummerrisk.oss.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.AccountWithBLOBs;
import com.hummerrisk.base.domain.OssBucket;
import com.hummerrisk.base.domain.OssLogWithBLOBs;
import com.hummerrisk.base.domain.OssWithBLOBs;
import com.hummerrisk.commons.utils.EncryptUtils;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.oss.controller.request.OssRequest;
import com.hummerrisk.oss.dto.BucketObjectDTO;
import com.hummerrisk.oss.dto.OssBucketDTO;
import com.hummerrisk.oss.dto.OssDTO;
import com.hummerrisk.oss.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "对象存储")
@RestController
@RequestMapping(value = "oss")
public class OssController {
    @Resource
    private OssService ossService;

    @I18n
    @ApiOperation(value = "对象存储账号列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<OssDTO>> ossList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody OssRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ossService.ossList(request));
    }

    @I18n
    @ApiOperation(value = "云账号列表")
    @GetMapping("accounts")
    public List<AccountWithBLOBs> getCloudAccountList() {
        return ossService.getCloudAccountList();
    }

    @I18n
    @ApiOperation(value = "IAM策略信息")
    @GetMapping("iam/strategy/{accountId}")
    public String strategy(@PathVariable String accountId) throws Exception {
        return ossService.strategy(accountId);
    }

    @ApiOperation(value = "云账号详情")
    @GetMapping("changeAccount/{accountId}")
    public String getCredential(@PathVariable String accountId) {
        return ossService.getCredential(accountId);
    }

    @I18n
    @ApiOperation(value = "添加对象存储")
    @PostMapping("add")
    public OssWithBLOBs addOss(@RequestBody OssWithBLOBs request) throws Exception {
        return ossService.addOss(request);
    }

    @I18n
    @ApiOperation(value = "更新对象存储")
    @PostMapping("update")
    public OssWithBLOBs editOss(@RequestBody OssWithBLOBs request) throws Exception {
        return ossService.editOss(request);
    }

    @ApiOperation(value = "删除对象存储")
    @GetMapping(value = "delete/{ossId}")
    public void deleteOss(@PathVariable String ossId) {
        ossService.deleteOss(ossId);
    }

    @I18n
    @ApiOperation(value = "同步对象存储")
    @GetMapping("batch/sync/{id}")
    public void sync(@PathVariable String id) throws Exception {
        ossService.batch(id);
    }

    @I18n
    @ApiOperation(value = "对象存储同步日志")
    @GetMapping(value = "log/{ossId}")
    public List<OssLogWithBLOBs> getLogList(@PathVariable String ossId) {
        return ossService.getLogList(ossId);
    }

    @I18n
    @ApiOperation(value = "对象存储桶列表")
    @PostMapping("bucketList/{goPage}/{pageSize}")
    public Pager<List<OssBucketDTO>> ossBucketList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody OssRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, ossService.ossBucketList(request));
    }

    @I18n
    @ApiOperation("文件目录列表")
    @GetMapping("objects/{bucketId}")
    public List<BucketObjectDTO> getObjects(@PathVariable String bucketId) throws Exception{
        return ossService.getObjects(bucketId, null);
    }

    @I18n
    @ApiOperation("文件列表")
    @PostMapping("objects/{bucketId}")
    public List<BucketObjectDTO> getObjects(@PathVariable String bucketId, @RequestBody Map map) throws Exception{
        String path = map.get("path").toString();
        return ossService.getObjects(bucketId, path);
    }

}
