package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.RuleInspectionReport;
import com.hummer.common.mapper.domain.request.rule.RuleInspectionReportRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExtRuleInspectionReportMapper {

    List<RuleInspectionReport> getRuleInspectionReportList(@Param("request") RuleInspectionReportRequest request);

}
