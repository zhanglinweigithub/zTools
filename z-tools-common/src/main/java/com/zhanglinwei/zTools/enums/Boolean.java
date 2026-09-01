package com.zhanglinwei.zTools.enums;

public enum Boolean {

    TRUE(true, 1, "Y"),
    FALSE(false, 0, "N");

    private final boolean booleanValue;
    private final int numberValue;
    private final String stringValue;

    Boolean(boolean booleanValue, int numberValue, String stringValue) {
        this.booleanValue = booleanValue;
        this.numberValue = numberValue;
        this.stringValue = stringValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public int getNumberValue() {
        return numberValue;
    }

    public String getStringValue() {
        return stringValue;
    }

}
