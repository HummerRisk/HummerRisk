package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.handler.annotation.I18n;
import com.hummerrisk.controller.request.image.ImageRepoRequest;
import com.hummerrisk.controller.request.image.ImageRequest;
import com.hummerrisk.controller.request.image.ImageResultRequest;
import com.hummerrisk.controller.request.image.ImageRuleRequest;
import com.hummerrisk.dto.*;
import com.hummerrisk.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "镜像管理")
@RestController
@RequestMapping(value = "image")
public class ImageController {

    @Resource
    private ImageService imageService;

    @I18n
    @ApiOperation(value = "镜像仓库列表")
    @PostMapping("imageRepoList/{goPage}/{pageSize}")
    public Pager<List<ImageRepo>> imageRepoList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRepoRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.imageRepoList(request));
    }

    @I18n
    @ApiOperation(value = "添加镜像仓库")
    @PostMapping("addImageRepo")
    public ImageRepo addImageRepo(@RequestBody ImageRepo imageRepo) throws Exception {
        return imageService.addImageRepo(imageRepo);
    }

    @I18n
    @ApiOperation(value = "编辑镜像仓库")
    @PostMapping("editImageRepo")
    public ImageRepo editImageRepo(@RequestBody ImageRepo imageRepo) throws Exception {
        return imageService.editImageRepo(imageRepo);
    }

    @ApiOperation(value = "删除镜像仓库")
    @GetMapping("deleteImageRepo/{id}")
    public void deleteImageRepo(@PathVariable String id) throws Exception {
        imageService.deleteImageRepo(id);
    }

    @I18n
    @ApiOperation(value = "查询所有镜像仓库")
    @GetMapping("allImageRepos")
    public List<ImageRepo> allImageRepos() {
        return imageService.allImageRepos();
    }

    @I18n
    @ApiOperation(value = "镜像列表")
    @PostMapping("imageList/{goPage}/{pageSize}")
    public Pager<List<ImageDTO>> imageList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.imageList(request));
    }

    @I18n
    @ApiOperation(value = "添加镜像")
    @PostMapping(value = "addImage", consumes = {"multipart/form-data"})
    public Image addImage(@RequestPart(value = "iconFile", required = false) MultipartFile iconFile,
                          @RequestPart(value = "tarFile", required = false) MultipartFile tarFile,
                          @RequestPart("request") Image request) throws Exception {
        return imageService.addImage(iconFile, tarFile, request);
    }

    @I18n
    @ApiOperation(value = "修改镜像")
    @PostMapping(value = "updateImage", consumes = {"multipart/form-data"})
    public Image updateImage(@RequestPart(value = "iconFile", required = false) MultipartFile iconFile,
                          @RequestPart(value = "tarFile", required = false) MultipartFile tarFile,
                          @RequestPart("request") Image request) throws Exception {
        return imageService.updateImage(iconFile, tarFile, request);
    }

    @ApiOperation(value = "删除镜像")
    @GetMapping("deleteImage/{id}")
    public void deleteImage(@PathVariable String id) throws Exception {
        imageService.deleteImage(id);
    }

    @I18n
    @ApiOperation(value = "镜像检测规则列表")
    @PostMapping(value = "ruleList/{goPage}/{pageSize}")
    public Pager<List<ImageRuleDTO>> ruleList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRuleRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.ruleList(request));
    }

    @ApiOperation(value = "添加镜像检测规则")
    @PostMapping(value = "addImageRule")
    public int addImageRule(@RequestBody ImageRuleRequest request) throws Exception {
        return imageService.addImageRule(request);
    }

    @ApiOperation(value = "修改镜像检测规则")
    @PostMapping(value = "updateImageRule")
    public int updateImageRule(@RequestBody ImageRuleRequest request) throws Exception {
        return imageService.updateImageRule(request);
    }

    @ApiOperation(value = "删除镜像检测规则")
    @GetMapping(value = "deleteImageRule/{id}")
    public void deleteImageRule(@PathVariable String id) throws Exception  {
        imageService.deleteImageRule(id);
    }

    @ApiOperation(value = "镜像检测规则启用")
    @PostMapping(value = "changeStatus")
    public int changeStatus(@RequestBody ImageRule rule) throws Exception {
        return imageService.changeStatus(rule);
    }

    @ApiOperation(value = "镜像检测规则")
    @GetMapping("scan/{id}")
    public void scan(@PathVariable String id) throws Exception {
        imageService.scan(id);
    }

    @ApiOperation(value = "重新检测镜像规则")
    @GetMapping("reScan/{id}")
    public void reScan(@PathVariable String id) throws Exception {
        imageService.reScan(id);
    }

    @I18n
    @ApiOperation(value = "镜像检测结果列表")
    @PostMapping(value = "resultListWithBLOBs/{goPage}/{pageSize}")
    public Pager<List<ImageResultWithBLOBsDTO>> resultListWithBLOBs(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.resultListWithBLOBs(request));
    }

    @I18n
    @ApiIgnore
    @PostMapping(value = "resultList/{goPage}/{pageSize}")
    public Pager<List<ImageResultDTO>> resultList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageResultRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.resultList(request));
    }


    @I18n
    @ApiIgnore
    @GetMapping(value = "getImageResultDto/{resultId}")
    public HistoryImageReportDTO getImageResultDto(@PathVariable String resultId) {
        return imageService.getImageResultDto(resultId);
    }

    @I18n
    @ApiIgnore
    @GetMapping(value = "getImageResult/{resultId}")
    public ImageResult getImageResult(@PathVariable String resultId) {
        return imageService.getImageResult(resultId);
    }

    @I18n
    @ApiOperation(value = "镜像检测结果详情")
    @GetMapping(value = "getImageResultWithBLOBs/{resultId}")
    public ImageResultWithBLOBs getImageResultWithBLOBs(@PathVariable String resultId) {
        return imageService.getImageResultWithBLOBs(resultId);
    }

    @I18n
    @ApiOperation(value = "镜像检测日志")
    @GetMapping(value = "log/{resultId}")
    public List<ImageResultLog> getPackageResultLog(@PathVariable String resultId) {
        return imageService.getImageResultLog(resultId);
    }

    @ApiOperation(value = "删除镜像检测记录")
    @GetMapping("deleteImageResult/{id}")
    public void deleteImageResult(@PathVariable String id) throws Exception {
        imageService.deleteImageResult(id);
    }

    @I18n
    @ApiOperation(value = "检测结果详情")
    @PostMapping("resultItemList/{goPage}/{pageSize}")
    public Pager<List<ImageGrypeTable>> resultItemList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageGrypeTable request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.resultItemList(request));
    }
}
