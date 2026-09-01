package com.zhanglinwei.zTools.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import static com.zhanglinwei.zTools.constant.StringPool.COLON;
import static com.zhanglinwei.zTools.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.constant.StringPool.NEWLINE;
import static com.zhanglinwei.zTools.constant.StringPool.SPACE_SLASH_SLASH_SPACE;

public final class JsonUtil {

    private JsonUtil() {}

    private static final Gson PRETTY_GSON = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.STATIC)
            .setPrettyPrinting()
            .create();
    private static final Gson FLATTEN_GSON = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.STATIC)
            .create();

    public static String toJsonString(Object object, boolean pretty) {
        if (object == null) {
            return EMPTY;
        }
        return pretty ? PRETTY_GSON.toJson(object) : FLATTEN_GSON.toJson(object);
    }

    /**
     * 把注释按 pretty JSON 里带冒号的行对齐。注释内容由调用方生成。
     */
    public static String mergePrettyWithComments(String prettyJson, List<String> comments) {
        if (prettyJson == null || comments == null || comments.isEmpty()) {
            return prettyJson;
        }
        Iterator<String> iterator = comments.iterator();
        String[] lines = prettyJson.split(NEWLINE);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                builder.append(NEWLINE);
            }
            String line = lines[i];
            if (line.contains(COLON) && iterator.hasNext()) {
                String comment = iterator.next();
                if (StringUtils.isNotBlank(comment)) {
                    line = line + SPACE_SLASH_SLASH_SPACE + comment;
                }
            }
            builder.append(line);
        }
        return builder.toString();
    }
}
