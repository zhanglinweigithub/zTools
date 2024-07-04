package com.zhanglinwei.zTools.template;

import com.zhanglinwei.zTools.model.ClassInfo;
import com.zhanglinwei.zTools.model.FieldInfo;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.JsonUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.List;

public abstract class AbstractWordTemplate {

    private final String JSON_RED = "a72020";
    private final String JSON_BLUE = "0451a5";
    private final String JSON_GREEN = "0a850a";

    public abstract XWPFDocument buildContent(ClassInfo classInfo);

    /**
     * 转为word格式的JSON
     */
    protected void toWordJson(FieldInfo fieldInfo, String jsonStr, XWPFDocument word) {
        // 获得注释内容
        List<String> descList = JsonUtil.buildFieldDescList(fieldInfo.getChildren());
        // 两个空格替换为四个空格
        jsonStr = jsonStr.replaceAll(" {2}", "    ");
        // 分隔JSON为行
        String[] jsonSplit = jsonStr.split("\n");
        int descIndex = 0;
        for (String lineJson : jsonSplit) {

            XWPFParagraph paragraph = word.createParagraph();
            XWPFRun line = paragraph.createRun();
            line.setFontFamily("Consolas");

            // 检查行是否包含冒号
            if (lineJson.contains(":")) {
                // 写入key并设置颜色
                String[] split = lineJson.split(":", 2);
                line.setText(split[0]);
                line.setColor(JSON_RED);
                // 写入:并设置颜色
                XWPFRun colon = paragraph.createRun();
                colon.setText(":");
                colon.setFontFamily("Consolas");
                // 处理值并
                String afterStr = split[1];
                XWPFRun value = paragraph.createRun();
                value.setFontFamily("Consolas");

                // 是否需要添加逗号
                boolean needsComma = afterStr.endsWith(",");
                if (needsComma) {
                    // 移除末尾的逗号，但不设置颜色（因为后面会单独处理）
                    afterStr = afterStr.substring(0, afterStr.length() - 1);
                }

                // 设置值和颜色
                value.setText(afterStr);
                value.setColor(buildColor(afterStr));

                // 添加逗号
                if (needsComma) {
                    XWPFRun comma = paragraph.createRun();
                    comma.setText(",");
                    comma.setFontFamily("Consolas");
                }

                // 设置注释
                if (AssertUtils.isNotBlank(descList.get(descIndex))) {
                    String desc = descList.get(descIndex);
                    XWPFRun descRun = paragraph.createRun();
                    descRun.setText(" // " + desc);
                    descRun.setColor(JSON_GREEN);
                    descRun.setFontFamily("Consolas");
                }
                descIndex++;
            } else {
                line.setText(lineJson);
                line.setColor(buildColor(lineJson));
            }
        }
    }

    /**
     * 字符串、Boolean类型: BLUE
     * 数字类型: GREEN
     * 其它: 默认
     */
    private String buildColor(String afterStr) {
        if (afterStr.contains("\"") || afterStr.contains("true") || afterStr.contains("false")) {
            return JSON_BLUE;
        }
        if (afterStr.contains("0") || afterStr.contains("1")) {
            return JSON_GREEN;
        }

        return "";
    }

}
