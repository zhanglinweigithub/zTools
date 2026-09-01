package com.zhanglinwei.zTools.annotation.validation;

/**
 * 校验注解的解析结果。布尔表示注解是否存在；数值 / 正则未写则为 {@code null}。
 */
public final class ValidationConstraints {

    private final boolean notNull;
    private final boolean notBlank;
    private final boolean notEmpty;
    private final boolean valid;
    private final boolean size;
    private final Integer sizeMin;
    private final Integer sizeMax;
    private final Long min;
    private final Long max;
    private final String pattern;

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

    public boolean notNull() {
        return notNull;
    }

    public boolean notBlank() {
        return notBlank;
    }

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

    public Integer sizeMin() {
        return sizeMin;
    }

    public Integer sizeMax() {
        return sizeMax;
    }

    public Long min() {
        return min;
    }

    public Long max() {
        return max;
    }

    public String pattern() {
        return pattern;
    }
}
