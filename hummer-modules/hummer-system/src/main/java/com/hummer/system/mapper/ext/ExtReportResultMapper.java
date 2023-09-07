package com.hummer.system.mapper.ext;

import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.task.*;
import com.hummer.system.dto.ReportResultDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtReportResultMapper {

    List<ReportResultDTO> reportList(@Param("request") ReportResultDTO request);

    ReportResultDTO getReport(String reportId);

    List<AccountVo> selectAccountByExample(AccountExample example);

    List<ServerVo> selectServerByExample(ServerExample example);

    List<ImageVo> selectImageByExample(ImageExample example);

    List<CodeVo> selectCodeByExample(CodeExample example);

    List<FileSystemVo> selectFsByExample(FileSystemExample example);

    List<K8sVo> selectK8sByExample(CloudNativeExample example);

    List<ConfigVo> selectConfigByExample(CloudNativeConfigExample example);

    List<ProjectVo> selectProjectByExample(CloudProjectExample example);

    CloudProject getCloudProject(@Param("id") String id);

    List<CloudTask> cloudTaskList(@Param("id") String id);

}
