package com.zhanglinwei.zTools.generatebuilder.field;

import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.project.Project;
import com.intellij.ui.NonFocusableCheckBox;
import com.zhanglinwei.zTools.generatebuilder.enums.BuilderOption;
import org.jetbrains.annotations.Nullable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import java.util.List;

/**
 * 字段选择对话框（IDEA MemberChooser）。
 * <p>
 * 下方复选框对应 {@link BuilderOption}，勾选会立刻写入 {@code PropertiesComponent}，
 * 生成时 {@link com.zhanglinwei.zTools.generatebuilder.generator.BuilderGenerator} 再读一遍。
 * 点取消或关掉窗口返回 {@code null}，调用方应中止生成。
 */
public final class BuilderFieldChooser {

    private BuilderFieldChooser() {}

    /**
     * @return 用户勾选的字段；取消对话框时为 {@code null}
     */
    @Nullable
    public static List<PsiFieldMember> choose(PsiFieldMember[] members, Project project) {
        if (members == null || members.length == 0) {
            return null;
        }

        MemberChooser<PsiFieldMember> chooser = new MemberChooser<>(
                members, false, true, project, null, optionCheckBoxes());
        chooser.setTitle("Select Fields and Options for the Builder");
        chooser.selectElements(members);
        chooser.setCopyJavadocVisible(false);
        if (chooser.showAndGet()) {
            return chooser.getSelectedElements();
        }
        return null;
    }

    /** 与枚举顺序对齐；非 checkBox 的选项位置保持 null，和原先 MemberChooser 用法一致。 */
    private static JComponent[] optionCheckBoxes() {
        BuilderOption[] options = BuilderOption.values();
        JComponent[] components = new JComponent[options.length];
        for (int i = 0; i < options.length; i++) {
            if (options[i].checkBox()) {
                components[i] = checkBox(options[i]);
            }
        }
        return components;
    }

    /** 勾选变化马上持久化，生成阶段不再问一遍对话框。 */
    private static JComponent checkBox(BuilderOption option) {
        JCheckBox box = new NonFocusableCheckBox(option.title());
        box.setToolTipText(option.tooltip());
        box.setSelected(option.isSelected());
        box.addItemListener(event -> option.setSelected(box.isSelected()));
        return box;
    }
}
