package com.zhanglinwei.zTools.generatebuilder;

import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class BuilderOptionSelector {

    @Nullable
    public static List<PsiFieldMember> selectFieldsAndOptions(PsiFieldMember[] members, Project project) {
        if (members == null || members.length == 0) {
            return null;
        }

        MemberChooser<PsiFieldMember> chooser = new MemberChooser<>(
                members, false, true, project
        );

        chooser.setTitle("Select Fields and Options for the Builder");
        chooser.selectElements(members);
        chooser.setCopyJavadocVisible(false);
        if (chooser.showAndGet()) {
            return chooser.getSelectedElements();
        }

        return null;
    }

}

