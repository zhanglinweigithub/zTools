package com.zhanglinwei.zTools.apidoc.formatter;

import com.zhanglinwei.zTools.common.enums.DocumentType;

/**
 * 一种 API 文档导出格式。
 * <p>
 * 职责只有三块：文档类型（决定后缀和设置页名称）、对应的 ftl 模板名、
 * 示例 JSON 如何嵌入模板。Markdown 用默认实现（原文）；HTML/Word 通过
 * {@link com.zhanglinwei.zTools.apidoc.decorator.ColoredJsonDecorator} 加颜色。
 * 新增格式：实现本接口，并在 {@link ApiDocumentFormatterHolder} 的列表里追加。
 */
public interface ApiDocumentFormatter {

    /** 与设置页「文档类型」以及文件后缀对应，例如 Html / MarkDown / Word。 */
    DocumentType documentType();

    /** classpath {@code /template/api} 下的模板文件名，如 {@code api-doc-html.ftl}。 */
    String templateName();

    /**
     * 把 pretty JSON（可能带 {@code // 字段注释}）转成模板可直接插入的字符串。
     * 默认原样返回；需要着色的格式覆盖此方法。
     */
    default String decorateJson(String prettyJson) {
        return prettyJson;
    }
}
