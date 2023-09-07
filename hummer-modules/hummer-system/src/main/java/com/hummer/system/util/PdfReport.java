package com.hummer.system.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.dto.CodeResultDTO;
import com.hummer.common.core.dto.FsResultDTO;
import com.hummer.common.core.dto.ImageResultDTO;
import com.hummer.common.core.utils.StringUtils;
import com.hummer.system.dto.*;
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

    public static List<AccountDTO> accountDTOList = new LinkedList<>();
    public static List<K8sDTO> k8sDTOList = new LinkedList<>();
    public static List<ServerDTO> serverDTOList = new LinkedList<>();
    public static List<ImageDTO> imageDTOList = new LinkedList<>();
    public static List<ConfigDTO> configDTOList = new LinkedList<>();
    public static List<CodeDTO> codeDTOList = new LinkedList<>();
    public static List<FileSystemDTO> fileSystemDTOList = new LinkedList<>();

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

        // 添加K8s检测图片
        InputStream k8sIn = this.getClass().getResourceAsStream("/image/k8s.png");
        Image k8sImage = Image.getInstance(k8sIn.readAllBytes());
        k8sImage.setAlignment(Image.ALIGN_CENTER);
        k8sImage.scalePercent(21); //依照比例缩放

        // 添加部署检测图片
        InputStream configIn = this.getClass().getResourceAsStream("/image/config.png");
        Image configImage = Image.getInstance(configIn.readAllBytes());
        configImage.setAlignment(Image.ALIGN_CENTER);
        configImage.scalePercent(21); //依照比例缩放

        // 添加主机检测图片
        InputStream serverIn = this.getClass().getResourceAsStream("/image/server.png");
        Image serverImage = Image.getInstance(serverIn.readAllBytes());
        serverImage.setAlignment(Image.ALIGN_CENTER);
        serverImage.scalePercent(21); //依照比例缩放

        // 添加镜像检测图片
        InputStream imageIn = this.getClass().getResourceAsStream("/image/image.png");
        Image imageImage = Image.getInstance(imageIn.readAllBytes());
        imageImage.setAlignment(Image.ALIGN_CENTER);
        imageImage.scalePercent(21); //依照比例缩放

        // 添加源码检测图片
        InputStream codeIn = this.getClass().getResourceAsStream("/image/code.png");
        Image codeImage = Image.getInstance(codeIn.readAllBytes());
        codeImage.setAlignment(Image.ALIGN_CENTER);
        codeImage.scalePercent(21); //依照比例缩放

        // 添加文件检测图片
        InputStream filesystemIn = this.getClass().getResourceAsStream("/image/filesystem.png");
        Image fsImage = Image.getInstance(filesystemIn.readAllBytes());
        fsImage.setAlignment(Image.ALIGN_CENTER);
        fsImage.scalePercent(21); //依照比例缩放

        document.add(cover);//封页
        document.add(paragraph);//第一页 Title

        //多云检测
        if (accountDTOList.size() > 0) {
            for (AccountDTO accountDTO : accountDTOList) {
                document.add(cloudImage);//云检测项
                Account account = accountDTO.getAccount();
                String name = "云账号: " + account.getName() + "    云平台: " + account.getPluginName();

                List<CloudTask> cloudTaskList = accountDTO.getCloudTaskList();
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

        //主机检测
        if (serverDTOList.size() > 0) {
            for (ServerDTO serverDTO : serverDTOList) {
                document.add(serverImage);//主机检测项
                Server server = serverDTO.getServer();
                String name = "主机名称: " + server.getName() + "    IP: " + server.getIp() + "    端口: " + server.getPort() + "    类型: " + server.getType();

                ServerLynisResult serverLynisResult = serverDTO.getServerLynisResult();

                List<ServerLynisResultDetail> serverLynisResultDetailList = serverDTO.getServerLynisResultDetailList();

                List<ServerResult> serverResultList = serverDTO.getServerResultList();
                // 表格1
                PdfPTable table1 = createTable(new float[] { 80, 90, 180, 70, 70, 70 });
                table1.addCell(createCell(name, headfont, Element.ALIGN_LEFT, 6, false));
                table1.addCell(createCell("主机名称", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("IP端口", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("检测规则", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("风险等级", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("检测状态", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("是否安全", keyfont, Element.ALIGN_CENTER));
                for (ServerResult item : serverResultList) {
                    table1.addCell(createCell(item.getServerName(), textfont));
                    table1.addCell(createCell(item.getIp() + ":" + item.getPort(), textfont));
                    table1.addCell(createCell(item.getRuleName(), textfont));
                    table1.addCell(createCell(severityTrans(item.getSeverity()), textfont));
                    table1.addCell(createCell(statusTrans(item.getResultStatus()), textfont));
                    table1.addCell(createCell(trueTrans(item.getIsSeverity()), textfont));
                }

                document.add(p2);// 点线
                document.add(table1);
                document.add(p1);// 直线
            }
        }

        //K8s检测
        if (k8sDTOList.size() > 0) {
            for (K8sDTO k8sDTO : k8sDTOList) {
                document.add(k8sImage);//K8s检测项
                CloudNativeResult cloudNativeResult = k8sDTO.getCloudNativeResult();
                String name = "K8s 账号: " + cloudNativeResult.getName() + "    K8s 平台: " + k8sTransByIcon(cloudNativeResult.getPluginIcon());

                // 漏洞检测
                List<CloudNativeResultItem> cloudNativeResultItemList = k8sDTO.getCloudNativeResultItemList();
                // 表格1
                PdfPTable table1 = createTable(new float[] { 120, 100, 80, 60, 100, 100 });
                table1.addCell(createCell(name, headfont, Element.ALIGN_LEFT, 6, false));
                table1.addCell(createCell("漏洞检测统计", headfont, Element.ALIGN_LEFT, 6, false));
                table1.addCell(createCell("资源名称", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("漏洞ID", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("风险等级", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("评分", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("安装版本", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("修复版本", keyfont, Element.ALIGN_CENTER));
                for (CloudNativeResultItem item : cloudNativeResultItemList) {
                    table1.addCell(createCell(item.getResource(), textfont));
                    table1.addCell(createCell(item.getVulnerabilityId(), textfont));
                    table1.addCell(createCell(severityTrans(item.getSeverity()), textfont));
                    table1.addCell(createCell(item.getScore(), textfont));
                    table1.addCell(createCell(item.getInstalledVersion(), textfont));
                    table1.addCell(createCell(item.getFixedVersion(), textfont));
                }

                // 配置审计
                List<CloudNativeResultConfigItem> cloudNativeResultConfigItemList = k8sDTO.getCloudNativeResultConfigItemList();
                // 表格2
                PdfPTable table2 = createTable(new float[] { 190, 70, 80, 160, 60 });
                table2.addCell(createCell(name, headfont, Element.ALIGN_LEFT, 6, false));
                table2.addCell(createCell("配置审计统计", headfont, Element.ALIGN_LEFT, 6, false));
                table2.addCell(createCell("名称", keyfont, Element.ALIGN_CENTER));
                table2.addCell(createCell("检查ID", keyfont, Element.ALIGN_CENTER));
                table2.addCell(createCell("风险等级", keyfont, Element.ALIGN_CENTER));
                table2.addCell(createCell("类别", keyfont, Element.ALIGN_CENTER));
                table2.addCell(createCell("状态", keyfont, Element.ALIGN_CENTER));
                for (CloudNativeResultConfigItem item : cloudNativeResultConfigItemList) {
                    table2.addCell(createCell(item.getTitle(), textfont));
                    table2.addCell(createCell(item.getCheckId(), textfont));
                    table2.addCell(createCell(severityTrans(item.getSeverity()), textfont));
                    table2.addCell(createCell(item.getCategory(), textfont));
                    table2.addCell(createCell(trueTrans(item.getSuccess()), textfont));
                }

                // kubench
                List<CloudNativeResultKubenchWithBLOBs> cloudNativeResultKubenchList = k8sDTO.getCloudNativeResultKubenchList();
                // 表格3
                PdfPTable table3 = createTable(new float[] { 60, 180, 80, 240 });
                table3.addCell(createCell(name, headfont, Element.ALIGN_LEFT, 6, false));
                table3.addCell(createCell("CIS Benchmark 统计", headfont, Element.ALIGN_LEFT, 6, false));
                table3.addCell(createCell("编号", keyfont, Element.ALIGN_CENTER));
                table3.addCell(createCell("名称", keyfont, Element.ALIGN_CENTER));
                table3.addCell(createCell("风险等级", keyfont, Element.ALIGN_CENTER));
                table3.addCell(createCell("描述", keyfont, Element.ALIGN_CENTER));
                for (CloudNativeResultKubenchWithBLOBs item : cloudNativeResultKubenchList) {
                    table3.addCell(createCell(item.getNumber(), textfont));
                    table3.addCell(createCell(item.getTitle(), textfont));
                    table3.addCell(createCell(item.getSeverity(), textfont));
                    table3.addCell(createCell(item.getDescription(), textfont));
                }

                // 合规检测
                List<CloudTask> cloudTaskList = k8sDTO.getCloudTaskList();
                // 表格4
                PdfPTable table4 = createTable(new float[] { 160, 80, 80, 100, 80 });
                table4.addCell(createCell(name, headfont, Element.ALIGN_LEFT, 6, false));
                table4.addCell(createCell("合规检测统计", headfont, Element.ALIGN_LEFT, 6, false));
                table4.addCell(createCell("检查项名称", keyfont, Element.ALIGN_CENTER));
                table4.addCell(createCell("风险等级", keyfont, Element.ALIGN_CENTER));
                table4.addCell(createCell("检测状态", keyfont, Element.ALIGN_CENTER));
                table4.addCell(createCell("不合规资产/总资产", keyfont, Element.ALIGN_CENTER));
                table4.addCell(createCell("是否合规", keyfont, Element.ALIGN_CENTER));
                for (CloudTask cloudTask : cloudTaskList) {
                    table4.addCell(createCell(cloudTask.getTaskName(), textfont));
                    table4.addCell(createCell(severityTrans(cloudTask.getSeverity()), textfont));
                    table4.addCell(createCell(statusTrans(cloudTask.getStatus()), textfont));
                    table4.addCell(createCell(cloudTask.getReturnSum() + "/" + cloudTask.getResourcesSum(), textfont));
                    table4.addCell(createCell(complianceTrans(cloudTask.getReturnSum()), textfont));
                }
                document.add(p2);// 点线
                document.add(table1);
                document.add(table2);
                document.add(table3);
                document.add(table4);
                document.add(p1);// 直线
            }
        }

        //部署检测
        if (configDTOList.size() > 0) {
            for (ConfigDTO configDTO : configDTOList) {
                document.add(configImage);//部署检测项
                CloudNativeConfigResult cloudNativeConfigResult = configDTO.getCloudNativeConfigResult();
                String name = "部署检测名称: " + cloudNativeConfigResult.getName();

                List<CloudNativeConfigResultItem> cloudNativeConfigResultItemList = configDTO.getCloudNativeConfigResultItemList();
                // 表格1
                PdfPTable table1 = createTable(new float[] { 80, 140, 100, 80, 160 });
                table1.addCell(createCell(name, headfont, Element.ALIGN_LEFT, 6, false));
                table1.addCell(createCell("编号", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("类型", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("风险等级", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("检测状态", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("检测项描述", keyfont, Element.ALIGN_CENTER));
                for (CloudNativeConfigResultItem item : cloudNativeConfigResultItemList) {
                    table1.addCell(createCell(item.getItemId(), textfont));
                    table1.addCell(createCell(item.getType(), textfont));
                    table1.addCell(createCell(severityTrans(item.getSeverity()), textfont));
                    table1.addCell(createCell(item.getStatus(), textfont));
                    table1.addCell(createCell(item.getTitle(), textfont));
                }

                document.add(p2);// 点线
                document.add(table1);
                document.add(p1);// 直线
            }
        }

        //镜像检测
        if (imageDTOList.size() > 0) {
            for (ImageDTO imageDTO : imageDTOList) {
                document.add(imageImage);//部署检测项
                ImageResultDTO imageResult = imageDTO.getImageResult();
                String name = "镜像名称: " + imageResult.getName() + "    镜像地址: " + imageResult.getImageUrl() + ":" + imageResult.getImageTag();

                List<ImageResultItem> imageResultItemList = imageDTO.getImageResultItemList();
                // 表格1
                PdfPTable table1 = createTable(new float[] { 100, 120, 100, 120, 120 });
                table1.addCell(createCell(name, headfont, Element.ALIGN_LEFT, 6, false));
                table1.addCell(createCell("资源名称", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("漏洞ID", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("风险等级", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("安装版本", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("修复版本", keyfont, Element.ALIGN_CENTER));
                for (ImageResultItem item : imageResultItemList) {
                    table1.addCell(createCell(item.getPkgName(), textfont));
                    table1.addCell(createCell(item.getVulnerabilityId(), textfont));
                    table1.addCell(createCell(severityTrans(item.getSeverity()), textfont));
                    table1.addCell(createCell(item.getInstalledVersion(), textfont));
                    table1.addCell(createCell(item.getFixedVersion(), textfont));
                }

                document.add(p2);// 点线
                document.add(table1);
                document.add(p1);// 直线
            }
        }

        //源码检测
        if (codeDTOList.size() > 0) {
            for (CodeDTO codeDTO : codeDTOList) {
                document.add(codeImage);//源码检测项
                CodeResultDTO codeResult = codeDTO.getCodeResult();
                String name = "源码名称: " + codeResult.getName() + "    源码地址: " + codeUrlTrans(codeResult.getCodeUrl());

                List<CodeResultItem> codeResultItemList = codeDTO.getCodeResultItemList();
                // 表格1
                PdfPTable table1 = createTable(new float[] { 100, 120, 100, 120, 120 });
                table1.addCell(createCell(name, headfont, Element.ALIGN_LEFT, 6, false));
                table1.addCell(createCell("资源名称", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("漏洞ID", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("风险等级", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("安装版本", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("修复版本", keyfont, Element.ALIGN_CENTER));
                for (CodeResultItem item : codeResultItemList) {
                    table1.addCell(createCell(item.getPkgName(), textfont));
                    table1.addCell(createCell(item.getVulnerabilityId(), textfont));
                    table1.addCell(createCell(severityTrans(item.getSeverity()), textfont));
                    table1.addCell(createCell(item.getInstalledVersion(), textfont));
                    table1.addCell(createCell(item.getFixedVersion(), textfont));
                }

                document.add(p2);// 点线
                document.add(table1);
                document.add(p1);// 直线
            }
        }

        //文件检测
        if (fileSystemDTOList.size() > 0) {
            for (FileSystemDTO fileSystemDTO : fileSystemDTOList) {
                document.add(fsImage);//源码检测项
                FsResultDTO fileSystemResult = fileSystemDTO.getFileSystemResult();
                String name = "文件别名: " + fileSystemResult.getName() + "    文件名: " + fileSystemResult.getFileName();

                List<FileSystemResultItem> fileSystemResultItemList = fileSystemDTO.getFileSystemResultItemList();
                // 表格1
                PdfPTable table1 = createTable(new float[] { 100, 120, 100, 120, 120 });
                table1.addCell(createCell(name, headfont, Element.ALIGN_LEFT, 6, false));
                table1.addCell(createCell("资源名称", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("漏洞ID", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("风险等级", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("安装版本", keyfont, Element.ALIGN_CENTER));
                table1.addCell(createCell("修复版本", keyfont, Element.ALIGN_CENTER));
                for (FileSystemResultItem item : fileSystemResultItemList) {
                    table1.addCell(createCell(item.getPkgName(), textfont));
                    table1.addCell(createCell(item.getVulnerabilityId(), textfont));
                    table1.addCell(createCell(severityTrans(item.getSeverity()), textfont));
                    table1.addCell(createCell(item.getInstalledVersion(), textfont));
                    table1.addCell(createCell(item.getFixedVersion(), textfont));
                }

                document.add(p2);// 点线
                document.add(table1);
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
