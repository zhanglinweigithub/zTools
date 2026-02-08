package com.zhanglinwei.zTools.generatebuilder;

import com.intellij.codeInsight.generation.OverrideImplementUtil;
import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.lang.LanguageCodeInsightActionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import com.zhanglinwei.zTools.util.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.java.generate.exception.GenerateCodeException;

import java.util.ArrayList;
import java.util.List;

import static com.zhanglinwei.zTools.generatebuilder.BuilderOptionSelector.selectFieldsAndOptions;


public class GenerateBuilderHandler implements LanguageCodeInsightActionHandler {

    @Override
    public boolean isValidFor(final Editor editor, final PsiFile file) {
        Project project = editor.getProject();
        if (project == null) {
            return false;
        }

        if (!(file instanceof PsiJavaFile)) {
            return false;
        }

        PsiClass targetClass = OverrideImplementUtil.getContextClass(file.getProject(), editor, file, false);
        if (targetClass == null) {
            return false;
        }

        return getAllOriginalMembers(targetClass).length > 0;
    }

    private PsiFieldMember[] getAllOriginalMembers(PsiClass aClass) {
        List<PsiFieldMember> list = IGenerateAccessorProviderRegistrar.getEncapsulatableClassMembers(aClass);
        if (list.isEmpty()) {
            return new PsiFieldMember[0];
        } else {
            List<PsiFieldMember> members = ContainerUtil.findAll(list, (member) -> {
                try {
                    return this.canGenerate(member);
                } catch (GenerateCodeException var4) {
                    return true;
                } catch (IncorrectOperationException e) {
                    return false;
                }
            });
            return members.toArray(new PsiFieldMember[0]);
        }
    }

    private boolean canGenerate(PsiFieldMember original) {
        PsiField field = original.getElement();
        return !field.hasModifierProperty("static");
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public void invoke(@NotNull final Project project, @NotNull final Editor editor, @NotNull final PsiFile file) {
        if (!EditorModificationUtil.checkModificationAllowed(editor)) {
            return;
        }

        if (!FileDocumentManager.getInstance().requestWriting(editor.getDocument(), project)) {
            return;
        }

        PsiClass targetClass = OverrideImplementUtil.getContextClass(file.getProject(), editor, file, false);

        PsiFieldMember[] existingFields = getAllOriginalMembers(targetClass);
        if (existingFields.length != 0) {
            List<PsiFieldMember> selectedFields = new ArrayList<>();

            if (!ClassUtils.isRecordClass(targetClass)) {
                selectedFields = selectFieldsAndOptions(existingFields, project);

                if (selectedFields == null || selectedFields.isEmpty()) {
                    return;
                }
            }

            BuilderGenerator.generate(project, editor, file, selectedFields);
        }
    }

}
