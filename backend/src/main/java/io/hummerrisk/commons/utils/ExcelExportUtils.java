package io.hummerrisk.commons.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel 相关操作类(小数据量写入<=60000)
 * @author harris
 */
public class ExcelExportUtils {

    private static final int DEFAULT_COLUMN_SIZE = 30;
    public static final String FORMULA = "FORMULA:";

    public static String underlineToCamelCase(String para) {
        StringBuilder result = new StringBuilder();
        String a[] = para.split("_");
        for (String s : a) {
            if (result.length() == 0) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * 日期转化为字符串,格式为yyyy-MM-dd HH:mm:ss
     */
    private static String getCnDate(Date date) {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    private static void setExcelTitle(Workbook workBook, String sheetName, Map<String, List<String>> columnNames) {

        Map<String, CellStyle> cellStyleMap = styleMap(workBook);
        // 表头样式
        CellStyle headStyle = cellStyleMap.get("head");
        // 生成一个表格
        Sheet sheet = workBook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workBook.createSheet(sheetName);
        }
        //最新Excel列索引,从0开始
        int lastRowIndex = sheet.getLastRowNum();
        if (lastRowIndex > 0) {
            lastRowIndex++;
        }
        // 设置表格默认列宽度
        sheet.setDefaultColumnWidth(DEFAULT_COLUMN_SIZE);
        // 合并单元格
        mergeRow(columnNames, headStyle, sheet, lastRowIndex);
    }

    private static void mergeRow(Map<String, List<String>> columnNames, CellStyle headStyle, Sheet sheet, int lastRowIndex) {
        int lastIndex = 0;

        Row rowMerged = sheet.createRow(lastRowIndex);
        Row secondRow = sheet.createRow(lastRowIndex + 1);
        for (String firstTitle : columnNames.keySet()) {
            Cell mergedCell = rowMerged.createCell(lastIndex);
            mergedCell.setCellStyle(headStyle);
            mergedCell.setCellValue(new XSSFRichTextString(firstTitle));
            List<String> names = columnNames.get(firstTitle);
            CellRangeAddress region;
            if (CollectionUtils.isEmpty(names)) {
                region = new CellRangeAddress(lastRowIndex, lastRowIndex + 1, lastIndex, lastIndex);
                lastIndex += 1;
                sheet.addMergedRegion(region);
                setRegionStyle(sheet, region, headStyle);
            } else {
                if (names.size() > 1) {
                    int endColIndex = lastIndex + names.size() - 1;
                    region = new CellRangeAddress(lastRowIndex, lastRowIndex, lastIndex, endColIndex);
                    setCellValue(names, headStyle, secondRow, lastIndex);
                    lastIndex += names.size();
                    sheet.addMergedRegion(region);
                    setRegionStyle(sheet, region, headStyle);
                } else {
                    setCellValue(names, headStyle, secondRow, lastIndex);
                    lastIndex += names.size();
                }
            }

        }
    }

    private static void setCellValue(List<String> columnNames, CellStyle headStyle, Row row, int lastIndex) {
        for (int i = lastIndex; i < lastIndex + columnNames.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(headStyle);
            RichTextString text = new XSSFRichTextString(columnNames.get(i - lastIndex));
            cell.setCellValue(text);
        }
    }

    /**
     * 导出字符串数据
     *
     * @param columnNames 列名
     * @param objects     目标数据
     */
    public static byte[] exportExcelData(String sheetName, List<String> columnNames, List<List<Object>> objects) throws Exception {
        // 声明一个工作薄
        Workbook workBook = new XSSFWorkbook();
        setExcelTitle(workBook, sheetName, columnNames);

        return getExcelBytes(sheetName, objects, workBook);
    }

    private static byte[] getExcelBytes(String sheetName, List<List<Object>> objects, Workbook workBook) throws IOException {
        Map<String, CellStyle> cellStyleMap = styleMap(workBook);
        // 正文样式
        CellStyle contentStyle = cellStyleMap.get("content");
        //正文整数样式
        CellStyle contentIntegerStyle = cellStyleMap.get("integer");
        //正文带小数整数样式
        CellStyle contentDoubleStyle = cellStyleMap.get("double");
        // 生成一个表格
        Sheet sheet = workBook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workBook.createSheet(sheetName);
        }
        //最新Excel列索引
        int lastRowIndex = sheet.getLastRowNum();
        lastRowIndex++;
        // 设置表格默认列宽度
        sheet.setDefaultColumnWidth(DEFAULT_COLUMN_SIZE);
        // 遍历集合数据,产生数据行,前两行为标题行与表头行
        exportData(objects, contentStyle, contentIntegerStyle, contentDoubleStyle, sheet, lastRowIndex);
        // 转换成字节数组
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            workBook.write(os);
            return os.toByteArray();
        }
    }

