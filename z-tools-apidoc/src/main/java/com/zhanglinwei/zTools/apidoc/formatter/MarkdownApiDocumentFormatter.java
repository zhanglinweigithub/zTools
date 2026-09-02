package com.zhanglinwei.zTools.apidoc.formatter;

import com.zhanglinwei.zTools.common.enums.DocumentType;

/**
 * Markdown 导出：模板 {@code api-doc-md.ftl}。
 * <p>
 * 不覆盖 {@link #decorateJson}，pretty JSON（含 {@code // 注释}）原样进代码块。
 */
public class MarkdownApiDocumentFormatter implements ApiDocumentFormatter {

    @Override
    public DocumentType documentType() {
        return DocumentType.MARKDOWN;
    }

    @Override
    public String templateName() {
        return "api-doc-md.ftl";
    }
}
