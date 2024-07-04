package com.zhanglinwei.zTools.model;

import com.zhanglinwei.zTools.constant.DocConstants;
import com.zhanglinwei.zTools.util.AssertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static com.zhanglinwei.zTools.util.CommonUtils.VERTICAL;

public class TableInfo {

    private String tableName;
    private String tableComment;
    private String columnName;
    private String dataType;
    private String maxLength;
    private String dataPrecision;
    private String isNullable;
    private String primaryKey;
    private String columnDefault;
    private String columnComment;

    public TableInfo() {
    }

    public TableInfo(String tableName, String tableComment) {
        this.tableName = tableName;
        this.tableComment = tableComment;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = AssertUtils.isBlank(tableName) ? "" : tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = AssertUtils.isBlank(tableComment) ? "" : tableComment;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = AssertUtils.isBlank(columnName) ? "" : columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = AssertUtils.isBlank(dataType) ? "" : dataType;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = AssertUtils.isBlank(maxLength) ? "" : maxLength;
    }

    public String getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(String isNullable) {
        this.isNullable = AssertUtils.isBlank(isNullable) ? "" : isNullable;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = AssertUtils.isBlank(primaryKey) ? "" : primaryKey;
    }

    public String getDataPrecision() {
        return dataPrecision;
    }

    public void setDataPrecision(String dataPrecision) {
        this.dataPrecision = AssertUtils.isBlank(dataPrecision) ? "" : dataPrecision;
    }

    public String getColumnDefault() {
        return columnDefault;
    }

    public void setColumnDefault(String columnDefault) {
        this.columnDefault = AssertUtils.isBlank(columnDefault) ? "" : columnDefault;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = AssertUtils.isBlank(columnComment) ? "" : columnComment;
    }

    public String toHtmlString() {
        return "<tr>\n" +
                "<td align='" + DocConstants.ALIGN_LEFT + "'>" + this.columnName + "</td>\n" +
                "<td align='" + DocConstants.ALIGN_CENTER + "'>" + this.dataType + "</td>\n" +
                "<td align='" + DocConstants.ALIGN_CENTER + "'>" + this.maxLength + "</td>\n" +
                "<td align='" + DocConstants.ALIGN_CENTER + "'>" + this.dataPrecision + "</td>\n" +
                "<td align='" + DocConstants.ALIGN_CENTER + "'>" + this.isNullable + "</td>\n" +
                "<td align='" + DocConstants.ALIGN_CENTER + "'>" + this.primaryKey + "</td>\n" +
                "<td align='" + DocConstants.ALIGN_CENTER + "'>" + this.columnDefault + "</td>\n" +
                "<td align='" + DocConstants.ALIGN_CENTER + "'>" + this.columnComment + "</td>\n" +
                "</tr>\n";

    }

    public String toMarkDownString() {
        StringJoiner joiner = new StringJoiner(VERTICAL);

        return joiner.add(this.columnName)
                .add(this.dataType)
                .add(this.maxLength)
                .add(this.dataPrecision)
                .add(this.isNullable)
                .add(this.primaryKey)
                .add(this.columnDefault)
                .add(this.columnComment)
                .toString() + "\n";
    }

    public List<String> toWordList() {
        List<String> stringList = new ArrayList<>();
        stringList.add(this.columnName);
        stringList.add(this.dataType);
        stringList.add(this.maxLength);
        stringList.add(this.dataPrecision);
        stringList.add(this.isNullable);
        stringList.add(this.primaryKey);
        stringList.add(this.columnDefault);
        stringList.add(this.columnComment);

        return stringList;
    }
}
