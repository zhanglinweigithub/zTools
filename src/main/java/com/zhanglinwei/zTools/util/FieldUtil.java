package com.zhanglinwei.zTools.util;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.doc.apidoc.constant.JacksonAnnotation;
import com.zhanglinwei.zTools.doc.apidoc.constant.SwaggerAnnotation;
import com.zhanglinwei.zTools.doc.apidoc.constant.TypeEnum;
import com.zhanglinwei.zTools.doc.apidoc.constant.WebAnnotation;
import com.zhanglinwei.zTools.doc.apidoc.model.FieldInfo;
import com.zhanglinwei.zTools.doc.apidoc.normal.RequireAndRange;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字段工具类
 */
public class FieldUtil {

    private FieldUtil(){}

    public static final Map<String, Object> normalTypes = new HashMap<>();

    private static List<String> requiredTexts = Arrays.asList("@NotNull", "@NotBlank", "@NotEmpty");
    /**
     * 泛型列表
     */
    public static final List<String> genericList = new ArrayList<>();


    static {
        normalTypes.put("int", 1);
        normalTypes.put("boolean", false);
        normalTypes.put("byte", 1);
        normalTypes.put("short", 1);
        normalTypes.put("long", 1L);
        normalTypes.put("float", 1.0F);
        normalTypes.put("double", 1.0D);
        normalTypes.put("char", 'a');
        normalTypes.put("Boolean", false);
        normalTypes.put("Byte", 0);
        normalTypes.put("Short", (short) 0);
        normalTypes.put("Integer", 0);
        normalTypes.put("Long", 0L);
        normalTypes.put("Float", 0.0F);
        normalTypes.put("Double", 0.0D);
        normalTypes.put("String", "stringValue");
        normalTypes.put("BigDecimal", 0.111111);
        normalTypes.put("Date", new Date().getTime());
        normalTypes.put("LocalDateTime", LocalDateTime.now().toString());
        normalTypes.put("LocalDate", LocalDate.now().toString());
        normalTypes.put("LocalTime", LocalTime.now().toString());
        normalTypes.put("Timestamp", Timestamp.valueOf(LocalDateTime.now()));
        normalTypes.put("BigInteger", 0);
        normalTypes.put("MultipartFile", "文件");
        genericList.add("T");
        genericList.add("E");
        genericList.add("K");
        genericList.add("V");
    }

