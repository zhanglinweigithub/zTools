<html lang="zh">

<head>
    <meta charset="UTF-8">
    <title>接口文档</title>
    <style type='text/css'>
        body {
            background-color: #fff;
            font-size: 14px;
            /* 基础字体大小调整为14px */
            line-height: 1.6;
            /* 行高调整为1.6倍 */
            color: #333;
            /* 文字颜色调整为深灰色 */
            font-family: "Consolas", "Microsoft YaHei", "SimHei", "Helvetica Neue", Arial, sans-serif, "PingFang SC"; /* 优化字体栈 */
            /* 优化字体栈 */
            margin: 0;
            padding: 0;
            display: flex;
            min-height: 100vh;
            overflow-x: auto;
        }

        #sidebar {
            width: 250px;
            background-color: #f8f9fa;
            border-right: 1px solid #e1e4e8;
            padding: 20px 15px;
            position: fixed;
            height: 100vh;
            overflow-y: auto;
            box-sizing: border-box;
            z-index: 100;
            flex-shrink: 0;
        }

        /* 主内容区域 */
        #main-content {
            flex: 1;
            margin-left: 250px;
            padding: 20px 0px;
            box-sizing: border-box;
            display: flex;
            justify-content: center;
        }

        /* 目录列表样式 */
        #table-list {
            list-style: none;
            padding: 0;
            margin: 0;
            text-align: left;
        }

        #table-list li {
            margin: 5px 0;
            padding: 5px 0;
            border-bottom: 1px dashed #e1e4e8;
        }

        #table-list a {
            color: #586069;
            text-decoration: none;
            display: block;
            padding: 3px 5px 3px 0;
            border-radius: 3px;
            transition: all 0.2s;
            text-align: left;
        }

        #table-list a:hover {
            color: #0366d6;
            background-color: #f0f3f6;
        }

        /* 当前选中的目录项 */
        #table-list a.active {
            color: #0366d6;
            font-weight: bold;
            background-color: #e1f0ff;
        }

        .content-container {
            max-width: 1000px;
            width: 100%;
            padding: 0 20px;
        }

        /* 标题通用样式 */
        h1,
        h2,
        h3,
        h4,
        h5,
        h6 {
            position: relative;
            /* 相对定位用于锚点等 */
            margin-top: 1rem;
            /* 上边距 */
            margin-bottom: 1rem;
            /* 下边距 */
            font-weight: bold;
            /* 加粗 */
            line-height: 1.4;
            /* 行高 */
            cursor: text;
            /* 文本光标 */
            white-space: pre-wrap;
            /* 保留空白但允许换行 */
            break-after: avoid-page;
            /* 避免分页时标题被分割 */
            break-inside: avoid;
            /* 避免标题内部分割 */
            orphans: 4;
            /* 避免单独一行 */
        }

        /* 各级标题字号 */
        h1 {
            font-size: 2.25em;
            /* 约31.5px (14x2.25) */
            border-bottom: 1px solid #eee;
            /* 下划线 */
            line-height: 1.2;
            margin-top: 2rem;
            margin-bottom: 1.5rem;
        }

        h2 {
            font-size: 1.75em;
            /* 约24.5px */
            border-bottom: 1px solid #eee;
            /* 下划线 */
            line-height: 1.225;
            margin-top: 1.8rem;
        }

        h3 {
            font-size: 1.5em;
            /* 约21px */
            margin-top: 1.6rem;
        }

        h4 {
            font-size: 1.25em;
            /* 约17.5px */
            margin-top: 1.4rem;
        }

        h5 {
            font-size: 1em;
            /* 14px */
            margin-top: 1.2rem;
        }

        h6 {
            font-size: 1em;
            color: #777;
            /* 14px灰色 */
            margin-top: 1rem;
        }

        /* 表格基础样式 */
        table {
            border-collapse: collapse;
            border-spacing: 0px;
            width: 100%;
            overflow: auto;
            break-inside: auto;
            text-align: left;
        }

        /* 表格单元格样式 */
        table.md-table td {
            min-width: 32px;
        }

        /* 表格行样式 */
        table tr {
            border: 1px solid #dfe2e5;
            margin: 0;
            padding: 0;
        }

        /* 表头及交替行背景色 */
        table tr:nth-child(2n),
        thead {
            background-color: #f8f8f8;
            text-align: center;
        }

        /* 表头单元格样式 */
        table th {
            font-weight: bold;
            border: 1px solid #dfe2e5;
            border-bottom: 0;
            margin: 0;
            padding: 6px 13px;
        }

        /* 表格数据单元格样式 */
        table td {
            border: 1px solid #dfe2e5;
            margin: 0;
            padding: 6px 13px;
        }

        /* 表格第一列样式 */
        table th:first-child,
        table td:first-child {
            margin-top: 0;
        }

        /* 表格最后一列样式 */
        table th:last-child,
        table td:last-child {
            margin-bottom: 0;
        }

        /* 表格容器样式 */
        figure.table-figure {
            overflow-x: auto;
            margin: 1.2em 0px;
            /*max-width: calc(100% + 16px);*/
            max-width: 100%;
            padding: 0px;
        }

        figure.table-figure>table {
            margin: 0px;
        }

        /* 无序列表基础样式 */
        ul {
            padding-left: 2em;
            /* 左侧缩进2字符 */
            margin: 0.8em 0;
            /* 上下外边距 */
            line-height: 1.6;
            /* 行高 */
            list-style-type: disc;
            /* 实心圆点 */
        }

        /* 列表项样式 */
        li {
            margin: 0.4em 0;
            /* 项间距 */
            padding-left: 0.3em;
            /* 文本与标记的距离 */
            break-inside: avoid;
            /* 避免跨页断开 */
        }

        /* 列表内段落样式 */
        li p {
            margin: 0.5em 0;
            /* 段落间距 */
            display: inline;
            /* 保持内联以正确显示标记 */
        }

        /* 嵌套列表样式 */
        ul ul {
            padding-left: 1.5em;
            /* 次级列表缩进 */
            list-style-type: circle;
            /* 空心圆点 */
            margin-top: 0.3em;
            /* 减少顶部间距 */
        }

        /* 三级嵌套列表 */
        ul ul ul {
            list-style-type: square;
            /* 方点标记 */
        }

        /* 列表中的代码块 */
        li code {
            font-size: 0.9em;
            /* 稍小的代码字体 */
            background-color: #f5f5f5;
            padding: 0.2em 0.4em;
            border-radius: 3px;
        }

        a:link,
        a:visited,
        a:active {
            color: #015fb6;
            text-decoration: none;
        }

        a:hover {
            color: #e33e06;
        }
    </style>
