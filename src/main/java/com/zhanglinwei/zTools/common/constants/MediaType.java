package com.zhanglinwei.zTools.common.constants;

import com.zhanglinwei.zTools.util.AssertUtils;

import java.util.HashMap;
import java.util.Map;

import static com.zhanglinwei.zTools.common.constants.SpringPool.DOT;

public final class MediaType {

    private MediaType() {}

    private static Map<String, String> MEDIA_TYPE_MAP = new HashMap<>();

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

    public static String getValue(String key, String dftValue) {
        if (key.contains(DOT)) {
            String[] split = key.split("\\.");
            key = split[1];
        }
        String value = MEDIA_TYPE_MAP.get(key);
        return AssertUtils.isBlank(value) ? dftValue : value;
    }

    public static String APPLICATION_JSON_VALUE() {
        return MEDIA_TYPE_MAP.get("APPLICATION_JSON_VALUE");
    }

    public static String MULTIPART_FORM_DATA_VALUE() {
        return MEDIA_TYPE_MAP.get("MULTIPART_FORM_DATA_VALUE");
    }
}
