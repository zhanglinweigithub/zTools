package com.zhanglinwei.zTools.generatebuilder.generator;

import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.PsiTypeParameterList;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.generatebuilder.psi.PsiClasses;
import com.zhanglinwei.zTools.generatebuilder.enums.BuilderFlavor;
import com.zhanglinwei.zTools.generatebuilder.enums.BuilderOption;
import com.zhanglinwei.zTools.generatebuilder.psi.PsiParameterLists;
import com.zhanglinwei.zTools.common.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.common.constant.StringPool.COMMA_SPACE;
import static com.zhanglinwei.zTools.common.constant.StringPool.DOT;

/**
 * 在写操作里往目标类插入内部 {@code Builder}。
 * <p>
 * 顺序：找或创建静态内部类 Builder → 字段 → 链式 setter（方法名与字段同名）→
 * {@code build()} → 外层全参构造 → 静态 {@code builder()} → 缩短引用并格式化。
 * 已有同名字段/同签名方法不会重复插入；{@code build()} 和 {@code builder()} 允许替换。
 */
public final class BuilderGenerator implements Runnable {

    private static final String BUILDER_CLASS_NAME = "Builder";
    /** 可继承形态里表示「外层类型」的类型参数。 */
    private static final String CLASS_GENERIC = "C";
    /** 可继承形态里表示「Builder 自身」的类型参数，用于 setter 返回 {@code (B) this}。 */
    private static final String BUILDER_GENERIC = "B";

    private final Project project;
    private final PsiFile file;
    private final Editor editor;
    private final List<PsiFieldMember> selectedFields;
    private final PsiElementFactory psiElementFactory;

    /**
     * 入口：确认光标在类上后，把自身丢进写操作。Handler 侧已经是读线程。
     * 目标形态见 {@link BuilderFlavor}。
     *
     * @param project        当前项目
     * @param editor         当前编辑器
     * @param file           当前 PSI 文件
     * @param selectedFields 对话框勾选的字段；Record 时为空列表
     */
    public static void generate(Project project, Editor editor, PsiFile file, List<PsiFieldMember> selectedFields) {
        PsiClass targetClass = PsiClasses.contextClass(editor, file);
        if (targetClass == null) {
            return;
        }
        ApplicationManager.getApplication().runWriteAction(
                new BuilderGenerator(project, file, editor, selectedFields));
    }

    /**
     * @param project        当前项目
     * @param file           当前文件
     * @param editor         当前编辑器
     * @param selectedFields 用户勾选的字段
     */
    private BuilderGenerator(Project project, PsiFile file, Editor editor, List<PsiFieldMember> selectedFields) {
        this.project = project;
        this.file = file;
        this.editor = editor;
        this.selectedFields = selectedFields;
        this.psiElementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
    }

    /**
     * 按选定字段与形态插入内部 Builder，并格式化。
     * 目标形态见 {@link BuilderFlavor}。
     */
    @Override
    public void run() {
        PsiClass targetClass = PsiClasses.contextClass(editor, file);
        if (targetClass == null) {
            return;
        }

        // 根据是否 Record、是否勾选 lite 决定生成形态
        Set<BuilderOption> options = BuilderOption.currentlySelected();
        BuilderFlavor flavor = BuilderFlavor.of(targetClass, options);
        List<PsiField> sourceFields = sourceFields(targetClass, flavor);

        // 静态内部类 → 字段 → 同名链式 setter → build() → 外层全参构造 → 静态 builder()
        PsiClass builderClass = findOrCreateBuilderClass(targetClass, flavor);
        addBuilderFields(builderClass, sourceFields, flavor.fieldModifier());
        PsiElement lastSetter = addBuilderSetters(builderClass, sourceFields, flavor);
        addMethod(builderClass, lastSetter, createBuildMethod(targetClass, builderClass, flavor), true);
        addMethod(targetClass, null, createAllArgsConstructor(targetClass, builderClass), false);
        addMethod(targetClass, null, createNewBuilderMethod(targetClass, builderClass, flavor), true);

        // 缩短 import 并按项目代码风格格式化 Builder
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(file);
        CodeStyleManager.getInstance(project).reformat(builderClass);
    }

