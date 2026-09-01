package com.zhanglinwei.zTools.util;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.constant.NormalType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class TypeUtils {

    private static final Set<String> COLLECTION_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "List", "ArrayList", "LinkedList", "CopyOnWriteArrayList", "AbstractList", "Vector",
            "Set", "TreeSet", "HashSet", "LinkedHashSet", "BitSet", "SortedSet",
            "Collection"
    )));

    private static final Set<String> MAP_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "Map", "HashMap", "LinkedHashMap", "ConcurrentHashMap", "ConcurrentMap",
            "Hashtable", "SortedMap", "TreeMap"
    )));

    private TypeUtils() {
    }

    public static class NestedInfo {
        private final PsiType realType;
        private final Integer depth;

        public NestedInfo(PsiType realType, Integer depth) {
            this.realType = realType;
            this.depth = depth;
        }

        public PsiType getRealType() {
            return realType;
        }

        public Integer getDepth() {
            return depth;
        }
    }

    /** 深度解析可迭代的真实类型 */
    public static NestedInfo deepExtractIterableType(PsiType psiType) {
        PsiType realType = psiType;
        Integer depth = 0;

        while (isCollectionType(realType)) {
            depth++;
            realType = PsiUtil.extractIterableTypeParameter(realType, true);
        }

        while (realType instanceof PsiArrayType) {
            depth++;
            realType = realType.getDeepComponentType();
        }

        return new NestedInfo(realType, depth);
    }

    /** 是否基本类型 */
    public static boolean isPrimitive(PsiType psiType) {
        return psiType != null && psiType instanceof PsiPrimitiveType;
    }

    /** 是否 http 类型 */
    public static boolean isHttpType(PsiType psiType) {
        return isInPackage(psiType, "javax.servlet.http") || isInPackage(psiType, "jakarta.servlet.http");
    }

    /** 是否 servlet 类型 */
    public static boolean isServletType(PsiType psiType) {
        return isInPackage(psiType, "javax.servlet") || isInPackage(psiType, "jakarta.servlet");
    }

    /** 是否 IO 类型 */
    public static boolean isIOType(PsiType psiType) {
        return isInPackage(psiType, "java.io");
    }

    private static boolean isInPackage(PsiType psiType, String packageName) {
        if (psiType == null || StringUtils.isBlank(packageName)) {
            return false;
        }

        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
        if (psiClass == null) {
            return false;
        }

        String qualifiedName = psiClass.getQualifiedName();
        return StringUtils.isNotBlank(qualifiedName) && qualifiedName.startsWith(packageName);
    }

    /** 是否Multipart类型 */
    public static boolean isMultipartType(PsiType psiType) {
        return psiType != null && isMultipart(psiType.getPresentableText());
    }

    /** 是否枚举类型 */
    public static boolean isEnum(PsiType psiType) {
        if (psiType != null) {
            PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
            return psiClass != null && psiClass.isEnum();
        }
        return false;
    }

    /** 是否普通类型 */
    public static boolean isNormalType(PsiType psiType) {
        return psiType != null && NormalType.containsKey(psiType.getPresentableText());
    }

    /** 是否集合类型（非Map、非数组） */
    public static boolean isCollectionType(PsiType psiType) {
        return psiType != null && isCollectionFamily(outerType(psiType.getPresentableText()));
    }

    /** 是否可迭代 */
    public static boolean isIterableType(PsiType psiType) {
        return isCollectionType(psiType) || psiType instanceof PsiArrayType;
    }

    /** 是否Map */
    public static boolean isMapType(PsiType psiType) {
        return psiType != null && isMap(psiType.getPresentableText());
    }

    public static boolean isMultipart(String type) {
        return type != null && type.contains("MultipartFile");
    }

    public static boolean isMap(String type) {
        return type != null && MAP_NAMES.contains(outerType(type));
    }

    public static boolean isCollection(String type) {
        if (type == null) {
            return false;
        }
        String text = type.trim();
        if (text.endsWith("[]")) {
            return true;
        }
        return isCollectionFamily(outerType(text));
    }

    public static int nestDepth(String type) {
        if (type == null) {
            return 0;
        }
        int depth = 0;
        String text = type.trim();
        while (text.endsWith("[]")) {
            depth++;
            text = text.substring(0, text.length() - 2).trim();
        }
        while (isCollectionFamily(outerType(text))) {
            depth++;
            String inner = innerGeneric(text);
            if (inner == null || inner.contains(",")) {
                break;
            }
            text = inner;
        }
        return depth;
    }

    public static String rawType(String type) {
        if (type == null) {
            return "";
        }
        String text = type.trim();
        if (text.endsWith("[]")) {
            text = text.substring(0, text.length() - 2);
        }
        String inner = innerGeneric(text);
        if (inner != null && !inner.contains(",")) {
            text = inner;
        } else {
            text = outerType(text);
        }
        int dot = text.lastIndexOf('.');
        return dot < 0 ? text : text.substring(dot + 1);
    }

    public static String outerType(String type) {
        if (type == null) {
            return "";
        }
        String text = type.trim();
        int generic = text.indexOf('<');
        if (generic > 0) {
            text = text.substring(0, generic);
        }
        if (text.endsWith("[]")) {
            text = text.substring(0, text.length() - 2);
        }
        int dot = text.lastIndexOf('.');
        return dot < 0 ? text : text.substring(dot + 1);
    }

    private static boolean isCollectionFamily(String simpleName) {
        return COLLECTION_NAMES.contains(simpleName);
    }

    private static String innerGeneric(String type) {
        if (type == null) {
            return null;
        }
        int start = type.indexOf('<');
        if (start < 0 || !type.endsWith(">")) {
            return null;
        }
        return type.substring(start + 1, type.length() - 1).trim();
    }
}
