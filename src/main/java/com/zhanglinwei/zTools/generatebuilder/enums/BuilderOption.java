package com.zhanglinwei.zTools.generatebuilder.enums;

public enum BuilderOption {

    USE_LITE_BUILDER("useLiteBuilder", "轻量级Builder（无泛型/继承，适配Record/简单类）", true),

    ;

    public final boolean checkBox;
    public final String title;
    public final String tooltip;

    BuilderOption(String title, String tooltip, boolean checkBox) {
        this.title = title;
        this.tooltip = tooltip;
        this.checkBox = checkBox;
    }

}
