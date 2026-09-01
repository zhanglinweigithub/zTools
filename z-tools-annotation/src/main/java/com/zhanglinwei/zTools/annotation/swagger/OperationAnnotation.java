package com.zhanglinwei.zTools.annotation.swagger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@code @Operation} / {@code @ApiOperation} 上源码写出的属性。
 */
public final class OperationAnnotation {

    private final String summary;
    private final String description;
    private final String httpMethod;
    private final String operationId;
    private final List<String> tags;
    private final Boolean hidden;

    public OperationAnnotation(String summary, String description, String httpMethod,
                               String operationId, List<String> tags, Boolean hidden) {
        this.summary = summary;
        this.description = description;
        this.httpMethod = httpMethod;
        this.operationId = operationId;
        this.tags = tags == null ? null : Collections.unmodifiableList(new ArrayList<String>(tags));
        this.hidden = hidden;
    }

    public String summary() {
        return summary;
    }

    public String description() {
        return description;
    }

    public String httpMethod() {
        return httpMethod;
    }

    public String operationId() {
        return operationId;
    }

    public List<String> tags() {
        return tags;
    }

    public Boolean hidden() {
        return hidden;
    }
}
