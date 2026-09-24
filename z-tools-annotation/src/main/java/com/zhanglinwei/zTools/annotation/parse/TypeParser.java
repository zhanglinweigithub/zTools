package com.zhanglinwei.zTools.annotation.parse;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.PsiWildcardType;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.annotation.model.PropertyDefinition;
import com.zhanglinwei.zTools.common.constant.CharacterPool;
import com.zhanglinwei.zTools.common.util.StringUtils;
import com.zhanglinwei.zTools.common.util.TypeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 从 {@link PsiType} 取出类型名、包、对象字段。不推断 required / example。
 * <p>
 * 类型名与源码展示一致：{@code List<User>} → {@code List<User>}，{@code User[]} → {@code User[]}。
     * {@code Optional<T>} 剥开后按 T 处理；{@code Mono<T>} 按 T 当对象展开；{@code Flux<T>} 按数组展开。
     * {@code Map<K,V>} 生成 {@code key} 示例而不是空对象。
 * 展开字段时会先解开集合 / 数组再取元素类型，例如 {@code List<User>} 展开的是 User 的字段。
 */
public final class TypeParser {

    /** 序列化字段，展开对象属性时跳过。 */
    private static final String SERIAL_VERSION_UID = "serialVersionUID";

    /** Map 示例 JSON 的占位 key。 */
    private static final String MAP_EXAMPLE_KEY = "key";

    /** 工具类，禁止实例化。 */
    private TypeParser() {}

    /**
     * 展示类型名，与源码写法一致；{@code Optional<T>} 剥开后展示 T。
     * {@code List<User>} → {@code List<User>}；{@code User[]} → {@code User[]}；{@code int} → {@code int}。
     * {@code Optional<User>} → {@code User}；裸 {@code Optional} 仍为 {@code Optional}。
     *
     * @param type PSI 类型，为 {@code null} 时返回 {@code null}
     * @return 展示类型名
     */
    public static String name(PsiType type) {
        if (type == null) {
            return null;
        }
        return peelOptional(type, Collections.<String, PsiType>emptyMap()).getPresentableText();
    }

    /**
     * 类型所在包。先解开集合 / 数组再取元素类型的包。
     * {@code List<User>}、{@code User[]} 均返回 User 的包；基本类型返回 {@code null}。
     *
     * @param type PSI 类型
     * @return 包名；基本类型或解析不到时为 {@code null}
     */
    public static String packageName(PsiType type) {
        if (type == null) {
            return null;
        }
        PsiClass psiClass = PsiUtil.resolveClassInType(unwrap(peelOptional(type, Collections.<String, PsiType>emptyMap())));
        if (psiClass == null) {
            return null;
        }
        return packageOf(psiClass.getQualifiedName());
    }

    /**
     * 对象类型的字段列表。叶子类型（基本类型、常见 JDK 类型、枚举、HTTP/Servlet/IO、Reactor、流等）返回空列表。
     * 展开时跳过 {@code static} 字段；入参与返回值共用此方法。
     * {@code List<User>}、{@code User[]} 会解开后展开 User 的字段。
     * {@code Optional<User>} 剥开泛型后按 User 展开；裸 {@code Optional} 不展开。
     * {@code Mono<User>} 按 User 展开；{@code Flux<User>} 解开后按 User 展开（JSON 侧再包数组）。
     * {@code Map<String, User>} 生成一条 {@code key → User} 的示例，不展开 Map 自身字段。
     * {@code Map<String, Map<String, User>>} 对值类型里的 Map 继续递归，得到多层 {@code key}。
     *
     * @param type PSI 类型
     * @return 字段定义；{@code type} 为 {@code null} 或叶子类型时为空列表
     */
    public static List<PropertyDefinition> properties(PsiType type) {
        if (type == null) {
            return Collections.emptyList();
        }
        return properties(type, new LinkedHashSet<String>(), genericsOf(type));
    }

