package com.zhanglinwei.zTools.doc.handler;

import com.zhanglinwei.zTools.doc.constant.DocConstants;
import com.zhanglinwei.zTools.doc.constant.DocType;
import com.zhanglinwei.zTools.doc.apidoc.constant.FontSizeConstants;
import com.zhanglinwei.zTools.doc.apidoc.model.ClassInfo;
import com.zhanglinwei.zTools.doc.apidoc.model.FieldInfo;
import com.zhanglinwei.zTools.doc.apidoc.model.MethodInfo;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.CommonUtils;
import com.zhanglinwei.zTools.util.JsonUtil;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class WordDocHandler implements DocHandler {

    private static final String JSON_RED = "a72020";
    private static final String JSON_BLUE = "0451a5";
    private static final String JSON_GREEN = "0a850a";

    @Override
    public boolean generateApiDoc(ClassInfo classInfo, String pathName) throws IOException {
        File apiDoc = new File(pathName);
        try (FileOutputStream fos = new FileOutputStream(apiDoc)) {
            XWPFDocument word = makeApiContent(classInfo);
            word.write(fos);
        }
        return true;
    }

    private XWPFDocument makeApiContent(ClassInfo classInfo) {
        XWPFDocument document = new XWPFDocument();

        createTitle(document, ParagraphAlignment.CENTER, classInfo.getTitle(), true, FontSizeConstants.TWO);

        List<MethodInfo> methodList = classInfo.getMethods();
        for (int i = 0; i < methodList.size(); i++) {
            MethodInfo method = methodList.get(i);
            if (method.isEmpty()) {
                continue;
            }
            createTitle(document, ParagraphAlignment.LEFT, i + 1 + "、" + method.getTitle(), true, FontSizeConstants.SMALL_TWO);
            createTitle(document, ParagraphAlignment.LEFT, "基本信息", true, FontSizeConstants.FOUR);
            createUl(document, ParagraphAlignment.LEFT, " 请求方式: " + method.getRequestType(), FontSizeConstants.FIVE);
            createUl(document, ParagraphAlignment.LEFT, " 接口路径: " + CommonUtils.buildPath(classInfo.getRequestPath(), method.getRequestPath()), FontSizeConstants.FIVE);
            createUl(document, ParagraphAlignment.LEFT, " 接口描述: " + method.getDesc(), FontSizeConstants.FIVE);

            createTitle(document, ParagraphAlignment.LEFT, "请求参数", true, FontSizeConstants.FOUR);


            if (AssertUtils.isNotEmpty(method.getRequestHeaders())) {
                List<FieldInfo> requestHeaders = method.getRequestHeaders();
                createTitle(document, ParagraphAlignment.LEFT, DocConstants.REQUEST_HEADER, true, FontSizeConstants.SMALL_FOUR);
                createFieldTable(document, requestHeaders, DocConstants.COMMON_TABLE_HEADER);
            }

            // PathVariable
            if (AssertUtils.isNotEmpty(method.getPathVariables())) {
                List<FieldInfo> pathVariables = method.getPathVariables();
                createTitle(document, ParagraphAlignment.LEFT, DocConstants.PATH_VARIABLE, true, FontSizeConstants.SMALL_FOUR);
                createFieldTable(document, pathVariables, DocConstants.COMMON_TABLE_HEADER);
            }

            // RequestParam
            if (AssertUtils.isNotEmpty(method.getRequestParams())) {
                List<FieldInfo> requestQuerys = method.getRequestParams();
                createTitle(document, ParagraphAlignment.LEFT, DocConstants.REQUEST_PARAM, true, FontSizeConstants.SMALL_FOUR);
                createFieldTable(document, requestQuerys, DocConstants.COMMON_TABLE_HEADER);
            }

            // Form
            if (AssertUtils.isNotEmpty(method.getFormParams())) {
                List<FieldInfo> requestBodyForms = method.getFormParams();
                createTitle(document, ParagraphAlignment.LEFT, DocConstants.FORM_PARAM, true, FontSizeConstants.SMALL_FOUR);
                createFieldTable(document, requestBodyForms, DocConstants.COMMON_TABLE_HEADER);
            }

            // RequestBody
            FieldInfo requestBody = method.getRequestBody();
            if (requestBody != null && requestBody.hasChildren()) {
                createTitle(document, ParagraphAlignment.LEFT, DocConstants.REQUEST_BODY, true, FontSizeConstants.SMALL_FOUR);
                createTitle(document, ParagraphAlignment.LEFT, "描述", true, FontSizeConstants.FIVE);
                createFieldTable(document, requestBody.getChildren(), DocConstants.BODY_TABLE_HEADER, true);

                createTitle(document, ParagraphAlignment.LEFT, "示例", true, FontSizeConstants.FIVE);
                createJson(document, method.getRequestBody(), method.getRequestBodyJson());

            }

            createTitle(document, ParagraphAlignment.LEFT, "响应参数", true, FontSizeConstants.FOUR);
            // ResponseBody
            FieldInfo responseBody = method.getResponseBody();
            if (responseBody != null && responseBody.hasChildren()) {
                createTitle(document, ParagraphAlignment.LEFT, DocConstants.RESPONSE_BODY, true, FontSizeConstants.SMALL_FOUR);
                createTitle(document, ParagraphAlignment.LEFT, "描述", true, FontSizeConstants.FIVE);
                createFieldTable(document, responseBody.getChildren(), DocConstants.BODY_TABLE_HEADER, true);

                createTitle(document, ParagraphAlignment.LEFT, "示例", true, FontSizeConstants.FIVE);
                createJson(document, method.getResponseBody(), method.getResponseBodyJson());
            }

        }

        return document;
    }

    @Override
    public boolean generateDataBaseDoc() {
        return false;
    }

    @Override
    public DocType support() {
        return DocType.WORD;
    }

    private void createJson(XWPFDocument document, FieldInfo fieldInfo, String jsonStr) {
        // 获得注释内容
        List<String> descList = JsonUtil.buildFieldDescList(fieldInfo.getChildren());
        // 两个空格替换为四个空格
        jsonStr = jsonStr.replaceAll(" {2}", "    ");
        // 分隔JSON为行
        String[] jsonSplit = jsonStr.split("\n");

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        int descIndex = 0;
        // 遍历每行JSON
        for (String line : jsonSplit) {
            // 写入JSON行
            writeJsonLine(paragraph, line);

            // 如果有注释并且注释索引未超出范围
            if (descIndex < descList.size() && line.contains(":")) {
                String desc = descList.get(descIndex).trim();
                if (!desc.isEmpty()) {
                    writeText(paragraph, "    // " + desc + "\n", JSON_GREEN); // 注释为灰色
                }
                descIndex++;
            }
            paragraph.createRun().addBreak();
        }
    }

    /**
     * 写入单行JSON内容到文档
     */
    private void writeJsonLine(XWPFParagraph paragraph, String line) {
        StringBuilder buffer = new StringBuilder();
        boolean inQuotes = false; // 是否在引号内
        boolean isKey = true; // 是否为键

        for (char c : line.toCharArray()) {
            // 引号内处理
            if (c == '"') {
                inQuotes = !inQuotes;
                buffer.append(c);
                continue;
            }

            // 引号外处理
            if (!inQuotes) {
                switch (c) {
                    case ':': // 键值分隔符
                        writeBuffer(paragraph, buffer, isKey ? JSON_RED : JSON_BLUE);
                        writeText(paragraph, ": ", null); // 冒号为黑色
                        isKey = false; // 冒号后为值
                        break;
                    case '{':
                    case '[':
                    case '}':
                    case ']':
                        writeBuffer(paragraph, buffer, isKey ? JSON_RED : JSON_BLUE);
                        writeText(paragraph, String.valueOf(c), null);
                        break;
                    case ',':
                        writeBuffer(paragraph, buffer, isKey ? JSON_RED : JSON_BLUE);
                        writeText(paragraph, String.valueOf(c), null);
                        isKey = true; // 逗号后重置为键
                        break;
                    default: // 普通字符
                        if (!inQuotes && Character.isDigit(c)) {
                            buffer.append(c);
                        } else {
                            // 如果缓冲区已有数字内容且遇到非数字字符，则将数字写出
                            if (!buffer.toString().isEmpty() && buffer.toString().matches("\\d+")) {
                                writeBuffer(paragraph, buffer, JSON_GREEN);
                            }
                            buffer.append(c); // 非数字继续累积
                        }
                }
            } else {
                buffer.append(c); // 引号内字符
            }
        }

        // 写出剩余缓冲内容
        writeBuffer(paragraph, buffer, inQuotes ? JSON_BLUE : JSON_GREEN);
        writeText(paragraph, "\n", null);
    }

    /**
     * 写出缓冲区内容到文档
     */
    private void writeBuffer(XWPFParagraph paragraph, StringBuilder buffer, String color) {
        if (buffer.length() > 0) {
            writeText(paragraph, buffer.toString(), color);
            buffer.setLength(0); // 清空缓冲区
        }
    }

    /**
     * 写入单段文字到文档
     */
    private void writeText(XWPFParagraph paragraph, String text, String color) {
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        if (AssertUtils.isNotBlank(color)) {
            run.setColor(color);
        }
        run.setFontSize(FontSizeConstants.FIVE);
    }

    private void createFieldTable(XWPFDocument document, List<FieldInfo> fieldInfoList, List<String> headerList) {
        createFieldTable(document, fieldInfoList, headerList, false);
    }
    private void createFieldTable(XWPFDocument document, List<FieldInfo> fieldInfoList, List<String> headerList, boolean withType) {
        int cols = headerList.size();
        int contentRows = fieldInfoList.size(); // 内容行数

        XWPFTable table = createTableStructure(document, cols, headerList);

        // 添加内容行
        for (int i = 0; i < contentRows; i++) {
            FieldInfo fieldInfo = fieldInfoList.get(i);
            boolean hasChildren = fieldInfo.hasChildren();
            buildRowContent(table, cols, fieldInfo, "", withType, hasChildren);
            if (hasChildren) {
                for (FieldInfo field : fieldInfo.getChildren()) {
                    buildChildrenFieldInfo(table, cols, field, CommonUtils.getPrefix(), withType);
                }
            }
        }
    }

    private XWPFTable createTableStructure(XWPFDocument document, int cols, List<String> headerList) {
        XWPFTable table = document.createTable(1, cols);
        table.setWidth("100%");
        XWPFTableRow headerRow = table.getRow(0);
        for (int j = 0; j < cols; j++) {
            XWPFTableCell cell = headerRow.getCell(j);
            // 设置表头颜色为灰色
            setCellBackgroundColor(cell);  // 设置表头颜色为灰色
            // 设置表头字体
            setCellFontStyle(cell, headerList.get(j), true);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            cell.getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
        }

        return table;
    }

    private static void setCellFontStyle(XWPFTableCell cell, String text, boolean isBold) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(isBold);
        run.setFontSize(FontSizeConstants.FIVE);
    }

    private static void setCellBackgroundColor(XWPFTableCell cell) {
        CTShd shd = cell.getCTTc().addNewTcPr().addNewShd();
        shd.setFill("AAABAB");
    }

    private static void buildRowContent(XWPFTable table, int cols, FieldInfo fieldInfo, String prefix, boolean withType, boolean bold) {
        List<String> split = fieldInfo.toWordList(withType, prefix);
        XWPFTableRow row = table.createRow();
        for (int j = 0; j < cols; j++) {
            XWPFTableCell cell = row.getCell(j);
            if (cell == null) {
                cell = row.createCell(); // 如果单元格不存在则创建
            }
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            setCellFontStyle(cell, split.get(j), bold);
            cell.getParagraphs().get(0).setAlignment(ParagraphAlignment.LEFT);
        }
    }

    private static void buildChildrenFieldInfo(XWPFTable table, int cols, FieldInfo field, String prefix, boolean withType) {
        boolean hasChildren = field.hasChildren();
        buildRowContent(table, cols, field, prefix, withType, hasChildren);
        if (hasChildren) {
            for (FieldInfo fieldInfo : field.getChildren()) {
                buildChildrenFieldInfo(table, cols, fieldInfo, prefix + CommonUtils.getPrefix(), withType);
            }
        }
    }


    private void createUl(XWPFDocument document, ParagraphAlignment paragraphAlignment, String text, double fontSize) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(paragraphAlignment);
        XWPFRun run = paragraph.createRun();
        run.setText("\u2022 " + text);
        run.setBold(false);
        run.setFontSize(fontSize);
    }

    private void createTitle(XWPFDocument document, ParagraphAlignment paragraphAlignment, String text, boolean bold, int fontSize) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(paragraphAlignment);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(text);
        titleRun.setBold(bold);
        titleRun.setFontSize(fontSize);
    }

    private void createTitle(XWPFDocument document, ParagraphAlignment paragraphAlignment, String text, boolean bold, double fontSize) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(paragraphAlignment);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(text);
        titleRun.setBold(bold);
        titleRun.setFontSize(fontSize);
    }
}
