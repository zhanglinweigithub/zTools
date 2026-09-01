package com.zhanglinwei.zTools.restful.matcher;

public interface PathMatcher {

    boolean match(String pattern, String path);
}
