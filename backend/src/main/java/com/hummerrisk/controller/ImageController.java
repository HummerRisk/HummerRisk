package com.hummerrisk.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummerrisk.base.domain.Image;
import com.hummerrisk.base.domain.ImageRepo;
import com.hummerrisk.commons.utils.PageUtils;
import com.hummerrisk.commons.utils.Pager;
import com.hummerrisk.controller.request.image.ImageRepoRequest;
import com.hummerrisk.controller.request.image.ImageRequest;
import com.hummerrisk.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "镜像管理")
@RestController
@RequestMapping(value = "image")
public class ImageController {

    @Resource
    private ImageService imageService;

    @ApiOperation(value = "镜像仓库列表")
    @PostMapping("imageRepoList/{goPage}/{pageSize}")
    public Pager<List<ImageRepo>> imageRepoList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRepoRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.imageRepoList(request));
    }

    @ApiOperation(value = "添加镜像仓库")
    @PostMapping("addImageRepo")
    public ImageRepo addImageRepo(@RequestBody ImageRepo imageRepo) throws Exception {
        return imageService.addImageRepo(imageRepo);
    }

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

    @ApiOperation(value = "查询所有镜像仓库")
    @GetMapping("allImageRepos")
    public List<ImageRepo> allImageRepos() {
        return imageService.imageRepoList(null);
    }

    @ApiOperation(value = "镜像列表")
    @PostMapping("imageList/{goPage}/{pageSize}")
    public Pager<List<Image>> imageList(
            @PathVariable int goPage, @PathVariable int pageSize, @RequestBody ImageRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, imageService.imageList(request));
    }


}
