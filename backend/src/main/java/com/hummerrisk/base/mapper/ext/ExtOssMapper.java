package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.oss.controller.request.OssRequest;
import com.hummerrisk.oss.dto.OssBucketDTO;
import com.hummerrisk.oss.dto.OssDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtOssMapper {

    List<OssDTO> ossList (@Param("request") OssRequest request);

    List<OssBucketDTO> ossBucketList (@Param("request") OssRequest request);

}
