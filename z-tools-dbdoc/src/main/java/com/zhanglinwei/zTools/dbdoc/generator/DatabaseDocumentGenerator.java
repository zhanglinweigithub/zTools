package com.zhanglinwei.zTools.dbdoc.generator;

import com.zhanglinwei.zTools.dbdoc.model.ColumnInfo;
import com.zhanglinwei.zTools.dbdoc.model.TableInfo;
import com.zhanglinwei.zTools.common.doc.TemplateDocWriter;
import com.zhanglinwei.zTools.common.enums.DocumentType;
import com.zhanglinwei.zTools.common.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.zhanglinwei.zTools.common.constant.StringPool.AMPERSAND;
import static com.zhanglinwei.zTools.common.constant.StringPool.HTML_GT;
import static com.zhanglinwei.zTools.common.constant.StringPool.HTML_LT;
import static com.zhanglinwei.zTools.common.constant.StringPool.LEFT_CHEV;
import static com.zhanglinwei.zTools.common.constant.StringPool.RIGHT_CHEV;

/**
 * 数据库文档写出。与具体数据库种类无关，只负责 HTML / Markdown / Word。
 */
public final class DatabaseDocumentGenerator {

    private static final String TEMPLATE_DIR = "/template/db";
    /** StringPool.HTML_AMP 没有分号，这里必须写完整实体，否则 XML 仍不合法。 */
    private static final String AMP_ENTITY = "&amp;";

    /**
     * 工具类，禁止实例化。
     */
    private DatabaseDocumentGenerator() {}

    /**
     * 按文档类型选用模板写出文件。Word/HTML 会先转义表名、注释里的 {@code <>&}，Markdown 保持原文。
     *
     * @param tableInfos 表结构
     * @param path       输出文件完整路径
     * @param docType    文档类型字符串，见 {@link DocumentType}
     * @return 写出成功为 {@code true}
     * @throws Exception 模板或 IO 失败
     */
    public static boolean write(Collection<TableInfo> tableInfos, String path, String docType) throws Exception {
        DocumentType documentType = DocumentType.of(docType);
        if (documentType == DocumentType.WORD || documentType == DocumentType.HTML) {
            escapeXml(tableInfos);
        }
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("tableList", tableInfos);
        return TemplateDocWriter.writeUtf8(
                DatabaseDocumentGenerator.class,
                TEMPLATE_DIR,
                templateName(documentType),
                dataModel,
                path
        );
    }

    /**
     * 文档类型对应的文件后缀。
     *
     * @param docType 文档类型字符串
     * @return 如 {@code .md}、{@code .html}
     */
    public static String suffixOf(String docType) {
        return DocumentType.of(docType).getSuffix();
    }

    /**
     * 按文档类型选择 classpath 模板名。
     *
     * @param documentType 文档类型
     * @return 模板文件名
     */
    private static String templateName(DocumentType documentType) {
        switch (documentType) {
            case HTML:
                return "db-doc-html.ftl";
            case WORD:
                return "db-doc-word.ftl";
            case MARKDOWN:
            default:
                return "db-doc-md.ftl";
        }
    }

    /**
     * 注释里的 {@code List<Long>} 等会把 Word/HTML 的标签结构拆掉，Markdown 保持原文。
     *
     * @param tableInfos 待转义的表集合
     */
    private static void escapeXml(Collection<TableInfo> tableInfos) {
        if (CollectionUtils.isEmpty(tableInfos)) {
            return;
        }
        for (TableInfo table : tableInfos) {
            if (table == null) {
                continue;
            }
            table.setTableName(escape(table.getTableName()));
            table.setTableComment(escape(table.getTableComment()));
            for (ColumnInfo column : table.getColumns()) {
                column.setColumnName(escape(column.getColumnName()));
                column.setDataType(escape(column.getDataType()));
                column.setIsNullable(escape(column.getIsNullable()));
                column.setColumnDefault(escape(column.getColumnDefault()));
                column.setColumnComment(escape(column.getColumnComment()));
            }
        }
    }

    /**
     * 必须先转 {@code &}，否则 {@code &lt;} 会被二次替换。
     *
     * @param value 原文，可为 {@code null}
     * @return 转义后的文本；输入为 {@code null} 则仍为 {@code null}
     */
    private static String escape(String value) {
        return value == null ? null : value
                .replace(AMPERSAND, AMP_ENTITY)
                .replace(LEFT_CHEV, HTML_LT)
                .replace(RIGHT_CHEV, HTML_GT);
    }
}
