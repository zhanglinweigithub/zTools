package com.zhanglinwei.zTools.model;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiNameValuePair;
import com.zhanglinwei.zTools.constant.CustomAnnotation;
import com.zhanglinwei.zTools.constant.IndexType;
import com.zhanglinwei.zTools.constant.MyBatisPlusAnnotation;
import com.zhanglinwei.zTools.util.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DDL对象
 */
public class DDLInfo {

    /**
     * 表名
     */
    private String tableName;

    /**
     * schema
     */
    private String schema;

    /**
     * 表字段
     */
    private List<DBFieldInfo> fieldInfo = new ArrayList<>();

    /**
     * 表索引
     */
    private List<DBIndexInfo> indexList = new ArrayList<>();

    public DDLInfo() {
    }

    public DDLInfo(PsiField[] psiFields, PsiClass psiClass) {
        PsiAnnotation annotation = AnnotationUtil.getAnnotationByName(psiClass.getAnnotations(), MyBatisPlusAnnotation.TableName);
        this.tableName = AnnotationUtil.getOrDefaultAttrValueByAnnotation(annotation, "value", CommonUtils.convertCamelToSnake(psiClass.getName()));

        // 过滤 static、final、exist 字段
        this.fieldInfo = Arrays.stream(psiFields)
                .filter(psiField -> !FieldUtil.isStaticOrFinalField(psiField))
                .filter(this::isExistInDB)
                .map(DBFieldInfo::new)
                .collect(Collectors.toList());

        List<PsiAnnotation> dbIndexAnnotationList = AnnotationUtil.getAnnotationListByName(psiClass.getAnnotations(), CustomAnnotation.DBIndex);

        this.indexList = buildDBIndex(dbIndexAnnotationList);
    }

