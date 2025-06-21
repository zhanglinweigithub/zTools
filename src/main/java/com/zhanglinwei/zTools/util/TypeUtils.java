package com.zhanglinwei.zTools.util;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.common.constants.NormalType;

public final class TypeUtils {

    private TypeUtils() {
    }

    public static class NestedInfo {
        private PsiType realType;
        private Integer depth;

        public NestedInfo(PsiType realType, Integer depth) {
            this.realType = realType;
            this.depth = depth;
        }

        public PsiType getRealType() {
            return realType;
        }

        public void setRealType(PsiType realType) {
            this.realType = realType;
        }

        public Integer getDepth() {
            return depth;
        }

        public void setDepth(Integer depth) {
            this.depth = depth;
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
        return isInPackage(psiType, "javax.servlet.http");
    }

    /** 是否 servlet 类型 */
    public static boolean isServletType(PsiType psiType) {
        return isInPackage(psiType, "javax.servlet");
    }

    /** 是否 IO 类型 */
    public static boolean isIOType(PsiType psiType) {
        return isInPackage(psiType, "java.io");
    }

    /** 是否属于某个包 */
    public static boolean isInPackage(PsiType psiType, String packageName) {
        if (psiType == null || AssertUtils.isBlank(packageName)) {
            return false;
        }

        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
        if (psiClass == null) {
            return false;
        }

        String qualifiedName = psiClass.getQualifiedName();
        return AssertUtils.isNotBlank(qualifiedName) && qualifiedName.startsWith(packageName);
    }

    /** 是否Multipart类型 */
    public static boolean isMultipartType(PsiType psiType) {
        return psiType != null && psiType.getPresentableText().contains("MultipartFile");
    }

    /** 是否注解类型 */
    public static boolean isAnnotation(PsiType psiType) {
        if (psiType != null) {
            PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
            return psiClass != null && psiClass.isAnnotationType();
        }
        return false;
    }

    /** 是否接口类型 */
    public static boolean isInterface(PsiType psiType) {
        if (psiType != null) {
            PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
            return psiClass != null && psiClass.isInterface();
        }
        return false;
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

    /** 是否集合类型（非Map） */
    public static boolean isCollectionType(PsiType psiType) {
        return psiType != null && isCollectionFamily(psiType.getPresentableText());
    }

    /** 是否可迭代 */
    public static boolean isIterableType(PsiType psiType) {
        return isCollectionType(psiType) || psiType instanceof PsiArrayType;
    }

    /** 是否Map */
    public static boolean isMapType(PsiType psiType) {
        return psiType != null && isMapFamily(psiType.getPresentableText());
    }

    /** 是否Collection家族 */
    private static boolean isCollectionFamily(String typeName) {
        return typeName.startsWith("List") ||
                typeName.startsWith("ArrayList") ||
                typeName.startsWith("LinkedList") ||
                typeName.startsWith("CopyOnWriteArrayList") ||
                typeName.startsWith("AbstractList") ||
                typeName.startsWith("Vector") ||

                typeName.startsWith("Set") ||
                typeName.startsWith("TreeSet") ||
                typeName.startsWith("HashSet") ||
                typeName.startsWith("LinkedHashSet") ||
                typeName.startsWith("BitSet") ||
                typeName.startsWith("SortedSet") ||

                typeName.startsWith("Collection")
                ;
    }

    private static boolean isMapFamily(String typeName) {
        return typeName.startsWith("Map") ||
                typeName.startsWith("HashMap") ||
                typeName.startsWith("LinkedHashMap") ||
                typeName.startsWith("ConcurrentHashMap") ||
                typeName.startsWith("ConcurrentMap") ||
                typeName.startsWith("Hashtable") ||
                typeName.startsWith("SortedMap") ||
                typeName.startsWith("TreeMap")
                ;
    }
}
