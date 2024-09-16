package com.zhanglinwei.zTools.builder.impl;

import com.zhanglinwei.zTools.constant.DBType;
import com.zhanglinwei.zTools.constant.IndexType;
import com.zhanglinwei.zTools.model.DbTableInfo;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.CommonUtils;

import java.util.Optional;

/**
 * MySQL
 */
public class MySqlSQLBuilder extends AbstractSQLBuilder {
    @Override
    public boolean support(String dbType) {
        return DBType.MYSQL.getCode().equals(dbType);
    }

    @Override
    public String generateDDL(DbTableInfo dbTableInfo) {
        String dropTableDDL = generateDropTableDDL(dbTableInfo.getTableName());
        String createTableDDL = generateCreateTableDDL(dbTableInfo);
        String tableIndexDDL = generateTableIndexDDL(dbTableInfo);

        return dropTableDDL + createTableDDL + tableIndexDDL;
    }


    @Override
    public String generateCreateTableDDL(DbTableInfo dbTableInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append("-- CREATE TABLE\n");
        builder.append("CREATE TABLE ").append("`").append(dbTableInfo.getTableName()).append("` (\n");
        dbTableInfo.getFieldInfo().stream().forEach(dbFieldInfo -> {
            builder.append("    `").append(dbFieldInfo.getName()).append("` ").append(dbFieldInfo.getType()).append(" ");
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

        builder.append(fillPrimaryKey(dbTableInfo));
        builder.append(");\n");

        return builder.toString();
    }

    @Override
    public String generateTableIndexDDL(DbTableInfo dbTableInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append("-- CREATE INDEX\n");
        dbTableInfo.getIndexList().stream().forEach(dbIndexInfo -> {
            if (dbIndexInfo.getIndex().getCode().equals(IndexType.UNIQUE.getCode())) {
                builder.append("CREATE UNIQUE INDEX ");
            }
            if (dbIndexInfo.getIndex().getCode().equals(IndexType.NORMAL.getCode())) {
                builder.append("CREATE INDEX ");
            }

            builder.append(dbIndexInfo.getIndexName().trim()).append(" ON ").append(dbTableInfo.getTableName()).append("(");

            dbIndexInfo.getFieldNameList().stream().forEach(fieldName -> {
                builder.append("`").append(fieldName.trim()).append("`").append(",");
            });
            if (builder.charAt(builder.length() - 1) == ',') {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append(");\n");
        });

        return builder.toString();
    }

    @Override
    protected String generateCommentDDL(DbTableInfo dbTableInfo) {
        return "";
    }

    @Override
    protected String generateTriggerDDL(DbTableInfo dbTableInfo) {
        return "";
    }

    @Override
    protected String generateIncrSequenceDDL(DbTableInfo dbTableInfo) {
        return "";
    }


    public String fillPrimaryKey(DbTableInfo dbTableInfo) {
        Optional<DbTableInfo.DBFieldInfo> primaryKeyOptional = dbTableInfo.getFieldInfo().stream().filter(DbTableInfo.DBFieldInfo::isPrimaryKey).findFirst();
        if (primaryKeyOptional.isPresent()) {
            DbTableInfo.DBFieldInfo primaryKey = primaryKeyOptional.get();
            return "    PRIMARY KEY (`" + CommonUtils.convertCamelToSnake(primaryKey.getName()) + "`)\n";
        }

        return "";
    }


    @Override
    public String generateDropTableDDL(String tableName) {
        return "-- DROP TABLE\nDROP TABLE IF EXISTS `" + tableName + "`;\n";
    }

    @Override
    public String getSymbol() {
        return "`";
    }
}
