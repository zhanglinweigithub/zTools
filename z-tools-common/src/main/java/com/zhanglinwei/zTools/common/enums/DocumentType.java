package com.zhanglinwei.zTools.common.enums;

/**
 * 接口文档导出格式：Markdown、HTML、Word。
 * <p>
 * 配置里通常存展示名（{@link #getType()}），再用 {@link #of(String)} 还原枚举以取得文件后缀。
 */
public enum DocumentType {

    MARKDOWN(".md", "MarkDown"),
    HTML(".html", "Html"),
    WORD(".doc", "Word"),
    ;

    /** 文件后缀，含点号，如 {@code .md} */
    private final String suffix;
    /** 配置 / UI 展示名，如 {@code MarkDown} */
    private final String type;

    /**
     * @param suffix 文件后缀
     * @param type   展示名
     */
    DocumentType(String suffix, String type){
        this.suffix = suffix;
        this.type = type;
    }

    /**
     * 按展示名解析；{@code null} 或不识别时回落到 {@link #MARKDOWN}。
     *
     * <pre>
     *   DocumentType.of("Html") → HTML
     *   DocumentType.of("unknown") → MARKDOWN
     *   DocumentType.of(null) → MARKDOWN
     * </pre>
     *
     * @param type 展示名，与 {@link #getType()} 相等才匹配
     * @return 对应枚举，默认 Markdown
     */
    public static DocumentType of(String type) {
        if (type == null) {
            return MARKDOWN;
        }
        for (DocumentType doc : values()) {
            if (doc.type.equals(type)) {
                return doc;
            }
        }
        return MARKDOWN;
    }

    /** 文件后缀（含点号）。 */
    public String getSuffix() {
        return suffix;
    }

    /** 配置 / UI 展示名。 */
    public String getType() {
        return type;
    }
}
