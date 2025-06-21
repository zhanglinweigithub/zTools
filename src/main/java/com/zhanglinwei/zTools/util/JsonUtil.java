package com.zhanglinwei.zTools.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiType;
import com.zhanglinwei.zTools.doc.apidoc.model.JavaProperty;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JSON工具类
 */
public final class JsonUtil {

    private JsonUtil(){}

    private static final Gson PRETTY_GSON = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC).setPrettyPrinting().create();
    private static final Gson FLATTEN_GSON = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC).create();

    /**
     * 格式化后、带注释的 Json
     */
    public static String prettyJsonWithComment(JavaProperty property) {
        if (property == null) {
            return "";
        }

        // 格式化后的Json
        String prettyJson = prettyJsonString(property);
        if (AssertUtils.isBlank(prettyJson)) {
            return prettyJson;
        }

        // Json注释
        List<JavaProperty> children = extractEffectiveChildren(property);
        List<String> commentList = extractAllPropertyComments(children);
        if (AssertUtils.isEmpty(commentList)) {
            return prettyJson;
        }

        // 合并Json、注释
        return mergePrettyJsonAndComment(prettyJson, commentList);
    }

    /**
     * 合并 Json、注释
     */
    private static String mergePrettyJsonAndComment(String prettyJson, List<String> commentList) {
        if (AssertUtils.isBlank(prettyJson) || AssertUtils.isEmpty(commentList)) {
            return prettyJson;
        }

        Iterator<String> commentIterator = commentList.iterator();
        return Arrays.stream(prettyJson.split("\n"))
                .map(line -> appendCommentToLine(line, commentIterator))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Json 追加注释
     */
    private static String appendCommentToLine(String line, Iterator<String> commentIterator) {
        if (!line.contains(":") || !commentIterator.hasNext()) {
            return line;
        }

        String comment = commentIterator.next();
        return AssertUtils.isNotBlank(comment) ? line + " // " + comment : line;
    }

    /**
     * 提取属性注释
     */
    private static List<String> extractAllPropertyComments(Collection<JavaProperty> properties) {
        return AssertUtils.isEmpty(properties) ? Collections.emptyList() : properties.stream()
                .flatMap(property -> extractOwnAndAllPropertyComments(property).stream())
                .collect(Collectors.toList());
    }

    /**
     * 提取属性注释 包含本身
     */
    private static List<String> extractOwnAndAllPropertyComments(JavaProperty rootProperty) {
        List<JavaProperty> allProperties = flattenProperties(rootProperty);
        return allProperties.stream()
                .map(JsonUtil::buildPropertyComment)
                .collect(Collectors.toList());
    }

    /**
     * 铺平属性和子属性 包含本身
     */
    private static List<JavaProperty> flattenProperties(JavaProperty property) {
        List<JavaProperty> result = new ArrayList<>();
        if (property == null) {
            return result;
        }

        result.add(property);
        extractEffectiveChildren(property).forEach(child -> {
            result.addAll(flattenProperties(child));
        });

        return result;
    }

    /**
     * 构建注释，添加说明信息
     */
    private static String buildPropertyComment(JavaProperty property) {
        if (property == null) {
            return "";
        }

        List<String> commentParts = new ArrayList<>();

        if (AssertUtils.isNotBlank(property.getComment())) {
            commentParts.add(property.getComment());
        }
        if (property.isRequired()) {
            commentParts.add("必填");
        }
        if (property.isCycle()) {
            commentParts.add("同外层");
        }

        return String.join(", ", commentParts);
    }

    /**
     * 格式化的 Json
     */
    public static String prettyJsonString(JavaProperty property) {
        if (property == null) {
            return "";
        }

        Object object = convertPropertyToJsonObject(property);
        return PRETTY_GSON.toJson(object);
    }

    public static String flattenJsonString(JavaProperty property) {
        if (property == null) {
            return "";
        }

        Object object = convertPropertyToJsonObject(property);
        return FLATTEN_GSON.toJson(object);
    }

    /**
     * 转换为 Json 对象
     */
    private static Object convertPropertyToJsonObject(JavaProperty property) {
        if (property == null) {
            return "";
        }

        TypeUtils.NestedInfo nestedInfo = TypeUtils.deepExtractIterableType(property.getPsiType());
        PsiType realType = nestedInfo.getRealType();

        Object value;
        if (TypeUtils.isNormalType(realType)) {
            value = ExampleUtils.createSimpleJsonExample(realType, property.getPsiAnnotations());
        } else {
            List<JavaProperty> children = extractEffectiveChildren(property);
            value = propertyConvertToMap(children);
        }

        // 添加嵌套层级
        return NestedUtils.wrapWithNesting(value, nestedInfo.getDepth());
    }

    /**
     * 提取有效子属性
     */
    private static List<JavaProperty> extractEffectiveChildren(JavaProperty property) {
        List<JavaProperty> children = property.getChildren();
        if (TypeUtils.isIterableType(property.getPsiType())) {
            PsiType realType = TypeUtils.deepExtractIterableType(property.getPsiType()).getRealType();
            return JavaProperty.createSimple(realType, property.getProject(), property.getParent())
                    .getChildren();
        }
        return children;
    }



    /**
     * 转换 Map 结构
     */
    private static Map<String, Object> propertyConvertToMap(List<JavaProperty> children) {
        if (AssertUtils.isEmpty(children)) {
            return new HashMap<>();
        }

        Map<String, Object> resultMap = new LinkedHashMap<>();
        for (JavaProperty property : children) {
            Object object = convertPropertyToJsonObject(property);
            resultMap.put(property.getOriginName(), object);
        }
        
        return resultMap;
    }

    public static boolean validateJson(String jsonString) {
        boolean passFlag = false;

        if (AssertUtils.isBlank(jsonString)) {
            return passFlag;
        }

        try {
            Object obj = PRETTY_GSON.fromJson(jsonString, Object.class);
            if (obj != null) {
                passFlag = true;
            }
        } catch (Exception ignore) {}

        return passFlag;
    }

    public static String formatJson(String jsonString, Project project) {
        return jsonString.startsWith("[") ? formatJsonArray(jsonString, project) : formatJsonObject(jsonString, project);
    }

    private static String formatJsonObject(String jsonString, Project project) {
        if (AssertUtils.isBlank(jsonString)) {
            return "";
        }

        try {
            JsonObject jsonObj = PRETTY_GSON.fromJson(jsonString, JsonObject.class);
            return PRETTY_GSON.toJson(jsonObj);
        } catch (Exception e) {
            NotificationUtil.errorNotify("json format error, Caused by: " + e.getMessage(), project);
        }
        return "";
    }

    public static String formatJsonArray(String jsonString, Project project) {
        if (AssertUtils.isBlank(jsonString)) {
            return "";
        }

        try {
            JsonArray jsonArray = PRETTY_GSON.fromJson(jsonString, JsonArray.class);
            return PRETTY_GSON.toJson(jsonArray);
        } catch (Exception e) {
            NotificationUtil.errorNotify("json format error, Caused by: " + e.getMessage(), project);
        }
        return "";
    }

    public static String toJsonString(Object object, boolean pretty) {
        if (object == null) {
            return "";
        }

        return pretty ? PRETTY_GSON.toJson(object) : FLATTEN_GSON.toJson(object);
    }
}