</head>

<body style='text-align:center;'>

    <div id="sidebar">
        <h2 style="margin-top: 0; margin-bottom: 0; border: 0">数据库表目录</h2>
        <ul id="table-list">
            <#list tableList as table>
                <li>
                    <a href="#${table_index + 1}_${table.tableName!''}">
                        ${table_index + 1}、表 ${table.tableName}${table.tableComment?has_content?then(' (' + table.tableComment + ')', '')}
                    </a>
                </li>
            </#list>
        </ul>
    </div>

    <div id="main-content">
        <div class="content-container">
            <h1>数据库表结构文档</h1>
            <div style='width: 100%; text-align:left;'>
                <#list tableList as table>
                    <div id="${table_index + 1}_${table.tableName!''}">
                        <h2 style='line-height:50px;'>${table_index + 1}、表 ${table.tableName}${table.tableComment?has_content?then(' (' + table.tableComment + ')', '')}</h2>
                    </div>
                    <!-- <h4>数据列: </h4> -->
                    <#if table.columns?? && table.columns?has_content>
                        <figure class='table-figure'>
                            <table cellspacing='1'>
                                <thead>
                                    <tr>
                                        <th>序号</th>
                                        <th>名称</th>
                                        <th>数据类型</th>
                                        <th>长度</th>
                                        <th>小数位</th>
                                        <th>允许空值</th>
                                        <th>主键</th>
                                        <th>默认值</th>
                                        <th>说明</th>
                                    </tr>
                                </thead>
                                <#list table.columns as column>
                                    <tr>
                                        <td style="text-align: center">${column_index + 1}</td>
                                        <td style="text-align: center">${column.columnName!''}</td>
                                        <td style="text-align: center">${column.dataType!''}</td>
                                        <td style="text-align: center">${column.characterMaximumLength!column.numericPrecision!'-'}</td>
                                        <td style="text-align: center">${column.numericScale!'-'}</td>
                                        <td style="text-align: center">${column.isNullable?substring(0, 1)!''}</td>
                                        <td style="text-align: center">${column.isPrimaryKeyAsString()!''}</td>
                                        <td style="text-align: center">${column.columnDefault!''}</td>
                                        <td style="text-align: center">${column.columnComment!''}</td>
                                    </tr>
                                </#list>
                            </table>
                        </figure>
                        <#else>
                            无数据列
                    </#if>
                </#list>
            </div>
        </div>
    </div>

    <footer></footer>
</body>

</html>