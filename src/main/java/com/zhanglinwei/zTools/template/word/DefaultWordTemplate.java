package com.zhanglinwei.zTools.template.word;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.constant.DocConstants;
import com.zhanglinwei.zTools.constant.MessageConstants;
import com.zhanglinwei.zTools.model.ClassInfo;
import com.zhanglinwei.zTools.model.FieldInfo;
import com.zhanglinwei.zTools.model.MethodInfo;
import com.zhanglinwei.zTools.model.RequestHeader;
import com.zhanglinwei.zTools.template.AbstractWordTemplate;
import com.zhanglinwei.zTools.util.*;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultWordTemplate extends AbstractWordTemplate {

    private static CTStyles templateStyles = null;


    @Override
    public XWPFDocument buildContent(ClassInfo classInfo) {
        Project currentProject = ProjectUtil.getActiveProject();
        boolean readStyle = preProcessor(currentProject);
        if (!readStyle) {
            NotificationUtil.errorNotify(MessageConstants.STYLE_NOT_EXIST, currentProject);
            return null;
        }
        XWPFDocument word = new XWPFDocument();
        XWPFStyles wordStyles = word.createStyles();
        wordStyles.setStyles(templateStyles);

        Word.createTitle(word, classInfo.getTitle(), "1", ParagraphAlignment.CENTER);
        List<MethodInfo> methodList = classInfo.getMethods();
        for (int i = 0; i < methodList.size(); i++) {
            MethodInfo method = methodList.get(i);
            if (method.isEmpty()) {
                continue;
            }
            Word.createTitle(word, method.getTitle(), "2");
            Word.createTitle(word, "基本信息", "3");
            Map<String, String> map = new HashMap<>();
            map.put(" 请求方式", method.getRequestType());
            map.put(" 接口路径", CommonUtils.buildPath(classInfo.getRequestPath(), method.getRequestPath()));
            map.put(" 接口描述", method.getDesc());
            Word.unorderedList(word, map, "Consolas");

            Word.createTitle(word, "请求参数", "3");
            if (AssertUtils.isNotEmpty(method.getRequestHeaders())) {
                List<RequestHeader> requestHeaders = method.getRequestHeaders();
                Word.createTitle(word, DocConstants.REQUEST_HEADER, "4");
                Word.requestHeaderTable(word, requestHeaders, DocConstants.REQUEST_HEADER_TABLE_HEADER);
            }

            // PathVariable
            if (AssertUtils.isNotEmpty(method.getPathVariables())) {
                List<FieldInfo> pathVariables = method.getPathVariables();
                Word.createTitle(word, DocConstants.PATH_VARIABLE, "4");
                Word.fieldTable(word, pathVariables, DocConstants.PATH_VARIABLE_TABLE_HEADER);
            }

            // RequestParam
            if (AssertUtils.isNotEmpty(method.getRequestParams())) {
                List<FieldInfo> requestQuerys = method.getRequestParams();
                Word.createTitle(word, DocConstants.REQUEST_PARAM, "4");
                Word.fieldTable(word, requestQuerys, DocConstants.REQUEST_PARAM_TABLE_HEADER);
            }

            // Form
            if (AssertUtils.isNotEmpty(method.getFormParams())) {
                List<FieldInfo> requestBodyForms = method.getFormParams();
                Word.createTitle(word, DocConstants.FORM_PARAM, "4");
                Word.fieldTable(word, requestBodyForms, DocConstants.FORM_PARAM_TABLE_HEADER, true);
            }

            // RequestBody
            FieldInfo requestBody = method.getRequestBody();
            if (requestBody != null && requestBody.hasChildren()) {
                Word.createTitle(word, DocConstants.REQUEST_BODY, "4");
                Word.createTitle(word, "描述", "5");
                Word.fieldTable(word, requestBody.getChildren(), DocConstants.REQUEST_BODY_TABLE_HEADER, true);

                Word.createTitle(word, "示例", "5");
                super.toWordJson(method.getRequestBody(), method.getRequestBodyJson(), word);

            }


            Word.createTitle(word, "响应参数", "3");
            // ResponseBody
            FieldInfo responseBody = method.getResponseBody();
            if (responseBody != null && responseBody.hasChildren()) {
                Word.createTitle(word, DocConstants.RESPONSE_BODY, "4");
                Word.createTitle(word, "描述", "5");
                Word.fieldTable(word, responseBody.getChildren(), DocConstants.RESPONSE_BODY_TABLE_HEADER, true);

                Word.createTitle(word, "示例", "5");
                super.toWordJson(method.getResponseBody(), method.getResponseBodyJson(), word);
            }

        }

        return word;
    }

    /** 读取模板样式 */
    private boolean preProcessor(Project project) {
        if (templateStyles == null) {
            try {
                XWPFDocument template = new XWPFDocument(ResourcesUtils.readFile("template/template.docx"));
                templateStyles = template.getStyle();

                return templateStyles != null;
            } catch (Exception e) {
                NotificationUtil.errorNotify(MessageConstants.READ_WORD_TEMPLATE_FAIL + "Caused by: " + e.getMessage(), project);
            }
        }

        return true;
    }
}
