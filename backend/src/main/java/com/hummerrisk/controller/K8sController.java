package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.controller.request.cloudNative.*;
import com.hummerrisk.controller.request.image.ImageRequest;
import com.hummerrisk.controller.request.k8s.*;
import com.hummerrisk.dto.*;
import com.hummerrisk.service.K8sService;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(tags = "云原生")
@RestController
@RequestMapping(value = "k8s")
public class K8sController {
    @Resource
    private K8sService k8sService;

    @I18n
    @ApiOperation(value = "云原生账号列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<CloudNativeDTO>> getCloudNativeList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudNativeRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.getCloudNativeList(request));
    }

    @I18n
    @ApiOperation(value = "所有云原生账号")
    @GetMapping("allCloudNativeList")
    public List<CloudNativeDTO> allCloudNativeList() {
        return k8sService.allCloudNativeList(new CloudNativeRequest());
    }

    @I18n
    @ApiOperation(value = "云原生账号详情")
    @GetMapping("getCloudNative/{id}")
    public CloudNative getCloudNative(@PathVariable String id) {
        return k8sService.getCloudNative(id);
    }

    @ApiOperation(value = "批量校验云原生账号")
    @PostMapping("validate")
    public List<ValidateDTO> validate(@RequestBody List<String> selectIds) {
        return k8sService.validate(selectIds);
    }

    @ApiOperation(value = "校验云原生账号")
    @PostMapping("validate/{id}")
    public ValidateDTO validate(@PathVariable String id) throws IOException, ApiException {
        return k8sService.validate(id);
    }

    @ApiOperation(value = "校验云原生Operator状态")
    @PostMapping("operatorStatusValidate/{id}")
    public ValidateDTO operatorStatusValidate(@PathVariable String id) throws Exception {
        return k8sService.operatorStatusValidate(id);
    }

    @I18n
    @ApiOperation(value = "添加云原生账号")
    @PostMapping("add")
    public ValidateDTO addCloudNative(@RequestBody CreateCloudNativeRequest request) {
        return k8sService.addCloudNative(request);
    }

    @I18n
    @ApiOperation(value = "更新云原生账号")
    @PostMapping("update")
    public ValidateDTO editCloudNative(@RequestBody UpdateCloudNativeRequest request) throws Exception {
        return k8sService.editCloudNative(request);
    }

    @ApiOperation(value = "删除云原生账号")
    @PostMapping(value = "delete/{accountId}")
    public void deleteAccount(@PathVariable String accountId) {
        k8sService.delete(accountId);
    }

    @I18n
    @ApiOperation(value = "资源态势列表")
    @PostMapping("cloudNativeSourceList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeSourceDTO>> getCloudNativeSourceList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudNativeSourceRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.getCloudNativeSourceList(request));
    }

    @I18n
    @ApiOperation(value = "资源态势")
    @PostMapping("situation")
    public SituationDTO situationInfo(@RequestBody Map<String, Object> params) {
        return k8sService.situationInfo(params);
    }

    @I18n
    @ApiOperation(value = "云原生检测")
    @GetMapping("scan/{id}")
    public void scan(@PathVariable String id) throws Exception {
        k8sService.scan(id);
    }

    @ApiOperation(value = "重新云原生检测")
    @GetMapping("reScan/{id}")
    public void reScan(@PathVariable String id) throws Exception {
        k8sService.reScan(id);
    }

    @I18n
    @ApiOperation(value = "云原生检测结果列表")
    @PostMapping(value = "resultList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeResultDTO>> resultList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.resultList(request));
    }

    @ApiOperation(value = "删除镜像检测记录")
    @GetMapping("deleteCloudNativeResult/{id}")
    public void deleteCloudNativeResult(@PathVariable String id) throws Exception {
        k8sService.deleteCloudNativeResult(id);
    }