    /**
     * Record 用类上全部字段；否则用对话框勾选的字段（Record 时该列表为空）。
     *
     * @param targetClass 外层类
     * @param flavor      生成形态
     * @return 将写入 Builder 的源字段
     */
    private List<PsiField> sourceFields(PsiClass targetClass, BuilderFlavor flavor) {
        if (flavor.usesAllClassFields()) {
            return Arrays.asList(targetClass.getAllFields());
        }
        List<PsiField> fields = new ArrayList<>(selectedFields.size());
        for (PsiFieldMember member : selectedFields) {
            fields.add(member.getElement());
        }
        return fields;
    }

    /**
     * 按源字段顺序插入；已存在同名字段则跳过，并把「上一个锚点」指到已有字段。
     *
     * @param builderClass 内部 Builder
     * @param sourceFields 源字段
     * @param modifier     字段修饰符；Record 为 {@code null}
     */
    private void addBuilderFields(PsiClass builderClass, List<PsiField> sourceFields, String modifier) {
        PsiElement lastField = null;
        for (PsiField source : sourceFields) {
            lastField = addField(builderClass, createBuilderField(source.getName(), source.getType(), modifier), lastField);
        }
    }

    /**
     * 可继承：setter 返回 {@code B} 并 {@code return (B) this;}；
     * lite/Record：返回 {@code Builder} 并 {@code return this;}。
     *
     * @param builderClass 内部 Builder
     * @param sourceFields 源字段
     * @param flavor       生成形态
     * @return 最后一个插入的 setter，作为 {@code build()} 的锚点
     */
    private PsiElement addBuilderSetters(PsiClass builderClass, List<PsiField> sourceFields, BuilderFlavor flavor) {
        PsiType returnType;
        String returnStatement;
        if (flavor.usesGenerics()) {
            returnType = psiElementFactory.createTypeFromText(BUILDER_GENERIC, null);
            returnStatement = "return (" + BUILDER_GENERIC + ") this;";
        } else {
            returnType = psiElementFactory.createTypeFromText(builderClass.getName(), null);
            returnStatement = "return this;";
        }

        PsiElement lastAdded = null;
        for (PsiField source : sourceFields) {
            lastAdded = addMethod(builderClass, lastAdded, createSetter(returnType, source, returnStatement), false);
        }
        return lastAdded;
    }

    /**
     * 外层类上的静态工厂。可继承返回 {@code Builder<?, ?>} 并 {@code new Builder<>()}，
     * 否则返回 {@code Builder} 并 {@code new Builder()}。
     *
     * @param targetClass  外层类
     * @param builderClass 内部 Builder
     * @param flavor       生成形态
     * @return 静态 {@code builder()} 方法
     */
    private PsiMethod createNewBuilderMethod(PsiClass targetClass, PsiClass builderClass, BuilderFlavor flavor) {
        PsiType returnType;
        String body;
        if (flavor.usesGenerics()) {
            returnType = psiElementFactory.createTypeFromText(builderClass.getName() + "<?, ?>", targetClass);
            body = "return new " + builderClass.getName() + "<>();";
        } else {
            returnType = psiElementFactory.createTypeFromText(builderClass.getName(), null);
            body = String.format("return new %s();", returnType.getPresentableText());
        }

        PsiMethod method = psiElementFactory.createMethod("builder", returnType);
        PsiUtil.setModifierProperty(method, PsiModifier.STATIC, true);
        PsiUtil.setModifierProperty(method, PsiModifier.PUBLIC, true);
        addStatement(method, body);
        return method;
    }

    /**
     * modifier 为 null 时保持 {@code createField} 的默认可见性（Record 用）。
     *
     * @param fieldName 字段名
     * @param fieldType 字段类型
     * @param modifier  PSI 修饰符；{@code null} 表示不加
     * @return 新建的 Builder 字段
     */
    private PsiField createBuilderField(String fieldName, PsiType fieldType, String modifier) {
        PsiField field = psiElementFactory.createField(fieldName, fieldType);
        if (StringUtils.isNotBlank(modifier)) {
            PsiUtil.setModifierProperty(field, modifier, true);
        }
        return field;
    }

