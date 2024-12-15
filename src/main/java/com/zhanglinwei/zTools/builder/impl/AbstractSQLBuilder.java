package com.zhanglinwei.zTools.builder.impl;

import com.zhanglinwei.zTools.builder.SQLBuilder;
import com.zhanglinwei.zTools.model.DbTableInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractSQLBuilder implements SQLBuilder {

    private final String DELIMITER = ", ";

    public abstract String getSymbol();
    protected abstract String generateCreateTableDDL(DbTableInfo dbTableInfo);
    protected abstract String generateTableIndexDDL(DbTableInfo dbTableInfo);
    protected abstract String generateCommentDDL(DbTableInfo dbTableInfo);
    protected abstract String generateTriggerDDL(DbTableInfo dbTableInfo);
    protected abstract String generateIncrSequenceDDL(DbTableInfo dbTableInfo);

    @Override
    public String generateSelect(DbTableInfo dbTableInfo) {
        List<DbTableInfo.DBFieldInfo> fieldList = dbTableInfo.getFieldInfo();
        String keyName = getPrimaryKeyNameOrDefault(fieldList, fieldList.get(0).getName());
        return "SELECT * FROM " + dbTableInfo.getFullTableName() + " WHERE " + keyName + " = '';";
    }

    @Override
    public String generateInsert(DbTableInfo dbTableInfo) {
        List<DbTableInfo.DBFieldInfo> fieldList = dbTableInfo.getFieldInfo();
        List<String> fieldNameList = fieldList.stream().map(field -> packageSymbol(field.getName())).collect(Collectors.toList());
        String joinNameStr = String.join(DELIMITER, fieldNameList);
        String joinValueStr = buildFieldValueStr(DELIMITER, fieldList);

        return "INSERT INTO " + dbTableInfo.getFullTableName() + " (" + joinNameStr + ") VALUES (" + joinValueStr + ");";
    }

    @Override
    public String generateUpdate(DbTableInfo dbTableInfo) {
        List<DbTableInfo.DBFieldInfo> fieldList = dbTableInfo.getFieldInfo();
        List<String> nameValueList = fieldList.stream().map(field -> {
            String value = buildFieldValueStr(field);
            return field.getName() + " = " + value;
        }).collect(Collectors.toList());
        String joinStr = String.join(DELIMITER, nameValueList);
        String keyName = getPrimaryKeyNameOrDefault(fieldList, fieldList.get(0).getName());

        return "UPDATE " + dbTableInfo.getFullTableName() + " SET " + joinStr + " WHERE " + keyName + " = '';";
    }

    @Override
    public String generateDelete(DbTableInfo dbTableInfo) {
        List<DbTableInfo.DBFieldInfo> fieldList = dbTableInfo.getFieldInfo();
        String keyName = getPrimaryKeyNameOrDefault(fieldList, fieldList.get(0).getName());

        return "DELETE FROM " + dbTableInfo.getFullTableName() + " WHERE " + keyName + " = '';";
    }

    protected String packageSymbol(String name) {
        String symbol = getSymbol();
        return symbol + name + symbol;
    }

    private String getPrimaryKeyNameOrDefault(List<DbTableInfo.DBFieldInfo> fieldList, String defaultValue) {
        Optional<DbTableInfo.DBFieldInfo> primaryKeyOptional = fieldList.stream().filter(DbTableInfo.DBFieldInfo::isPrimaryKey).findFirst();
        return primaryKeyOptional.isPresent() ? primaryKeyOptional.get().getName() : defaultValue;
    }

    private String buildFieldValueStr(String delimiter, List<DbTableInfo.DBFieldInfo> fieldList) {
        List<String> fieldValueList = fieldList.stream().map(this::buildFieldValueStr).collect(Collectors.toList());
        return String.join(delimiter, fieldValueList);
    }

    private String buildFieldValueStr(DbTableInfo.DBFieldInfo field) {
        String dbType = field.getType().toLowerCase();
        if (dbType.startsWith("varchar") || "text".equals(dbType)) {
            return "''";
        }

        return "NULL";
    }

    protected String generateDropTableDDL(String tableName) {
        return "-- DROP TABLE\nDROP TABLE IF EXISTS " + tableName + ";\n";
    }

}
