package io.hummerrisk.base.mapper.ext;


import io.hummerrisk.controller.request.packageSetting.PackageRequest;
import io.hummerrisk.dto.PackageDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author harris
 */
public interface ExtPackageMapper {

    List<PackageDTO> packageList(@Param("request") PackageRequest request);
}
