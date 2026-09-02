package com.zhanglinwei.zTools.annotation.swagger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@code @Operation} / {@code @ApiOperation} 上源码写出的属性。
 */
public final class OperationAnnotation {

    /** {@code summary} 或 {@code @ApiOperation} 的 {@code value}。 */
    private final String summary;
    /** {@code description} 或 {@code notes}。 */
    private final String description;
    /** {@code method} 或 {@code httpMethod}。 */
    private final String httpMethod;
    /** {@code operationId} 或 {@code nickname}。 */
    private final String operationId;
    /** {@code tags}。 */
    private final List<String> tags;
    /** {@code hidden}；未写则为 {@code null}。 */
    private final Boolean hidden;

    /**
     * @param summary     摘要
     * @param description 说明
     * @param httpMethod  HTTP 动词
     * @param operationId 操作 id
     * @param tags        标签
     * @param hidden      是否隐藏
     */
    public OperationAnnotation(String summary, String description, String httpMethod,
                               String operationId, List<String> tags, Boolean hidden) {
        this.summary = summary;
        this.description = description;
        this.httpMethod = httpMethod;
        this.operationId = operationId;
        this.tags = tags == null ? null : Collections.unmodifiableList(new ArrayList<String>(tags));
        this.hidden = hidden;
    }

    /** {@code summary} 或 {@code @ApiOperation} 的 {@code value}。 */
    public String summary() {
        return summary;
    }

    /** {@code description} 或 {@code notes}。 */
    public String description() {
        return description;
    }

    /** {@code method} 或 {@code httpMethod}。 */
    public String httpMethod() {
        return httpMethod;
    }

    /** {@code operationId} 或 {@code nickname}。 */
    public String operationId() {
        return operationId;
    }

    /** {@code tags}。 */
    public List<String> tags() {
        return tags;
    }

    /** {@code hidden}；未写则为 {@code null}。 */
    public Boolean hidden() {
        return hidden;
    }
}
