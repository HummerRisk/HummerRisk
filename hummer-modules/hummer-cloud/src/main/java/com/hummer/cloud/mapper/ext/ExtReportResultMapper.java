package com.hummer.cloud.mapper.ext;

import com.hummer.cloud.dto.ReportResultDTO;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.task.AccountVo;
import com.hummer.common.core.domain.request.task.ProjectVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtReportResultMapper {

    List<ReportResultDTO> reportList(@Param("request") ReportResultDTO request);

    ReportResultDTO getReport(String reportId);

    List<AccountVo> selectAccountByExample(AccountExample example);

    List<ProjectVo> selectProjectByExample(CloudProjectExample example);

    CloudProject getCloudProject(@Param("id") String id);

    List<CloudTask> cloudTaskList(@Param("id") String id);

}
