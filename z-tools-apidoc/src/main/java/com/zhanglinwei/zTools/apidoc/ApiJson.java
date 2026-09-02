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
 * 从参数 / 字段定义生成示例 JSON；注释按 pretty 后带冒号的行对齐。
 */
public final class ApiJson {

    private ApiJson() {}

    public static String prettyWithComments(ParameterDefinition parameter) {
        if (parameter == null) {
            return null;
        }
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

    public static String flatten(ParameterDefinition parameter) {
        return parameter == null ? EMPTY : JsonUtil.toJsonString(jsonValue(parameter), false);
    }

    public static Object jsonValue(ParameterDefinition parameter) {
        if (parameter == null) {
            return EMPTY;
        }
        Object value;
        if (parameter.properties() == null || parameter.properties().isEmpty()) {
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

    private static Object jsonValue(PropertyDefinition property) {
        Object value;
        if (property.cycle()) {
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

    private static Map<String, Object> jsonObject(List<PropertyDefinition> properties) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (PropertyDefinition property : properties) {
            map.put(ApiFields.name(property), jsonValue(property));
        }
        return map;
    }

    private static List<String> comments(ParameterDefinition parameter) {
        List<String> comments = new ArrayList<String>();
        appendComments(comments, parameter.properties());
        return comments;
    }

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
