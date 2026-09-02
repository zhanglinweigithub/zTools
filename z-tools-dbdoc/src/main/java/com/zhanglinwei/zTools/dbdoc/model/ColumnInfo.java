package com.zhanglinwei.zTools.dbdoc.model;

import com.zhanglinwei.zTools.common.enums.Boolean;

/**
 * 一列的文档模型，字段与 {@code information_schema.COLUMNS} 对应。
 * <p>
 * 方言把 ResultSet 一行映到本对象，例如 MySQL：
 * {@code COLUMN_NAME → columnName}，{@code DATA_TYPE → dataType}，
 * {@code COLUMN_KEY} 含 {@code PRI} 则 {@code primaryKey=true}。
 */
public class ColumnInfo {

    /** 列名 */
    private String columnName;
    /** 数据类型，如 {@code varchar}、{@code bigint} */
    private String dataType;
    /** 字符最大长度，数值类型时可能为 0 */
    private Long characterMaximumLength;
    /** 数值精度 */
    private Long numericPrecision;
    /** 数值小数位 */
    private Long numericScale;
    /** 是否可空，一般为 {@code YES}/{@code NO} */
    private String isNullable;
    /** 是否主键 */
    private boolean primaryKey;
    /** 默认值 */
    private String columnDefault;
    /** 列注释 */
    private String columnComment;

    /**
     * 主键标记转成文档用的是/否字符串。
     *
     * @return 主键为 {@code true} 对应的展示值，否则为否
     */
    public String isPrimaryKeyAsString() {
        return primaryKey ? Boolean.TRUE.getStringValue() : Boolean.FALSE.getStringValue();
    }

    /**
     * 获取列名。
     *
     * @return 列名
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * 设置列名。
     *
     * @param columnName 列名
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * 获取数据类型。
     *
     * @return 如 {@code varchar}
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * 设置数据类型。
     *
     * @param dataType 如 {@code varchar}
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * 获取字符最大长度。
     *
     * @return 最大长度，数值列可能为 0
     */
    public Long getCharacterMaximumLength() {
        return characterMaximumLength;
    }

    /**
     * 设置字符最大长度。
     *
     * @param characterMaximumLength 最大长度
     */
    public void setCharacterMaximumLength(Long characterMaximumLength) {
        this.characterMaximumLength = characterMaximumLength;
    }

    /**
     * 获取数值精度。
     *
     * @return 精度
     */
    public Long getNumericPrecision() {
        return numericPrecision;
    }

    /**
     * 设置数值精度。
     *
     * @param numericPrecision 精度
     */
    public void setNumericPrecision(Long numericPrecision) {
        this.numericPrecision = numericPrecision;
    }

    /**
     * 获取数值小数位。
     *
     * @return 小数位数
     */
    public Long getNumericScale() {
        return numericScale;
    }

    /**
     * 设置数值小数位。
     *
     * @param numericScale 小数位数
     */
    public void setNumericScale(Long numericScale) {
        this.numericScale = numericScale;
    }

    /**
     * 获取是否可空。
     *
     * @return 一般为 {@code YES} 或 {@code NO}
     */
    public String getIsNullable() {
        return isNullable;
    }

    /**
     * 设置是否可空。
     *
     * @param isNullable 一般为 {@code YES} 或 {@code NO}
     */
    public void setIsNullable(String isNullable) {
        this.isNullable = isNullable;
    }

    /**
     * 是否主键。
     *
     * @return 主键则为 {@code true}
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * 设置是否主键。
     *
     * @param primaryKey 是否主键
     */
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * 获取默认值。
     *
     * @return 默认值，可能为 {@code null}
     */
    public String getColumnDefault() {
        return columnDefault;
    }

    /**
     * 设置默认值。
     *
     * @param columnDefault 默认值
     */
    public void setColumnDefault(String columnDefault) {
        this.columnDefault = columnDefault;
    }

    /**
     * 获取列注释。
     *
     * @return 列注释
     */
    public String getColumnComment() {
        return columnComment;
    }

    /**
     * 设置列注释。
     *
     * @param columnComment 列注释
     */
    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }
}
