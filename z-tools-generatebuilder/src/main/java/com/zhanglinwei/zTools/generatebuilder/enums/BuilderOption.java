package com.zhanglinwei.zTools.generatebuilder.enums;

import com.intellij.ide.util.PropertiesComponent;

import java.util.EnumSet;
import java.util.Set;

/**
 * 生成 Builder 时的可选项。
 * <p>
 * 存储 key 是 {@link #name()}（如 {@code USE_LITE_BUILDER}），换标题不会丢用户勾选。
 * 目前只有「lite」一项：不生成 {@code C/B} 泛型，适合不想被继承的简单类。
 */
public enum BuilderOption {

    USE_LITE_BUILDER("use lite builder", "轻量级Builder（无泛型/继承，适配Record/简单类）", true),
    ;

    private final String title;
    private final String tooltip;
    private final boolean checkBox;

    BuilderOption(String title, String tooltip, boolean checkBox) {
        this.title = title;
        this.tooltip = tooltip;
        this.checkBox = checkBox;
    }

    /** 对话框上显示的短标题。 */
    public String title() {
        return title;
    }

    /** 鼠标悬停说明。 */
    public String tooltip() {
        return tooltip;
    }

    /** 是否在 MemberChooser 底部画成复选框；false 的项只占位、不展示。 */
    public boolean checkBox() {
        return checkBox;
    }

    /** 未存过时默认未勾选。 */
    public boolean isSelected() {
        return PropertiesComponent.getInstance().getBoolean(name(), false);
    }

    /** 存成 {@code "true"}/{@code "false"} 字符串，和历史 PropertiesComponent 用法一致。 */
    public void setSelected(boolean selected) {
        PropertiesComponent.getInstance().setValue(name(), Boolean.toString(selected));
    }

    /** 当前已勾选、且会画成复选框的选项。 */
    public static Set<BuilderOption> currentlySelected() {
        Set<BuilderOption> selected = EnumSet.noneOf(BuilderOption.class);
        for (BuilderOption option : values()) {
            if (option.checkBox() && option.isSelected()) {
                selected.add(option);
            }
        }
        return selected;
    }
}
