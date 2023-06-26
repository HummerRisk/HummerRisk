package com.hummer.cloud.mapper.ext;

import com.hummer.common.core.dto.CloudResourceRelaDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudResourceRelaMapper {

    List<CloudResourceRelaDTO> selectCloudTopologyRela(@Param("resourceItemId") String resourceItemId);


}
