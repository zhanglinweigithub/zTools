package com.zhanglinwei.zTools.generatebuilder;

import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.NonFocusableCheckBox;
import com.zhanglinwei.zTools.generatebuilder.enums.BuilderOption;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public final class BuilderOptionSelector {

    @Nullable
    public static List<PsiFieldMember> selectFieldsAndOptions(PsiFieldMember[] members, Project project) {
        if (members == null || members.length == 0) {
            return null;
        }

        JComponent[] builderOptions = createBuilderOptions();
        MemberChooser<PsiFieldMember> chooser = new MemberChooser<>(
                members, false, true, project,
                null, builderOptions
        );

        chooser.setTitle("Select Fields and Options for the Builder");
        chooser.selectElements(members);
        chooser.setCopyJavadocVisible(false);
        if (chooser.showAndGet()) {
            return chooser.getSelectedElements();
        }

        return null;
    }

    private static JComponent[] createBuilderOptions() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        BuilderOption[] builderOptions = BuilderOption.values();

        JComponent[] components = new JComponent[builderOptions.length];
        for (int i = 0; i < builderOptions.length; i++) {
            BuilderOption builderOption = builderOptions[i];

            if (builderOption.checkBox) {
                components[i] = buildCheckbox(propertiesComponent, builderOption);
            }
        }

        return components;
    }

    private static JComponent buildCheckbox(PropertiesComponent propertiesComponent, BuilderOption builderOption) {
        JCheckBox optionCheckBox = new NonFocusableCheckBox(builderOption.title);
        optionCheckBox.setToolTipText(builderOption.tooltip);

        optionCheckBox.setSelected(propertiesComponent.isTrueValue(builderOption.name()));
        optionCheckBox.addItemListener(event -> propertiesComponent.setValue(builderOption.name(), Boolean.toString(optionCheckBox.isSelected())));
        return optionCheckBox;
    }

}

