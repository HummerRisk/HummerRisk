package com.hummer.cloud.service;

import com.hummer.cloud.dto.AccountDTO;
import com.hummer.cloud.dto.ReportDTO;
import com.hummer.cloud.dto.ReportResultDTO;
import com.hummer.cloud.mapper.ReportResultDetailMapper;
import com.hummer.cloud.mapper.ReportResultLogMapper;
import com.hummer.cloud.mapper.ReportResultMapper;
import com.hummer.cloud.mapper.ext.ExtAccountMapper;
import com.hummer.cloud.mapper.ext.ExtReportResultMapper;
import com.hummer.cloud.util.MyHeaderFooter;
import com.hummer.cloud.util.PdfReport;
import com.hummer.cloud.util.Watermark;
import com.hummer.common.core.constant.CloudTaskConstants;
import com.hummer.common.core.constant.ReportConstants;
import com.hummer.common.core.constant.ResourceOperation;
import com.hummer.common.core.constant.ResourceTypeConstants;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.task.AccountVo;
import com.hummer.common.core.dto.AccountTreeDTO;
import com.hummer.common.core.utils.*;
import com.hummer.system.api.IOperationLogService;
import com.hummer.system.api.model.LoginUser;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.*;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ReportService {


    @Autowired
    private ReportResultMapper reportResultMapper;
    @Autowired
    private ReportResultLogMapper reportResultLogMapper;
    @Autowired
    private ReportResultDetailMapper reportResultDetailMapper;
    @Autowired
    private ExtReportResultMapper extReportResultMapper;
    @Autowired
    private ExtAccountMapper extAccountMapper;
    @DubboReference
    private IOperationLogService operationLogService;

    public List<ReportResultDTO> reportList(ReportResultDTO request) {
        return extReportResultMapper.reportList(request);
    }

    public ReportResultDTO getReport(String reportId) {
        return extReportResultMapper.getReport(reportId);
    }

    public AccountTreeDTO listAccounts() {
        AccountTreeDTO dto = new AccountTreeDTO();
        //云账号
        AccountExample accountExample = new AccountExample();
        accountExample.createCriteria().andPluginIdIn(PlatformUtils.getCloudPlugin());
        accountExample.setOrderByClause("create_time desc");
        List<AccountVo> accounts = extReportResultMapper.selectAccountByExample(accountExample);
        dto.setCloudAccount(accounts);
        return dto;
    }

    public void createReport(ReportDTO reportDTO, LoginUser loginUser) {
        ReportResult reportResult = new ReportResult();
        String uuid = UUIDUtil.newUUID();
        String name = reportDTO.getName();
        long now = System.currentTimeMillis();
        String operator = loginUser.getUserName();
        reportResult.setId(uuid);
        reportResult.setName(name);
        reportResult.setCreateTime(now);
        reportResult.setUpdateTime(now);
        reportResult.setOperator(operator);
        reportResult.setHistoryNumber(0L);
        reportResult.setDownloadNumber(0L);
        reportResult.setStatus(CloudTaskConstants.TASK_STATUS.APPROVED.name());
        reportResultMapper.insertSelective(reportResult);

        List<Map<String, String>> list = reportDTO.getList();
        for (Map<String, String> map : list) {
            ReportResultDetail reportResultDetail = new ReportResultDetail();
            reportResultDetail.setResultId(uuid);
            reportResultDetail.setAccountId(map.get("id"));
            reportResultDetail.setOperator(operator);
            reportResultDetail.setType(map.get("type"));
            reportResultDetail.setCreateTime(now);
            reportResultDetail.setStatus(CloudTaskConstants.TASK_STATUS.APPROVED.name());
            reportResultDetailMapper.insertSelective(reportResultDetail);
        }

        this.generatorReport(uuid, loginUser);
    }

    public void updateReport(ReportDTO reportDTO, LoginUser loginUser) throws Exception {
        ReportResult reportResult = reportResultMapper.selectByPrimaryKey(reportDTO.getId());
        String name = reportDTO.getName();
        long now = System.currentTimeMillis();
        String operator = loginUser.getUserName();
        reportResult.setName(name);
        reportResult.setUpdateTime(now);
        reportResult.setOperator(operator);
        reportResult.setStatus(CloudTaskConstants.TASK_STATUS.APPROVED.name());
        reportResultMapper.updateByPrimaryKey(reportResult);

        deleteResultDetailByResultId(reportDTO.getId(), loginUser);

        List<Map<String, String>> list = reportDTO.getList();
        for (Map<String, String> map : list) {
            ReportResultDetail reportResultDetail = new ReportResultDetail();
            reportResultDetail.setResultId(reportDTO.getId());
            reportResultDetail.setAccountId(map.get("id"));
            reportResultDetail.setOperator(operator);
            reportResultDetail.setType(map.get("type"));
            reportResultDetail.setCreateTime(now);
            reportResultDetail.setStatus(CloudTaskConstants.TASK_STATUS.APPROVED.name());
            reportResultDetailMapper.insertSelective(reportResultDetail);
        }

        this.generatorReport(reportDTO.getId(), loginUser);
    }

    public void deleteReports(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteReport(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void deleteReport(String id, LoginUser loginUser) throws Exception {
        ReportResult reportResult = reportResultMapper.selectByPrimaryKey(id);
        reportResultMapper.deleteByPrimaryKey(id);

        deleteResultDetailByResultId(id, loginUser);
        deleteResultLogByResultId(id, loginUser);
        operationLogService.log(loginUser, id, reportResult.getName(), ResourceTypeConstants.REPORT_CENTER.name(), ResourceOperation.DELETE, "i18n_delete_report");

    }

    public void deleteResultDetailByResultId(String id, LoginUser loginUser) throws Exception {
        ReportResultDetailExample example = new ReportResultDetailExample();
        example.createCriteria().andResultIdEqualTo(id);
        reportResultDetailMapper.deleteByExample(example);
    }

    public void deleteResultLogByResultId(String id, LoginUser loginUser) throws Exception {
        ReportResultLogExample reportResultLogExample = new ReportResultLogExample();
        reportResultLogExample.createCriteria().andResultIdEqualTo(id);
        reportResultLogMapper.deleteByExample(reportResultLogExample);
    }

    public void genReport(String id, LoginUser loginUser) {
        ReportResult reportResult = reportResultMapper.selectByPrimaryKey(id);
        long now = System.currentTimeMillis();
        String operator = loginUser.getUserName();
        reportResult.setUpdateTime(now);
        reportResult.setOperator(operator);
        reportResult.setStatus(CloudTaskConstants.TASK_STATUS.APPROVED.name());
        reportResultMapper.updateByPrimaryKey(reportResult);

        this.generatorReport(id, loginUser);
    }

    public void generatorReport(String id, LoginUser loginUser) {
        ReportResult reportResult = reportResultMapper.selectByPrimaryKey(id);

        try {

            ReportResultDetailExample example = new ReportResultDetailExample();
            example.createCriteria().andResultIdEqualTo(id);
            List<ReportResultDetail> reportResultDetailList = reportResultDetailMapper.selectByExample(example);

            List<AccountDTO> accountDTOList = new LinkedList<>();

            for (ReportResultDetail reportResultDetail : reportResultDetailList) {
                if (StringUtils.equals(reportResultDetail.getType(), "cloudAccount")) {
                    AccountDTO accountDTO = new AccountDTO();
                    String accountId = reportResultDetail.getAccountId();
                    accountDTO.setAccount(extAccountMapper.account(accountId));
                    accountDTO.setCloudTaskList(extAccountMapper.cloudTaskList(accountId));
                    accountDTOList.add(accountDTO);
                }
            }

            PdfReport.accountDTOList = accountDTOList;

            // 1.新建document对象
            Document document = new Document(PageSize.A4);// 建立一个Document对象

            String path = ReportConstants.DEFAULT_BASE_DIR + DateUtils.datePath() + "-" + UUIDUtil.newUUID() + "-report.pdf";

            // 2.建立一个书写器(Writer)与document对象关联
            File file = new File(path);
            file.createNewFile();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            writer.setPageEvent(new Watermark("HummerRisk"));// 水印
            writer.setPageEvent(new MyHeaderFooter());// 页眉/页脚

            // 3.打开文档
            document.open();
            document.addTitle("整合报告");// 标题
            document.addAuthor("HummerRisk");// 作者
            document.addSubject("HummerRisk Report");// 主题
            document.addKeywords("HummerRisk");// 关键字
            document.addCreator("HummerRisk");// 创建者

            // 4.向文档中添加内容
            new PdfReport().generatePDF(document);

            // 5.关闭文档
            document.close();

            reportResult.setPdfPath(path);
            reportResult.setHistoryNumber(reportResult.getHistoryNumber() + 1);
            reportResult.setStatus(CloudTaskConstants.TASK_STATUS.FINISHED.name());
            reportResultMapper.updateByPrimaryKey(reportResult);

            ReportResultLog reportResultLog = new ReportResultLog();
            reportResultLog.setResultId(reportResult.getId());
            reportResultLog.setCreateTime(System.currentTimeMillis());
            reportResultLog.setOperator(reportResult.getOperator());
            reportResultLog.setPdfPath(path);
            reportResultLog.setStatus(CloudTaskConstants.TASK_STATUS.FINISHED.name());
            reportResultLog.setDownloadNumber(0L);
            reportResultLogMapper.insertSelective(reportResultLog);
        } catch (Exception e) {
            reportResult.setStatus(CloudTaskConstants.TASK_STATUS.ERROR.name());
            reportResult.setPdfLog(e.getMessage());
            reportResultMapper.updateByPrimaryKey(reportResult);
            LogUtil.error(e.getMessage());
        }

    }

    public void downloadReport(String id, LoginUser loginUser, HttpServletResponse response) throws Exception {
        ReportResult reportResult = reportResultMapper.selectByPrimaryKey(id);
        String path = reportResult.getPdfPath();

        FileInputStream fis = null;
        ServletOutputStream sos = null;
        try {
            String fileName = path.replace(ReportConstants.DEFAULT_BASE_DIR, "");
            //设置响应头
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            File file = new File(path);
            InputStream inputStream = new FileInputStream(file);
            sos = response.getOutputStream();
            IOUtils.copy(inputStream, sos);
        } catch (Exception e) {
            throw new RuntimeException("下载失败！" + e.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (sos != null) {
                    sos.flush();
                    sos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        reportResult.setDownloadNumber(reportResult.getDownloadNumber() + 1);
        reportResultMapper.updateByPrimaryKey(reportResult);

        ReportResultLogExample example = new ReportResultLogExample();
        example.createCriteria().andResultIdEqualTo(id).andPdfPathEqualTo(reportResult.getPdfPath());
        List<ReportResultLog> reportResultLogs = reportResultLogMapper.selectByExample(example);
        if (reportResultLogs.size() > 0) {
            ReportResultLog reportResultLog = reportResultLogs.get(0);
            reportResultLog.setDownloadNumber(reportResultLog.getDownloadNumber() + 1);
            reportResultLogMapper.updateByPrimaryKey(reportResultLog);
        }
    }

    public void downloadHistoryReport(Integer id, LoginUser loginUser, HttpServletResponse response) throws Exception {
        ReportResultLog reportResultLog = reportResultLogMapper.selectByPrimaryKey(id);
        String path = reportResultLog.getPdfPath();

        FileInputStream fis = null;
        ServletOutputStream sos = null;
        try {
            String fileName = path.replace(ReportConstants.DEFAULT_BASE_DIR, "");
            //设置响应头
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            File file = new File(path);
            InputStream inputStream = new FileInputStream(file);
            sos = response.getOutputStream();
            IOUtils.copy(inputStream, sos);
        } catch (Exception e) {
            throw new RuntimeException("下载失败！" + e.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (sos != null) {
                    sos.flush();
                    sos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        reportResultLog.setDownloadNumber(reportResultLog.getDownloadNumber() + 1);
        reportResultLogMapper.updateByPrimaryKey(reportResultLog);

        ReportResult reportResult = reportResultMapper.selectByPrimaryKey(reportResultLog.getResultId());
        reportResult.setDownloadNumber(reportResult.getDownloadNumber() + 1);
        reportResultMapper.updateByPrimaryKey(reportResult);
    }

    public List<ReportResultLog> reportResultLogs(@PathVariable String id, LoginUser loginUser) throws Exception {
        ReportResultLogExample example = new ReportResultLogExample();
        example.createCriteria().andResultIdEqualTo(id);
        List<ReportResultLog> reportResultLogs = reportResultLogMapper.selectByExampleWithBLOBs(example);
        return reportResultLogs;
    }

    public void deleteHistoryReport(Integer id, LoginUser loginUser) throws Exception {
        ReportResultLog reportResultLog = reportResultLogMapper.selectByPrimaryKey(id);
        reportResultLogMapper.deleteByPrimaryKey(id);
    }

}
