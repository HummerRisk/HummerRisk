package com.hummer.system.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class PdfUtil {
    // 标准字体
    public static Font NORMALFONT;
    // 加粗字体
    public static Font BOLDFONT;
    //固定高
    public static float fixedHeight = 27f;
    //间距
    public static int spacing = 5;

    static {
        try {
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            NORMALFONT = new Font(bfChinese, 10, Font.NORMAL);
            BOLDFONT = new Font(bfChinese, 14, Font.BOLD);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Document createDocument() {
        //生成pdf
        Document document = new Document();
        // 页面大小
        Rectangle rectangle = new Rectangle(PageSize.A4);
        // 页面背景颜色
        rectangle.setBackgroundColor(BaseColor.WHITE);
        document.setPageSize(rectangle);
        // 页边距 左，右，上，下
        document.setMargins(20, 20, 20, 20);
        return document;
    }


    /**
     * @param text 段落内容
     * @return
     */
    public static Paragraph createParagraph(String text, Font font) {
        Paragraph elements = new Paragraph(text, font);
        elements.setSpacingBefore(5);
        elements.setSpacingAfter(5);
        elements.setSpacingAfter(spacing);
        return elements;
    }


    public static Font createFont(int fontNumber, int fontSize, BaseColor fontColor) {
        //中文字体 ----不然中文会乱码
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            return new Font(bf, fontNumber, fontSize, fontColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Font(bf, Font.DEFAULTSIZE, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * 隐藏表格边框线
     *
     * @param cell 单元格
     */
    public static void disableBorderSide(PdfPCell cell) {
        if (cell != null) {
            cell.disableBorderSide(1);
            cell.disableBorderSide(2);
            cell.disableBorderSide(4);
            cell.disableBorderSide(8);
        }
    }


    /**
     * 创建居中得单元格
     *
     * @return
     */
    public static PdfPCell createCenterPdfPCell() {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setFixedHeight(fixedHeight);
        return cell;
    }

    /**
     * 创建指定文字得单元格
     *
     * @param text
     * @return
     */
    public static PdfPCell createCenterPdfPCell(String text, int rowSpan, int colSpan, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setFixedHeight(fixedHeight);
        cell.setRowspan(rowSpan);
        cell.setColspan(colSpan);
        return cell;
    }

    /**
     * @param len 表格列数
     * @return
     */
    public static PdfPTable createPdfPTable(int len) {
        PdfPTable pdfPTable = new PdfPTable(len);
        pdfPTable.setSpacingBefore(5);
        pdfPTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        return pdfPTable;
    }
}
