package com.zhanglinwei.zTools.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import static com.zhanglinwei.zTools.common.constant.StringPool.COLON;
import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.NEWLINE;
import static com.zhanglinwei.zTools.common.constant.StringPool.SPACE_SLASH_SLASH_SPACE;

/**
 * JSON 序列化，以及把字段注释对齐到 pretty JSON 的冒号行。
 * <p>
 * 序列化时排除 {@code static} 字段。pretty 模式用于文档展示，flatten 用于紧凑拷贝。
 */
public final class JsonUtil {

    /** 工具类，禁止实例化 */
    private JsonUtil() {}

    /** 带缩进的 Gson，排除 static 字段 */
    private static final Gson PRETTY_GSON = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.STATIC)
            .setPrettyPrinting()
            .create();
    /** 无缩进的 Gson，排除 static 字段 */
    private static final Gson FLATTEN_GSON = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.STATIC)
            .create();

    /**
     * 对象转 JSON 字符串；{@code object} 为 {@code null} 时返回空串。
     *
     * <pre>
     *   toJsonString(null, true) → ""
     *   toJsonString(map, true)  → 带换行缩进的 JSON
     *   toJsonString(map, false) → 单行 JSON
     * </pre>
     *
     * @param object 待序列化对象，可为 {@code null}
     * @param pretty {@code true} 则缩进换行
     * @return JSON 文本，或空串
     */
    public static String toJsonString(Object object, boolean pretty) {
        if (object == null) {
            return EMPTY;
        }
        return pretty ? PRETTY_GSON.toJson(object) : FLATTEN_GSON.toJson(object);
    }

    /**
     * 把注释按 pretty JSON 里带冒号的行对齐。注释内容由调用方生成。
     * <p>
     * 仅给含 {@code :} 的行追加注释（对应 JSON 的 key-value 行）；注释耗尽后剩余行原样输出。
     * 空白注释会被跳过，不追加 {@code // }。
     *
     * <pre>
     *   prettyJson:
     *   {
     *     "name": "stringValue"
     *   }
     *   comments: ["用户名"]
     *   →
     *   {
     *     "name": "stringValue" // 用户名
     *   }
     * </pre>
     *
     * @param prettyJson 已 pretty 的 JSON 文本
     * @param comments   与冒号行顺序对应的注释列表
     * @return 合并后的文本；任一入参为空则原样返回 {@code prettyJson}
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
