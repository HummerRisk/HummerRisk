package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.controller.request.server.ServerCertificateRequest;
import com.hummerrisk.controller.request.server.ServerRequest;
import com.hummerrisk.controller.request.server.ServerResultRequest;
import com.hummerrisk.controller.request.server.ServerRuleRequest;
import com.hummerrisk.dto.*;
import com.hummerrisk.service.ServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "主机管理")
@RestController
@RequestMapping(value = "server")
public class ServerController {
    @Resource
    private ServerService serverService;

    @ApiOperation(value = "所有主机分组")
    @GetMapping("serverGroupList")
    public List<ServerGroup> getServerGroupList() {
        return serverService.getServerGroupList();
    }

    @I18n
    @ApiOperation(value = "所有主机")
    @GetMapping("allServerList")
    public List<Server> allServerList() {
        return serverService.allServerList();
    }

    @I18n
    @ApiOperation(value = "主机列表")
    @PostMapping("serverList/{goPage}/{pageSize}")
    public Pager<List<ServerDTO>> getServerList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody ServerRequest server) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, serverService.getServerList(server));
    }

    @ApiOperation(value = "查询主机")
    @GetMapping("getServer/{id}")
    public Server getServer(@PathVariable String id) throws Exception {
        return serverService.getServer(id);
    }

    @ApiOperation(value = "批量校验主机连通性")
    @PostMapping("validate")
    public List<ServerValidateDTO> validate(@RequestBody List<String> selectIds) {
        return serverService.validate(selectIds);
    }

    @ApiOperation(value = "校验主机配置连通性")
    @PostMapping("validate/{id}")
    public ServerValidateDTO validate(@PathVariable String id) {
        return serverService.validate(id);
    }

    @ApiOperation(value = "一键检测主机规则")
    @PostMapping("scan")
    public Boolean scan(@RequestBody List<String> selectIds) {
        return serverService.scan(selectIds);
    }

    @ApiOperation(value = "添加主机分组")
    @PostMapping("addServerGroup")
    public int addServerGroup(@RequestBody ServerGroup serverGroup) {
        return serverService.addServerGroup(serverGroup);
    }

    @ApiOperation(value = "修改主机分组")
    @PostMapping("editServerGroup")
    public int editServerGroup(@RequestBody ServerGroup serverGroup) throws Exception {
        return serverService.editServerGroup(serverGroup);
    }

    @ApiOperation(value = "删除主机分组")
    @PostMapping("deleteServerGroup")
    public void deleteServerGroup(@RequestBody ServerGroup serverGroup) throws Exception {
        serverService.deleteServerGroup(serverGroup);
    }

    @ApiOperation(value = "添加主机")
    @PostMapping(value = "addServer", consumes = {"multipart/form-data"})
    public ServerValidateDTO addServer(@RequestPart(value = "keyFile", required = false) MultipartFile keyFile,
                         @RequestPart("request") Server request) throws Exception {
        return serverService.addServer(keyFile, request);
    }

    @ApiOperation(value = "编辑主机")
    @PostMapping(value = "editServer", consumes = {"multipart/form-data"})
    public ServerValidateDTO editServer(@RequestPart(value = "keyFile", required = false) MultipartFile keyFile,
                          @RequestPart("request") Server request) throws Exception {
        return serverService.editServer(keyFile, request);
    }

    @ApiOperation(value = "复制主机")
    @PostMapping(value = "copyServer", consumes = {"multipart/form-data"})
    public ServerValidateDTO copyServer(@RequestPart(value = "keyFile", required = false) MultipartFile keyFile,
                                        @RequestPart("request") Server request) throws Exception {
        return serverService.copyServer(keyFile, request);
    }

    @ApiOperation(value = "删除主机")
    @GetMapping("deleteServer/{id}")
    public void deleteServer(@PathVariable String id) throws Exception {
        serverService.deleteServer(id);
    }

    @I18n
    @ApiOperation(value = "主机规则列表")
    @PostMapping(value = "ruleList/{goPage}/{pageSize}")
    public Pager<List<ServerRuleDTO>> ruleList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ServerRuleRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, serverService.ruleList(request));
    }

    @ApiOperation(value = "添加主机规则")
    @PostMapping(value = "addServerRule")
    public int addServerRule(@RequestBody ServerRuleRequest request) throws Exception {
        request.setId(null);
        return serverService.addServerRule(request);
    }

    @ApiOperation(value = "修改主机规则")
    @PostMapping(value = "updateServerRule")
    public int updateServerRule(@RequestBody ServerRuleRequest request) throws Exception {
        return serverService.updateServerRule(request);
    }

    @ApiOperation(value = "删除主机规则")
    @GetMapping(value = "deleteServerRule/{id}")
    public void deleteServerRule(@PathVariable String id) throws Exception  {
        serverService.deleteServerRule(id);
    }

    @ApiOperation(value = "主机规则启用")
    @PostMapping(value = "changeStatus")
    public int changeStatus(@RequestBody ServerRule rule) throws Exception {
        return serverService.changeStatus(rule);
    }

    @I18n
    @ApiOperation(value = "主机检测结果列表")
    @PostMapping(value = "resultList/{goPage}/{pageSize}")
    public Pager<List<ServerResultDTO>> resultList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ServerResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, serverService.resultList(request));
    }

    @I18n
    @ApiOperation(value = "主机检测结果")
    @GetMapping(value = "getServerResult/{resultId}")
    public ServerResultDTO getServerResult(@PathVariable String resultId) {
        return serverService.getServerResult(resultId);
    }

    @I18n
    @ApiOperation(value = "主机检测日志")
    @GetMapping(value = "log/{resultId}")
    public List<ServerResultLogWithBLOBs> getServerResultLog(@PathVariable String resultId) {
        return serverService.getServerResultLog(resultId);
    }

    @ApiOperation(value = "重新检测主机规则")
    @GetMapping("rescan/{id}")
    public void rescan(@PathVariable String id) throws Exception {
        serverService.rescan(id);
    }

    @ApiOperation(value = "删除主机检测记录")
    @GetMapping("deleteServerResult/{id}")
    public void deleteServerResult(@PathVariable String id) throws Exception {
        serverService.deleteServerResult(id);
    }

    @ApiOperation(value = "所有主机凭据")
    @GetMapping("allCertificateList")
    public List<ServerCertificate> allCertificateList() {
        return serverService.allCertificateList();
    }

    @I18n
    @ApiOperation(value = "主机凭据列表")
    @PostMapping("certificateList/{goPage}/{pageSize}")
    public Pager<List<ServerCertificateDTO>> certificateList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody ServerCertificateRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, serverService.certificateList(request));
    }

    @ApiOperation(value = "添加主机凭据")
    @PostMapping(value = "addCertificate", consumes = {"multipart/form-data"})
    public int addCertificate(@RequestPart(value = "keyFile", required = false) MultipartFile keyFile,
                         @RequestPart("request") ServerCertificate request) throws Exception {
        return serverService.addCertificate(keyFile, request);
    }

    @ApiOperation(value = "编辑主机凭据")
    @PostMapping(value = "editCertificate", consumes = {"multipart/form-data"})
    public int editCertificate(@RequestPart(value = "keyFile", required = false) MultipartFile keyFile,
                          @RequestPart("request") ServerCertificate request) throws Exception {
        return serverService.editCertificate(keyFile, request);
    }

    @ApiOperation(value = "删除主机凭据")
    @GetMapping("deleteCertificate/{id}")
    public void deleteCertificate(@PathVariable String id) throws Exception {
        serverService.deleteCertificate(id);
    }

    @I18n
    @ApiOperation(value = "概览TOP统计")
    @PostMapping("topInfo")
    public Map<String, Object> topInfo(@RequestBody Map<String, Object> params) {
        return serverService.topInfo(params);
    }

    @I18n
    @ApiOperation(value = "主机统计")
    @GetMapping("serverChart")
    public List<Map<String, Object>> serverChart() {
        return serverService.serverChart();
    }

    @I18n
    @ApiOperation(value = "主机风险统计")
    @GetMapping("severityChart")
    public List<Map<String, Object>> severityChart() {
        return serverService.severityChart();
    }

    @I18n
    @ApiOperation(value = "检测统计")
    @PostMapping("serverLineChart")
    public ChartDTO serverLineChart(@RequestBody Map<String, Object> params) {
        return serverService.serverLineChart(params);
    }

    @I18n
    @ApiOperation(value = "主机检测历史记录")
    @PostMapping("history/{goPage}/{pageSize}")
    public Pager<List<HistoryServerResultDTO>> history(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, serverService.history(params));
    }

    @I18n
    @ApiOperation(value = "删除主机检测历史记录")
    @GetMapping("deleteHistoryServerResult/{id}")
    public void deleteHistoryServerResult(@PathVariable String id) throws Exception {
        serverService.deleteHistoryServerResult(id);
    }

    @I18n
    @ApiOperation("通过Excel导入专家数据")
    @PostMapping("/ExcelInsertExperts")
    public void insertExperts(@RequestParam("file") MultipartFile file) throws Exception {
        serverService.insertExperts(file);
    }

}
