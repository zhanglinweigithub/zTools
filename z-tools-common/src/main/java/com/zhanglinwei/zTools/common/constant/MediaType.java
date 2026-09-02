package com.zhanglinwei.zTools.common.constant;

import com.zhanglinwei.zTools.common.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.zhanglinwei.zTools.common.constant.StringPool.DOT;

/**
 * HTTP MediaType 常量名到实际 MIME 值的映射，对齐 Spring {@code MediaType} 的 {@code *_VALUE} 命名。
 * <p>
 * 用于解析注解里写的 {@code MediaType.APPLICATION_JSON_VALUE} 这类常量引用，转成真正的 Content-Type。
 *
 * <pre>
 *   MediaType.getValue("APPLICATION_JSON_VALUE", "text/plain") → "application/json"
 *   MediaType.getValue("MediaType.APPLICATION_JSON_VALUE", "text/plain") → "application/json"
 *   MediaType.getValue("UNKNOWN", "text/plain") → "text/plain"
 * </pre>
 */
public final class MediaType {

    /** 工具类，禁止实例化 */
    private MediaType() {}

    /** 常量名 → MIME 值，例如 {@code APPLICATION_JSON_VALUE} → {@code application/json} */
    private static final Map<String, String> MEDIA_TYPE_MAP = new HashMap<>();

    static {
        MEDIA_TYPE_MAP.put("ALL_VALUE", "*/*");
        MEDIA_TYPE_MAP.put("APPLICATION_ATOM_XML_VALUE", "application/atom+xml");
        MEDIA_TYPE_MAP.put("APPLICATION_CBOR_VALUE", "application/cbor");
        MEDIA_TYPE_MAP.put("APPLICATION_FORM_URLENCODED_VALUE", "application/x-www-form-urlencoded");
        MEDIA_TYPE_MAP.put("APPLICATION_JSON_VALUE", "application/json");
        MEDIA_TYPE_MAP.put("APPLICATION_JSON_UTF8_VALUE", "application/json;charset=UTF-8");
        MEDIA_TYPE_MAP.put("APPLICATION_OCTET_STREAM_VALUE", "application/octet-stream");
        MEDIA_TYPE_MAP.put("APPLICATION_PDF_VALUE", "application/pdf");
        MEDIA_TYPE_MAP.put("APPLICATION_PROBLEM_JSON_VALUE", "application/problem+json");
        MEDIA_TYPE_MAP.put("APPLICATION_PROBLEM_JSON_UTF8_VALUE", "application/problem+json;charset=UTF-8");
        MEDIA_TYPE_MAP.put("APPLICATION_PROBLEM_XML_VALUE", "application/problem+xml");
        MEDIA_TYPE_MAP.put("APPLICATION_RSS_XML_VALUE", "application/rss+xml");
        MEDIA_TYPE_MAP.put("APPLICATION_STREAM_JSON_VALUE", "application/stream+json");
        MEDIA_TYPE_MAP.put("APPLICATION_XHTML_XML_VALUE", "application/xhtml+xml");
        MEDIA_TYPE_MAP.put("APPLICATION_XML_VALUE", "application/xml");
        MEDIA_TYPE_MAP.put("IMAGE_GIF_VALUE", "image/gif");
        MEDIA_TYPE_MAP.put("IMAGE_JPEG_VALUE", "image/jpeg");
        MEDIA_TYPE_MAP.put("IMAGE_PNG_VALUE", "image/png");
        MEDIA_TYPE_MAP.put("MULTIPART_FORM_DATA_VALUE", "multipart/form-data");
        MEDIA_TYPE_MAP.put("MULTIPART_MIXED_VALUE", "multipart/mixed");
        MEDIA_TYPE_MAP.put("TEXT_EVENT_STREAM_VALUE", "text/event-stream");
        MEDIA_TYPE_MAP.put("TEXT_HTML_VALUE", "text/html");
        MEDIA_TYPE_MAP.put("TEXT_MARKDOWN_VALUE", "text/markdown");
        MEDIA_TYPE_MAP.put("TEXT_PLAIN_VALUE", "text/plain");
        MEDIA_TYPE_MAP.put("TEXT_XML_VALUE", "text/xml");
        MEDIA_TYPE_MAP.put("PARAM_QUALITY_FACTOR", "q");
    }

    /**
     * 按常量名取 MIME 值；找不到则返回默认值。
     * <p>
     * 若 {@code key} 含点号（如 {@code MediaType.APPLICATION_JSON_VALUE}），取 {@code split(".")} 的第二段再查表。
     *
     * <pre>
     *   getValue("APPLICATION_JSON_VALUE", "text/plain") → "application/json"
     *   getValue("MediaType.APPLICATION_JSON_VALUE", "text/plain") → "application/json"
     *   getValue("UNKNOWN", "text/plain") → "text/plain"
     * </pre>
     *
     * @param key      常量名，或带类前缀的常量引用
     * @param dftValue 查不到时的默认 MIME
     * @return 映射到的 MIME，或 {@code dftValue}
     */
    public static String getValue(String key, String dftValue) {
        if (key.contains(DOT)) {
            // 含点号时取第二段作为 map key，以支持 MediaType.XXX_VALUE 这种写法
            String[] split = key.split("\\.");
            key = split[1];
        }
        String value = MEDIA_TYPE_MAP.get(key);
        return StringUtils.isBlank(value) ? dftValue : value;
    }

    /**
     * {@code application/json}。
     *
     * @return JSON MIME
     */
    public static String APPLICATION_JSON_VALUE() {
        return MEDIA_TYPE_MAP.get("APPLICATION_JSON_VALUE");
    }

    /**
     * {@code multipart/form-data}。
     *
     * @return 表单上传 MIME
     */
    public static String MULTIPART_FORM_DATA_VALUE() {
        return MEDIA_TYPE_MAP.get("MULTIPART_FORM_DATA_VALUE");
    }
}
