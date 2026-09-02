package com.zhanglinwei.zTools.common.util;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.common.constant.CharacterPool;
import com.zhanglinwei.zTools.common.constant.NormalType;
import com.zhanglinwei.zTools.common.constant.StringPool;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA;
import static com.zhanglinwei.zTools.common.constant.StringPool.EMPTY;

/**
 * PSI 类型与类型字符串解析：集合/Map/文件上传判定、泛型剥离、嵌套深度。
 * <p>
 * 字符串 API 只看 presentable 文本，不依赖 classpath 解析，适合文档生成时快速分类。
 *
 * <pre>
 *   TypeUtils.rawType("List&lt;User&gt;") → "User"
 *   TypeUtils.rawType("List&lt;int[]&gt;") → "int"
 *   TypeUtils.outerType("List&lt;User&gt;") → "List"
 *   TypeUtils.nestDepth("List&lt;List&lt;A&gt;&gt;") → 2
 *   TypeUtils.nestDepth("List&lt;int[]&gt;") → 2
 *   TypeUtils.isCollection("User[]") → true
 *   TypeUtils.isMap("Map&lt;String, User&gt;") → true
 *   TypeUtils.isMultipart("List&lt;MultipartFile&gt;") → true
 * </pre>
 */
public final class TypeUtils {

