package com.zhanglinwei.zTools.apidoc.formatter;

/**
 * Word OOXML 小片段，供 {@link WordApiDocumentFormatter} 拼示例 JSON。
 * <p>
 * 属性（paraId、字体）需与历史输出一致，否则已有 Word 文档/模板可能对不上。
 */
final class WordXmlHelper {

    /** 与原先文档一致的 Times New Roman 声明。 */
    private static final String FONTS = "<w:rFonts w:hint=\"default\" w:ascii=\"Times New Roman\" w:hAnsi=\"Times New Roman\" w:cs=\"Times New Roman\" />";

    /** 带冒号的 JSON 行：先开段落，再往里塞多个 run。 */
    static final String PARAGRAPH_OPEN = "<w:p w14:paraId=\"7C11989A\"><w:pPr><w:rPr>" + FONTS + "</w:rPr></w:pPr>";
    static final String PARAGRAPH_CLOSE = "</w:p>";

    /** 工具类，禁止实例化。 */
    private WordXmlHelper() {}

    /**
     * 一段内的一次着色文本。{@code xml:space="preserve"} 保住前后空格（JSON 缩进）。
     *
     * @param color 不带 # 的 6 位 hex
     * @param text  文本内容
     * @return {@code <w:r>} 片段
     */
    static String run(String color, String text) {
        return "<w:r><w:rPr>" + FONTS + "<w:color w:val=\"" + color + "\" /></w:rPr>"
                + "<w:t xml:space=\"preserve\">" + text + "</w:t></w:r>";
    }

    /**
     * 整行一种颜色时用：开段落 + 单个 run + 关段落。paraId 与带 key 的行不同，沿用旧模板。
     *
     * @param color 不带 # 的 6 位 hex
     * @param text  整行文本
     * @return 完整 {@code <w:p>} 段落
     */
    static String paragraph(String color, String text) {
        return "<w:p w14:paraId=\"320F952D\"><w:pPr><w:rPr>" + FONTS + "</w:rPr></w:pPr>"
                + run(color, text) + "</w:p>";
    }
}
