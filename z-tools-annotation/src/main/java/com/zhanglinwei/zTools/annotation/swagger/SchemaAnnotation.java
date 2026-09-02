package com.zhanglinwei.zTools.annotation.swagger;

/**
 * {@code @Schema} / {@code @ApiModelProperty} 上源码写出的属性。
 */
public final class SchemaAnnotation {

    /** {@code name}。 */
    private final String name;
    /** {@code title} 或 {@code @ApiModelProperty} 的 {@code value}。 */
    private final String title;
    /** {@code description} 或 {@code notes}。 */
    private final String description;
    /** {@code example}。 */
    private final String example;
    /** OAS3 {@code type}。 */
    private final String type;
    /** OAS3 {@code format}。 */
    private final String format;
    /** OAS3 {@code requiredMode}（REQUIRED / NOT_REQUIRED / AUTO）；未写则为 {@code null}。 */
    private final String requiredMode;
    /** Swagger2 {@code required}；未写则为 {@code null}。 */
    private final Boolean required;
    /** {@code hidden}；未写则为 {@code null}。 */
    private final Boolean hidden;

    /**
     * @param name         名称
     * @param title        标题
     * @param description  说明
     * @param example      示例
     * @param type         OAS3 类型
     * @param format       OAS3 格式
     * @param requiredMode OAS3 必填模式
     * @param required     Swagger2 是否必填
     * @param hidden       是否隐藏
     */
    public SchemaAnnotation(String name, String title, String description, String example,
                            String type, String format, String requiredMode, Boolean required, Boolean hidden) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.example = example;
        this.type = type;
        this.format = format;
        this.requiredMode = requiredMode;
        this.required = required;
        this.hidden = hidden;
    }

    /** {@code name}。 */
    public String name() {
        return name;
    }

    /** {@code title} 或 {@code @ApiModelProperty} 的 {@code value}。 */
    public String title() {
        return title;
    }

    /** {@code description} 或 {@code notes}。 */
    public String description() {
        return description;
    }

    /** {@code example}。 */
    public String example() {
        return example;
    }

    /** OAS3 {@code type}。 */
    public String type() {
        return type;
    }

    /** OAS3 {@code format}。 */
    public String format() {
        return format;
    }

    /** OAS3 {@code requiredMode}（REQUIRED / NOT_REQUIRED / AUTO）；未写则为 {@code null}。 */
    public String requiredMode() {
        return requiredMode;
    }

    /** Swagger2 {@code required}；未写则为 {@code null}。 */
    public Boolean required() {
        return required;
    }

    /** {@code hidden}；未写则为 {@code null}。 */
    public Boolean hidden() {
        return hidden;
    }
}
