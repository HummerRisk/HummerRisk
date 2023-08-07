package com.hummer.cloud.mapper.ext;

import com.hummer.cloud.dto.ReportResultDTO;
import com.hummer.common.core.domain.AccountExample;
import com.hummer.common.core.domain.request.task.AccountVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtReportResultMapper {

    List<ReportResultDTO> reportList(@Param("request") ReportResultDTO request);

    ReportResultDTO getReport(String reportId);

    List<AccountVo> selectAccountByExample(AccountExample example);

}