    private List<DBIndexInfo> buildDBIndex(List<PsiAnnotation> dbIndexAnnotationList) {
        if (AssertUtils.isNotEmpty(dbIndexAnnotationList)) {
            return dbIndexAnnotationList.stream()
                    .map(DBIndexInfo::new)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private boolean isExistInDB(PsiField psiField) {
        PsiAnnotation tableFieldAnnotation = AnnotationUtil.getAnnotationByName(psiField.getAnnotations(), MyBatisPlusAnnotation.TableField);
        if (tableFieldAnnotation == null) {
            return true;
        }
        String attrValue = AnnotationUtil.getOrDefaultAttrValueByAnnotation(tableFieldAnnotation, "exist", "true");
        return !"false".equals(attrValue);
    }

    /**
     * 表字段
     */
    public static class DBFieldInfo {
        /**
         * 字段名
         */
        private String name;

        /**
         * 字段类型
         */
        private String type;

        /**
         * 是否自增
         */
        private boolean autoIncr;

        /**
         * 是否必填
         */
        private boolean required;

        /**
         * 默认值
         */
        private String defaultValue = "NULL";

        /**
         * 注释
         */
        private String desc;

        /**
         * 是否更新时操作
         */
        private boolean onUpdate;

        /**
         * 是否主键
         */
        private boolean primaryKey;

        public DBFieldInfo() {
        }

        public DBFieldInfo(PsiField psiField) {
            PsiAnnotation tableIdAnnotation = AnnotationUtil.getAnnotationByName(psiField.getAnnotations(), MyBatisPlusAnnotation.TableId);
            boolean isTableId = tableIdAnnotation != null;
            if (isTableId) {
                this.defaultValue = null;
                this.autoIncr = isAutoIncrField(tableIdAnnotation);
                this.name = AnnotationUtil.getOrDefaultAttrValueByAnnotation(tableIdAnnotation, null, CommonUtils.convertCamelToSnake(psiField.getName()));
            } else {
                PsiAnnotation tableFieldAnnotation = AnnotationUtil.getAnnotationByName(psiField.getAnnotations(), MyBatisPlusAnnotation.TableField);
                this.name = AnnotationUtil.getOrDefaultAttrValueByAnnotation(tableFieldAnnotation, null, CommonUtils.convertCamelToSnake(psiField.getName()));
            }
            this.type = convertToDbType(psiField.getType().getPresentableText());
            this.desc = DesUtil.getDescription(psiField.getDocComment());
            this.onUpdate = false;
            this.primaryKey = isTableId;
        }

        private boolean isAutoIncrField(PsiAnnotation tableIdAnnotation) {
            if (tableIdAnnotation != null) {
                for (PsiNameValuePair nameValuePair : tableIdAnnotation.getParameterList().getAttributes()) {
                    if ("type".equals(nameValuePair.getName()) && nameValuePair.getValue().getText().contains("AUTO")) {
                        return true;
                    }
                }
            }

            return false;
        }

        private String convertToDbType(String javaType) {
            switch (javaType) {
                case "String":
                case "Character":
                case "char":
                    return "varchar(255)";

                case "Integer":
                case "int":
                    return "int";

                case "Long":
                case "long":
                    return "bigint";

                case "BigDecimal":
                case "Double":
                case "double":
                case "Float":
                case "float":
                    return "decimal(10,2)";

                case "Boolean":
                case "boolean":
                case "Short":
                case "short":
                case "Byte":
                case "byte":
                    return "tinyint";

                case "Date":
                case "DateTime":
                case "Timestamp":
                case "Time":
                case "Locale":
                case "LocalDateTime":
                case "LocalDate":
                case "LocalTime":
                    return "timestamp";
                default:
                    return "varchar(1000)";
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isAutoIncr() {
            return autoIncr;
        }

        public void setAutoIncr(boolean autoIncr) {
            this.autoIncr = autoIncr;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public boolean isOnUpdate() {
            return onUpdate;
        }

        public void setOnUpdate(boolean onUpdate) {
            this.onUpdate = onUpdate;
        }

        public boolean isPrimaryKey() {
            return primaryKey;
        }

        public void setPrimaryKey(boolean primaryKey) {
            this.primaryKey = primaryKey;
        }
    }

    /**
     * 表索引
     */
    public static class DBIndexInfo {
        /**
         * 索引类型
         */
        private IndexType index;

        /**
         * 索引名称
         */
        private String indexName;

        /**
         * 索引字段
         */
        private List<String> fieldNameList = new ArrayList<>();

        public DBIndexInfo() {
        }

        public DBIndexInfo(PsiAnnotation psiAnnotation) {
            String typeValue = AnnotationUtil.getOrDefaultAttrValueByAnnotation(psiAnnotation, "type", "normal");
            String nameValue = AnnotationUtil.getOrDefaultAttrValueByAnnotation(psiAnnotation, "name", "");
            String fieldsValue = AnnotationUtil.getOrDefaultAttrValueByAnnotation(psiAnnotation, "fields", "");
            this.index = IndexType.findByCode(typeValue);
            this.indexName = nameValue;
            this.fieldNameList = Arrays.stream(
                            fieldsValue
                                    .replaceAll("\"", "")
                                    .replace("{", "")
                                    .replaceAll("}", "")
                                    .split(","))
                    .map(CommonUtils::convertCamelToSnake)
                    .collect(Collectors.toList());
        }

        public IndexType getIndex() {
            return index;
        }

        public void setIndex(IndexType index) {
            this.index = index;
        }

        public String getIndexName() {
            return indexName;
        }

        public void setIndexName(String indexName) {
            indexName = indexName;
        }

        public List<String> getFieldNameList() {
            return fieldNameList;
        }

        public void setFieldNameList(List<String> fieldNameList) {
            this.fieldNameList = fieldNameList;
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableNae) {
        this.tableName = tableNae;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public List<DBFieldInfo> getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(List<DBFieldInfo> fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public List<DBIndexInfo> getIndexList() {
        return indexList;
    }

    public void setIndexList(List<DBIndexInfo> indexList) {
        this.indexList = indexList;
    }
}
