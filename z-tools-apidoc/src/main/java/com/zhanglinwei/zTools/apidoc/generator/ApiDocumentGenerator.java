package com.zhanglinwei.zTools.apidoc.generator;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.zhanglinwei.zTools.apidoc.formatter.ApiDocumentFormatter;
import com.zhanglinwei.zTools.apidoc.formatter.ApiDocumentFormatterHolder;
import com.zhanglinwei.zTools.apidoc.ApiInfo;
import com.zhanglinwei.zTools.common.doc.DocOutput;
import com.zhanglinwei.zTools.common.doc.TemplateDocWriter;
import com.zhanglinwei.zTools.configure.config.DocumentConfig;
import com.zhanglinwei.zTools.common.util.CollectionUtils;
import com.zhanglinwei.zTools.common.util.PathUtils;
import com.zhanglinwei.zTools.common.util.ProjectConfigs;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.zhanglinwei.zTools.common.constant.StringPool.HTML_GT;
import static com.zhanglinwei.zTools.common.constant.StringPool.HTML_LT;
import static com.zhanglinwei.zTools.common.constant.StringPool.LEFT_CHEV;
import static com.zhanglinwei.zTools.common.constant.StringPool.RIGHT_CHEV;

/**
 * 把已解析的 {@link ApiInfo} 写成文档文件。
 * <p>
 * 步骤：按设置页的文档类型选出 {@link ApiDocumentFormatter} →
 * {@link #prepare} 给示例 JSON 着色、把类型里的 {@code <>} 转成 HTML 实体 →
 * 组装 Freemarker 数据模型（apiList、网关前缀）→ 调 {@link TemplateDocWriter} 写文件。
 * 模板目录为 classpath 上的 {@code /template/api}。
 */
public final class ApiDocumentGenerator {

    /** Freemarker 模板所在 classpath 目录，对应模块 resources/template/api。 */
    private static final String TEMPLATE_DIR = "/template/api";

    /** 工具类，禁止实例化。 */
    private ApiDocumentGenerator() {}

    /**
     * 把接口列表写成文档文件。
     *
     * @param apiInfos 一个或多个接口（方法生成时只有一条）
     * @param project  当前工程（读文档类型、保存目录、网关前缀）
     * @param fileName 输出文件名（不含后缀），一般来自方法/类注释
     * @return 写出成功则为 {@code true}
     * @throws Exception 模板渲染或写文件失败
     */
    public static boolean write(Collection<ApiInfo> apiInfos, Project project, String fileName) throws Exception {
        DocumentConfig documentConfig = DocumentConfig.getInstance(project);
        ApiDocumentFormatter format = ApiDocumentFormatterHolder.ofDocType(documentConfig.getDocType());
        String path = DocOutput.resolveDir(project, documentConfig.getSaveDir()) + fileName + format.documentType().getSuffix();
        prepare(apiInfos, format);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("apiList", apiInfos);
        dataModel.put("requestPrefix", PathUtils.leadingSlash(
                ProjectConfigs.globalRequestPrefix(ProjectUtil.getActiveProject())));
        return TemplateDocWriter.writeUtf8(ApiDocumentGenerator.class, TEMPLATE_DIR, format.templateName(), dataModel, path);
    }

    /**
     * 写模板前改 ApiInfo：JSON 按格式着色，表格里的泛型符号转义。
     *
     * @param apiInfos 待写出的接口
     * @param format   当前文档格式
     */
    private static void prepare(Collection<ApiInfo> apiInfos, ApiDocumentFormatter format) {
        if (CollectionUtils.isEmpty(apiInfos)) {
            return;
        }
        for (ApiInfo apiInfo : apiInfos) {
            if (apiInfo == null) {
                continue;
            }
            decorateJson(apiInfo, format);
            escapeTypes(apiInfo);
        }
    }

    /**
     * 请求/响应示例 JSON 交给当前格式处理：Markdown 原样，HTML/Word 加颜色标记。
     *
     * @param apiInfo 单份接口
     * @param format  当前文档格式
     */
    private static void decorateJson(ApiInfo apiInfo, ApiDocumentFormatter format) {
        ApiInfo.ApiRequestInfo requestInfo = apiInfo.getRequestInfo();
        if (requestInfo != null && StringUtils.isNotBlank(requestInfo.getRequestBodyJson())) {
            requestInfo.setRequestBodyJson(format.decorateJson(requestInfo.getRequestBodyJson()));
        }
        ApiInfo.ApiResponseInfo responseInfo = apiInfo.getResponseInfo();
        if (responseInfo != null && StringUtils.isNotBlank(responseInfo.getResponseBodyJson())) {
            responseInfo.setResponseBodyJson(format.decorateJson(responseInfo.getResponseBodyJson()));
        }
    }

    /**
     * 参数表、Header、Path、Form、响应体里的类型字段都做 {@code <>} 转义，避免 HTML/Word 当标签解析。
     *
     * @param apiInfo 单份接口
     */
    private static void escapeTypes(ApiInfo apiInfo) {
        ApiInfo.ApiRequestInfo requestInfo = apiInfo.getRequestInfo();
        if (requestInfo != null) {
            escapeTable(requestInfo.getRequestBody());
            escapeTable(requestInfo.getRequestHeader());
            escapeTable(requestInfo.getRequestParam());
            escapeTable(requestInfo.getPathVariable());
            escapeTable(requestInfo.getFormParam());
        }
        ApiInfo.ApiResponseInfo responseInfo = apiInfo.getResponseInfo();
        if (responseInfo != null) {
            escapeTable(responseInfo.getResponseBody());
        }
    }

    /**
     * 遍历表格每一行，只改 type 列。
     *
     * @param tableInfo 参数表；{@code null} 则跳过
     */
    private static void escapeTable(ApiInfo.ApiTableInfo tableInfo) {
        if (tableInfo != null && tableInfo.getRowList() != null) {
            tableInfo.getRowList().forEach(row -> row.setType(escapeType(row.getType())));
        }
    }

    /**
     * {@code List<User>} → {@code List&lt;User&gt;}，空类型原样返回。
     *
     * @param type 类型文本
     * @return 转义后的类型；{@code type} 为 {@code null} 时返回 {@code null}
     */
    private static String escapeType(String type) {
        return type == null ? null : type.replaceAll(LEFT_CHEV, HTML_LT).replaceAll(RIGHT_CHEV, HTML_GT);
    }
}
