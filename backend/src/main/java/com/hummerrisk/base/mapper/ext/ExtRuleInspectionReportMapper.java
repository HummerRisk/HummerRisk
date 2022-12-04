package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.Plugin;
import com.hummerrisk.base.domain.RuleInspectionReport;
import com.hummerrisk.controller.request.plugin.PluginRequest;
import com.hummerrisk.controller.request.rule.RuleInspectionReportRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExtRuleInspectionReportMapper {

    List<RuleInspectionReport> getRuleInspectionReportList(@Param("request") RuleInspectionReportRequest request);

}
