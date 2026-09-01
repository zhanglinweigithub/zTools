package com.zhanglinwei.zTools.annotation.swagger;

/**
 * {@code @Schema} / {@code @ApiModelProperty} 上源码写出的属性。
 */
public final class SchemaAnnotation {

    private final String name;
    private final String title;
    private final String description;
    private final String example;
    private final String type;
    private final String format;
    private final String requiredMode;
    private final Boolean required;
    private final Boolean hidden;

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

    public String name() {
        return name;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public String example() {
        return example;
    }

    public String type() {
        return type;
    }

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

    public Boolean hidden() {
        return hidden;
    }
}
