package com.hummer.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.cloud.dto.ReportDTO;
import com.hummer.cloud.dto.ReportResultDTO;
import com.hummer.cloud.service.ReportService;
import com.hummer.common.core.domain.ReportResultLog;
import com.hummer.common.core.dto.AccountTreeDTO;
import com.hummer.common.core.dto.ProjectTreeDTO;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "报告中心")
@RestController
@RequestMapping(value = "report")
public class ReportController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private TokenService tokenService;

    @I18n
    @Operation(summary = "报告列表")
    @PostMapping("reportList/{goPage}/{pageSize}")
    public Pager<List<ReportResultDTO>> reportList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ReportResultDTO request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, reportService.reportList(request));
    }

    @I18n
    @Operation(summary = "资源信息列表")
    @GetMapping("accountList")
    public AccountTreeDTO listAccounts() throws Exception {
        return reportService.listAccounts();
    }

    @I18n
    @Operation(summary = "资源信息列表")
    @GetMapping("accountByProjectList")
    public ProjectTreeDTO accountByProjectList() throws Exception {
        return reportService.accountByProjectList();
    }

    @I18n
    @Operation(summary = "查询报告")
    @GetMapping("getReport/{reportId}")
    public ReportResultDTO getReport(@PathVariable String reportId) throws Exception {
        return reportService.getReport(reportId);
    }

    @I18n
    @Operation(summary = "创建报告")
    @PostMapping("createReport")
    public void createReport(@RequestBody ReportDTO reportDTO) {
        reportService.createReport(reportDTO, tokenService.getLoginUser());
    }

    @I18n
    @Operation(summary = "修改报告")
    @PostMapping("updateReport")
    public void updateReport(@RequestBody ReportDTO reportDTO) throws Exception {
        reportService.updateReport(reportDTO, tokenService.getLoginUser());
    }

    @Operation(summary = "删除报告")
    @GetMapping("deleteReport/{id}")
    public void deleteReport(@PathVariable String id) throws Exception {
        reportService.deleteReport(id, tokenService.getLoginUser());
    }

    @Operation(summary = "批量删除报告")
    @PostMapping("deleteReports")
    public void deleteReports(@RequestBody List<String> selectIds) throws Exception {
        reportService.deleteReports(selectIds, tokenService.getLoginUser());
    }

    @Operation(summary = "生成报告")
    @GetMapping("generatorReport/{id}")
    public void generatorReport(@PathVariable String id) throws Exception {
        reportService.genReport(id, tokenService.getLoginUser());
    }

    @Operation(summary = "下载报告")
    @GetMapping("downloadReport/{id}")
    public void downloadReport(@PathVariable String id, HttpServletResponse response) throws Exception {
        reportService.downloadReport(id, tokenService.getLoginUser(), response);
    }

    @Operation(summary = "历史报告列表")
    @GetMapping("reportResultLogs/{id}")
    public List<ReportResultLog> reportResultLogs(@PathVariable String id) throws Exception {
        return reportService.reportResultLogs(id, tokenService.getLoginUser());
    }

    @Operation(summary = "下载历史报告")
    @GetMapping("downloadHistoryReport/{id}")
    public void downloadHistoryReport(@PathVariable Integer id, HttpServletResponse response) throws Exception {
        reportService.downloadHistoryReport(id, tokenService.getLoginUser(), response);
    }

    @Operation(summary = "删除历史报告")
    @GetMapping("deleteHistoryReport/{id}")
    public void deleteHistoryReport(@PathVariable Integer id) throws Exception {
        reportService.deleteHistoryReport(id, tokenService.getLoginUser());
    }

}
