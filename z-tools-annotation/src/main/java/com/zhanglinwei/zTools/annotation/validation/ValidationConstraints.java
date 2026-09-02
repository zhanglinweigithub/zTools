package com.zhanglinwei.zTools.annotation.validation;

/**
 * 校验注解的解析结果。布尔表示注解是否存在；数值 / 正则未写则为 {@code null}。
 */
public final class ValidationConstraints {

    /** 是否存在 {@code @NotNull}。 */
    private final boolean notNull;
    /** 是否存在 {@code @NotBlank}。 */
    private final boolean notBlank;
    /** 是否存在 {@code @NotEmpty}。 */
    private final boolean notEmpty;
    /** 是否存在 {@code @Valid} 或 {@code @Validated}。 */
    private final boolean valid;
    /** 是否存在 {@code @Size}。 */
    private final boolean size;
    /** {@code @Size.min}；未写则为 {@code null}。 */
    private final Integer sizeMin;
    /** {@code @Size.max}；未写则为 {@code null}。 */
    private final Integer sizeMax;
    /** {@code @Min.value}；未写则为 {@code null}。 */
    private final Long min;
    /** {@code @Max.value}；未写则为 {@code null}。 */
    private final Long max;
    /** {@code @Pattern.regexp}；未写则为 {@code null}。 */
    private final String pattern;

    /**
     * @param notNull  是否存在 @NotNull
     * @param notBlank 是否存在 @NotBlank
     * @param notEmpty 是否存在 @NotEmpty
     * @param valid    是否存在 @Valid 或 @Validated
     * @param size     是否存在 @Size
     * @param sizeMin  @Size.min
     * @param sizeMax  @Size.max
     * @param min      @Min.value
     * @param max      @Max.value
     * @param pattern  @Pattern.regexp
     */
    public ValidationConstraints(boolean notNull, boolean notBlank, boolean notEmpty, boolean valid, boolean size,
                                 Integer sizeMin, Integer sizeMax, Long min, Long max, String pattern) {
        this.notNull = notNull;
        this.notBlank = notBlank;
        this.notEmpty = notEmpty;
        this.valid = valid;
        this.size = size;
        this.sizeMin = sizeMin;
        this.sizeMax = sizeMax;
        this.min = min;
        this.max = max;
        this.pattern = pattern;
    }

    /** 是否存在 {@code @NotNull}。 */
    public boolean notNull() {
        return notNull;
    }

    /** 是否存在 {@code @NotBlank}。 */
    public boolean notBlank() {
        return notBlank;
    }

    /** 是否存在 {@code @NotEmpty}。 */
    public boolean notEmpty() {
        return notEmpty;
    }

    /** {@code @Valid} 或 {@code @Validated}。 */
    public boolean valid() {
        return valid;
    }

    /** {@code @Size} 是否存在；min/max 未写时仍可能为 {@code null}。 */
    public boolean size() {
        return size;
    }

    /** {@code @Size.min}；未写则为 {@code null}。 */
    public Integer sizeMin() {
        return sizeMin;
    }

    /** {@code @Size.max}；未写则为 {@code null}。 */
    public Integer sizeMax() {
        return sizeMax;
    }

    /** {@code @Min.value}；未写则为 {@code null}。 */
    public Long min() {
        return min;
    }

    /** {@code @Max.value}；未写则为 {@code null}。 */
    public Long max() {
        return max;
    }

    /** {@code @Pattern.regexp}；未写则为 {@code null}。 */
    public String pattern() {
        return pattern;
    }
}
