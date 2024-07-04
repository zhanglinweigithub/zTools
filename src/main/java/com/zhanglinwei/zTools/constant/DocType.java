package com.zhanglinwei.zTools.constant;

public enum DocType {

    MD(".md", "MarkDown"),
    HTML(".html", "Html"),
    WORD(".docx", "Word"),
    ;


    private String suffix;
    private String type;

    DocType(String suffix, String type){
        this.suffix = suffix;
        this.type = type;
    }

    public static String getSuffixByType(String type) {
        for (DocType doc : DocType.values()) {
            if (type.equals(doc.getType())) {
                return doc.getSuffix();
            }
        }

        return "";
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
