package io.hummerrisk.base.mapper.ext;

import io.hummerrisk.controller.request.packageSetting.PackageResultRequest;
import io.hummerrisk.dto.PackageResultDTO;

import java.util.List;

public interface ExtPackageResultMapper {

    List<PackageResultDTO> resultList(PackageResultRequest request);

}