    private static void setExcelTitle(Workbook workBook, String sheetName, List<String> columnNames) throws Exception {

        Map<String, CellStyle> cellStyleMap = styleMap(workBook);
        // 表头样式
        CellStyle headStyle = cellStyleMap.get("head");
        // 生成一个表格
        Sheet sheet = workBook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workBook.createSheet(sheetName);
        }
        //最新Excel列索引,从0开始
        int lastRowIndex = sheet.getLastRowNum();
        // 设置表格默认列宽度
        sheet.setDefaultColumnWidth(DEFAULT_COLUMN_SIZE);
        // 产生表格标题行
        // 产生表格表头列标题行
        Row row = sheet.createRow(lastRowIndex);
        setCellValue(columnNames, headStyle, row, 0);
    }


    /**
     * 创建单元格表头样式
     *
     * @param workbook 工作薄
     */
    private static CellStyle createCellHeadStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //设置对齐样式
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 表头样式
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    /**
     * 创建单元格正文样式
     *
     * @param workbook 工作薄
     */
    private static CellStyle createCellContentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //设置对齐样式
        style.setAlignment(HorizontalAlignment.LEFT);
        // 生成字体
        Font font = workbook.createFont();
        // 正文样式
        style.setFillPattern(FillPatternType.NO_FILL);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        font.setBold(false);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    /**
     * 单元格样式(Integer)列表
     */
    private static CellStyle createCellContent4IntegerStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //设置对齐样式
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 正文样式
        style.setFillPattern(FillPatternType.NO_FILL);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        font.setBold(false);
        // 把字体应用到当前的样式
        style.setFont(font);
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));//数据格式只显示整数
        return style;
    }

    /**
     * 单元格样式(Double)列表
     */
    private static CellStyle createCellContent4DoubleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //设置对齐样式
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 正文样式
        style.setFillPattern(FillPatternType.NO_FILL);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        font.setBold(false);
        // 把字体应用到当前的样式
        style.setFont(font);
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));//保留两位小数点
        return style;
    }

    /**
     * 单元格样式列表
     */
    private static Map<String, CellStyle> styleMap(Workbook workbook) {
        Map<String, CellStyle> styleMap = new LinkedHashMap<>();
        styleMap.put("head", createCellHeadStyle(workbook));
        styleMap.put("content", createCellContentStyle(workbook));
        styleMap.put("integer", createCellContent4IntegerStyle(workbook));
        styleMap.put("double", createCellContent4DoubleStyle(workbook));
        return styleMap;
    }

    private static void setRegionStyle(Sheet sheet, CellRangeAddress region, CellStyle cs) {
        for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
            Row row = CellUtil.getRow(i, sheet);
            for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
                Cell cell = CellUtil.getCell(row, (short) j);
                cell.setCellStyle(cs);
            }
        }
    }


    /**
     * 导出excel
     *
     * @param sheetName   sheet name
     * @param columnNames 表头双层
     * @param objects     数据列表
     * @return excel byte []
     */
    public static byte[] exportExcelData(String sheetName, Map<String, List<String>> columnNames, List<List<Object>> objects) throws Exception {

        // 声明一个工作薄
        Workbook workBook = new XSSFWorkbook();
        setExcelTitle(workBook, sheetName, columnNames);

        return getExcelBytes(sheetName, objects, workBook);
    }

    private static void exportData(List<List<Object>> objects, CellStyle contentStyle, CellStyle contentIntegerStyle, CellStyle contentDoubleStyle, Sheet sheet, int lastRowIndex) {
        for (List<Object> dataRow : objects) {
            Row row = sheet.createRow(lastRowIndex);
            lastRowIndex++;
            for (int j = 0; j < dataRow.size(); j++) {
                Cell contentCell = row.createCell(j);
                Object dataObject = dataRow.get(j);
                if (dataObject != null) {
                    if (dataObject instanceof Integer) {
                        contentCell.setCellStyle(contentIntegerStyle);
                        contentCell.setCellValue(Integer.parseInt(dataObject.toString()));
                    } else if (dataObject instanceof Double) {
                        contentCell.setCellStyle(contentDoubleStyle);
                        contentCell.setCellValue(Double.parseDouble(dataObject.toString()));
                    } else if (dataObject instanceof Long && dataObject.toString().length() == 13) {
                        contentCell.setCellStyle(contentStyle);
                        contentCell.setCellValue(getCnDate(new Date(Long.parseLong(dataObject.toString()))));
                    } else if (dataObject instanceof Date) {
                        contentCell.setCellStyle(contentStyle);
                        contentCell.setCellValue(getCnDate((Date) dataObject));
                    } else {
                        if (dataObject instanceof String && ((String) dataObject).startsWith(FORMULA)) {
                            contentCell.setCellStyle(contentStyle);
                            contentCell.setCellFormula(((String) dataObject).substring(FORMULA.length()));
                        } else {
                            contentCell.setCellStyle(contentStyle);
                            contentCell.setCellValue(dataObject.toString());
                        }
                    }
                } else {
                    contentCell.setCellStyle(contentStyle);
                    // 设置单元格内容为字符型
                    contentCell.setCellValue("");
                }
            }
        }
    }


    /**
     * 导出excel
     *
     * @param sheetName   sheet name
     * @param columnNames 表头双层
     * @param trNodes     数据列表
     * @return excel byte []
     */
    public static byte[] exportExcelTreeData(String sheetName, Map<String, List<String>> columnNames, List<TrNode> trNodes) throws Exception {

        // 声明一个工作薄
        Workbook workBook = new XSSFWorkbook();
        setExcelTitle(workBook, sheetName, columnNames);
        //
        int columnNum = 0;
        for (List<String> secondNames : columnNames.values()) {
            if (CollectionUtils.isEmpty(secondNames)) {
                columnNum += 1;
            } else {
                columnNum += secondNames.size();
            }
        }
        return getExcelTreeBytes(sheetName, trNodes, workBook, columnNum);
    }

    /**
     * 导出excel
     *
     * @param sheetName   sheet name
     * @param columnNames 表头双层
     * @param trNodes     数据列表
     * @return excel byte []
     */
    public static byte[] exportExcelTreeData(String sheetName, List<String> columnNames, List<TrNode> trNodes) throws Exception {

        // 声明一个工作薄
        Workbook workBook = new XSSFWorkbook();
        setExcelTitle(workBook, sheetName, columnNames);

        return getExcelTreeBytes(sheetName, trNodes, workBook, columnNames.size());
    }

    private static byte[] getExcelTreeBytes(String sheetName, List<TrNode> trNodes, Workbook workBook, int columnNum) throws Exception {
        Map<String, CellStyle> cellStyleMap = styleMap(workBook);
        CellStyle contentStyle = cellStyleMap.get("content");
        // 生成一个表格
        Sheet sheet = workBook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workBook.createSheet(sheetName);
        }
        //最新Excel列索引
        int lastRowIndex = sheet.getLastRowNum();
        lastRowIndex++;
        // 设置表格默认列宽度
        sheet.setDefaultColumnWidth(DEFAULT_COLUMN_SIZE);
        List<CellRangeAddress> regions = new ArrayList<>();

        for (int rowIndex = 0, size = trNodes.size(); rowIndex < size; rowIndex++) {
            int rowNum = lastRowIndex + rowIndex;
            Row row = sheet.createRow(rowNum);
            List<TdNode> tds = trNodes.get(rowIndex).getTds();
            for (int colIndex = 0, tdSize = tds.size(); colIndex < tdSize; colIndex++) {
                TdNode tdNode = tds.get(colIndex);
                DataNode data = tdNode.getData();
                if (data != null && !data.isEndRow()) {
                    int lastCol = colIndex + columnNum - tdSize;
                    int lastRow = rowNum + tdNode.getRowSpan() - 1;
                    if (rowNum < lastRow) {
                        CellRangeAddress region = new CellRangeAddress(rowNum, lastRow, lastCol, lastCol);
                        regions.add(region);
                    }
                }

                Cell cell = row.createCell(columnNum - tdSize + colIndex);
                if (data != null) {
                    String value = data.getValue();
                    if (StringUtils.isBlank(value)) {
                        value = data.getTitle();
                    }
                    cell.setCellValue(value);
                } else {
                    cell.setCellValue("");
                }
                cell.setCellStyle(contentStyle);
            }
        }
        // 单元格放在最后合并，不然边框没有样式。血泪
        for (CellRangeAddress region : regions) {
            sheet.addMergedRegion(region);
            setRegionStyle(sheet, region, contentStyle);
        }
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            workBook.write(os);
            return os.toByteArray();
        }
    }

    public static class TrNode {
        private List<TdNode> tds;

        public List<TdNode> getTds() {
            return tds;
        }

        public void setTds(List<TdNode> tds) {
            this.tds = tds;
        }
    }

    public static class TdNode {
        private DataNode data;
        private int rowSpan;

        public DataNode getData() {
            return data;
        }

        public void setData(DataNode data) {
            this.data = data;
        }

        public int getRowSpan() {
            return rowSpan;
        }

        public void setRowSpan(int rowSpan) {
            this.rowSpan = rowSpan;
        }
    }

    public static class DataNode {
        private String id;

        private String parentId;

        private String title;

        private String desc;

        private String type;

        private String value;

        private boolean endRow;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isEndRow() {
            return endRow;
        }

        public void setEndRow(boolean endRow) {
            this.endRow = endRow;
        }
    }
}



