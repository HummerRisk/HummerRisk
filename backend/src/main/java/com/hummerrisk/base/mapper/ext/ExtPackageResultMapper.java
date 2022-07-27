package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.packageSetting.PackageResultRequest;
import com.hummerrisk.dto.HistoryPackageReportDTO;
import com.hummerrisk.dto.PackageResultDTO;
import com.hummerrisk.dto.PackageResultWithBLOBsDTO;

import java.util.List;

public interface ExtPackageResultMapper {

    List<PackageResultWithBLOBsDTO> resultListWithBLOBs(PackageResultRequest request);

    List<PackageResultDTO> resultList(PackageResultRequest request);

    HistoryPackageReportDTO getPackageResultDto(String resultId);

}
