package com.zhanglinwei.zTools.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.doc.apidoc.constant.TypeEnum;
import com.zhanglinwei.zTools.doc.apidoc.model.FieldInfo;
import com.zhanglinwei.zTools.doc.apidoc.model.JavaProperty;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JSON工具类
 */
public class JsonUtil {

    private JsonUtil(){}

    private static final Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC).setPrettyPrinting().create();

    public static String buildPrettyJson(FieldInfo fieldInfo) {
        if (TypeEnum.LITERAL.equals(fieldInfo.getParamType())) {
            return FieldUtil.getValue(fieldInfo).toString();
        }
        Map<String, Object> stringObjectMap = getStringObjectMap(fieldInfo.getChildren());
        if (TypeEnum.ARRAY.equals(fieldInfo.getParamType())) {
            return gson.toJson(Collections.singletonList(stringObjectMap));
        }
        return gson.toJson(stringObjectMap);
    }

    private static String buildJson5(String prettyJson, List<String> fieldDesc) {
        String[] split = prettyJson.split("\n");
        StringBuffer json5 = new StringBuffer();
        int index = 0;
        for (String str : split) {
            String temp = str;
            if (str.contains(":")) {
                index++;
                String desc = fieldDesc.get(index - 1);
                if (AssertUtils.isNotBlank(desc)) {
                    temp = str + " // " + desc;
                }
            }
            json5.append(temp);
            json5.append("\n");
        }
        return json5.toString();
    }

    public static String buildJson5(FieldInfo fieldInfo) {
        return buildJson5(buildPrettyJson(fieldInfo), buildFieldDescList(fieldInfo));
    }

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
        return gson.toJson(object);
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


    public static List<String> buildFieldDescList(List<FieldInfo> children) {
        List<String> descList = new ArrayList<>();
        if (children == null) {
            return descList;
        }
        for (FieldInfo fieldInfo : children) {
            String desc = buildDesc(fieldInfo);
            if (fieldInfo.isCycle()) {
                descList.add(AssertUtils.isBlank(desc) ? "同外层" : desc + " 同外层");
            } else {
                descList.add(desc);
            }
            if (!TypeEnum.LITERAL.equals(fieldInfo.getParamType())) {
                descList.addAll(buildFieldDescList(fieldInfo.getChildren()));
            }
        }
        return descList;
    }

    public static List<String> buildFieldDescList(FieldInfo fieldInfo) {
        List<String> descList = new ArrayList<>();
        if (fieldInfo == null) {
            return descList;
        }
        if (TypeEnum.LITERAL.equals(fieldInfo.getParamType())) {
            if (AssertUtils.isBlank(fieldInfo.getDesc())) {
                return descList;
            }
            descList.add(buildDesc(fieldInfo));
        } else {
            descList.addAll(buildFieldDescList(fieldInfo.getChildren()));
        }
        return descList;
    }

    private static String buildDesc(FieldInfo fieldInfo) {
        String desc = fieldInfo.getDesc();
        if (!fieldInfo.isRequired()) {
            return desc;
        }
        if (AssertUtils.isEmpty(desc)) {
            return "必填";
        }
        return desc + ", 必填";
    }

    public static Map<String, Object> getStringObjectMap(List<FieldInfo> fieldInfos) {
        Map<String, Object> map = new LinkedHashMap<>(64);
        if (fieldInfos == null) {
            return map;
        }
        for (FieldInfo fieldInfo : fieldInfos) {
            buildJsonValue(map, fieldInfo);
        }
        return map;
    }

    private static void buildJsonValue(Map<String, Object> map, FieldInfo fieldInfo) {
        Object example = fieldInfo.getExample();
        if (TypeEnum.LITERAL.equals(fieldInfo.getParamType())) {
            map.put(fieldInfo.getName(), example == null ? FieldUtil.getValue(fieldInfo) : example);
            return;
        }
        if (TypeEnum.ARRAY.equals(fieldInfo.getParamType())) {
            if (AssertUtils.isNotEmpty(fieldInfo.getChildren())) {
                map.put(fieldInfo.getName(), Collections.singletonList(getStringObjectMap(fieldInfo.getChildren())));
                return;
            }
            PsiClass psiClass = PsiUtil.resolveClassInType(fieldInfo.getPsiType());
            String innerType = fieldInfo.getPsiType() instanceof PsiArrayType ? ((PsiArrayType)fieldInfo.getPsiType()).getComponentType().getPresentableText() :
                    PsiUtil.substituteTypeParameter(fieldInfo.getPsiType(), psiClass, 0, true).getPresentableText();
            map.put(fieldInfo.getName(), Collections.singletonList(FieldUtil.normalTypes.get(innerType) == null ? new HashMap<>() : FieldUtil.normalTypes.get(innerType)));
            return;
        }
        if (fieldInfo.getChildren() == null) {
            map.put(fieldInfo.getName(), new HashMap<>());
            return;
        }
        for (FieldInfo info : fieldInfo.getChildren()) {
            if (!info.getName().equals(fieldInfo.getName())) {
                map.put(fieldInfo.getName(), getStringObjectMap(fieldInfo.getChildren()));
            }
        }
    }

    public static boolean validateJson(String jsonString) {
        boolean passFlag = false;

        if (AssertUtils.isBlank(jsonString)) {
            return passFlag;
        }

        try {
            Object obj = gson.fromJson(jsonString, Object.class);
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
            JsonObject jsonObj = gson.fromJson(jsonString, JsonObject.class);
            return gson.toJson(jsonObj);
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
            JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);
            return gson.toJson(jsonArray);
        } catch (Exception e) {
            NotificationUtil.errorNotify("json format error, Caused by: " + e.getMessage(), project);
        }
        return "";
    }

    public static String toJsonString(Object object) {
        return object == null ? "" : gson.toJson(object);
    }
}
