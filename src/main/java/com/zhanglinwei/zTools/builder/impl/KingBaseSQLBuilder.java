package com.zhanglinwei.zTools.builder.impl;

import com.zhanglinwei.zTools.constant.DBType;
import com.zhanglinwei.zTools.constant.IndexType;
import com.zhanglinwei.zTools.model.DbTableInfo;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.ConfigUtils;

import java.util.Optional;

/**
 * 人大金仓
 */
public class KingBaseSQLBuilder extends AbstractSQLBuilder {
    @Override
    public boolean support(String dbType) {
        return dbType.equals(DBType.KING_BASE.getCode());
    }

    @Override
    public String generateDDL(DbTableInfo ddlInfo) {
        String dropTableDDL = generateDropTableDDL(ddlInfo.getTableName());
        String incrSequenceDDL = generateIncrSequenceDDL(ddlInfo);
        String createTableDDL = generateCreateTableDDL(ddlInfo);
        String commentDDL = generateCommentDDL(ddlInfo);
        String tableIndexDDL = generateTableIndexDDL(ddlInfo);
        String triggerDDL = generateTriggerDDL(ddlInfo);

        return dropTableDDL + incrSequenceDDL + createTableDDL + commentDDL + tableIndexDDL + triggerDDL;
    }

    private String generateTriggerDDL(DbTableInfo ddlInfo) {
        String createTimeField = ConfigUtils.getDDLCreateTimeField();
        String updateTimeField = ConfigUtils.getDDLUpdateTimeField();

        StringBuilder triggerBuilder = new StringBuilder();
        triggerBuilder.append("-- CREATE TRIGGER\n");
        triggerBuilder.append("CREATE OR REPLACE TRIGGER ").append(ddlInfo.getTableName()).append("_BEFORE_INSERT_TRIGGER\n")
                .append("BEFORE INSERT ON ").append("\"").append(ddlInfo.getSchema()).append("\".\"").append(ddlInfo.getTableName()).append("\"\n")
                .append("FOR EACH ROW\n")
                .append("BEGIN\n")
                .append("    :new.").append("\"").append(createTimeField).append("\" := NOW();\n")
                .append("    :new.").append("\"").append(updateTimeField).append("\" := NOW();\n")
                .append("END;\n");

        triggerBuilder.append("CREATE OR REPLACE TRIGGER ").append(ddlInfo.getTableName()).append("_BEFORE_UPDATE_TRIGGER\n")
                .append("BEFORE UPDATE ON ").append("\"").append(ddlInfo.getSchema()).append("\".\"").append(ddlInfo.getTableName()).append("\"\n")
                .append("FOR EACH ROW\n")
                .append("BEGIN\n")
                .append("    :new.").append("\"").append(updateTimeField).append("\" := NOW();\n")
                .append("END;\n");

        return triggerBuilder.toString();
    }

    private String generateCommentDDL(DbTableInfo ddlInfo) {
        String prefix = "COMMENT ON COLUMN " + ddlInfo.getSchema() + "." + ddlInfo.getTableName() + ".";
        StringBuilder commentBuilder = new StringBuilder();
        commentBuilder.append("-- CREATE COMMENT\n");
        ddlInfo.getFieldInfo().stream()
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
    public String generateCreateTableDDL(DbTableInfo ddlInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append("-- CREATE TABLE\n");
        builder.append("CREATE TABLE ").append(ddlInfo.getTableName()).append(" (\n");
        ddlInfo.getFieldInfo().stream().forEach(dbFieldInfo -> {
            builder.append("    \"").append(dbFieldInfo.getName()).append("\" ").append(dbFieldInfo.getType()).append(" ");
            if (dbFieldInfo.isAutoIncr() || dbFieldInfo.isRequired() || dbFieldInfo.isPrimaryKey()) {
                builder.append("NOT NULL ");
            }
            if (dbFieldInfo.isAutoIncr()) {
                builder.append(buildIncrSeq(dbFieldInfo, ddlInfo));
            }

            if (AssertUtils.isNotBlank(dbFieldInfo.getDefaultValue())) {
                builder.append("DEFAULT ").append(dbFieldInfo.getDefaultValue()).append(" ");
            }
            builder.append(",\n");
        });

        builder.append(fillPrimaryKey(ddlInfo));
        builder.append(");\n");

        return builder.toString();
    }

    @Override
    public String generateTableIndexDDL(DbTableInfo ddlInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append("-- CREATE INDEX\n");
        ddlInfo.getIndexList().stream().forEach(dbIndexInfo -> {
            if (dbIndexInfo.getIndex().getCode().equals(IndexType.UNIQUE.getCode())) {
                builder.append("CREATE UNIQUE INDEX ");
            }
            if (dbIndexInfo.getIndex().getCode().equals(IndexType.NORMAL.getCode())) {
                builder.append("CREATE INDEX ");
            }

            builder.append(dbIndexInfo.getIndexName().trim()).append(" ON ").append(ddlInfo.getTableName()).append("(");

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

    private String generateIncrSequenceDDL(DbTableInfo ddlInfo) {
        Optional<DbTableInfo.DBFieldInfo> autoIncrFieldOptional = ddlInfo.getFieldInfo().stream().filter(DbTableInfo.DBFieldInfo::isAutoIncr).findFirst();
        if (autoIncrFieldOptional.isPresent()) {
            DbTableInfo.DBFieldInfo autoIncrField = autoIncrFieldOptional.get();
            return "-- CREATE SEQUENCE\nCREATE SEQUENCE " + ddlInfo.getSchema() + "." + ddlInfo.getTableName() + "_" + autoIncrField.getName() + "_SEQ;" + "\n";
        }
        return "";
    }

    private String fillPrimaryKey(DbTableInfo ddlInfo) {
        Optional<DbTableInfo.DBFieldInfo> primaryKeyOptional = ddlInfo.getFieldInfo().stream().filter(DbTableInfo.DBFieldInfo::isPrimaryKey).findFirst();
        if (primaryKeyOptional.isPresent()) {
            DbTableInfo.DBFieldInfo primaryKey = primaryKeyOptional.get();
            return "    CONSTRAINT C_" + ddlInfo.getTableName() + "_PRIMARY PRIMARY KEY" + "(\"" + primaryKey.getName() + "\")\n";
        }

        return "";
    }

    private String buildIncrSeq(DbTableInfo.DBFieldInfo dbFieldInfo, DbTableInfo ddlInfo) {
        return "DEFAULT \"" + ddlInfo.getSchema() + "\".\"" + ddlInfo.getTableName() + "_" + dbFieldInfo.getName() + "_SEQ\".NEXTVAL ";
    }

    @Override
    public String getSymbol() {
        return "\"";
    }
}
