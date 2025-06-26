package com.zhanglinwei.zTools.copycurl;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.zhanglinwei.zTools.common.enums.HttpMethod;
import com.zhanglinwei.zTools.common.enums.SpringConfigProperties;
import com.zhanglinwei.zTools.doc.apidoc.model.ApiInfo;
import com.zhanglinwei.zTools.doc.apidoc.model.JavaProperty;
import com.zhanglinwei.zTools.util.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

public class CopyCurlAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        Editor editor = actionEvent.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }
        Project project = editor.getProject();
        if (project == null) {
            return;
        }

        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiMethod selectedMethod = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);

        if (!isAllow(project, selectedMethod)) {
            return;
        }

        if (generateCURL(selectedMethod, project)) {
            NotificationUtil.infoNotify("Copy CURL successfully!", project);
        }
    }

    private boolean generateCURL(PsiMethod selectedMethod, Project project) {
        // 1. 创建API对象
        ApiInfo apiInfo = ApiInfo.create(selectedMethod);
        ApiInfo.ApiRequestInfo requestInfo = apiInfo.getRequestInfo();

        // 2. 解析请求方式
        HttpMethod requestType = resolveRequestType(selectedMethod, HttpMethod.GET);
        // 3. 解析请求URL
        String requestUrl = resolveRequestUrl(apiInfo, project);

        // 4. 构建CURL
        StringBuilder builder = new StringBuilder("curl ");
        builder.append("-X ").append(requestType.name()).append(requestUrl).append(" \\\n");

        // 5. 拼接请求头
        ApiInfo.ApiTableInfo requestHeader = requestInfo.getRequestHeader();
        if (requestHeader != null && AssertUtils.isNotEmpty(requestHeader.getRowList())) {
            requestHeader.getRowList().forEach(header -> {
                builder.append("-H \"").append(header.getName()).append(COLON_SPACE).append(header.getExample()).append("\" \\\n");
            });
        }

        // 6. 拼接表单参数
        ApiInfo.ApiTableInfo formParam = requestInfo.getFormParam();
        if (formParam != null && AssertUtils.isNotEmpty(formParam.getRowList())) {
            String formString = formParam.getRowList().stream()
                    .map(row -> row.getName() + EQUAL + String.valueOf(row.getExample()).replaceAll(QUOTE, EMPTY))
                    .collect(Collectors.joining(AMPERSAND, QUOTE, QUOTE));
            builder.append("-d ").append(formString).append(" \\\n");
        }

        // 7. 拼接请求体参数
        JavaProperty requestBody = requestInfo.getOriginRequestBody();
        if (requestBody != null) {
            builder.append("-d ").append(SINGLE_QUOTE).append(JsonUtil.toJsonString(requestBody, false)).append(SINGLE_QUOTE);
        }

        // 8. 拷贝到剪贴板
        ClipboardUtils.copyToClipboard(builder.toString());
        return true;
    }

    private String resolveRequestUrl(ApiInfo apiInfo, Project project) {
        String serverPort = resolveServerPort(project, "80");
        String requestPath = apiInfo.getBaseInfo().getRequestPath();

        ApiInfo.ApiTableInfo requestParam = apiInfo.getRequestInfo().getRequestParam();
        if (requestParam != null && AssertUtils.isNotEmpty(requestParam.getRowList())) {
            String queryString = requestParam.getRowList().stream()
                    .map(row -> row.getName() + EQUAL + String.valueOf(row.getExample()).replaceAll(QUOTE, EMPTY))
                    .collect(Collectors.joining(AMPERSAND));
            requestPath = requestPath + QUESTION_MARK + queryString;
        }

        return " http://127.0.0.1:" + serverPort + requestPath;
    }

    private HttpMethod resolveRequestType(PsiMethod selectedMethod, HttpMethod dftMethod) {
        List<String> requestTypeList = AnnotationUtil.extractRequestTypeAsList(selectedMethod.getAnnotations());
        return AssertUtils.isNotEmpty(requestTypeList) ? HttpMethod.valueOf(requestTypeList.get(0)) : dftMethod;
    }

    private String resolveServerPort(Project project, String dftPort) {
        String cfgPort = SpringConfigUtils.propertyAsString(project, SpringConfigProperties.SERVER_PROT);
        return AssertUtils.isBlank(cfgPort) ? dftPort : cfgPort;
    }

    private boolean isAllow(Project project, PsiMethod selectedMethod) {
        if (selectedMethod == null) {
            NotificationUtil.errorNotify("This operation only supports Controller method!", project);
            return false;
        }
        if (!AnnotationUtil.hasMappingAnnotation(selectedMethod)) {
            NotificationUtil.warnNotify("The method is not a RestApi!", project);
            return false;
        }
        PsiClass containingClass = selectedMethod.getContainingClass();
        if (!AnnotationUtil.hasController(containingClass)) {
            NotificationUtil.errorNotify("The file is not a Controller!", project);
            return false;
        }

        return true;
    }
}
