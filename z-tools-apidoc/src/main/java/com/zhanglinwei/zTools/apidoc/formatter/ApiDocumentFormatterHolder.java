package com.zhanglinwei.zTools.apidoc.formatter;

import com.zhanglinwei.zTools.common.enums.DocumentType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 已注册的导出格式表。
 * <p>
 * {@link com.zhanglinwei.zTools.apidoc.generator.ApiDocumentGenerator} 用设置页的文档类型字符串查找格式；
 * 找不到时退回列表第一项（Markdown）。新增格式时在本类的格式列表里追加即可。
 */
public final class ApiDocumentFormatterHolder {

    /** 注册顺序：第一项同时是未知类型时的默认格式。 */
    private static final List<ApiDocumentFormatter> ALL = Collections.unmodifiableList(Arrays.asList(
            new MarkdownApiDocumentFormatter(),
            new HtmlApiDocumentFormatter(),
            new WordApiDocumentFormatter()
    ));

    /** 工具类，禁止实例化。 */
    private ApiDocumentFormatterHolder() {}

    /**
     * 按设置页的文档类型字符串查找格式；找不到时退回列表第一项（Markdown）。
     *
     * @param docType {@link DocumentType#getType()}，如 {@code Html}
     * @return 对应格式化器，不会为 {@code null}
     */
    public static ApiDocumentFormatter ofDocType(String docType) {
        for (ApiDocumentFormatter format : ALL) {
            if (format.documentType().getType().equals(docType)) {
                return format;
            }
        }
        return ALL.get(0);
    }
}
