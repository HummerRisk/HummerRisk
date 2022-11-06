package com.hummerrisk.oss.service;

import com.hummerrisk.base.mapper.OssMapper;
import com.hummerrisk.base.mapper.ext.ExtOssMapper;
import com.hummerrisk.oss.controller.request.OssRequest;
import com.hummerrisk.oss.dto.OssDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OssService {

    @Resource
    private OssMapper ossMapper;
    @Resource
    private ExtOssMapper extOssMapper;

    public List<OssDTO> ossList (OssRequest request) {
        return extOssMapper.ossList(request);
    }

}
