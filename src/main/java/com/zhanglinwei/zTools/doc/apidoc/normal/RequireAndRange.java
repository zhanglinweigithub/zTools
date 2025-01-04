package com.zhanglinwei.zTools.doc.apidoc.normal;

import static com.zhanglinwei.zTools.util.CommonUtils.NULL;
public class RequireAndRange {

    private boolean require;
    private String range;

    public RequireAndRange(boolean require, String range) {
        this.require = require;
        this.range = range;
    }

    public static RequireAndRange instance() {
        return new RequireAndRange(false, NULL);
    }

    public boolean isRequire() {
        return require;
    }

    public void setRequire(boolean require) {
        this.require = require;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }
}
