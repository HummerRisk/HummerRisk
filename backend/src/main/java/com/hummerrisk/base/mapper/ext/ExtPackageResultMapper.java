package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.packageSetting.PackageResultRequest;
import com.hummerrisk.dto.PackageResultDTO;

import java.util.List;

public interface ExtPackageResultMapper {

    List<PackageResultDTO> resultList(PackageResultRequest request);

}
