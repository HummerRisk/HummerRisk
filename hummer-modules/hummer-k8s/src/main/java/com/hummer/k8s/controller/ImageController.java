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
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("image/repo/list/{goPage}/{pageSize}")
    public Pager<List<ImageRepo>> imageRepoList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRepoRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.imageRepoList(request));
    }

    @I18n
    @Operation(summary = "添加镜像仓库")
    @PostMapping("add/image/repo")
    public ImageRepo addImageRepo(@RequestBody ImageRepo imageRepo) throws Exception {
        return imageService.addImageRepo(imageRepo, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "编辑镜像仓库")
    @PostMapping("edit/image/repo")
    public ImageRepo editImageRepo(@RequestBody ImageRepo imageRepo) throws Exception {
        return imageService.editImageRepo(imageRepo, tokenService.getLoginUser());
    }

    @Operation(summary = "删除镜像仓库")
    @GetMapping("delete/image/repo/{id}")
    public void deleteImageRepo(@PathVariable String id) throws Exception {
        imageService.deleteImageRepo(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "查询所有镜像仓库")
    @GetMapping("all/image/repos")
    public List<ImageRepo> allImageRepos() {
        return imageService.allImageRepos();
    }

    @I18n
    @Operation(summary = "镜像仓库中的镜像列表")
    @PostMapping("repo/item/list/{goPage}/{pageSize}")
    public Pager<List<ImageRepoItemDTO>> repoItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRepoItemRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.repoItemList(request));
    }

    @I18n
    @Operation(summary = "镜像仓库中的镜像列表")
    @PostMapping("repo/item/list")
    public List<ImageRepoItemDTO> repoItemList(@RequestBody ImageRepoItemRequest request) {
        return imageService.repoItemList(request);
    }

    @I18n
    @Operation(summary = "镜像列表")
    @PostMapping("image/list/{goPage}/{pageSize}")
    public Pager<List<ImageDTO>> imageList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.imageList(request));
    }

    @I18n
    @Operation(summary = "所有已绑定项目的镜像")
    @GetMapping("all/bind/list/{sbomVersionId}")
    public List<Image> allBindList(@PathVariable String sbomVersionId) {
        return imageService.allBindList(sbomVersionId);
    }

    @I18n
    @Operation(summary = "所有未绑定项目的镜像")
    @GetMapping("unbind/list")
    public List<Image> unBindList() {
        return imageService.unBindList();
    }

    @I18n
    @Operation(summary = "添加镜像")
    @PostMapping(value = "add/image", consumes = {"multipart/form/data"})
    public Image addImage(@RequestPart(value = "tarFile", required = false) MultipartFile tarFile,
                          @RequestPart("request") ImageRequest request) throws Exception {
        return imageService.addImage(tarFile, request, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "修改镜像")
    @PostMapping(value = "update/image", consumes = {"multipart/form/data"})
    public Image updateImage(@RequestPart(value = "tarFile", required = false) MultipartFile tarFile,
                          @RequestPart("request") ImageRequest request) throws Exception {
        return imageService.updateImage(tarFile, request, tokenService.getLoginUser());
    }

    @Operation(summary = "删除镜像")
    @GetMapping("delete/image/{id}")
    public void deleteImage(@PathVariable String id) throws Exception {
        imageService.deleteImage(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "镜像检测规则列表")
    @PostMapping(value = "rule/list/{goPage}/{pageSize}")
    public Pager<List<ImageRuleDTO>> ruleList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRuleRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.ruleList(request));
    }

    @Operation(summary = "添加镜像检测规则")
    @PostMapping(value = "add/image/rule")
    public int addImageRule(@RequestBody ImageRuleRequest request) throws Exception {
        return imageService.addImageRule(request, tokenService.getLoginUser());
    }

    @Operation(summary = "修改镜像检测规则")
    @PostMapping(value = "update/image/rule")
    public int updateImageRule(@RequestBody ImageRuleRequest request) throws Exception {
        return imageService.updateImageRule(request, tokenService.getLoginUser());
    }

    @Operation(summary = "删除镜像检测规则")
    @GetMapping(value = "delete/image/rule/{id}")
    public void deleteImageRule(@PathVariable String id) throws Exception  {
        imageService.deleteImageRule(id, tokenService.getLoginUser());
    }

    @Operation(summary = "镜像检测规则启用")
    @PostMapping(value = "change/status")
    public int changeStatus(@RequestBody ImageRule rule) throws Exception {
        return imageService.changeStatus(rule);
    }

    @Operation(summary = "镜像检测规则")
    @GetMapping("scan/{id}")
    public void scan(@PathVariable String id) throws Exception {
        imageService.scan(id, tokenService.getLoginUser());
    }

    @Operation(summary = "重新检测镜像规则")
    @GetMapping("rescan/{id}")
    public void reScan(@PathVariable String id) throws Exception {
        imageService.reScan(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "镜像检测结果列表")
    @PostMapping(value = "result/list/withblobs/{goPage}/{pageSize}")
    public Pager<List<ImageResultWithBLOBsDTO>> resultListWithBLOBs(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.resultListWithBLOBs(request));
    }

    @I18n
    @Hidden
    @PostMapping(value = "result/list/{goPage}/{pageSize}")
    public Pager<List<ImageResultDTO>> resultList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.resultList(request));
    }

    @I18n
    @Hidden
    @GetMapping(value = "get/image/result/dto/{resultId}")
    public HistoryImageReportDTO getImageResultDto(@PathVariable String resultId) {
        return imageService.getImageResultDto(resultId);
    }

    @I18n
    @Hidden
    @GetMapping(value = "get/image/result/{resultId}")
    public ImageResultDTO getImageResult(@PathVariable String resultId) {
        return imageService.getImageResult(resultId);
    }

    @I18n
    @Operation(summary = "镜像检测结果详情")
    @GetMapping(value = "get/image/result/withblobs/{resultId}")
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
    @GetMapping("delete/image/result/{id}")
    public void deleteImageResult(@PathVariable String id) throws Exception {
        imageService.deleteImageResult(id, tokenService.getLoginUser());
    }

    @I18n
    @Hidden
    @PostMapping("result/item/list/{goPage}/{pageSize}")
    public Pager<List<ImageResultItemWithBLOBs>> resultItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageResultItem request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.resultItemList(request));
    }

    @I18n
    @Operation(summary = "检测结果详情")
    @PostMapping("result/item/list/by/search/{goPage}/{pageSize}")
    public Pager<List<ImageResultItemWithBLOBs>> resultItemListBySearch(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageResultItemRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.resultItemListBySearch(request));
    }

    @I18n
    @Operation(summary = "镜像仓库同步日志列表")
    @PostMapping("repo/sync/list/{goPage}/{pageSize}")
    public Pager<List<ImageRepoSyncLogWithBLOBs>> repoSyncList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRepoSyncLog imageRepoSyncLog) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.repoSyncList(imageRepoSyncLog.getRepoId()));
    }

    @I18n
    @Operation(summary = "同步镜像")
    @GetMapping("sync/image/{id}")
    public void syncImage(@PathVariable String id) throws Exception {
        imageService.syncImage(id, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "执行镜像仓库中的镜像")
    @PostMapping("scan/image/repo")
    public void scanImageRepo(@RequestBody ScanImageRepoRequest request) throws Exception {
        imageService.scanImageRepo(request, tokenService.getLoginUser());
    }

    @Operation(summary = "批量执行镜像仓库中的镜像")
    @PostMapping("scan/images/repo")
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
    @PostMapping("top/info")
    public Map<String, Object> topInfo(@RequestBody Map<String, Object> params) {
        return imageService.topInfo(params);
    }

    @I18n
    @Operation(summary = "镜像仓库统计")
    @GetMapping("image/repo/chart")
    public List<Map<String, Object>> imageRepoChart() {
        return imageService.imageRepoChart();
    }

    @I18n
    @Operation(summary = "风险统计")
    @GetMapping("severity/chart")
    public List<Map<String, Object>> severityChart() {
        return imageService.severityChart();
    }

    @I18n
    @Operation(summary = "所有镜像")
    @GetMapping("all/list")
    public List<Image> allList() {
        return imageService.allList();
    }

    @I18n
    @Operation(summary = "检测结果历史详情")
    @PostMapping("history/result/item/list")
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
    @PostMapping("delete/image/repos")
    public void deleteImageRepos(@RequestBody List<String> selectIds) throws Exception {
        imageService.deleteImageRepos(selectIds, tokenService.getLoginUser());
    }

    @Operation(summary = "批量删除镜像")
    @PostMapping("delete/images")
    public void deleteImages(@RequestBody List<String> selectIds) throws Exception {
        imageService.deleteImages(selectIds, tokenService.getLoginUser());
    }

    @Operation(summary = "所有镜像分组")
    @GetMapping("image/group/list")
    public List<ImageGroup> imageGroupList() {
        return imageService.imageGroupList();
    }

    @Operation(summary = "添加镜像分组")
    @PostMapping("add/image/group")
    public int addImageGroup(@RequestBody ImageGroup imageGroup) {
        return imageService.addImageGroup(imageGroup, tokenService.getLoginUser());
    }

    @Operation(summary = "修改镜像分组")
    @PostMapping("edit/image/group")
    public int editImageGroup(@RequestBody ImageGroup imageGroup) throws Exception {
        return imageService.editImageGroup(imageGroup, tokenService.getLoginUser());
    }

    @Operation(summary = "删除镜像分组")
    @PostMapping("delete/image/group")
    public void deleteImageGroup(@RequestBody ImageGroup imageGroup) throws Exception {
        imageService.deleteImageGroup(imageGroup, tokenService.getLoginUser());
    }

    @Operation(summary = "一键检测镜像")
    @PostMapping("scan/images")
    public void scanImages(@RequestBody List<String> selectIds) {
        imageService.scanImages(selectIds, tokenService.getLoginUser());
    }

}
