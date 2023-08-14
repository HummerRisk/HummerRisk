package com.hummer.k8s.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.cloudNative.*;
import com.hummer.common.core.domain.request.image.ImageRequest;
import com.hummer.common.core.domain.request.k8s.*;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import com.hummer.k8s.service.K8sService;
import com.hummer.k8s.service.ResourceCreateService;
import io.kubernetes.client.openapi.ApiException;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "云原生")
@RestController
@RequestMapping(value = "k8s")
public class K8sController {
    @Autowired
    private K8sService k8sService;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private ResourceCreateService resourceCreateService;

    @I18n
    @Operation(summary = "云原生账号列表")
    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<CloudNativeDTO>> getCloudNativeList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudNativeRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.getCloudNativeList(request));
    }

    @I18n
    @Operation(summary = "所有云原生账号")
    @GetMapping("allCloudNativeList")
    public List<CloudNativeDTO> allCloudNativeList() {
        return k8sService.allCloudNativeList(new CloudNativeRequest());
    }

    @I18n
    @Operation(summary = "云原生账号详情")
    @GetMapping("getCloudNative/{id}")
    public CloudNative getCloudNative(@PathVariable String id) {
        return k8sService.getCloudNative(id);
    }

    @Operation(summary = "批量校验云原生账号")
    @PostMapping("validate")
    public List<ValidateDTO> validate(@RequestBody List<String> selectIds) {
        return k8sService.validate(selectIds);
    }

    @Operation(summary = "校验云原生账号")
    @PostMapping("validate/{id}")
    public ValidateDTO validate(@PathVariable String id) throws IOException, ApiException {
        return k8sService.validate(id, tokenService.getLoginUser());
    }

    @Operation(summary = "校验云原生Operator状态")
    @PostMapping("operatorStatusValidate/{id}")
    public ValidateDTO operatorStatusValidate(@PathVariable String id) throws Exception {
        return k8sService.operatorStatusValidate(id);
    }

    @Operation(summary = "校验云原生Kubench状态")
    @PostMapping("kubenchStatusValidate/{id}")
    public ValidateDTO kubenchStatusValidate(@PathVariable String id) throws Exception {
        return k8sService.kubenchStatusValidate(id);
    }

    @I18n
    @Operation(summary = "添加云原生账号")
    @PostMapping("add")
    public ValidateDTO addCloudNative(@RequestBody CreateCloudNativeRequest request) {
        return k8sService.addCloudNative(request, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "更新云原生账号")
    @PostMapping("update")
    public ValidateDTO editCloudNative(@RequestBody UpdateCloudNativeRequest request) throws Exception {
        return k8sService.editCloudNative(request, tokenService.getLoginUser());
    }

    @Operation(summary = "删除云原生账号")
    @PostMapping(value = "delete/{accountId}")
    public void deleteAccount(@PathVariable String accountId) throws Exception {
        k8sService.delete(accountId, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "资源态势列表")
    @PostMapping("cloudNativeSourceList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeSourceDTO>> getCloudNativeSourceList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudNativeSourceRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.getCloudNativeSourceList(request));
    }

    @I18n
    @Operation(summary = "资源态势")
    @PostMapping("situation")
    public SituationDTO situationInfo(@RequestBody Map<String, Object> params) {
        return k8sService.situationInfo(params);
    }

    @I18n
    @Operation(summary = "云原生检测")
    @PostMapping("scan")
    public void scan(@RequestBody CloudNativeRequest request) throws Exception {
        k8sService.scan(request, tokenService.getLoginUser());
    }

    @Operation(summary = "重新云原生检测")
    @GetMapping("rescan/{id}")
    public void reScan(@PathVariable String id) throws Exception {
        k8sService.reScan(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "云原生检测结果列表")
    @PostMapping(value = "resultList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeResultDTO>> resultList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.resultList(request));
    }

    @Operation(summary = "删除镜像检测记录")
    @GetMapping("deleteCloudNativeResult/{id}")
    public void deleteCloudNativeResult(@PathVariable String id) throws Exception {
        k8sService.deleteCloudNativeResult(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "镜像列表")
    @PostMapping("imageList/{goPage}/{pageSize}")
    public Pager<List<ImageDTO>> imageList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.imageList(request));
    }

    @I18n
    @Hidden
    @PostMapping("resultItemList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeResultItem>> resultItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.resultItemList(request));
    }

    @I18n
    @Operation(summary = "云原生漏洞检测结果详情列表")
    @PostMapping("resultItemListBySearch/{goPage}/{pageSize}")
    public Pager<List<CloudNativeResultItem>> resultItemListBySearch(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sResultItemRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.resultItemListBySearch(request));
    }

    @I18n
    @Operation(summary = "云原生配置审计结果详情列表")
    @PostMapping("resultConfigItemList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeResultConfigItemWithBLOBs>> resultConfigItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.resultConfigItemList(request));
    }

    @I18n
    @Operation(summary = "云原生配置审计结果详情列表")
    @PostMapping("resultConfigItemListBySearch/{goPage}/{pageSize}")
    public Pager<List<CloudNativeResultConfigItemWithBLOBs>> resultConfigItemListBySearch(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sConfigResultItemRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.resultConfigItemListBySearch(request));
    }

    @I18n
    @Operation(summary = "云原生Cis结果详情列表")
    @PostMapping("resultKubenchItemListBySearch/{goPage}/{pageSize}")
    public Pager<List<CloudNativeResultKubenchWithBLOBs>> resultKubenchItemListBySearch(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody K8sKubenchResultItemRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.resultKubenchItemListBySearch(request));
    }

    @I18n
    @Hidden
    @GetMapping(value = "getCloudNativeResult/{resultId}")
    public CloudNativeResultDTO getCloudNativeResult(@PathVariable String resultId) {
        return k8sService.getCloudNativeResult(resultId);
    }

    @I18n
    @Operation(summary = "云原生检测结果详情")
    @GetMapping(value = "getCloudNativeResultWithBLOBs/{resultId}")
    public CloudNativeResultWithBLOBs getCloudNativeResultWithBLOBs(@PathVariable String resultId) {
        return k8sService.getCloudNativeResultWithBLOBs(resultId);
    }

    @I18n
    @Hidden
    @GetMapping(value = "getCloudNativeResultWithBLOBs/topo/{accountId}")
    public CloudNativeResultWithBLOBs topoResult(@PathVariable String accountId) {
        return k8sService.topoResult(accountId);
    }

    @I18n
    @Operation(summary = "云原生安装日志")
    @PostMapping(value = "installLog/{goPage}/{pageSize}")
    public Pager<List<CloudNativeResultLogWithBLOBs>> getCloudNativeResultLog(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudNativeRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.getCloudNativeResultLog(request.getId()));
    }

    @I18n
    @Operation(summary = "云原生检测日志")
    @GetMapping(value = "log/{resultId}")
    public List<CloudNativeResultLogWithBLOBs> getCloudNativeResultLog(@PathVariable String resultId) {
        return k8sService.getCloudNativeResultLog(resultId);
    }

    @I18n
    @Hidden
    @GetMapping(value = "log/topo/{accountId}")
    public List<CloudNativeResultLogWithBLOBs> topoLog(@PathVariable String accountId) {
        return k8sService.topoLog(accountId);
    }

    @I18n
    @Operation(summary = "所有带有 YAML 的云原生资源信息")
    @GetMapping("allCloudNativeSource2YamlList")
    public List<CloudNativeSourceVo> allCloudNativeSource2YamlList() {
        return k8sService.allCloudNativeSource2YamlList();
    }

    @I18n
    @Operation(summary = "云原生资源信息")
    @GetMapping("cloudNativeSource2Yaml/{id}")
    public CloudNativeSourceWithBLOBs cloudNativeSource2Yaml(@PathVariable String id) {
        return k8sService.cloudNativeSource2Yaml(id);
    }

    @I18n
    @Operation(summary = "资源态势同步日志列表")
    @PostMapping("syncList/{goPage}/{pageSize}")
    public Pager<List<CloudNativeSourceSyncLogWithBLOBsDTO>> syncList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody CloudNativeSyncLogRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, k8sService.syncList(request));
    }

    @I18n
    @Operation(summary = "同步资源态势资源")
    @GetMapping("syncSource/{id}")
    public void syncSource(@PathVariable String id) throws Exception {
        k8sService.syncSource(id, tokenService.getLoginUser());
    }

    @Operation(summary = "删除资源态势同步日志")
    @GetMapping("deleteSyncLog/{id}")
    public void deleteSyncLog(@PathVariable String id) throws Exception {
        k8sService.deleteSyncLog(id);
    }

    @I18n
    @Operation(summary = "漏洞风险数据信息")
    @GetMapping("metricChart/{resultId}")
    public MetricChartDTO metricChart(@PathVariable String resultId) {
        return k8sService.metricChart(resultId);
    }

    @I18n
    @Operation(summary = "配置审计风险数据信息")
    @GetMapping("metricConfigChart/{resultId}")
    public MetricChartDTO metricConfigChart(@PathVariable String resultId) {
        return k8sService.metricConfigChart(resultId);
    }

    @I18n
    @Operation(summary = "Kubench 风险数据信息")
    @GetMapping("kubenchChart/{resultId}")
    public KubenchChartDTO kubenchChart(@PathVariable String resultId) {
        return k8sService.kubenchChart(resultId);
    }

    @Operation(summary = "下载检测报告")
    @PostMapping("download")
    public String download(@RequestBody Map<String, Object> map) throws Exception {
        return k8sService.download(map);
    }

    @I18n
    @Operation(summary = "概览TOP统计")
    @PostMapping("topInfo")
    public Map<String, Object> topInfo(@RequestBody Map<String, Object> params) {
        return k8sService.topInfo(params);
    }

    @I18n
    @Operation(summary = "K8s 统计")
    @GetMapping("k8sChart")
    public List<Map<String, Object>> k8sChart() {
        return k8sService.k8sChart();
    }

    @I18n
    @Operation(summary = "风险统计")
    @GetMapping("severityChart")
    public List<Map<String, Object>> severityChart() {
        return k8sService.severityChart();
    }

    @I18n
    @Operation(summary = "所有K8s")
    @GetMapping("allList")
    public List<CloudNative> allList() {
        return k8sService.allList();
    }

    @I18n
    @Operation(summary = "漏洞检测结果历史详情")
    @PostMapping("historyResultItemList")
    public List<CloudNativeResultItem> historyResultItemList(@RequestBody CloudNativeResultItem request) {
        return k8sService.historyResultItemList(request);
    }

    @I18n
    @Operation(summary = "配置审计结果历史详情")
    @PostMapping("historyResultConfigItemList")
    public List<CloudNativeResultConfigItemWithBLOBs> historyResultConfigItemList(@RequestBody CloudNativeResultConfigItem request) {
        return k8sService.historyResultConfigItemList(request);
    }

    @I18n
    @Operation(summary = "Kubench 结果历史详情")
    @PostMapping("historyResultKubenchList")
    public List<CloudNativeResultKubenchWithBLOBs> historyResultKubenchList(@RequestBody CloudNativeResultKubenchWithBLOBs request) {
        return k8sService.historyResultKubenchList(request);
    }

    @I18n
    @Operation(summary = "详情资源态势拓扑图")
    @GetMapping(value = "k8sTopology")
    public K8sTopology k8sTopology() {
        return k8sService.k8sTopology();
    }

    @I18n
    @Operation(summary = "节点资源态势拓扑图")
    @GetMapping(value = "nodeTopology")
    public NodeTopology nodeTopology() {
        return k8sService.nodeTopology();
    }

    @I18n
    @Operation(summary = "命名空间资源态势拓扑图")
    @GetMapping(value = "namespaceTopology")
    public NameSpaceTopology namespaceTopology() {
        return k8sService.namespaceTopology();
    }

    @I18n
    @Operation(summary = "资源态势资源对应的镜像")
    @GetMapping("sourceImages/{sourceId}")
    public List<CloudNativeSourceImageDTO> sourceImages(@PathVariable String sourceId) throws Exception {
        return k8sService.sourceImages(sourceId);
    }

    @I18n
    @Operation(summary = "K8s风险态势拓扑图")
    @PostMapping(value = "riskTopology")
    public RiskTopology riskTopology(@RequestBody RiskRequest request) {
        return k8sService.riskTopology(request);
    }

    @I18n
    @Operation(summary = "镜像拓扑图")
    @PostMapping(value = "getImage")
    public K8sImage getImage(@RequestBody RiskRequest request) {
        return k8sService.getImage(request);
    }

    @Operation(summary = "重新安装 Operator")
    @PostMapping("reinstallOperator/{id}")
    public void reinstallOperator(@PathVariable String id) throws Exception {
        k8sService.reinstallOperator(id, tokenService.getLoginUser());
    }

    @Operation(summary = "重新安装 CIS Benchmark")
    @PostMapping("reinstallKubench/{id}")
    public void reinstallKubench(@PathVariable String id) throws Exception {
        k8sService.reinstallKubench(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "RBAC 资源态势")
    @GetMapping("rbacChart/{k8sId}")
    public RbacDTO rbacChart(@PathVariable String k8sId) throws Exception {
        return k8sService.rbacChart(k8sId);
    }

    @I18n
    @Operation(summary = "规则组获取云原生账号")
    @GetMapping("listByGroup/{pluginId}")
    public List<CloudNative> listByGroup(@PathVariable String pluginId) {
        return k8sService.listByGroup(pluginId);
    }

    @Operation(summary = "批量删除K8s资源同步日志")
    @PostMapping("deleteSyncLogs")
    public void deleteSyncLogs(@RequestBody List<String> selectIds) throws Exception {
        k8sService.deleteSyncLogs(selectIds);
    }

    @Operation(summary = "批量删除K8s账号")
    @PostMapping("deleteK8ss")
    public void deleteK8ss(@RequestBody List<String> selectIds) throws Exception {
        k8sService.deleteK8ss(selectIds, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "所有K8s命名空间")
    @GetMapping("namespaces")
    public List<Map<String, String>> namespaces() {
        return k8sService.namespaces();
    }

}
