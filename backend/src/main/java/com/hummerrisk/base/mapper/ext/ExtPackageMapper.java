package com.hummerrisk.base.mapper.ext;


import com.hummerrisk.controller.request.packageSetting.PackageRequest;
import com.hummerrisk.dto.PackageDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author harris
 */
public interface ExtPackageMapper {

    List<PackageDTO> packageList(@Param("request") PackageRequest request);
}
