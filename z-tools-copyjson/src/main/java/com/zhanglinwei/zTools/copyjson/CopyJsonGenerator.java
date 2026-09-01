package com.zhanglinwei.zTools.copyjson;

import com.intellij.psi.PsiType;
import com.zhanglinwei.zTools.annotation.model.PropertyDefinition;
import com.zhanglinwei.zTools.annotation.parse.TypeParser;
import com.zhanglinwei.zTools.annotation.validation.ValidationAnnotationParser;
import com.zhanglinwei.zTools.annotation.validation.ValidationConstraints;
import com.zhanglinwei.zTools.constant.NormalType;
import com.zhanglinwei.zTools.util.CollectionUtils;
import com.zhanglinwei.zTools.util.JsonUtil;
import com.zhanglinwei.zTools.util.NestedUtils;
import com.zhanglinwei.zTools.util.StringUtils;
import com.zhanglinwei.zTools.util.TypeUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.zhanglinwei.zTools.constant.StringPool.COMMA_SPACE;
import static com.zhanglinwei.zTools.constant.StringPool.EMPTY;

/**
 * 从类型字段定义生成带注释的示例 JSON。
 */
public final class CopyJsonGenerator {

    private CopyJsonGenerator() {}

    public static String of(PsiType type) {
        if (type == null) {
            return "{}";
        }
        List<PropertyDefinition> properties = TypeParser.properties(type);
        if (CollectionUtils.isEmpty(properties)) {
            if (TypeUtils.isNormalType(type)) {
                return JsonUtil.toJsonString(NormalType.get(type.getPresentableText()), true);
            }
            return "{}";
        }
        String pretty = JsonUtil.toJsonString(jsonObject(properties), true);
        List<String> comments = comments(properties);
        return comments.isEmpty() ? pretty : JsonUtil.mergePrettyWithComments(pretty, comments);
    }

    private static Map<String, Object> jsonObject(List<PropertyDefinition> properties) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (PropertyDefinition property : properties) {
            map.put(property.name(), jsonValue(property));
        }
        return map;
    }

    private static Object jsonValue(PropertyDefinition property) {
        Object value;
        if (property.cycle()) {
            value = new LinkedHashMap<String, Object>();
        } else if (CollectionUtils.isEmpty(property.properties())) {
            Object example = NormalType.get(TypeUtils.rawType(property.type()));
            value = example == null ? "" : example;
        } else {
            value = jsonObject(property.properties());
        }
        return NestedUtils.wrapWithNesting(value, TypeUtils.nestDepth(property.type()));
    }

    private static List<String> comments(List<PropertyDefinition> properties) {
        List<String> comments = new ArrayList<String>();
        appendComments(comments, properties);
        return comments;
    }

    private static void appendComments(List<String> comments, List<PropertyDefinition> properties) {
        if (properties == null) {
            return;
        }
        for (PropertyDefinition property : properties) {
            comments.add(commentOf(property));
            appendComments(comments, property.properties());
        }
    }

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
