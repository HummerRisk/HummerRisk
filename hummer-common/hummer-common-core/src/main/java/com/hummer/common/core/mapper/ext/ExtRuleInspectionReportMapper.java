package com.hummer.common.core.mapper.ext;

import com.hummer.common.core.domain.RuleInspectionReport;
import com.hummer.common.core.domain.request.rule.RuleInspectionReportRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExtRuleInspectionReportMapper {

    List<RuleInspectionReport> getRuleInspectionReportList(@Param("request") RuleInspectionReportRequest request);

}
