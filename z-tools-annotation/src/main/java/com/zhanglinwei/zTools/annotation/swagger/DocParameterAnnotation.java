package com.zhanglinwei.zTools.annotation.swagger;

/**
 * {@code @Parameter} / {@code @ApiParam} 上源码写出的属性。
 */
public final class DocParameterAnnotation {

    private final String name;
    private final String description;
    private final Boolean required;
    private final String example;
    private final String in;
    private final Boolean hidden;
    private final SchemaAnnotation schema;

    public DocParameterAnnotation(String name, String description, Boolean required,
                                  String example, String in, Boolean hidden, SchemaAnnotation schema) {
        this.name = name;
        this.description = description;
        this.required = required;
        this.example = example;
        this.in = in;
        this.hidden = hidden;
        this.schema = schema;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Boolean required() {
        return required;
    }

    public String example() {
        return example;
    }

    /** OAS3 {@code in}（query / path / header / cookie）；未写则为 {@code null}。 */
    public String in() {
        return in;
    }

    public Boolean hidden() {
        return hidden;
    }

    /** {@code @Parameter(schema = @Schema(...))} 的嵌套 Schema；未写则为 {@code null}。 */
    public SchemaAnnotation schema() {
        return schema;
    }
}
