package com.zhanglinwei.zTools.generatebuilder;

import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.lang.LanguageCodeInsightActionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.zhanglinwei.zTools.generatebuilder.field.BuilderFieldChooser;
import com.zhanglinwei.zTools.generatebuilder.field.BuilderFields;
import com.zhanglinwei.zTools.generatebuilder.generator.BuilderGenerator;
import com.zhanglinwei.zTools.generatebuilder.psi.PsiClasses;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate 菜单「Builder」的实际处理。
 * <p>
 * 流程：确认可写 → 取光标所在类 → 收集实例字段 → 普通类弹出字段/选项对话框，
 * Record 不弹窗、用全部组件字段 → {@link BuilderGenerator} 在写操作里改 PSI。
 * {@link #startInWriteAction()} 返回 false，因为弹窗必须在读线程，真正改代码由 Generator 自己开写操作。
 */
public class GenerateBuilderHandler implements LanguageCodeInsightActionHandler {

    /** Java 文件、能解析到类、且至少有一个非 static 字段时，菜单才可用。 */
    @Override
    public boolean isValidFor(Editor editor, PsiFile file) {
        if (editor.getProject() == null || !(file instanceof PsiJavaFile)) {
            return false;
        }
        PsiClass targetClass = PsiClasses.contextClass(editor, file);
        return targetClass != null && BuilderFields.hasInstanceFields(targetClass);
    }

    /**
     * 弹窗和写文件分开：这里不进写操作。
     */
    @Override
    public boolean startInWriteAction() {
        return false;
    }

    /**
     * 用户点了 Builder 之后：Record 直接生成；其它类取消对话框或一个字段都不选则中止。
     */
    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (!EditorModificationUtil.checkModificationAllowed(editor)) {
            return;
        }
        if (!FileDocumentManager.getInstance().requestWriting(editor.getDocument(), project)) {
            return;
        }

        PsiClass targetClass = PsiClasses.contextClass(editor, file);
        PsiFieldMember[] members = BuilderFields.instanceFields(targetClass);
        if (members.length == 0) {
            return;
        }

        List<PsiFieldMember> selectedFields = new ArrayList<>();
        if (!PsiClasses.isRecord(targetClass)) {
            selectedFields = BuilderFieldChooser.choose(members, project);
            if (selectedFields == null || selectedFields.isEmpty()) {
                return;
            }
        }

        BuilderGenerator.generate(project, editor, file, selectedFields);
    }
}