    /**
     * 递归展开对象字段。跳过 {@code static} 字段（含接口常量和 {@code serialVersionUID}），
     * 入参与返回值走同一套展开，因此两边都不会出现静态字段。
     *
     * @param type     当前类型
     * @param visiting 当前解析链上的全限定名，用于检测循环引用
     * @param generics 外层已绑定的类型变量 → 实际类型
     * @return 字段定义；叶子类型或循环引用时为空列表
     */
    private static List<PropertyDefinition> properties(PsiType type, Set<String> visiting, Map<String, PsiType> generics) {
        if (type == null) {
            return Collections.emptyList();
        }
        PsiType resolved = peelMono(peelOptional(resolveGeneric(type, generics), generics), generics);
        if (resolved == null) {
            return Collections.emptyList();
        }
        if (TypeUtils.isMapType(resolved)) {
            return mapExampleProperties(resolved, visiting, generics);
        }
        if (isLeaf(resolved)) {
            return Collections.emptyList();
        }
        PsiType real = peelMono(peelOptional(resolveGeneric(unwrap(resolved), generics), generics), generics);
        if (real == null) {
            return Collections.emptyList();
        }
        if (TypeUtils.isMapType(real)) {
            return mapExampleProperties(real, visiting, generics);
        }
        if (isLeaf(real) || TypeUtils.isOptionalType(real)) {
            return Collections.emptyList();
        }
        PsiClass psiClass = PsiUtil.resolveClassInType(real);
        if (psiClass == null || psiClass.isEnum() || psiClass.isAnnotationType()) {
            return Collections.emptyList();
        }
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName != null && !visiting.add(qualifiedName)) {
            return Collections.emptyList();
        }
        Map<String, PsiType> childGenerics = mergeGenerics(real, generics);
        List<PropertyDefinition> result = new ArrayList<PropertyDefinition>();
        for (PsiField field : psiClass.getAllFields()) {
            if (isStatic(field) || SERIAL_VERSION_UID.equals(field.getName())) {
                continue;
            }
            result.add(fromField(field, visiting, childGenerics));
        }
        if (qualifiedName != null) {
            visiting.remove(qualifiedName);
        }
        return result;
    }

    /**
     * 将字段转为属性定义；若字段类型已在解析链上，则标记循环并不再展开子字段。
     *
     * @param field     PSI 字段
     * @param visiting  当前解析链
     * @param generics  当前类型的泛型绑定
     * @return 属性定义
     */
    private static PropertyDefinition fromField(PsiField field, Set<String> visiting, Map<String, PsiType> generics) {
        PsiType fieldType = peelOptional(resolveGeneric(field.getType(), generics), generics);
        boolean cycle = cyclic(fieldType, visiting);
        // Map 只取 V 做示例，不要把 Map 自己的 K/V 写进泛型表，避免盖住外层类的同名类型变量
        Map<String, PsiType> childGenerics = TypeUtils.isMapType(fieldType)
                ? generics
                : mergeGenerics(fieldType, generics);
        List<PropertyDefinition> children = cycle
                ? Collections.emptyList()
                : properties(fieldType, visiting, childGenerics);
        return new PropertyDefinition(
                field.getName(),
                name(fieldType),
                packageName(fieldType),
                AnnotationParser.of(field),
                CommentParser.text(field),
                children,
                cycle,
                enumConstantNames(fieldType),
                field
        );
    }

    /**
     * 枚举类型的常量名，按源码声明顺序。非枚举为空列表。
     * {@code List&lt;Status&gt;} 会先解开再取 Status 的常量。
     *
     * @param type PSI 类型
     * @return 常量名；非枚举为空列表
     */
    public static List<String> enumConstantNames(PsiType type) {
        PsiClass psiClass = PsiUtil.resolveClassInType(unwrap(peelOptional(type, Collections.<String, PsiType>emptyMap())));
        if (psiClass == null || !psiClass.isEnum()) {
            return Collections.emptyList();
        }
        List<String> names = new ArrayList<String>();
        for (PsiField field : psiClass.getFields()) {
            if (field instanceof PsiEnumConstant) {
                names.add(field.getName());
            }
        }
        return names;
    }

    /**
     * 解开集合 / 数组后的真实类型已在当前解析链上，视为循环引用。
     *
     * @param type     字段类型
     * @param visiting 当前解析链上的全限定名
     * @return 已在链上则为 {@code true}
     */
    private static boolean cyclic(PsiType type, Set<String> visiting) {
        if (type == null || visiting == null || visiting.isEmpty()) {
            return false;
        }
        PsiClass psiClass = PsiUtil.resolveClassInType(unwrap(type));
        if (psiClass == null) {
            return false;
        }
        String qualifiedName = psiClass.getQualifiedName();
        return qualifiedName != null && visiting.contains(qualifiedName);
    }

    /**
     * 是否为不再展开字段的叶子类型。
     *
     * @param type PSI 类型
     * @return 基本类型、常见 JDK 类型、枚举、上传 / HTTP / Servlet / IO、裸 Mono/Flux 等为 {@code true}
     */
    private static boolean isLeaf(PsiType type) {
        PsiType real = unwrap(type);
        return TypeUtils.isPrimitive(real)
                || TypeUtils.isNormalType(real)
                || TypeUtils.isEnum(real)
                || TypeUtils.isMultipartType(real)
                || TypeUtils.isHttpType(real)
                || TypeUtils.isServletType(real)
                || TypeUtils.isIOType(real)
                || TypeUtils.isStreamType(real)
                || TypeUtils.isVoidType(real)
                || TypeUtils.isMonoType(real)
                || TypeUtils.isFluxType(real);
    }

    /**
     * 连续剥开 {@code Optional<T>}。没有泛型实参（裸 {@code Optional} / {@code Optional<?>}）时停止，留给调用方当叶子。
     */
    private static PsiType peelOptional(PsiType type, Map<String, PsiType> generics) {
        return peelSingleArgWrapper(type, generics, true, false);
    }

    /**
     * 连续剥开 {@code Mono<T>}。裸 Mono 停止。
     */
    private static PsiType peelMono(PsiType type, Map<String, PsiType> generics) {
        return peelSingleArgWrapper(type, generics, false, true);
    }

    private static PsiType peelSingleArgWrapper(PsiType type, Map<String, PsiType> generics,
                                                boolean optional, boolean mono) {
        PsiType current = type;
        while ((optional && TypeUtils.isOptionalType(current)) || (mono && TypeUtils.isMonoType(current))) {
            PsiType inner = TypeUtils.firstTypeArgument(current);
            if (inner == null) {
                break;
            }
            inner = boundOf(inner);
            if (inner == null) {
                break;
            }
            PsiType resolved = resolveGeneric(inner, generics);
            if (resolved == current) {
                break;
            }
            current = resolved;
        }
        return current;
    }

    /**
     * {@code Map<K, V>} 生成一条示例 entry：JSON 键固定为 {@code key}，值按 V 展开。
     * V 仍是 Map 时继续递归，因此 {@code Map<String, Map<String, User>>} 会得到两层 {@code key}。
     * 没有 V（裸 Map）返回空列表，调用方会序列化成 {@code {}}。
     */
    private static List<PropertyDefinition> mapExampleProperties(PsiType mapType, Set<String> visiting,
                                                                Map<String, PsiType> generics) {
        PsiType valueType = mapValueType(mapType);
        if (valueType == null) {
            return Collections.emptyList();
        }
        valueType = peelMono(peelOptional(resolveGeneric(valueType, generics), generics), generics);
        boolean cycle = cyclic(valueType, visiting);
        Map<String, PsiType> valueGenerics = TypeUtils.isMapType(valueType)
                ? generics
                : mergeGenerics(valueType, generics);
        List<PropertyDefinition> valueProperties = cycle
                ? Collections.emptyList()
                : properties(valueType, visiting, valueGenerics);
        PropertyDefinition example = new PropertyDefinition(
                MAP_EXAMPLE_KEY,
                name(valueType),
                packageName(valueType),
                Collections.emptyList(),
                null,
                valueProperties,
                cycle,
                enumConstantNames(valueType),
                null
        );
        return Collections.singletonList(example);
    }

    /**
     * Map 的值类型 V；参数不足 2 个视为裸 Map。
     */
    private static PsiType mapValueType(PsiType type) {
        if (!(type instanceof PsiClassType)) {
            return null;
        }
        PsiType[] parameters = ((PsiClassType) type).getParameters();
        if (parameters.length < 2) {
            return null;
        }
        return boundOf(parameters[1]);
    }

    /**
     * 通配符取上界 / 下界；无界 {@code ?} 视为无法展开。
     */
    private static PsiType boundOf(PsiType type) {
        if (!(type instanceof PsiWildcardType)) {
            return type;
        }
        PsiWildcardType wildcard = (PsiWildcardType) type;
        PsiType bound = wildcard.getBound();
        return bound == null ? null : bound;
    }

    /**
     * 解开集合 / 数组，取出最内层元素类型。
     * {@code List<User>}、{@code User[]} 均得到 User。
     *
     * @param type 原始类型
     * @return 解开后的类型；无法解开时返回原类型
     */
    private static PsiType unwrap(PsiType type) {
        if (type == null) {
            return null;
        }
        TypeUtils.NestedInfo nested = TypeUtils.deepExtractIterableType(type);
        return nested.getRealType() == null ? type : nested.getRealType();
    }

    /**
     * 读取当前类型自身的泛型绑定。
     *
     * @param type PSI 类型
     * @return 类型变量名 → 实际类型
     */
    private static Map<String, PsiType> genericsOf(PsiType type) {
        return mergeGenerics(type, Collections.<String, PsiType>emptyMap());
    }

    /**
     * 把当前类型的泛型实参叠到外层绑定上。
     * 例如 {@code Page<User>} 在已有 {@code T=User} 时，再解析 Page 内部字段仍能替换 T。
     *
     * @param type   当前类型
     * @param parent 外层已绑定的类型变量
     * @return 合并后的泛型表
     */
    private static Map<String, PsiType> mergeGenerics(PsiType type, Map<String, PsiType> parent) {
        Map<String, PsiType> merged = new HashMap<String, PsiType>(parent);
        PsiType resolved = type instanceof PsiArrayType ? ((PsiArrayType) type).getComponentType() : type;
        if (!(resolved instanceof PsiClassType)) {
            return merged;
        }
        PsiClassType classType = (PsiClassType) resolved;
        PsiClass psiClass = classType.resolve();
        if (psiClass == null) {
            return merged;
        }
        PsiTypeParameter[] parameters = psiClass.getTypeParameters();
        PsiType[] actuals = classType.getParameters();
        int len = Math.min(parameters.length, actuals.length);
        for (int i = 0; i < len; i++) {
            merged.put(parameters[i].getName(), resolveGeneric(actuals[i], parent));
        }
        return merged;
    }

    /**
     * 若 {@code type} 含已绑定的类型变量，替换为实际类型。
     * {@code T} → {@code FormPageInfo}；{@code List<T>} → {@code List<FormPageInfo>}。
     *
     * @param type     待替换类型
     * @param generics 类型变量 → 实际类型
     * @return 替换后的类型；未命中则原样返回
     */
    private static PsiType resolveGeneric(PsiType type, Map<String, PsiType> generics) {
        if (type == null || generics.isEmpty()) {
            return type;
        }
        PsiType mapped = generics.get(type.getPresentableText());
        if (mapped != null) {
            return mapped;
        }
        if (type instanceof PsiArrayType) {
            PsiType component = ((PsiArrayType) type).getComponentType();
            PsiType resolved = resolveGeneric(component, generics);
            return resolved == component ? type : resolved.createArrayType();
        }
        if (!(type instanceof PsiClassType)) {
            return type;
        }
        PsiClassType classType = (PsiClassType) type;
        PsiClass psiClass = classType.resolve();
        if (psiClass instanceof PsiTypeParameter) {
            mapped = generics.get(psiClass.getName());
            return mapped == null ? type : mapped;
        }
        PsiType[] actuals = classType.getParameters();
        if (psiClass == null || actuals.length == 0) {
            return type;
        }
        boolean changed = false;
        PsiType[] substituted = new PsiType[actuals.length];
        for (int i = 0; i < actuals.length; i++) {
            substituted[i] = resolveGeneric(actuals[i], generics);
            if (substituted[i] != actuals[i]) {
                changed = true;
            }
        }
        if (!changed) {
            return type;
        }
        return JavaPsiFacade.getElementFactory(psiClass.getProject()).createType(psiClass, substituted);
    }

    /**
     * 从全限定名截取包名。
     *
     * @param qualifiedName 全限定名，如 {@code com.example.User}
     * @return 包名；无包或空白时为 {@code null}
     */
    static String packageOf(String qualifiedName) {
        if (StringUtils.isBlank(qualifiedName)) {
            return null;
        }
        int dot = qualifiedName.lastIndexOf(CharacterPool.DOT);
        return dot < 0 ? null : qualifiedName.substring(0, dot);
    }

    /**
     * 字段是否为 static（含 {@code serialVersionUID} 之外的静态常量）。
     *
     * @param field PSI 字段
     * @return 带 {@code static} 修饰则为 {@code true}
     */
    private static boolean isStatic(PsiField field) {
        PsiModifierList modifiers = field.getModifierList();
        return modifiers != null && modifiers.hasModifierProperty(PsiModifier.STATIC);
    }
}
