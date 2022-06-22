package com.hummerrisk.service;

import com.hummerrisk.base.domain.ImageRepo;
import com.hummerrisk.base.domain.Package;
import com.hummerrisk.base.mapper.ImageMapper;
import com.hummerrisk.base.mapper.ImageRepoMapper;
import com.hummerrisk.base.mapper.ext.ExtImageRepoMapper;
import com.hummerrisk.commons.constants.ResourceOperation;
import com.hummerrisk.commons.constants.ResourceTypeConstants;
import com.hummerrisk.commons.utils.SessionUtils;
import com.hummerrisk.commons.utils.UUIDUtil;
import com.hummerrisk.controller.request.image.ImageRepoRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ImageService {

    @Resource
    private ExtImageRepoMapper extImageRepoMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private ImageRepoMapper imageRepoMapper;

    public List<ImageRepo> imageRepoList(ImageRepoRequest request) {
        return extImageRepoMapper.imageRepoList(request);
    }

    public ImageRepo addImageRepo(ImageRepo imageRepo) throws Exception {
        String id = UUIDUtil.newUUID();
        imageRepo.setId(id);
        imageRepo.setCreator(SessionUtils.getUserId());
        imageRepo.setCreateTime(System.currentTimeMillis());
        imageRepo.setUpdateTime(System.currentTimeMillis());
        imageRepo.setStatus("VALID");

        OperationLogService.log(SessionUtils.getUser(), imageRepo.getId(), imageRepo.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.CREATE, "创建镜像仓库");
        imageRepoMapper.insertSelective(imageRepo);
        return imageRepo;
    }

    public ImageRepo editImageRepo(ImageRepo imageRepo) throws Exception {
        imageRepo.setUpdateTime(System.currentTimeMillis());
        imageRepo.setStatus("VALID");

        OperationLogService.log(SessionUtils.getUser(), imageRepo.getId(), imageRepo.getName(), ResourceTypeConstants.IMAGE.name(), ResourceOperation.UPDATE, "更新镜像仓库");
        imageRepoMapper.updateByPrimaryKeySelective(imageRepo);
        return imageRepo;
    }

    public void deleteImageRepo(String id) throws Exception {
        imageRepoMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.IMAGE.name(), ResourceOperation.DELETE, "删除镜像仓库");
    }


}