    /** 是否必填*/
    private static boolean isParamRequired(PsiAnnotation annotation) {
        String annotationText = annotation.getText();
        if (
                annotationText.contains(WebAnnotation.RequestParam)
                || annotationText.contains(WebAnnotation.RequestHeader)
                || annotationText.contains(WebAnnotation.PathVariable)
                || annotationText.contains(WebAnnotation.ResponseBody)
                || annotationText.contains(WebAnnotation.RequestPart)
        ) {
            PsiNameValuePair[] psiNameValuePairs = annotation.getParameterList().getAttributes();
            for (PsiNameValuePair psiNameValuePair : psiNameValuePairs) {
                if ("required".equals(psiNameValuePair.getName()) && "false".equals(psiNameValuePair.getLiteralValue())) {
                    return false;
                }
            }
            return true;
        }
        if(requiredTexts.contains(annotationText.split("\\(")[0])) {
            return true;
        }
        if(annotationText.contains(SwaggerAnnotation.ApiModelProperty)) {
            PsiNameValuePair[] psiNameValuePairs = annotation.getParameterList().getAttributes();
            for (PsiNameValuePair psiNameValuePair : psiNameValuePairs) {
                if ("required".equals(psiNameValuePair.getName()) && "true".equals(psiNameValuePair.getLiteralValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 必填、值域范围 */
    public static RequireAndRange getRequireAndRange(PsiAnnotation[] annotations) {
        if (annotations.length == 0) {
            return RequireAndRange.instance();
        }
        boolean require = false;
        String min = "";
        String max = "";
        String range = "N/A";
        for (PsiAnnotation annotation : annotations) {
            if (isParamRequired(annotation)) {
                require = true;
            }
            if (annotation.getText().contains(WebAnnotation.RequestPart)) {
                range = "文件";
                break;
            }
            String qualifiedName = annotation.getText();
            if (qualifiedName.contains("Length") || qualifiedName.contains("Range") || qualifiedName.contains("Size")) {
                PsiAnnotationMemberValue minValue = annotation.findAttributeValue("min");
                if (minValue != null) {
                    min = minValue.getText();
                    break;
                }
            }
            if (qualifiedName.contains("Min")) {
                PsiAnnotationMemberValue minValue = annotation.findAttributeValue("value");
                if (minValue != null) {
                    min = minValue.getText();
                    break;
                }
            }
        }
        if (AssertUtils.isNotBlank(min) || AssertUtils.isNotBlank(max)) {
            range = "[" + min + "," + max + "]";
        }
        return new RequireAndRange(require, range);
    }

    /** 示例 */
    public static Object buildExample(FieldInfo fieldInfo) {
        if (TypeEnum.OBJECT.equals(fieldInfo.getParamType())) {
            return " ";
        }

        return getValue(fieldInfo);
    }

    /** 示例值 */
    public static Object getValue(FieldInfo fieldInfo) {
        PsiType psiType = fieldInfo.getPsiType();
        if (isIterableType(psiType)) {
            PsiType type = PsiUtil.extractIterableTypeParameter(psiType, false);
            if (type == null) {
                return "[]";
            }
            if (isNormalType(type)) {
                Object value = getValue(type.getPresentableText(), fieldInfo.getAnnotations());
                if (value == null) {
                    return "[]";
                }

                return "[" + value + "," + value + "]";
            }
        }
        Object value = getValue(psiType.getPresentableText(),fieldInfo.getAnnotations());
        return value == null ? " " : value;
    }

    /** 示例值 */
    private static Object getValue(String typeStr,List<PsiAnnotation> annotations) {
        if(Arrays.asList("LocalDateTime","Date").contains(typeStr)) {
            for (PsiAnnotation annotation : annotations) {
                if(annotation.getText().contains(JacksonAnnotation.JsonFormat)) {
                    PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
                    if (attributes.length >= 1) {
                        for (PsiNameValuePair attribute : attributes) {
                            if ("pattern".equals(attribute.getName())) {
                                return attribute.getLiteralValue();
                            }
                        }
                    }
                }
            }
        }
        return normalTypes.get(typeStr);
    }

    /** 是否泛型 */
    public static boolean isGenericType(PsiType psiType) {
        return psiType != null && isGenericType(psiType.getPresentableText());
    }
    /** 是否泛型 */
    public static boolean isGenericType(String typeName) {
        return genericList.contains(typeName);
    }

    /** 是否可迭代 */
    public static boolean isIterableType(String typeName) {
        return isCollectionType(typeName) || typeName.contains("[]");
    }

    /** 是否可迭代 */
    public static boolean isIterableType(PsiType psiType) {
        return isIterableType(psiType.getPresentableText());
    }

    /** 是否集合 */
    public static boolean isCollectionType(PsiType psiType) {
        return isCollectionType(psiType.getPresentableText());
    }

    /** 是否集合 */
    private static boolean isCollectionType(String typeName) {
        if (AssertUtils.isEmpty(typeName)) {
            return false;
        }
        return typeName.startsWith("List")
                || typeName.startsWith("Set")
                || typeName.startsWith("Collection");
    }

    /** 是否普通类型 */
    public static boolean isNormalType(PsiType psiType) {
        return isNormalType(psiType.getPresentableText());
    }

    /** 是否普通类型 */
    public static boolean isNormalType(String typeName) {
        return normalTypes.containsKey(typeName);
    }

    /** 是否Map */
    public static boolean isMapType(PsiType psiType) {
        return isMapType(psiType.getPresentableText());
    }

    /** 是否Map */
    public static boolean isMapType(String typeName) {
        return isMapFamily(typeName);
    }

    /** 是否简单集合 */
    public static boolean isNormalCollectionType(PsiType psiType) {
        return isNormalCollectionType(psiType.getPresentableText());
    }

    /** 是否简单集合 */
    public static boolean isNormalCollectionType(String typeName) {
        if (!isListFamily(typeName)) {
            return false;
        }
        String regex = "(\\w+)(<(.+?)>)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(typeName);
        if (matcher.matches()) {
            String baseType = matcher.group(1); // 基础类型，例如 ArrayList, HashMap
            String genericContent = matcher.group(2); // 泛型内容，例如 <String> 或 <String, Integer>

            if (genericContent != null) {
                // 获得尖括号中的嵌套内容
                typeName = genericContent.replaceAll("[<>]", "");
            }
        }

        return isNormalType(typeName) || isMapFamily(typeName);
    }

    public static boolean isListFamily(String typeString) {
        return typeString.startsWith("ArrayList") ||
                typeString.startsWith("LinkedList") ||
                typeString.startsWith("Vector") ||
                typeString.startsWith("List") ||
                typeString.startsWith("Set") ||
                typeString.startsWith("TreeSet") ||
                typeString.startsWith("HashSet") ||
                typeString.startsWith("LinkedHashSet") ||
                typeString.startsWith("BitSet") ||
                typeString.startsWith("SortedSet")
                ;
    }

    private static boolean isMapFamily(String typeString) {
        return typeString.startsWith("Map") ||
                typeString.startsWith("HashMap") ||
                typeString.startsWith("LinkedHashMap") ||
                typeString.startsWith("ConcurrentHashMap") ||
                typeString.startsWith("ConcurrentMap") ||
                typeString.startsWith("Hashtable") ||
                typeString.startsWith("SortedMap") ||
                typeString.startsWith("TreeMap")
                ;
    }

    /** 是否简单数组 */
    public static boolean isNormalArrayType(PsiType psiType) {
        return isNormalArrayType(psiType.getPresentableText());
    }

    /** 是否简单数组 */
    public static boolean isNormalArrayType(String typeName) {
        typeName = typeName.replace("[", "").replace("]", "");
        return isNormalType(typeName);
    }

    /** 是否枚举 */
    public static boolean isEnum(PsiType psiType) {
        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
        return psiClass != null && psiClass.isEnum();
    }

    /** 是否Multipart类型 */
    public static boolean isMultipartType(PsiType psiType) {
        return isMultipartType(psiType.getPresentableText());
    }

    /** 是否Multipart类型 */
    public static boolean isMultipartType(String typeName) {
        return typeName.contains("MultipartFile");
    }

    /** 是否static字段 */
    public static boolean isStaticField(PsiField psiField) {
        PsiModifierList modifierList = psiField.getModifierList();
        if(modifierList == null) {
            return false;
        }
        for (PsiElement child : modifierList.getChildren()) {
            if(child instanceof PsiKeyword) {
                if(child.getText().equals("static")) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 是否final字段 */
    public static boolean isFinalField(PsiField psiField) {
        PsiModifierList modifierList = psiField.getModifierList();
        if(modifierList == null) {
            return false;
        }
        for (PsiElement child : modifierList.getChildren()) {
            if(child instanceof PsiKeyword) {
                if(child.getText().equals("final")) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 是否static、final字段 */
    public static boolean isStaticOrFinalField(PsiField psiField) {
        return isFinalField(psiField) || isStaticField(psiField);
    }

    /** 是否忽略字段 */
    public static boolean isIgnoredField(PsiField psiField) {
        PsiAnnotation[] annotations = psiField.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            String qualifiedName = annotation.getText();
            if(qualifiedName.contains("Ignore")) {
                return true;
            }
        }
        return false;
    }

    /** 获得类型枚举 */
    public static TypeEnum getTypeEnum(PsiType psiType) {
        if (FieldUtil.isNormalType(psiType)) {
            return TypeEnum.LITERAL;
        }
        if (FieldUtil.isIterableType(psiType)) {
            return TypeEnum.ARRAY;
        }

        return TypeEnum.OBJECT;
    }

}