    @I18n
    @ApiOperation(value = "镜像列表")
    @PostMapping("imageList/{goPage}/{pageSize}")
    public Pager<List<ImageDTO>> imageList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.imageList(request));
    }

    @I18n
    @ApiOperation(value = "云原生漏洞检测结果详情列表")
    @PostMapping("resultItemList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeResultItem>> resultItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.resultItemList(request));
    }

    @I18n
    @ApiOperation(value = "云原生配置审计结果详情列表")
    @PostMapping("resultConfigItemList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeResultConfigItemWithBLOBs>> resultConfigItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.resultConfigItemList(request));
    }

    @I18n
    @ApiIgnore
    @GetMapping(value = "getCloudNativeResult/{resultId}")
    public CloudNativeResultDTO getCloudNativeResult(@PathVariable String resultId) {
        return k8sService.getCloudNativeResult(resultId);
    }

    @I18n
    @ApiOperation(value = "云原生检测结果详情")
    @GetMapping(value = "getCloudNativeResultWithBLOBs/{resultId}")
    public CloudNativeResultWithBLOBs getCloudNativeResultWithBLOBs(@PathVariable String resultId) {
        return k8sService.getCloudNativeResultWithBLOBs(resultId);
    }

    @I18n
    @ApiIgnore
    @GetMapping(value = "getCloudNativeResultWithBLOBs/topo/{accountId}")
    public CloudNativeResultWithBLOBs topoResult(@PathVariable String accountId) {
        return k8sService.topoResult(accountId);
    }

    @I18n
    @ApiOperation(value = "云原生检测日志")
    @GetMapping(value = "log/{resultId}")
    public List<CloudNativeResultLogWithBLOBs> getCloudNativeResultLog(@PathVariable String resultId) {
        return k8sService.getCloudNativeResultLog(resultId);
    }

    @I18n
    @ApiIgnore
    @GetMapping(value = "log/topo/{accountId}")
    public List<CloudNativeResultLogWithBLOBs> topoLog(@PathVariable String accountId) {
        return k8sService.topoLog(accountId);
    }

    @I18n
    @ApiOperation(value = "所有带有 YAML 的云原生资源信息")
    @GetMapping("allCloudNativeSource2YamlList")
    public List<CloudNativeSourceWithBLOBs> allCloudNativeSource2YamlList() {
        return k8sService.allCloudNativeSource2YamlList();
    }

    @I18n
    @ApiOperation(value = "资源态势同步日志列表")
    @PostMapping("syncList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeSourceSyncLogWithBLOBsDTO>> syncList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudNativeSyncLogRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.syncList(request));
    }

    @I18n
    @ApiOperation(value = "同步资源态势资源")
    @GetMapping("syncSource/{id}")
    public void syncSource(@PathVariable String id) throws Exception {
        k8sService.syncSource(id);
    }

    @ApiOperation(value = "删除资源态势同步日志")
    @GetMapping("deleteSyncLog/{id}")
    public void deleteSyncLog(@PathVariable String id) throws Exception {
        k8sService.deleteSyncLog(id);
    }

    @I18n
    @ApiOperation(value = "漏洞风险数据信息")
    @GetMapping("metricChart/{resultId}")
    public MetricChartDTO metricChart(@PathVariable String resultId) {
        return k8sService.metricChart(resultId);
    }

    @I18n
    @ApiOperation(value = "配置审计风险数据信息")
    @GetMapping("metricConfigChart/{resultId}")
    public MetricChartDTO metricConfigChart(@PathVariable String resultId) {
        return k8sService.metricConfigChart(resultId);
    }

    @ApiOperation(value = "下载检测报告")
    @PostMapping("download")
    public String download(@RequestBody Map<String, Object> map) throws Exception {
        return k8sService.download(map);
    }

    @I18n
    @ApiOperation(value = "概览TOP统计")
    @PostMapping("topInfo")
    public Map<String, Object> topInfo(@RequestBody Map<String, Object> params) {
        return k8sService.topInfo(params);
    }

    @I18n
    @ApiOperation(value = "K8s 统计")
    @GetMapping("k8sChart")
    public List<Map<String, Object>> k8sChart() {
        return k8sService.k8sChart();
    }

    @I18n
    @ApiOperation(value = "风险统计")
    @GetMapping("severityChart")
    public List<Map<String, Object>> severityChart() {
        return k8sService.severityChart();
    }

    @I18n
    @ApiOperation(value = "所有K8s")
    @GetMapping("allList")
    public List<CloudNative> allList() {
        return k8sService.allList();
    }

    @I18n
    @ApiOperation(value = "K8s 检测历史记录")
    @PostMapping("history/{goPage}/{pageSize}")
    public Pager<List<HistoryCloudNativeResultDTO>> history(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody Map<String, Object> params) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.history(params));
    }

    @I18n
    @ApiOperation(value = "漏洞检测结果历史详情")
    @PostMapping("historyResultItemList")
    public List<CloudNativeResultItem> historyResultItemList(@RequestBody CloudNativeResultItem request) {
        return k8sService.historyResultItemList(request);
    }

    @I18n
    @ApiOperation(value = "配置审计结果历史详情")
    @PostMapping("historyResultConfigItemList")
    public List<CloudNativeResultConfigItemWithBLOBs> historyResultConfigItemList(@RequestBody CloudNativeResultConfigItem request) {
        return k8sService.historyResultConfigItemList(request);
    }

    @ApiOperation(value = "删除检测历史记录")
    @GetMapping("deleteHistoryK8sResult/{id}")
    public void deleteHistoryK8sResult(@PathVariable String id) throws Exception {
        k8sService.deleteHistoryK8sResult(id);
    }

    @I18n
    @ApiOperation(value = "详情资源态势拓扑图")
    @GetMapping(value = "k8sTopology")
    public K8sTopology k8sTopology() {
        return k8sService.k8sTopology();
    }

    @I18n
    @ApiOperation(value = "节点资源态势拓扑图")
    @GetMapping(value = "nodeTopology")
    public NodeTopology nodeTopology() {
        return k8sService.nodeTopology();
    }

    @I18n
    @ApiOperation(value = "命名空间资源态势拓扑图")
    @GetMapping(value = "namespaceTopology")
    public NameSpaceTopology namespaceTopology() {
        return k8sService.namespaceTopology();
    }

    @I18n
    @ApiOperation(value = "资源态势资源对应的镜像")
    @GetMapping("sourceImages/{sourceId}")
    public List<CloudNativeSourceImageDTO> sourceImages(@PathVariable String sourceId) throws Exception {
        return k8sService.sourceImages(sourceId);
    }

    @I18n
    @ApiOperation(value = "K8s风险态势拓扑图")
    @PostMapping(value = "riskTopology")
    public RiskTopology riskTopology(@RequestBody RiskRequest request) {
        return k8sService.riskTopology(request);
    }

    @I18n
    @ApiOperation(value = "镜像拓扑图")
    @PostMapping(value = "getImage")
    public K8sImage getImage(@RequestBody RiskRequest request) {
        return k8sService.getImage(request);
    }

}
