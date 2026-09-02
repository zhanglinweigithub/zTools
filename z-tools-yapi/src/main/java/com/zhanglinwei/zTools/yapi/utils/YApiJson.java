package com.zhanglinwei.zTools.yapi.utils;

import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.annotation.model.PropertyDefinition;
import com.zhanglinwei.zTools.common.util.CollectionUtils;
import com.zhanglinwei.zTools.common.util.JsonUtil;
import com.zhanglinwei.zTools.common.util.NestedUtils;
import com.zhanglinwei.zTools.common.util.StringUtils;
import com.zhanglinwei.zTools.common.util.TypeUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA_SPACE;
import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;
import static com.zhanglinwei.zTools.common.constant.StringPool.SPACE_SLASH_SLASH_SPACE;

/**
 * YApi 侧 JSON 生成工具。基于 annotation 模块的解析结果，
 * 生成带注释的 pretty JSON，用于 YApi 的 req_body_other 和 res_body。
 * <p>
 * 类型如何变成 JSON 字段（示例值由 {@link YApiFields#example} 给出）：
 * <pre>
 * class User {
 *     Long id;      // 用户ID
 *     String name;  // 用户名
 *     List&lt;Role&gt; roles;
 * }
 * →
 * {
 *   "id": 0,           // 用户ID
 *   "name": "",        // 用户名
 *   "roles": [ { } ]
 * }
 * </pre>
 * 基本类型走默认示例（{@code Long} → {@code 0}，{@code String} → {@code ""}）；
 * 对象递归展开字段；{@code List}/{@code []} 按嵌套深度包一层数组。
 */
public final class YApiJson {

    /** 工具类，禁止实例化。 */
    private YApiJson() {}

    /**
     * 生成参数的带注释 pretty JSON。
     * 用于 @RequestBody 参数和返回值。
     *
     * @param parameter 请求体或返回值定义
     * @return 带行尾注释的 pretty JSON；参数为 {@code null} 时返回 {@code null}
     */
    public static String prettyWithComments(ParameterDefinition parameter) {
        if (parameter == null) {
            return null;
        }
        String placeholder = placeholderComment(parameter);
        if (placeholder != null) {
            return "{}" + SPACE_SLASH_SLASH_SPACE + placeholder;
        }
        // 先生成 pretty JSON，再按字段顺序把说明贴到对应行
        String pretty = JsonUtil.toJsonString(jsonValue(parameter), true);
        if (StringUtils.isBlank(pretty)) {
            return pretty;
        }
        List<String> comments = comments(parameter);
        if (comments.isEmpty()) {
            return pretty;
        }
        return JsonUtil.mergePrettyWithComments(pretty, comments);
    }

    /**
     * void / Reactor / 流无法展开成业务 JSON 时，给出空对象上的说明。
     *
     * @param parameter 请求体或返回值
     * @return 注释文案；普通业务类型则为 {@code null}
     */
    private static String placeholderComment(ParameterDefinition parameter) {
        String type = parameter.type();
        if (TypeUtils.isVoid(type)) {
            return "无返回值";
        }
        if (TypeUtils.isReactor(type)) {
            return "Reactor 类型 " + type + "，非 JSON 业务体";
        }
        if (TypeUtils.isStream(parameter.packageName(), type)) {
            return "流类型 " + TypeUtils.outerType(type) + "，无法以 JSON 展示";
        }
        return null;
    }

    // ───── 内部方法 ─────

    /**
     * 把参数转成 JSON 值：叶子用示例，对象递归字段，再按嵌套深度包数组。
     *
     * @param parameter 参数定义
     * @return JSON 值（Map / List / 标量）
     */
    private static Object jsonValue(ParameterDefinition parameter) {
        if (parameter == null) {
            return EMPTY;
        }
        Object value;
        if (CollectionUtils.isEmpty(parameter.properties())) {
            // 无字段：Map 给空对象，其余用类型默认示例（Long→0, String→""）
            if (TypeUtils.isMap(parameter.type())) {
                value = new LinkedHashMap<String, Object>();
            } else {
                value = YApiFields.example(parameter);
            }
        } else {
            value = jsonObject(parameter.properties());
        }
        return NestedUtils.wrapWithNesting(value, TypeUtils.nestDepth(parameter.type()));
    }

    /**
     * 把属性转成 JSON 值。循环引用给空对象，避免无限递归。
     *
     * @param property 字段定义
     * @return JSON 值
     */
    private static Object jsonValue(PropertyDefinition property) {
        Object value;
        if (property.cycle()) {
            value = new LinkedHashMap<String, Object>();
        } else if (CollectionUtils.isEmpty(property.properties())) {
            if (TypeUtils.isMap(property.type())) {
                value = new LinkedHashMap<String, Object>();
            } else {
                value = YApiFields.example(property);
            }
        } else {
            value = jsonObject(property.properties());
        }
        return NestedUtils.wrapWithNesting(value, TypeUtils.nestDepth(property.type()));
    }

    /**
     * 字段列表转 JSON 对象，键为 YApi 字段名。
     *
     * @param properties 字段列表
     * @return 保持声明顺序的 Map
     */
    private static Map<String, Object> jsonObject(List<PropertyDefinition> properties) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (PropertyDefinition property : properties) {
            map.put(YApiFields.name(property), jsonValue(property));
        }
        return map;
    }

    /**
     * 收集参数全部字段的行尾注释，顺序与 pretty JSON 字段一致。
     *
     * @param parameter 参数定义
     * @return 注释列表
     */
    private static List<String> comments(ParameterDefinition parameter) {
        List<String> comments = new ArrayList<String>();
        appendComments(comments, parameter.properties());
        return comments;
    }

    /**
     * 深度优先追加字段注释。
     *
     * @param comments   注释收集列表
     * @param properties 字段列表
     */
    private static void appendComments(List<String> comments, List<PropertyDefinition> properties) {
        if (CollectionUtils.isEmpty(properties)) {
            return;
        }
        for (int i = 0; i < properties.size(); i++) {
            PropertyDefinition property = properties.get(i);
            comments.add(commentOf(property));
            appendComments(comments, property.properties());
        }
    }

    /**
     * 拼一条字段注释：说明、必填、循环引用。
     *
     * @param property 字段定义
     * @return 如 {@code 用户名, 必填}；无内容时为空串
     */
    private static String commentOf(PropertyDefinition property) {
        List<String> parts = new ArrayList<String>();
        String desc = YApiFields.description(property);
        if (StringUtils.isNotBlank(desc)) {
            parts.add(desc);
        }
        if (YApiFields.required(property)) {
            parts.add("必填");
        }
        if (property.cycle()) {
            parts.add("同外层");
        }
        return parts.isEmpty() ? EMPTY : String.join(COMMA_SPACE, parts);
    }
}
