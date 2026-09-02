package com.zhanglinwei.zTools.apidoc;

import com.zhanglinwei.zTools.annotation.model.ParameterDefinition;
import com.zhanglinwei.zTools.annotation.model.PropertyDefinition;
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

/**
 * 把参数 / 字段定义转成示例 JSON，供文档模板插入。
 * <p>
 * 在流程中位于 {@link ApiInfo} 组装请求/响应体之后、{@link com.zhanglinwei.zTools.apidoc.formatter.ApiDocumentFormatter}
 * 着色之前：本类只负责「对象 → pretty JSON + 行注释」。
 * 字段名走 {@link ApiFields#name}（Spring 绑定优先）；必填、说明也走 {@link ApiFields}。
 * 循环引用写成 {@code {}}，行注释带「同外层」。
 * <p>
 * 示例：字段 {@code id: Long}、{@code name: String}（注释「用户名」、{@code @NotNull}）、
 * {@code parent: User}（类型已在外层出现）→
 * <pre>
 * {
 *   "id": 0,
 *   "name": "stringValue", // 用户名, 必填
 *   "parent": {} // 同外层
 * }
 * </pre>
 * 注释按 pretty JSON 里带冒号的行顺序对齐，由 {@link JsonUtil#mergePrettyWithComments} 合并。
 */
public final class ApiJson {

    /** 工具类，禁止实例化。 */
    private ApiJson() {}

    /**
     * 生成带行注释的 pretty JSON。
     *
     * @param parameter 请求体、表单对象或方法返回值；{@code null} 时返回 {@code null}
     * @return pretty JSON；无字段注释时不加 {@code //}；参数为 {@code null} 时返回 {@code null}
     */
    public static String prettyWithComments(ParameterDefinition parameter) {
        if (parameter == null) {
            return null;
        }
        // 先按字段树拼出可序列化的 Map / 标量
        String pretty = JsonUtil.toJsonString(jsonValue(parameter), true);
        if (StringUtils.isBlank(pretty)) {
            return pretty;
        }
        // 按同样的前序遍历收集行注释，再对齐到带冒号的 JSON 行
        List<String> comments = comments(parameter);
        if (comments.isEmpty()) {
            return pretty;
        }
        return JsonUtil.mergePrettyWithComments(pretty, comments);
    }

    /**
     * 生成无缩进、无注释的 JSON，用于表单等单行示例。
     *
     * @param parameter 参数定义；{@code null} 时返回空串
     * @return 压缩 JSON；参数为 {@code null} 时返回空串
     */
    public static String flatten(ParameterDefinition parameter) {
        return parameter == null ? EMPTY : JsonUtil.toJsonString(jsonValue(parameter), false);
    }

    /**
     * 把参数转成 Gson 可序列化的值：对象 → {@code Map}，集合按嵌套深度包 {@code List}。
     *
     * @param parameter 参数定义
     * @return 标量示例、空 Map（Map 类型或无字段）、或字段 Map；参数为 {@code null} 时返回空串
     */
    public static Object jsonValue(ParameterDefinition parameter) {
        if (parameter == null) {
            return EMPTY;
        }
        Object value;
        if (parameter.properties() == null || parameter.properties().isEmpty()) {
            // 叶子：Map 给 {}，其余用注解 example / 类型默认值
            if (TypeUtils.isMap(parameter.type())) {
                value = new LinkedHashMap<String, Object>();
            } else {
                value = ApiFields.example(parameter);
            }
        } else {
            value = jsonObject(parameter.properties());
        }
        return NestedUtils.wrapWithNesting(value, TypeUtils.nestDepth(parameter.type()));
    }

    /**
     * 把字段转成 JSON 值。循环引用固定为 {@code {}}，不再展开子字段。
     *
     * @param property 对象上的一个字段
     * @return 标量、空 Map 或嵌套 Map
     */
    private static Object jsonValue(PropertyDefinition property) {
        Object value;
        if (property.cycle()) {
            // 循环引用：值用 {}，注释侧会标「同外层」
            value = new LinkedHashMap<String, Object>();
        } else if (property.properties() == null || property.properties().isEmpty()) {
            if (TypeUtils.isMap(property.type())) {
                value = new LinkedHashMap<String, Object>();
            } else {
                value = ApiFields.example(property);
            }
        } else {
            value = jsonObject(property.properties());
        }
        return NestedUtils.wrapWithNesting(value, TypeUtils.nestDepth(property.type()));
    }

    /**
     * 按字段列表顺序组成 JSON 对象，键名走 Spring 绑定。
     *
     * @param properties 对象字段，顺序即 JSON 字段顺序
     * @return 保持插入顺序的 Map
     */
    private static Map<String, Object> jsonObject(List<PropertyDefinition> properties) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (PropertyDefinition property : properties) {
            map.put(ApiFields.name(property), jsonValue(property));
        }
        return map;
    }

    /**
     * 收集参数对象上全部字段的行注释（前序，与 pretty JSON 带冒号的行一一对应）。
     *
     * @param parameter 带字段树的参数
     * @return 每字段一条注释，可能为空串
     */
    private static List<String> comments(ParameterDefinition parameter) {
        List<String> comments = new ArrayList<String>();
        appendComments(comments, parameter.properties());
        return comments;
    }

    /**
     * 前序遍历字段树，把每条字段的注释追加进列表。
     *
     * @param comments   输出列表
     * @param properties 当前层字段；{@code null} 则直接返回
     */
    private static void appendComments(List<String> comments, List<PropertyDefinition> properties) {
        if (properties == null) {
            return;
        }
        for (int i = 0; i < properties.size(); i++) {
            PropertyDefinition property = properties.get(i);
            comments.add(commentOf(property));
            appendComments(comments, property.properties());
        }
    }

    /**
     * 拼一条行注释：说明、必填、循环引用，用逗号分隔。
     *
     * @param property 字段
     * @return 如 {@code 用户名, 必填}；没有任何片段时返回空串
     */
    private static String commentOf(PropertyDefinition property) {
        List<String> parts = new ArrayList<String>();
        String description = ApiFields.description(property);
        if (StringUtils.isNotBlank(description)) {
            parts.add(description);
        }
        if (ApiFields.required(property)) {
            parts.add("必填");
        }
        if (property.cycle()) {
            parts.add("同外层");
        }
        return parts.isEmpty() ? EMPTY : String.join(COMMA_SPACE, parts);
    }
}
