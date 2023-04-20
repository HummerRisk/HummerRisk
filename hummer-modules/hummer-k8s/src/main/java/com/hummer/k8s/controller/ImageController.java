package com.hummer.k8s.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.image.*;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import com.hummer.k8s.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;

@Tag(name = "镜像管理")
@RestController
@RequestMapping(value = "image")
public class ImageController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private TokenService tokenService;

    @I18n
    @Operation(summary = "镜像仓库列表")
    @PostMapping("imageRepoList/{goPage}/{pageSize}")
    public Pager<List<ImageRepo>> imageRepoList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRepoRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.imageRepoList(request));
    }

    @I18n
    @Operation(summary = "添加镜像仓库")
    @PostMapping("addImageRepo")
    public ImageRepo addImageRepo(@RequestBody ImageRepo imageRepo) throws Exception {
        return imageService.addImageRepo(imageRepo, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "编辑镜像仓库")
    @PostMapping("editImageRepo")
    public ImageRepo editImageRepo(@RequestBody ImageRepo imageRepo) throws Exception {
        return imageService.editImageRepo(imageRepo, tokenService.getLoginUser());
    }

    @Operation(summary = "删除镜像仓库")
    @GetMapping("deleteImageRepo/{id}")
    public void deleteImageRepo(@PathVariable String id) throws Exception {
        imageService.deleteImageRepo(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "查询所有镜像仓库")
    @GetMapping("allImageRepos")
    public List<ImageRepo> allImageRepos() {
        return imageService.allImageRepos();
    }

    @I18n
    @Operation(summary = "镜像仓库中的镜像列表")
    @PostMapping("repoItemList/{goPage}/{pageSize}")
    public Pager<List<ImageRepoItemDTO>> repoItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRepoItemRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.repoItemList(request));
    }

    @I18n
    @Operation(summary = "镜像仓库中的镜像列表")
    @PostMapping("repoItemList")
    public List<ImageRepoItemDTO> repoItemList(@RequestBody ImageRepoItemRequest request) {
        return imageService.repoItemList(request);
    }

    @I18n
    @Operation(summary = "镜像列表")
    @PostMapping("imageList/{goPage}/{pageSize}")
    public Pager<List<ImageDTO>> imageList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.imageList(request));
    }

    @I18n
    @Operation(summary = "所有已绑定项目的镜像")
    @GetMapping("allBindList/{sbomVersionId}")
    public List<Image> allBindList(@PathVariable String sbomVersionId) {
        return imageService.allBindList(sbomVersionId);
    }

    @I18n
    @Operation(summary = "所有未绑定项目的镜像")
    @GetMapping("unBindList")
    public List<Image> unBindList() {
        return imageService.unBindList();
    }

    @I18n
    @Operation(summary = "添加镜像")
    @PostMapping(value = "addImage", consumes = {"multipart/form-data"})
    public Image addImage(@RequestPart(value = "tarFile", required = false) MultipartFile tarFile,
                          @RequestPart("request") ImageRequest request) throws Exception {
        return imageService.addImage(tarFile, request, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "修改镜像")
    @PostMapping(value = "updateImage", consumes = {"multipart/form-data"})
    public Image updateImage(@RequestPart(value = "tarFile", required = false) MultipartFile tarFile,
                          @RequestPart("request") ImageRequest request) throws Exception {
        return imageService.updateImage(tarFile, request, tokenService.getLoginUser());
    }

    @Operation(summary = "删除镜像")
    @GetMapping("deleteImage/{id}")
    public void deleteImage(@PathVariable String id) throws Exception {
        imageService.deleteImage(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "镜像检测规则列表")
    @PostMapping(value = "ruleList/{goPage}/{pageSize}")
    public Pager<List<ImageRuleDTO>> ruleList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRuleRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.ruleList(request));
    }

    @Operation(summary = "添加镜像检测规则")
    @PostMapping(value = "addImageRule")
    public int addImageRule(@RequestBody ImageRuleRequest request) throws Exception {
        return imageService.addImageRule(request, tokenService.getLoginUser());
    }

    @Operation(summary = "修改镜像检测规则")
    @PostMapping(value = "updateImageRule")
    public int updateImageRule(@RequestBody ImageRuleRequest request) throws Exception {
        return imageService.updateImageRule(request, tokenService.getLoginUser());
    }

    @Operation(summary = "删除镜像检测规则")
    @GetMapping(value = "deleteImageRule/{id}")
    public void deleteImageRule(@PathVariable String id) throws Exception  {
        imageService.deleteImageRule(id, tokenService.getLoginUser());
    }

    @Operation(summary = "镜像检测规则启用")
    @PostMapping(value = "changeStatus")
    public int changeStatus(@RequestBody ImageRule rule) throws Exception {
        return imageService.changeStatus(rule);
    }

    @Operation(summary = "镜像检测规则")
    @GetMapping("scan/{id}")
    public void scan(@PathVariable String id) throws Exception {
        imageService.scan(id, tokenService.getLoginUser());
    }

    @Operation(summary = "重新检测镜像规则")
    @GetMapping("reScan/{id}")
    public void reScan(@PathVariable String id) throws Exception {
        imageService.reScan(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "镜像检测结果列表")
    @PostMapping(value = "resultListWithBLOBs/{goPage}/{pageSize}")
    public Pager<List<ImageResultWithBLOBsDTO>> resultListWithBLOBs(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.resultListWithBLOBs(request));
    }

    @I18n
    @Hidden
    @PostMapping(value = "resultList/{goPage}/{pageSize}")
    public Pager<List<ImageResultDTO>> resultList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.resultList(request));
    }


    @I18n
    @Hidden
    @GetMapping(value = "getImageResultDto/{resultId}")
    public HistoryImageReportDTO getImageResultDto(@PathVariable String resultId) {
        return imageService.getImageResultDto(resultId);
    }

    @I18n
    @Hidden
    @GetMapping(value = "getImageResult/{resultId}")
    public ImageResultDTO getImageResult(@PathVariable String resultId) {
        return imageService.getImageResult(resultId);
    }

    @I18n
    @Operation(summary = "镜像检测结果详情")
    @GetMapping(value = "getImageResultWithBLOBs/{resultId}")
    public ImageResultWithBLOBs getImageResultWithBLOBs(@PathVariable String resultId) {
        return imageService.getImageResultWithBLOBs(resultId);
    }

    @I18n
    @Operation(summary = "镜像检测日志")
    @GetMapping(value = "log/{resultId}")
    public List<ImageResultLogWithBLOBs> getImageResultLog(@PathVariable String resultId) {
        return imageService.getImageResultLog(resultId);
    }

    @Operation(summary = "删除镜像检测记录")
    @GetMapping("deleteImageResult/{id}")
    public void deleteImageResult(@PathVariable String id) throws Exception {
        imageService.deleteImageResult(id, tokenService.getLoginUser());
    }

    @I18n
    @Hidden
    @PostMapping("resultItemList/{goPage}/{pageSize}")
    public Pager<List<ImageResultItemWithBLOBs>> resultItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageResultItem request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.resultItemList(request));
    }

    @I18n
    @Operation(summary = "检测结果详情")
    @PostMapping("resultItemListBySearch/{goPage}/{pageSize}")
    public Pager<List<ImageResultItemWithBLOBs>> resultItemListBySearch(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageResultItemRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.resultItemListBySearch(request));
    }

    @I18n
    @Operation(summary = "镜像仓库同步日志列表")
    @PostMapping("repoSyncList/{goPage}/{pageSize}")
    public Pager<List<ImageRepoSyncLogWithBLOBs>> repoSyncList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRepoSyncLog imageRepoSyncLog) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.repoSyncList(imageRepoSyncLog.getRepoId()));
    }

    @I18n
    @Operation(summary = "同步镜像")
    @GetMapping("syncImage/{id}")
    public void syncImage(@PathVariable String id) throws Exception {
        imageService.syncImage(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "执行镜像仓库中的镜像")
    @PostMapping("scanImageRepo")
    public void scanImageRepo(@RequestBody ScanImageRepoRequest request) throws Exception {
        imageService.scanImageRepo(request, tokenService.getLoginUser());
    }

    @Operation(summary = "批量执行镜像仓库中的镜像")
    @PostMapping("scanImagesRepo")
    public void scanImagesRepo(@RequestBody List<String> selectIds) {
        imageService.scanImagesRepo(selectIds, tokenService.getLoginUser());
    }

    @Operation(summary = "下载检测报告")
    @PostMapping("download")
    public String download(@RequestBody Map<String, Object> map) throws Exception {
        return imageService.download(map);
    }

    @I18n
    @Operation(summary = "概览TOP统计")
    @PostMapping("topInfo")
    public Map<String, Object> topInfo(@RequestBody Map<String, Object> params) {
        return imageService.topInfo(params);
    }

    @I18n
    @Operation(summary = "镜像仓库统计")
    @GetMapping("imageRepoChart")
    public List<Map<String, Object>> imageRepoChart() {
        return imageService.imageRepoChart();
    }

    @I18n
    @Operation(summary = "风险统计")
    @GetMapping("severityChart")
    public List<Map<String, Object>> severityChart() {
        return imageService.severityChart();
    }

    @I18n
    @Operation(summary = "所有镜像")
    @GetMapping("allList")
    public List<Image> allList() {
        return imageService.allList();
    }

    @I18n
    @Operation(summary = "检测结果历史详情")
    @PostMapping("historyResultItemList")
    public List<ImageResultItemWithBLOBs> historyResultItemList(@RequestBody ImageResultItem request) {
        return imageService.historyResultItemList(request);
    }

    @I18n
    @Operation(summary = "镜像仓库列表")
    @PostMapping("repo/setting")
    public void imageRepoSetting(@RequestBody ImageRepoSetting imageRepoSetting) throws Exception {
        imageService.imageRepoSetting(imageRepoSetting);
    }

    @I18n
    @Operation(summary = "镜像仓库列表")
    @GetMapping("repo/setting/{repoId}")
    public ImageRepoSetting getImageRepoSetting(@PathVariable String repoId) throws Exception {
        return imageService.getImageRepoSetting(repoId);
    }

    @Operation(summary = "批量删除镜像仓库")
    @PostMapping("deleteImageRepos")
    public void deleteImageRepos(@RequestBody List<String> selectIds) throws Exception {
        imageService.deleteImageRepos(selectIds, tokenService.getLoginUser());
    }

    @Operation(summary = "批量删除镜像")
    @PostMapping("deleteImages")
    public void deleteImages(@RequestBody List<String> selectIds) throws Exception {
        imageService.deleteImages(selectIds, tokenService.getLoginUser());
    }

}
