package com.zhanglinwei.zTools.copyjson;

import com.intellij.psi.PsiType;
import com.zhanglinwei.zTools.annotation.model.PropertyDefinition;
import com.zhanglinwei.zTools.annotation.parse.TypeParser;
import com.zhanglinwei.zTools.annotation.validation.ValidationAnnotationParser;
import com.zhanglinwei.zTools.annotation.validation.ValidationConstraints;
import com.zhanglinwei.zTools.common.constant.NormalType;
import com.zhanglinwei.zTools.common.constant.StringPool;
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

/**
 * 从当前类的字段结构生成带行注释的示例 JSON，供剪贴板复制。
 * <p>
 * 由 {@link CopyJsonAction} 调用：光标所在类 → {@link TypeParser#properties} → 本类。
 * 与 apidoc 的 {@code ApiJson} 不同：字段名用源码名，必填只看 {@code @NotNull}/{@code @NotBlank}/{@code @NotEmpty}，
 * 说明只用字段 JavaDoc。循环引用写成 {@code {}}，行注释带「同外层」。
 * <p>
 * 示例：类 {@code User} 含 {@code Long id}、{@code String name}（注释「用户名」、{@code @NotNull}）、
 * {@code User parent}（循环）→
 * <pre>
 * {
 *   "id": 0,
 *   "name": "stringValue", // 用户名, 必填
 *   "parent": {} // 同外层
 * }
 * </pre>
 */
public final class CopyJsonGenerator {

    /** 工具类，禁止实例化。 */
    private CopyJsonGenerator() {}

    /**
     * 把类型转成 pretty JSON（对象带行注释；普通类型则是类型默认值）。
     *
     * @param type 光标所在类对应的 {@link PsiType}；{@code null} 时返回 {@code {}}
     * @return pretty JSON 字符串
     */
    public static String of(PsiType type) {
        if (type == null) {
            return StringPool.EMPTY_OBJECT;
        }
        // 展开对象字段；普通类型没有字段
        List<PropertyDefinition> properties = TypeParser.properties(type);
        if (CollectionUtils.isEmpty(properties)) {
            if (TypeUtils.isNormalType(type)) {
                return JsonUtil.toJsonString(NormalType.get(type.getPresentableText()), true);
            }
            return StringPool.EMPTY_OBJECT;
        }
        // 先序列化对象，再把前序注释对齐到带冒号的行
        String pretty = JsonUtil.toJsonString(jsonObject(properties), true);
        List<String> comments = comments(properties);
        return comments.isEmpty() ? pretty : JsonUtil.mergePrettyWithComments(pretty, comments);
    }

    /**
     * 按字段列表组成 JSON 对象，键为源码字段名。
     *
     * @param properties 对象字段
     * @return 保持插入顺序的 Map
     */
    private static Map<String, Object> jsonObject(List<PropertyDefinition> properties) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (PropertyDefinition property : properties) {
            map.put(property.name(), jsonValue(property));
        }
        return map;
    }

    /**
     * 字段转 JSON 值：循环引用为 {@code {}}，叶子用 {@link NormalType} 默认值。
     *
     * @param property 字段定义
     * @return 标量、空 Map 或嵌套 Map；集合类型按嵌套深度包 List
     */
    private static Object jsonValue(PropertyDefinition property) {
        Object value;
        if (property.cycle()) {
            // 循环引用：值用 {}，注释侧会标「同外层」
            value = new LinkedHashMap<String, Object>();
        } else if (CollectionUtils.isEmpty(property.properties())) {
            // Map → {}；Number 等走默认值表；未知类型仍用空串
            Object example = NormalType.exampleOf(property.type());
            value = example == null ? EMPTY : example;
        } else {
            value = jsonObject(property.properties());
        }
        return NestedUtils.wrapWithNesting(value, TypeUtils.nestDepth(property.type()));
    }

    /**
     * 收集字段树的行注释（前序，与 pretty JSON 带冒号的行对应）。
     *
     * @param properties 顶层字段
     * @return 每字段一条注释
     */
    private static List<String> comments(List<PropertyDefinition> properties) {
        List<String> comments = new ArrayList<String>();
        appendComments(comments, properties);
        return comments;
    }

    /**
     * 前序遍历字段树，追加每条字段的注释。
     *
     * @param comments   输出列表
     * @param properties 当前层字段；{@code null} 则直接返回
     */
    private static void appendComments(List<String> comments, List<PropertyDefinition> properties) {
        if (properties == null) {
            return;
        }
        for (PropertyDefinition property : properties) {
            comments.add(commentOf(property));
            appendComments(comments, property.properties());
        }
    }

    /**
     * 拼一条行注释：JavaDoc、必填、循环引用。
     *
     * @param property 字段
     * @return 如 {@code 用户名, 必填}；没有任何片段时返回空串
     */
    private static String commentOf(PropertyDefinition property) {
        List<String> parts = new ArrayList<String>();
        if (StringUtils.isNotBlank(property.comment())) {
            parts.add(property.comment());
        }
        ValidationConstraints constraints = ValidationAnnotationParser.parse(property);
        if (constraints.notNull() || constraints.notBlank() || constraints.notEmpty()) {
            parts.add("必填");
        }
        if (property.cycle()) {
            parts.add("同外层");
        }
        return parts.isEmpty() ? EMPTY : String.join(COMMA_SPACE, parts);
    }
}