    /** 视为集合家族的简单类名（不含 Map、数组） */
    private static final Set<String> COLLECTION_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "List", "ArrayList", "LinkedList", "CopyOnWriteArrayList", "AbstractList", "Vector",
            "Set", "TreeSet", "HashSet", "LinkedHashSet", "BitSet", "SortedSet",
            "Collection"
    )));

    /** 视为 Map 家族的简单类名 */
    private static final Set<String> MAP_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "Map", "HashMap", "LinkedHashMap", "ConcurrentHashMap", "ConcurrentMap",
            "Hashtable", "SortedMap", "TreeMap"
    )));

    /** 工具类，禁止实例化 */
    private TypeUtils() {
    }

    /**
     * 可迭代类型剥离后的真实元素类型及其嵌套深度。
     * 例如 {@code List<List<User>>} 的 {@code realType} 为 User，{@code depth} 为 2。
     */
    public static class NestedInfo {
        /** 剥掉集合/数组后的元素类型 */
        private final PsiType realType;
        /** 集合或数组的嵌套层数 */
        private final Integer depth;

        /**
         * @param realType 真实元素类型
         * @param depth    嵌套深度
         */
        public NestedInfo(PsiType realType, Integer depth) {
            this.realType = realType;
            this.depth = depth;
        }

        /** 剥掉集合/数组后的元素类型。 */
        public PsiType getRealType() {
            return realType;
        }

        /** 集合或数组的嵌套层数。 */
        public Integer getDepth() {
            return depth;
        }
    }

    /**
     * 深度解析可迭代的真实类型：循环剥离 Collection 泛型与数组分量。
     *
     * @param psiType 原始 PSI 类型
     * @return 元素类型与嵌套深度
     */
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

    /**
     * 是否基本类型（{@code int}/{@code boolean} 等，不含包装类）。
     *
     * @param psiType PSI 类型
     * @return 是基本类型则为 {@code true}
     */
    public static boolean isPrimitive(PsiType psiType) {
        return psiType != null && psiType instanceof PsiPrimitiveType;
    }

    /**
     * 是否 Servlet HTTP 类型（{@code javax/jakarta.servlet.http} 包下）。
     *
     * @param psiType PSI 类型
     * @return 属于该包则为 {@code true}
     */
    public static boolean isHttpType(PsiType psiType) {
        return isInPackage(psiType, "javax.servlet.http") || isInPackage(psiType, "jakarta.servlet.http");
    }

    /**
     * 是否 Servlet 类型（{@code javax/jakarta.servlet} 包下）。
     *
     * @param psiType PSI 类型
     * @return 属于该包则为 {@code true}
     */
    public static boolean isServletType(PsiType psiType) {
        return isInPackage(psiType, "javax.servlet") || isInPackage(psiType, "jakarta.servlet");
    }

    /**
     * 是否 {@code java.io} 包下的类型。
     *
     * @param psiType PSI 类型
     * @return 属于该包则为 {@code true}
     */
    public static boolean isIOType(PsiType psiType) {
        return isInPackage(psiType, "java.io");
    }

    /**
     * 解析类型的全限定名是否以指定包名为前缀。
     *
     * @param psiType     PSI 类型
     * @param packageName 包名前缀
     * @return 命中则为 {@code true}
     */
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

    /**
     * 是否 Multipart 上传类型（presentable 文本含 {@code MultipartFile}）。
     *
     * @param psiType PSI 类型
     * @return 是上传类型则为 {@code true}
     */
    public static boolean isMultipartType(PsiType psiType) {
        return psiType != null && isMultipart(psiType.getPresentableText());
    }

    /**
     * 是否枚举类型。
     *
     * @param psiType PSI 类型
     * @return 解析到的类是枚举则为 {@code true}
     */
    public static boolean isEnum(PsiType psiType) {
        if (psiType != null) {
            PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
            return psiClass != null && psiClass.isEnum();
        }
        return false;
    }

    /**
     * 是否普通类型（能在 {@link NormalType} 中取到示例值）。
     *
     * @param psiType PSI 类型
     * @return 是普通类型则为 {@code true}
     */
    public static boolean isNormalType(PsiType psiType) {
        return psiType != null && NormalType.containsKey(psiType.getPresentableText());
    }

    /**
     * 是否集合类型（非 Map、非数组；数组请用 {@link #isIterableType(PsiType)}）。
     *
     * @param psiType PSI 类型
     * @return 外层简单名属于集合家族则为 {@code true}
     */
    public static boolean isCollectionType(PsiType psiType) {
        return psiType != null && isCollectionFamily(outerType(psiType.getPresentableText()));
    }

    /**
     * 是否可迭代：集合或数组。
     *
     * @param psiType PSI 类型
     * @return 可迭代则为 {@code true}
     */
    public static boolean isIterableType(PsiType psiType) {
        return isCollectionType(psiType) || psiType instanceof PsiArrayType;
    }

    /**
     * 是否 Map 类型。
     *
     * @param psiType PSI 类型
     * @return 外层简单名属于 Map 家族则为 {@code true}
     */
    public static boolean isMapType(PsiType psiType) {
        return psiType != null && isMap(psiType.getPresentableText());
    }

    /**
     * 文本是否表示文件上传类型：含 {@code MultipartFile} 即视为是（含 {@code List<MultipartFile>}）。
     *
     * <pre>
     *   isMultipart("MultipartFile") → true
     *   isMultipart("List&lt;MultipartFile&gt;") → true
     *   isMultipart("User") → false
     *   isMultipart(null) → false
     * </pre>
     *
     * @param type presentable 类型文本
     * @return 含 MultipartFile 则为 {@code true}
     */
    public static boolean isMultipart(String type) {
        return type != null && type.contains("MultipartFile");
    }

    /**
     * 文本是否表示 Map：看外层简单名是否在 Map 家族中。
     *
     * <pre>
     *   isMap("Map&lt;String, User&gt;") → true
     *   isMap("java.util.HashMap") → true
     *   isMap("List&lt;User&gt;") → false
     *   isMap(null) → false
     * </pre>
     *
     * @param type presentable 类型文本
     * @return 是 Map 则为 {@code true}
     */
    public static boolean isMap(String type) {
        return type != null && MAP_NAMES.contains(outerType(type));
    }

    /**
     * 文本是否表示集合或数组：以 {@code []} 结尾，或外层简单名属于集合家族。
     * Map 不算集合。
     *
     * <pre>
     *   isCollection("List&lt;User&gt;") → true
     *   isCollection("User[]") → true
     *   isCollection("Map&lt;String, User&gt;") → false
     *   isCollection(null) → false
     * </pre>
     *
     * @param type presentable 类型文本
     * @return 是集合或数组则为 {@code true}
     */
    public static boolean isCollection(String type) {
        if (type == null) {
            return false;
        }
        String text = type.trim();
        if (text.endsWith(StringPool.EMPTY_ARRAY)) {
            return true;
        }
        return isCollectionFamily(outerType(text));
    }

    /**
     * 集合/数组嵌套深度。数组后缀 {@code []} 与集合泛型交替剥离，直到碰到非集合或 Map。
     * 内层泛型含逗号（如 Map 的两个类型参数）时停止，避免把 Map 误当成嵌套集合。
     *
     * <pre>
     *   nestDepth("List&lt;User&gt;") → 1
     *   nestDepth("List&lt;List&lt;A&gt;&gt;") → 2
     *   nestDepth("List&lt;int[]&gt;") → 2
     *   nestDepth("User[]") → 1
     *   nestDepth("int[][]") → 2
     *   nestDepth("User") → 0
     * </pre>
     *
     * @param type presentable 类型文本
     * @return 嵌套层数；{@code null} 为 0
     */
    public static int nestDepth(String type) {
        if (type == null) {
            return 0;
        }
        int depth = 0;
        String text = type.trim();
        while (true) {
            if (text.endsWith(StringPool.EMPTY_ARRAY)) {
                depth++;
                text = text.substring(0, text.length() - 2).trim();
                continue;
            }
            if (!isCollectionFamily(outerType(text))) {
                break;
            }
            depth++;
            String inner = innerGeneric(text);
            // 内层含逗号（Map 等）或剥不出泛型时停止，避免误计
            if (inner == null || inner.contains(COMMA)) {
                break;
            }
            text = inner;
        }
        return depth;
    }

    /**
     * 取最内层元素简单名：剥掉全部集合泛型与数组后缀，再去掉包名。
     * 内层泛型含逗号（Map）时停止，返回当时的外层名。
     *
     * <pre>
     *   rawType("List&lt;User&gt;") → "User"
     *   rawType("List&lt;List&lt;A&gt;&gt;") → "A"
     *   rawType("List&lt;int[]&gt;") → "int"
     *   rawType("int[][]") → "int"
     *   rawType("java.util.List&lt;com.foo.User&gt;") → "User"
     *   rawType("User[]") → "User"
     *   rawType(null) → ""
     * </pre>
     *
     * @param type presentable 类型文本
     * @return 元素简单名；{@code null} 为空串
     */
    public static String rawType(String type) {
        if (type == null) {
            return EMPTY;
        }
        String text = type.trim();
        while (true) {
            if (text.endsWith(StringPool.EMPTY_ARRAY)) {
                text = text.substring(0, text.length() - 2).trim();
                continue;
            }
            String inner = innerGeneric(text);
            if (inner != null && !inner.contains(COMMA) && isCollectionFamily(outerType(text))) {
                text = inner;
                continue;
            }
            break;
        }
        return outerType(text);
    }

    /**
     * 取外层简单类名：去掉泛型实参、数组后缀和包名。
     *
     * <pre>
     *   outerType("List&lt;User&gt;") → "List"
     *   outerType("java.util.Map&lt;String, User&gt;") → "Map"
     *   outerType("User[]") → "User"
     *   outerType("com.foo.User") → "User"
     *   outerType(null) → ""
     * </pre>
     *
     * @param type presentable 类型文本
     * @return 外层简单名；{@code null} 为空串
     */
    public static String outerType(String type) {
        if (type == null) {
            return EMPTY;
        }
        String text = type.trim();
        int generic = text.indexOf(CharacterPool.LEFT_CHEV);
        if (generic > 0) {
            text = text.substring(0, generic);
        }
        if (text.endsWith(StringPool.EMPTY_ARRAY)) {
            text = text.substring(0, text.length() - 2);
        }
        int dot = text.lastIndexOf(CharacterPool.DOT);
        return dot < 0 ? text : text.substring(dot + 1);
    }

    /**
     * 简单类名是否属于集合家族（不含 Map）。
     *
     * @param simpleName 外层简单名
     * @return 在集合名单中则为 {@code true}
     */
    private static boolean isCollectionFamily(String simpleName) {
        return COLLECTION_NAMES.contains(simpleName);
    }

    /**
     * 取 {@code <...>} 内的泛型实参文本（不去嵌套）。
     *
     * @param type 含泛型的类型文本
     * @return 尖括号内的内容；不是 {@code Xxx<...>} 形式则为 {@code null}
     */
    private static String innerGeneric(String type) {
        if (type == null) {
            return null;
        }
        int start = type.indexOf(CharacterPool.LEFT_CHEV);
        if (start < 0 || !type.endsWith(StringPool.RIGHT_CHEV)) {
            return null;
        }
        return type.substring(start + 1, type.length() - 1).trim();
    }
}
