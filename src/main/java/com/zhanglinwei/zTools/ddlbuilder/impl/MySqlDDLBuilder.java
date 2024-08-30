package com.zhanglinwei.zTools.ddlbuilder.impl;

import com.zhanglinwei.zTools.constant.IndexType;
import com.zhanglinwei.zTools.model.DDLInfo;
import com.zhanglinwei.zTools.util.AssertUtils;

import java.util.Optional;

public class MySqlDDLBuilder extends AbstractDDLBuilder{
    @Override
    public boolean support(String dbType) {
        return dbType.equals("MySql");
    }

    @Override
    public String generateDDLString(DDLInfo ddlInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append(generateDropTable(ddlInfo.getTableName()));
        builder.append("CREATE TABLE ").append("`").append(ddlInfo.getTableName()).append("` (\n");
        ddlInfo.getFieldInfo().stream().forEach(dbFieldInfo -> {
            builder.append("`").append(dbFieldInfo.getName()).append("` ").append(dbFieldInfo.getType());
            if (dbFieldInfo.isAutoIncr() || dbFieldInfo.isRequired() || dbFieldInfo.isPrimaryKey()) {
                builder.append("NOT NULL ");
            }
            if (dbFieldInfo.isAutoIncr()) {
                builder.append("AUTO_INCREMENT ");
            }
            if (AssertUtils.isNotBlank(dbFieldInfo.getDefaultValue())) {
                builder.append("DEFAULT ").append(dbFieldInfo.getDefaultValue()).append(" ");
                if (dbFieldInfo.isOnUpdate()) {
                    builder.append("ON UPDATE ").append(dbFieldInfo.getDefaultValue()).append(" ");
                }
            }
            if (AssertUtils.isNotBlank(dbFieldInfo.getDesc())) {
                builder.append("COMMENT ").append("'").append(dbFieldInfo.getDesc()).append("'");
            }
            builder.append(",\n");
        });

        ddlInfo.getIndexList().stream().forEach(dbIndexInfo -> {
            builder.append(buildPrimaryKey(ddlInfo));

            if (dbIndexInfo.getIndex().getCode().equals(IndexType.UNIQUE.getCode())) {
                builder.append("UNIQUE KEY `").append(dbIndexInfo.getIndexName()).append("` (");
                dbIndexInfo.getFieldNameList().stream().forEach(fieldName -> {
                    builder.append("`").append(fieldName).append("`").append(",");
                });
                if (builder.charAt(builder.length() - 1) == ',') {
                    builder.deleteCharAt(builder.length() - 1);
                }
                builder.append("),\n");
            }

            if (dbIndexInfo.getIndex().getCode().equals(IndexType.NORMAL.getCode())) {
                builder.append("KEY `").append(dbIndexInfo.getIndexName()).append("` (");
                dbIndexInfo.getFieldNameList().stream().forEach(fieldName -> {
                    builder.append("`").append(fieldName).append("`").append(",");
                });
                if (builder.charAt(builder.length() - 1) == ',') {
                    builder.deleteCharAt(builder.length() - 1);
                }
                builder.append("),\n");
            }
        });

        if (builder.charAt(builder.length() - 1) == '\n') {
            builder.deleteCharAt(builder.length() - 1);
        }
        if (builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append(")\n");

        return builder.toString();
    }

    private String buildPrimaryKey(DDLInfo ddlInfo) {
        Optional<DDLInfo.DBFieldInfo> primaryKeyOptional = ddlInfo.getFieldInfo().stream().filter(DDLInfo.DBFieldInfo::isPrimaryKey).findFirst();
        if (primaryKeyOptional.isPresent()) {
            DDLInfo.DBFieldInfo primaryKey = primaryKeyOptional.get();
            return "PRIMARY KEY (" + primaryKey.getName() + "`),";
        }

        return "";
    }
}
