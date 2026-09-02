package com.zhanglinwei.zTools.common.enums;

public enum DocumentType {

    MARKDOWN(".md", "MarkDown"),
    HTML(".html", "Html"),
    WORD(".doc", "Word"),
    ;

    private final String suffix;
    private final String type;

    DocumentType(String suffix, String type){
        this.suffix = suffix;
        this.type = type;
    }

    public static DocumentType of(String type) {
        if (type == null) {
            return MARKDOWN;
        }
        for (DocumentType doc : values()) {
            if (doc.type.equals(type)) {
                return doc;
            }
        }
        return MARKDOWN;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getType() {
        return type;
    }
}
