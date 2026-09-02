package com.zhanglinwei.zTools.common.enums;

/**
 * HTTP 方法。{@link #NONE} 表示注解上无法识别或未指定方法。
 */
public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH, NONE;

    /**
     * 忽略大小写解析；{@code null} 或不识别时返回 {@code null}（不会回落到 {@link #NONE}）。
     *
     * <pre>
     *   HttpMethod.of("post") → POST
     *   HttpMethod.of("GET") → GET
     *   HttpMethod.of("TRACE") → null
     *   HttpMethod.of(null) → null
     * </pre>
     *
     * @param httpMethod 方法名，如 {@code GET}、{@code post}
     * @return 对应枚举；无法识别为 {@code null}
     */
    public static HttpMethod of(String httpMethod) {
        if (httpMethod == null) {
            return null;
        }
        for (HttpMethod item : values()) {
            if (item.name().equalsIgnoreCase(httpMethod)) {
                return item;
            }
        }
        return null;
    }
}
