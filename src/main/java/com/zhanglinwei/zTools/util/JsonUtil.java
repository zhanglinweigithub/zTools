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

/**
 * JSON工具类
 */
public class JsonUtil {

    private JsonUtil(){}

    public static final Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.FINAL).setPrettyPrinting().create();

    public static String buildPrettyJson(List<FieldInfo> children) {
        return gson.toJson(getStringObjectMap(children));
    }

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

    public static String prettyJsonWithComment(JavaProperty javaProperty) {
        if (javaProperty == null) {
            return "";
        }

        // 格式化后的Json
        String prettyJson = prettyJsonString(javaProperty);
        if (AssertUtils.isBlank(prettyJson)) {
            return prettyJson;
        }

        // Json注释
        List<JavaProperty> children = javaProperty.getChildren();
        PsiType psiType = javaProperty.getPsiType();
        if (TypeUtils.isIterableType(psiType)) {
            PsiType realType = TypeUtils.deepExtractIterableType(psiType).getRealType();
            children = JavaProperty.createSimple(realType, javaProperty.getProject(), javaProperty.getParent()).getChildren();
        }
        List<String> commentList = extractJsonCommentList(children);
        if (AssertUtils.isEmpty(commentList)) {
            return prettyJson;
        }

        // 合并Json、注释
        return mergePrettyJsonAndComment(prettyJson, commentList);
    }

    private static String mergePrettyJsonAndComment(String prettyJson, List<String> commentList) {
        if (AssertUtils.isBlank(prettyJson) || AssertUtils.isEmpty(commentList)) {
            return prettyJson;
        }

        StringBuilder jsonBuilder = new StringBuilder();

        String[] splitJson = prettyJson.split("\n");
        int index = 0;
        for (String line : splitJson) {
            String temp = line;
            if (line.contains(":")) {
                String comment = commentList.get(index++);
                if (AssertUtils.isNotBlank(comment)) {
                    temp = line + " // " + comment;
                }
            }
            jsonBuilder.append(temp);
            jsonBuilder.append("\n");
        }

        return jsonBuilder.toString();
    }

    private static List<String> extractJsonCommentList(JavaProperty javaProperty, boolean isRoot) {
        if (javaProperty == null) {
            return Collections.emptyList();
        }

        PsiType psiType = javaProperty.getPsiType();
        if (TypeUtils.isNormalType(psiType)) {
            return Collections.emptyList();
        }

        List<String> commentList = new ArrayList<>();
        if (!isRoot) {
            commentList.add(buildPropertyComment(javaProperty));
        }

        if (javaProperty.hasChildren()) {
            commentList.addAll(extractJsonCommentList(javaProperty.getChildren()));
        }

        return commentList;
    }

    private static List<String> extractJsonCommentList(Collection<JavaProperty> javaProperties) {
        if (AssertUtils.isEmpty(javaProperties)) {
            return Collections.emptyList();
        }

        List<String> commentList = new ArrayList<>();
        for (JavaProperty property : javaProperties) {
            commentList.add(buildPropertyComment(property));

            List<JavaProperty> children = property.getChildren();
            PsiType psiType = property.getPsiType();
            if (TypeUtils.isIterableType(psiType)) {
                PsiType realType = TypeUtils.deepExtractIterableType(psiType).getRealType();
                children = JavaProperty.createSimple(realType, property.getProject(), property.getParent()).getChildren();
            }
            commentList.addAll(extractJsonCommentList(children));
        }

        return commentList;
    }

    private static String buildPropertyComment(JavaProperty javaProperty) {
        if (javaProperty == null) {
            return "";
        }

        String comment = javaProperty.getComment();
        if (javaProperty.isRequired()) {
            comment = AssertUtils.isNotBlank(comment) ? comment + ", 必填" : "必填";
        }
        if (javaProperty.isCycle()) {
            comment = AssertUtils.isNotBlank(comment) ? comment + ", 同外层" : "同外层";
        }

        return comment;
    }


    public static String prettyJsonString(JavaProperty javaProperty) {
        if (javaProperty == null) {
            return "";
        }

        TypeUtils.NestedInfo nestedInfo = TypeUtils.deepExtractIterableType(javaProperty.getPsiType());
        PsiType psiType = nestedInfo.getRealType();

        Object object = null;
        if (TypeUtils.isNormalType(psiType)) {
            object = ExampleUtils.normalExample(javaProperty);
        } else {
            JavaProperty realProperty = JavaProperty.createSimple(psiType, javaProperty.getProject(), null);
            object = propertyConvertToMap(realProperty.getChildren());
        }

        for (int i = 0; i < nestedInfo.getDepth(); i++) {
            object = Collections.singletonList(object);
        }

        return gson.toJson(object);
    }

    private static Map<String, Object> propertyConvertToMap(List<JavaProperty> children) {
        if (AssertUtils.isEmpty(children)) {
            return new HashMap<>();
        }

        Map<String, Object> resultMap = new LinkedHashMap<>();

        for (JavaProperty property : children) {
            TypeUtils.NestedInfo nestedInfo = TypeUtils.deepExtractIterableType(property.getPsiType());
            PsiType psiType = nestedInfo.getRealType();

            Object object = null;
            // 获得值
            if (TypeUtils.isNormalType(psiType)) {
                object = ExampleUtils.normalExample(property);
            } else {
                object = propertyConvertToMap(property.getChildren());
            }

            // 添加嵌套层级
            for (int i = 0; i < nestedInfo.getDepth(); i++) {
                object = Collections.singletonList(object);
            }

            resultMap.put(property.getName(), object);
        }
        
        return resultMap;
    }

//    private static Map<String, Object> propertyConvertToMap(JavaProperty property) {
//        if (property == null) {
//            return new HashMap<>();
//        }
//
//        Map<String, Object> resultMap = new LinkedHashMap<>();
//        for (JavaProperty property : propertyList) {
//            Map<String, Object> propertyMap = propertyConvertToMap(property);
//            if (AssertUtils.isNotEmpty(propertyMap)) {
//                resultMap.putAll(propertyMap);
//            }
//        }
//
//        return resultMap;
//    }

    public static String buildJson5(String json, FieldInfo field) {
        return buildJson5(json, buildFieldDescList(field));
    }

    public static String buildJson5(List<FieldInfo> children) {
        return buildJson5(buildPrettyJson(children), buildFieldDescList(children));
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
}
