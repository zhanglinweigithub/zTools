package com.zhanglinwei.zTools.util;

import com.intellij.psi.PsiArrayType;
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
