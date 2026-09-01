# 数据库表结构文档

<#list tableList as table>

## ${table_index + 1}、表 ${table.tableName}${table.tableComment?has_content?then(' (' + table.tableComment + ')', '')}

<#if table.columns?? && table.columns?has_content>

| 序号 | 名称 | 数据类型 | 长度 | 小数位 | 允许空值 | 主键 | 默认值 | 说明 |
| :--: | :--: | :------: | :--: | :----: | :------: | :--: | :----: | :--: |
<#list table.columns as column>
|  ${column_index + 1}   |  ${column.columnName!''}  |    ${column.dataType!''}   |  ${column.characterMaximumLength!column.numericPrecision!'-'}  |   ${column.numericScale!'-'}   |    ${column.isNullable?substring(0, 1)!''}    |  ${column.isPrimaryKeyAsString()!''}  |   ${column.columnDefault!''}   |  ${column.columnComment!''}  |
</#list>
<#else>
无数据列
</#if>
</#list>