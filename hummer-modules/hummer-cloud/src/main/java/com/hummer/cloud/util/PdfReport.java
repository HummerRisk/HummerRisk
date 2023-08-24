package com.hummer.cloud.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hummer.cloud.dto.ProjectDTO;
import com.hummer.common.core.domain.Account;
import com.hummer.common.core.domain.CloudProject;
import com.hummer.common.core.domain.CloudTask;
import com.hummer.common.core.utils.StringUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class PdfReport {

    public static List<ProjectDTO> projectDTOList = new LinkedList<>();

    // main测试
    /*public static void main(String[] args) throws Exception {
        try {
            // 1.新建document对象
            Document document = new Document(PageSize.A4);// 建立一个Document对象

            // 2.建立一个书写器(Writer)与document对象关联
            File file = new File(ReportConstants.DEFAULT_BASE_DIR_DIR + DateUtils.datePath() + "-" + UUIDUtil.newUUID() + "-report.pdf");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    // 定义全局的字体静态变量
    private static Font titlefont;
    private static Font headfont;
    private static Font keyfont;
    private static Font textfont;
    // 最大宽度
    private static int maxWidth = 520;
    // 静态代码块
    static {
        try {
            // 不同字体（这里定义为同一种字体：包含不同字号、不同style）
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            titlefont = new Font(bfChinese, 16, Font.BOLD);
            headfont = new Font(bfChinese, 14, Font.BOLD);
            keyfont = new Font(bfChinese, 10, Font.BOLD);
            textfont = new Font(bfChinese, 10, Font.NORMAL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成PDF文件
    public void generatePDF(Document document) throws Exception {

        // 段落
        Paragraph paragraph = new Paragraph("整合报告", titlefont);
        paragraph.setAlignment(1); //设置文字居中 0靠左   1，居中     2，靠右
        paragraph.setIndentationLeft(12); //设置左缩进
        paragraph.setIndentationRight(12); //设置右缩进
        paragraph.setFirstLineIndent(24); //设置首行缩进
        paragraph.setLeading(20f); //行间距
        paragraph.setSpacingBefore(5f); //设置段落上空白
        paragraph.setSpacingAfter(10f); //设置段落下空白

        // 直线
        Paragraph p1 = new Paragraph();
        p1.add(new Chunk(new LineSeparator()));

        // 点线
        Paragraph p2 = new Paragraph();
        p2.add(new Chunk(new DottedLineSeparator()));

        // 超链接
        Anchor anchor = new Anchor("HummerRisk Support");
        anchor.setReference("www.hummerrisk.com");

        // 添加封面图片
        InputStream coverIn = this.getClass().getResourceAsStream("/image/cover.png");
        Image cover = Image.getInstance(coverIn.readAllBytes());
        cover.setAlignment(Image.ALIGN_CENTER);
        cover.scalePercent(24); //依照比例缩放

        // 添加云检测图片
        InputStream cloudIn = this.getClass().getResourceAsStream("/image/cloud.png");
        Image cloudImage = Image.getInstance(cloudIn.readAllBytes());
        cloudImage.setAlignment(Image.ALIGN_CENTER);
        cloudImage.scalePercent(21); //依照比例缩放

        document.add(cover);//封页
        document.add(paragraph);//第一页 Title

        //多云检测
        if (!projectDTOList.isEmpty()) {
            for (ProjectDTO projectDTO : projectDTOList) {
                document.add(cloudImage);//云检测项
                CloudProject cloudProject = projectDTO.getCloudProject();
                String name = "云账号: " + cloudProject.getAccountName() + "    云平台: " + cloudProject.getPluginName();

                List<CloudTask> cloudTaskList = projectDTO.getCloudTaskList();
                // 表格
                PdfPTable table = createTable(new float[] { 210, 80, 80, 110, 80 });
                table.addCell(createCell(name, headfont, Element.ALIGN_LEFT, 6, false));
                table.addCell(createCell("检查项名称", keyfont, Element.ALIGN_CENTER));
                table.addCell(createCell("风险等级", keyfont, Element.ALIGN_CENTER));
                table.addCell(createCell("检测状态", keyfont, Element.ALIGN_CENTER));
                table.addCell(createCell("不合规资产/总资产", keyfont, Element.ALIGN_CENTER));
                table.addCell(createCell("是否合规", keyfont, Element.ALIGN_CENTER));
                for (CloudTask item : cloudTaskList) {
                    table.addCell(createCell(item.getTaskName(), textfont));
                    table.addCell(createCell(severityTrans(item.getSeverity()), textfont));
                    table.addCell(createCell(statusTrans(item.getStatus()), textfont));
                    table.addCell(createCell(item.getReturnSum() + "/" + item.getResourcesSum(), textfont));
                    table.addCell(createCell(complianceTrans(item.getReturnSum()), textfont));
                }
                document.add(p2);// 点线
                document.add(table);
                document.add(p1);// 直线
            }
        }

        document.add(anchor);//底部超链接

    }

    public String severityTrans (String risk) {
        if (StringUtils.equalsIgnoreCase(risk, "CriticalRisk") || StringUtils.equalsIgnoreCase(risk, "CRITICAL")) {
            return "高危风险";
        } else if (StringUtils.equalsIgnoreCase(risk, "HighRisk") || StringUtils.equalsIgnoreCase(risk, "HIGH")) {
            return "高风险";
        } else if (StringUtils.equalsIgnoreCase(risk, "MediumRisk") || StringUtils.equalsIgnoreCase(risk, "MEDIUM")) {
            return "中风险";
        } else if (StringUtils.equalsIgnoreCase(risk, "LowRisk") || StringUtils.equalsIgnoreCase(risk, "LOW")) {
            return "低风险";
        }  else if (StringUtils.equalsIgnoreCase(risk, "UNKNOWN")) {
            return "未知风险";
        }
        return "无风险";
    }

    public String codeUrlTrans (String credential) {
        JSONObject jsonObject = JSON.parseObject(credential);
        String url = jsonObject.getString("url");
        return url;
    }

    public String statusTrans (String status) {
        if (StringUtils.equalsIgnoreCase(status, "FINISHED")) {
            return "已完成";
        } else if (StringUtils.equalsIgnoreCase(status, "ERROR")) {
            return "异 常";
        } else if (StringUtils.equalsIgnoreCase(status, "WARNING")) {
            return "告 警";
        } else if (StringUtils.equalsIgnoreCase(status, "APPROVED") || StringUtils.equalsIgnoreCase(status, "PROCESSING")) {
            return "正在处理";
        }
        return "已完成";
    }

    public String complianceTrans (long sum) {
        if (sum > 0) {
            return "不合规";
        }
        return "合规";
    }

    public String k8sTransByIcon (String icon) {
        if (StringUtils.equalsIgnoreCase(icon, "k8s.png")) {
            return "Kubernetes";
        } else if (StringUtils.equalsIgnoreCase(icon, "rancher.png")) {
            return "Rancher";
        } else if (StringUtils.equalsIgnoreCase(icon, "kubesphere.png")) {
            return "Kubesphere";
        } else if (StringUtils.equalsIgnoreCase(icon, "openshift.png")) {
            return "Openshift";
        }
        return "Kubernetes";
    }

    public String trueTrans (boolean status) {
        if (status) {
            return "True";
        }
        return "False";
    }

    public String trueTrans (String status) {
        if (StringUtils.equalsIgnoreCase(status, "true")) {
            return "True";
        }
        return "False";
    }


    /**------------------------创建表格单元格的方法start----------------------------*/
    /**
     * 创建单元格(指定字体)
     * @param value
     * @param font
     * @return
     */
    public PdfPCell createCell(String value, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }
    /**
     * 创建单元格（指定字体、水平..）
     * @param value
     * @param font
     * @param align
     * @return
     */
    public PdfPCell createCell(String value, Font font, int align) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }
    /**
     * 创建单元格（指定字体、水平居..、单元格跨x列合并）
     * @param value
     * @param font
     * @param align
     * @param colspan
     * @return
     */
    public PdfPCell createCell(String value, Font font, int align, int colspan) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }
    /**
     * 创建单元格（指定字体、水平居..、单元格跨x列合并、设置单元格内边距）
     * @param value
     * @param font
     * @param align
     * @param colspan
     * @param boderFlag
     * @return
     */
    public PdfPCell createCell(String value, Font font, int align, int colspan, boolean boderFlag) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setPhrase(new Phrase(value, font));
        cell.setPadding(3.0f);
        if (!boderFlag) {
            cell.setBorder(0);
            cell.setPaddingTop(15.0f);
            cell.setPaddingBottom(8.0f);
        } else if (boderFlag) {
            cell.setBorder(0);
            cell.setPaddingTop(0.0f);
            cell.setPaddingBottom(15.0f);
        }
        return cell;
    }
    /**
     * 创建单元格（指定字体、水平..、边框宽度：0表示无边框、内边距）
     * @param value
     * @param font
     * @param align
     * @param borderWidth
     * @param paddingSize
     * @param flag
     * @return
     */
    public PdfPCell createCell(String value, Font font, int align, float[] borderWidth, float[] paddingSize, boolean flag) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        cell.setBorderWidthLeft(borderWidth[0]);
        cell.setBorderWidthRight(borderWidth[1]);
        cell.setBorderWidthTop(borderWidth[2]);
        cell.setBorderWidthBottom(borderWidth[3]);
        cell.setPaddingTop(paddingSize[0]);
        cell.setPaddingBottom(paddingSize[1]);
        if (flag) {
            cell.setColspan(2);
        }
        return cell;
    }
    /**------------------------创建表格单元格的方法end----------------------------*/


    /**--------------------------创建表格的方法start------------------- ---------*/
    /**
     * 创建默认列宽，指定列数、水平(居中、右、左)的表格
     * @param colNumber
     * @param align
     * @return
     */
    public PdfPTable createTable(int colNumber, int align) {
        PdfPTable table = new PdfPTable(colNumber);
        try {
            table.setTotalWidth(maxWidth);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(align);
            table.getDefaultCell().setBorder(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }
    /**
     * 创建指定列宽、列数的表格
     * @param widths
     * @return
     */
    public PdfPTable createTable(float[] widths) {
        PdfPTable table = new PdfPTable(widths);
        try {
            table.setTotalWidth(maxWidth);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setBorder(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }
    /**
     * 创建空白的表格
     * @return
     */
    public PdfPTable createBlankTable() {
        PdfPTable table = new PdfPTable(1);
        table.getDefaultCell().setBorder(0);
        table.addCell(createCell("", keyfont));
        table.setSpacingAfter(20.0f);
        table.setSpacingBefore(20.0f);
        return table;
    }
    /**--------------------------创建表格的方法end------------------- ---------*/


}
