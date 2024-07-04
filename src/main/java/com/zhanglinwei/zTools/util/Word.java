package com.zhanglinwei.zTools.util;

import com.zhanglinwei.zTools.model.FieldInfo;
import com.zhanglinwei.zTools.model.RequestHeader;
import com.zhanglinwei.zTools.model.TableInfo;
import org.apache.poi.xwpf.usermodel.*;

import java.util.List;
import java.util.Map;

/**
 * word相关操作
 */
public class Word {

    private Word() {
    }

    public static void json(XWPFDocument word, String json) {
        XWPFParagraph paragraph = word.createParagraph();
        paragraph.setStyle("JSON");
        XWPFRun run = paragraph.createRun();
        run.setText(json);
    }

    public static void createTitle(XWPFDocument word, String content, String level) {
        XWPFParagraph paragraph = word.createParagraph();
        paragraph.setStyle(level);

        XWPFRun h1Text = paragraph.createRun();
        h1Text.setText(content);
    }

    public static void createTitle(XWPFDocument word, String content, String level, ParagraphAlignment align) {
        XWPFParagraph paragraph = word.createParagraph();
        paragraph.setStyle(level);
        paragraph.setAlignment(align);

        XWPFRun h1Text = paragraph.createRun();
        h1Text.setText(content);
    }

    public static void unorderedList(XWPFDocument word, Map<String, String> content, String fontFamily) {
        for (Map.Entry<String, String> item : content.entrySet()) {
            XWPFParagraph listParagraph = word.createParagraph();
            XWPFRun listRun = listParagraph.createRun();
            listRun.setText("\u2022" + item.getKey() + ": " + item.getValue());
            listRun.setFontFamily(fontFamily);
        }
    }

    public static void requestHeaderTable(XWPFDocument word, List<RequestHeader> requestHeaders, List<String> headerList) {
        int cols = headerList.size();
        int contentRows = requestHeaders.size(); // 内容行数
        int totalRows = contentRows + 1; // 总行数（包括表头）

        // 创建表格
        XWPFTable table = word.createTable(totalRows, cols);
        table.setWidth("100%");

        // 设置表头单元格内容
        XWPFTableRow headerRow = table.getRow(0);
        for (int j = 0; j < cols; j++) {
            XWPFTableCell cell = headerRow.getCell(j);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            XWPFParagraph paragraph = cell.getParagraphs().get(0);
            XWPFRun run = paragraph.createRun();
            run.setText(headerList.get(j));
            run.setBold(true);
            run.setFontSize(9);
        }

        // 添加内容行
        for (int i = 0; i < contentRows; i++) {
            XWPFTableRow row = table.getRow(i + 1); // 从第二行开始, 第一行是表头
            RequestHeader requestHeader = requestHeaders.get(i);
            String[] split = requestHeader.toWordArray();

            // 填充数据到单元格
            for (int k = 0; k < cols; k++) {
                XWPFTableCell cell = row.getCell(k);
                XWPFRun run = cell.getParagraphs().get(0).createRun();
                run.setFontSize(9);
                run.setText(split[k]);
//                cell.setText(split[k]);
            }
        }
    }

    public static void fieldTable(XWPFDocument word, List<FieldInfo> fieldInfoList, List<String> headerList ) {
        fieldTable(word, fieldInfoList, headerList, false);
    }

    public static void dbTable(XWPFDocument word, List<TableInfo> tableInfoList, List<String> dbTableHeader) {
        int cols = dbTableHeader.size();
        int contentRows = tableInfoList.size(); // 内容行数
        // 创建表格
        XWPFTable table = word.createTable(1, cols);
//        table.setTableAlignment(TableRowAlign.CENTER);
        table.setWidth("100%");
        // 设置表头单元格内容
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.setHeight(1);
        for (int j = 0; j < cols; j++) {
            XWPFTableCell cell = headerRow.getCell(j);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            XWPFParagraph paragraph = cell.getParagraphs().get(0);
            XWPFRun run = paragraph.createRun();
            run.setText(dbTableHeader.get(j));
            run.setBold(true);
            run.setFontSize(9);
        }
        // 添加内容行
        for (int i = 0; i < contentRows; i++) {
            TableInfo tableInfo = tableInfoList.get(i);
            buildRowContent(table, cols, tableInfo);
        }
    }

    public static void fieldTable(XWPFDocument word, List<FieldInfo> fieldInfoList, List<String> headerList, boolean withType) {
        int cols = headerList.size();
        int contentRows = fieldInfoList.size(); // 内容行数
        // 创建表格
        XWPFTable table = word.createTable(1, cols);
        table.setWidth("100%");
        // 设置表头单元格内容
        XWPFTableRow headerRow = table.getRow(0);
        for (int j = 0; j < cols; j++) {
            XWPFTableCell cell = headerRow.getCell(j);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            XWPFParagraph paragraph = cell.getParagraphs().get(0);
            XWPFRun run = paragraph.createRun();
            run.setText(headerList.get(j));
            run.setBold(true);
            run.setFontSize(9);
        }
        // 添加内容行
        for (int i = 0; i < contentRows; i++) {
            FieldInfo fieldInfo = fieldInfoList.get(i);
            buildRowContent(table, cols, fieldInfo, withType, "");
            if (fieldInfo.hasChildren()) {
                for (FieldInfo field : fieldInfo.getChildren()) {
                    buildChildrenFieldInfo(table, cols, field, CommonUtils.getPrefix(), withType);
                }
            }
        }
    }
    private static void buildChildrenFieldInfo(XWPFTable table, int cols, FieldInfo field, String prefix, boolean withType) {
        buildRowContent(table, cols, field, withType, prefix);
        if (field.hasChildren()) {
            for (FieldInfo fieldInfo : field.getChildren()) {
                buildChildrenFieldInfo(table, cols, fieldInfo, prefix + CommonUtils.getPrefix(), withType);
            }
        }
    }

    private static void buildRowContent(XWPFTable table, int cols, TableInfo tableInfo) {
        List<String> split = tableInfo.toWordList();
        XWPFTableRow row = table.createRow();
        row.setHeight(1);
        for (int j = 0; j < cols; j++) {
            XWPFTableCell cell = row.getCell(j);
            if (cell == null) {
                cell = row.createCell(); // 如果单元格不存在则创建
            }
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            XWPFParagraph paragraph = cell.getParagraphs().get(0);
            XWPFRun run = paragraph.createRun();
            run.setText(split.get(j));
            run.setFontSize(9);
        }
    }

    private static void buildRowContent(XWPFTable table, int cols, FieldInfo fieldInfo, boolean withType, String prefix) {
        List<String> split = fieldInfo.toWordList(withType, prefix);
        XWPFTableRow row = table.createRow();
        for (int j = 0; j < cols; j++) {
            XWPFTableCell cell = row.getCell(j);
            if (cell == null) {
                cell = row.createCell(); // 如果单元格不存在则创建
            }
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            XWPFRun run = cell.getParagraphs().get(0).createRun();
            run.setFontSize(9);
            run.setText(split.get(j));
//            cell.setText(split.get(j)); // 设置内容单元格内容
        }
    }


}
