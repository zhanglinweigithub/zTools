package com.zhanglinwei.zTools.builder.impl;

import com.zhanglinwei.zTools.constant.DBType;
import com.zhanglinwei.zTools.constant.IndexType;
import com.zhanglinwei.zTools.model.DbTableInfo;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.ConfigUtils;

import java.util.Optional;

/**
 * 崖山
 */
public class YaShanSQLBuilder extends AbstractSQLBuilder {
    @Override
    public boolean support(String dbType) {
        return DBType.YA_SHAN.getCode().equals(dbType);
    }

    @Override
    public String generateDDL(DbTableInfo dbTableInfo) {
        String dropTableDDL = generateDropTableDDL(dbTableInfo.getTableName()).toUpperCase();
        String incrSequenceDDL = generateIncrSequenceDDL(dbTableInfo).toUpperCase();
        String createTableDDL = generateCreateTableDDL(dbTableInfo).toUpperCase();
        String commentDDL = generateCommentDDL(dbTableInfo).toUpperCase();
        String tableIndexDDL = generateTableIndexDDL(dbTableInfo).toUpperCase();
        String triggerDDL = generateTriggerDDL(dbTableInfo).toUpperCase();

        return (dropTableDDL + incrSequenceDDL + createTableDDL + commentDDL + tableIndexDDL + triggerDDL).toUpperCase();
    }

    @Override
    protected String generateCommentDDL(DbTableInfo dbTableInfo) {
        String prefix = "COMMENT ON COLUMN " + dbTableInfo.getFullTableName() + ".";
        StringBuilder commentBuilder = new StringBuilder();
        commentBuilder.append("-- CREATE COMMENT\n");
        dbTableInfo.getFieldInfo().stream()
                .filter(fieldInfo -> AssertUtils.isNotBlank(fieldInfo.getDesc()))
                .forEach(fieldInfo -> {
                    commentBuilder
                            .append(prefix)
                            .append(fieldInfo.getName())
                            .append(" IS '")
                            .append(fieldInfo.getDesc())
                            .append("';\n");
                });

        return commentBuilder.toString();
    }

    @Override
    protected String generateTriggerDDL(DbTableInfo dbTableInfo) {
        String createTimeField = ConfigUtils.getDDLCreateTimeField();
        String updateTimeField = ConfigUtils.getDDLUpdateTimeField();

        StringBuilder triggerBuilder = new StringBuilder();
        triggerBuilder.append("-- CREATE TRIGGER\n");
        triggerBuilder.append("CREATE OR REPLACE TRIGGER ").append(dbTableInfo.getFullTableName()).append("_BEFORE_INSERT_TRIGGER\n")
                .append("BEFORE INSERT ON ").append(packageSymbol(dbTableInfo.getFullTableName())).append("\n")
                .append("FOR EACH ROW\n")
                .append("BEGIN\n")
                .append("    :new.").append("\"").append(createTimeField).append("\" := NOW();\n")
                .append("    :new.").append("\"").append(updateTimeField).append("\" := NOW();\n")
                .append("END;\n");
        triggerBuilder.append("CREATE OR REPLACE TRIGGER ").append(dbTableInfo.getFullTableName()).append("_BEFORE_UPDATE_TRIGGER\n")
                .append("BEFORE UPDATE ON ").append(packageSymbol(dbTableInfo.getFullTableName())).append("\n")
                .append("FOR EACH ROW\n")
                .append("BEGIN\n")
                .append("    :new.").append("\"").append(updateTimeField).append("\" := NOW();\n")
                .append("END;\n");

        return triggerBuilder.toString();
    }

    @Override
    protected String generateIncrSequenceDDL(DbTableInfo dbTableInfo) {
        Optional<DbTableInfo.DBFieldInfo> autoIncrFieldOptional = dbTableInfo.getFieldInfo().stream().filter(DbTableInfo.DBFieldInfo::isAutoIncr).findFirst();
        if (autoIncrFieldOptional.isPresent()) {
            DbTableInfo.DBFieldInfo autoIncrField = autoIncrFieldOptional.get();
            return "-- CREATE SEQUENCE\nCREATE SEQUENCE " + dbTableInfo.getFullTableName() + "_" + autoIncrField.getName() + "_SEQ;" + "\n";
        }
        return "";
    }

    @Override
    protected String generateCreateTableDDL(DbTableInfo dbTableInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append("-- CREATE TABLE\n");
        builder.append("CREATE TABLE ").append(dbTableInfo.getTableName()).append(" (\n");
        dbTableInfo.getFieldInfo().stream().forEach(dbFieldInfo -> {
            builder.append("    \"").append(dbFieldInfo.getName()).append("\" ").append(dbFieldInfo.getType()).append(" ");
            if (dbFieldInfo.isAutoIncr() || dbFieldInfo.isRequired() || dbFieldInfo.isPrimaryKey()) {
                builder.append("NOT NULL ");
            }
            if (dbFieldInfo.isAutoIncr()) {
                builder.append(buildIncrSeq(dbFieldInfo, dbTableInfo));
            }

            if (AssertUtils.isNotBlank(dbFieldInfo.getDefaultValue())) {
                builder.append("DEFAULT ").append(dbFieldInfo.getDefaultValue()).append(" ");
            }
            builder.append(",\n");
        });

        builder.append(fillPrimaryKey(dbTableInfo));
        builder.append(");\n");

        return builder.toString();
    }

    private String fillPrimaryKey(DbTableInfo dbTableInfo) {
        Optional<DbTableInfo.DBFieldInfo> primaryKeyOptional = dbTableInfo.getFieldInfo().stream().filter(DbTableInfo.DBFieldInfo::isPrimaryKey).findFirst();
        if (primaryKeyOptional.isPresent()) {
            DbTableInfo.DBFieldInfo primaryKey = primaryKeyOptional.get();
            return "    CONSTRAINT C_" + dbTableInfo.getTableName() + "_PRIMARY PRIMARY KEY" + "(\"" + primaryKey.getName() + "\")\n";
        }

        return "";
    }

    private String buildIncrSeq(DbTableInfo.DBFieldInfo dbFieldInfo, DbTableInfo dbTableInfo) {

        return "DEFAULT \"" + dbTableInfo.getSchema() + "\".\"" + dbTableInfo.getTableName() + "_" + dbFieldInfo.getName() + "_SEQ\".NEXTVAL ";
    }

    @Override
    protected String generateTableIndexDDL(DbTableInfo dbTableInfo) {
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
                builder.append("\"").append(fieldName.trim()).append("\"").append(",");
            });
            if (builder.charAt(builder.length() - 1) == ',') {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append(");\n");
        });

        return builder.toString();
    }

    @Override
    public String getSymbol() {
        return "\"";
    }

}