    /**
     * 方法名与字段名相同，不是 {@code setXxx}。
     *
     * @param builderType     setter 返回类型
     * @param psiField        对应源字段
     * @param returnStatement {@code return this;} 或 {@code return (B) this;}
     * @return 链式 setter
     */
    private PsiMethod createSetter(PsiType builderType, PsiField psiField, String returnStatement) {
        PsiMethod setter = psiElementFactory.createMethod(psiField.getName(), builderType);
        setter.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);
        setter.getParameterList().add(psiElementFactory.createParameter(psiField.getName(), psiField.getType()));

        PsiCodeBlock body = setter.getBody();
        if (body != null) {
            body.add(psiElementFactory.createStatementFromText(
                    String.format("this.%s = %s;", psiField.getName(), psiField.getName()), setter));
            body.add(psiElementFactory.createStatementFromText(returnStatement, null));
        }
        return setter;
    }

    /**
     * 加在外层类上，参数顺序与 Builder 字段一致，供 {@code build()} 里 {@code new Outer(...)} 使用。
     * 同签名构造已存在则不替换。
     *
     * @param targetClass  外层类
     * @param builderClass 内部 Builder
     * @return 全参构造方法
     */
    private PsiMethod createAllArgsConstructor(PsiClass targetClass, PsiClass builderClass) {
        PsiMethod constructor = psiElementFactory.createConstructor(targetClass.getName());
        constructor.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);

        PsiField[] builderFields = builderClass.getAllFields();
        for (PsiField field : builderFields) {
            constructor.getParameterList().add(psiElementFactory.createParameter(field.getName(), field.getType()));
        }

        PsiCodeBlock body = constructor.getBody();
        if (body != null) {
            for (PsiField field : builderFields) {
                body.add(psiElementFactory.createStatementFromText(
                        String.format("this.%s = %s;", field.getName(), field.getName()), null));
            }
        }
        return constructor;
    }

    /**
     * 可继承返回 {@code C} 并带强制转换，便于子类 Builder 覆写后仍能当子类型用；
     * lite/Record 直接 {@code return new Outer(fields...)}。
     *
     * @param targetClass  外层类
     * @param builderClass 内部 Builder
     * @param flavor       生成形态
     * @return {@code build()} 方法
     */
    private PsiMethod createBuildMethod(PsiClass targetClass, PsiClass builderClass, BuilderFlavor flavor) {
        PsiType returnType = flavor.usesGenerics()
                ? psiElementFactory.createTypeFromText(CLASS_GENERIC, null)
                : psiElementFactory.createType(targetClass);

        PsiMethod build = psiElementFactory.createMethod("build", returnType);
        build.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);

        PsiCodeBlock body = build.getBody();
        if (body != null) {
            String fieldNames = Arrays.stream(builderClass.getAllFields())
                    .map(PsiField::getName)
                    .collect(Collectors.joining(COMMA_SPACE));
            String statement = flavor.usesGenerics()
                    ? String.format("return (" + CLASS_GENERIC + ") new %s(%s);", targetClass.getName(), fieldNames)
                    : String.format("return new %s(%s);", targetClass.getName(), fieldNames);
            body.add(psiElementFactory.createStatementFromText(statement, build));
        }
        return build;
    }

    /**
     * 已有内部类 Builder 则复用（不再补泛型），否则新建 static 内部类。
     *
     * @param targetClass 外层类
     * @param flavor      生成形态，决定是否加 C/B 泛型
     * @return 内部 Builder 类
     */
    private PsiClass findOrCreateBuilderClass(PsiClass targetClass, BuilderFlavor flavor) {
        PsiClass builderClass = targetClass.findInnerClassByName(BUILDER_CLASS_NAME, false);
        if (builderClass != null) {
            return builderClass;
        }

        builderClass = (PsiClass) targetClass.add(psiElementFactory.createClass(BUILDER_CLASS_NAME));
        PsiUtil.setModifierProperty(builderClass, PsiModifier.STATIC, true);
        if (flavor.usesGenerics()) {
            addInheritableGenerics(builderClass, targetClass);
        }
        return builderClass;
    }

    /**
     * 用带约束的方法签名解析类型参数，避免 {@code createTypeParameter} 丢掉
     * {@code B extends Outer.Builder<C, B>} 这种自引用边界。
     *
     * @param builderClass 新建的内部 Builder
     * @param targetClass  外层类，用于拼 {@code C extends Outer}
     */
    private void addInheritableGenerics(PsiClass builderClass, PsiClass targetClass) {
        PsiTypeParameterList typeParameterList = builderClass.getTypeParameterList();
        if (typeParameterList == null) {
            typeParameterList = psiElementFactory.createTypeParameterList();
            builderClass.add(typeParameterList);
        }

        String outerName = targetClass.getName();
        String methodText = "public <" + CLASS_GENERIC + " extends " + outerName + ", " + BUILDER_GENERIC
                + " extends " + outerName + "." + BUILDER_CLASS_NAME + "<" + CLASS_GENERIC + ", "
                + BUILDER_GENERIC + ">> void _() {}";
        PsiMethod dummyMethod = psiElementFactory.createMethodFromText(methodText, targetClass);
        for (PsiTypeParameter parameter : dummyMethod.getTypeParameters()) {
            typeParameterList.add(parameter.copy());
        }
    }

    /**
     * 方法体存在才插入，避免接口/抽象方法 NPE。
     *
     * @param method    目标方法
     * @param statement 语句文本
     */
    private void addStatement(PsiMethod method, String statement) {
        PsiCodeBlock body = method.getBody();
        if (body != null) {
            body.add(psiElementFactory.createStatementFromText(statement, method));
        }
    }

    /**
     * 同名字段已存在则当作锚点，避免重复声明。
     *
     * @param targetClass 插入目标（内部 Builder）
     * @param field       待插入字段
     * @param after       插在该元素之后；{@code null} 时由 PSI 决定位置
     * @return 实际存在的字段（新建或已有），作为下一字段的锚点
     */
    private PsiElement addField(PsiClass targetClass, PsiField field, PsiElement after) {
        PsiField existing = targetClass.findFieldByName(field.getName(), false);
        if (existing != null) {
            return existing;
        }
        return targetClass.addAfter(field, after);
    }

    /**
     * @param targetClass 插入目标类
     * @param after       插在该元素之后；{@code null} 时追加到类末尾
     * @param newMethod   待插入方法
     * @param replace     {@code true} 时覆盖已有同签名方法（{@code build}/{@code builder}）；
     *                    {@code false} 时保留用户已写的构造或 setter
     * @return 插入或已有的方法元素，作为后续插入锚点
     */
    private PsiElement addMethod(PsiClass targetClass, PsiElement after, PsiMethod newMethod, boolean replace) {
        PsiMethod existing = findSameSignature(targetClass, newMethod);
        if (existing == null) {
            return after != null ? targetClass.addAfter(newMethod, after) : targetClass.add(newMethod);
        }
        if (replace) {
            try {
                existing.replace(newMethod);
            } catch (Exception ignored) {
                // 与原先行为一致：替换失败时保留已有方法
            }
        }
        return existing;
    }

    /**
     * 构造器按参数列表比；普通方法先按名字再按参数列表。
     *
     * @param targetClass 查找范围
     * @param newMethod   待匹配的新方法
     * @return 同签名已有方法；没有则为 {@code null}
     */
    private static PsiMethod findSameSignature(PsiClass targetClass, PsiMethod newMethod) {
        PsiMethod[] candidates = newMethod.isConstructor()
                ? targetClass.getConstructors()
                : targetClass.findMethodsByName(newMethod.getName(), false);
        for (PsiMethod candidate : candidates) {
            if (PsiParameterLists.equal(candidate.getParameterList(), newMethod.getParameterList())) {
                return candidate;
            }
        }
        return null;
    }
}
