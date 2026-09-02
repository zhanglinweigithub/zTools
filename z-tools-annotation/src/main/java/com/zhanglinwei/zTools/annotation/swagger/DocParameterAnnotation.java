package com.zhanglinwei.zTools.annotation.swagger;

/**
 * {@code @Parameter} / {@code @ApiParam} 上源码写出的属性。
 */
public final class DocParameterAnnotation {

    /** {@code name}。 */
    private final String name;
    /** {@code description} 或 {@code @ApiParam} 的 {@code value}。 */
    private final String description;
    /** {@code required}；未写则为 {@code null}。 */
    private final Boolean required;
    /** {@code example}。 */
    private final String example;
    /** OAS3 {@code in}（query / path / header / cookie）；未写则为 {@code null}。 */
    private final String in;
    /** {@code hidden}；未写则为 {@code null}。 */
    private final Boolean hidden;
    /** {@code @Parameter(schema = @Schema(...))} 的嵌套 Schema；未写则为 {@code null}。 */
    private final SchemaAnnotation schema;

    /**
     * @param name        参数名
     * @param description 说明
     * @param required    是否必填
     * @param example     示例
     * @param in          OAS3 参数位置
     * @param hidden      是否隐藏
     * @param schema      嵌套 Schema
     */
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

    /** {@code name}。 */
    public String name() {
        return name;
    }

    /** {@code description} 或 {@code @ApiParam} 的 {@code value}。 */
    public String description() {
        return description;
    }

    /** {@code required}；未写则为 {@code null}。 */
    public Boolean required() {
        return required;
    }

    /** {@code example}。 */
    public String example() {
        return example;
    }

    /** OAS3 {@code in}（query / path / header / cookie）；未写则为 {@code null}。 */
    public String in() {
        return in;
    }

    /** {@code hidden}；未写则为 {@code null}。 */
    public Boolean hidden() {
        return hidden;
    }

    /** {@code @Parameter(schema = @Schema(...))} 的嵌套 Schema；未写则为 {@code null}。 */
    public SchemaAnnotation schema() {
        return schema;
    }
}
