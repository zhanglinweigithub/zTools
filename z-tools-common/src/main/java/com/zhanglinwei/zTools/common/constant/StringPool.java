package com.zhanglinwei.zTools.common.constant;

/**
 * 常用字符串常量池，避免魔法字符串散落在各工具类中。
 * <p>
 * 仅按常量名引用即可，不要给每个常量写注释。分组大致为：标点与路径、空白换行、
 * 布尔/开关字面量、空集合占位、字符编码、HTML 实体。
 */
public interface StringPool {

    // 标点、路径与文件后缀
    String AMPERSAND = "&";
    String AND = "and";
    String AT = "@";
    String ASTERISK = "*";
    String STAR = ASTERISK;
    String STAR_STAR = "**";
    String DOT_STAR = ".*";
    String BACK_SLASH = "\\";
    String COLON = ":";
    String COLON_SPACE = ": ";
    String COMMA = ",";
    String COMMA_SPACE = ", ";
    String DASH = "-";
    String DOLLAR = "$";
    String DUN = "、";
    String DOT = ".";
    String DOT_DOT = "..";
    String DOT_CLASS = ".class";
    String DOT_JAVA = ".java";
    String DOT_XML = ".xml";
    String EMPTY = "";
    String EQUAL = "=";
    String EQUALS = "==";
    String FALSE = "false";
    String SLASH = "/";
    String DOUBLE_SLASH = "//";
    String SPACE_SLASH_SLASH_SPACE = " // ";
    String HASH = "#";
    String HAT = "^";
    String LEFT_BRACE = "{";
    String LEFT_BRACKET = "(";
    String LEFT_CHEV = "<";
    String DOT_NEWLINE = ",\n";
    String NEWLINE = "\n";
    String n = "n";
    String N = "N";
    String NO = "no";
    String NULL = "null";
    String NA = "N/A";
    String NUM = "NUM";
    String OFF = "off";
    String ON = "on";
    String PERCENT = "%";
    String PIPE = "|";
    String PLUS = "+";
    String QUESTION_MARK = "?";
    String EXCLAMATION_MARK = "!";
    String QUOTE = "\"";
    String RETURN = "\r";
    String TAB = "\t";
    String FOLD = "└";
    String RIGHT_BRACE = "}";
    String RIGHT_BRACKET = ")";
    String RIGHT_CHEV = ">";
    String SEMICOLON = ";";
    String SINGLE_QUOTE = "'";
    String BACKTICK = "`";
    String SPACE = " ";
    String SQL = "sql";
    String TILDA = "~";
    String LEFT_SQ_BRACKET = "[";
    String RIGHT_SQ_BRACKET = "]";
    String EMPTY_ARRAY = "[]";
    String EMPTY_OBJECT = "{}";
    String TRUE = "true";
    String UNDERSCORE = "_";

    // 字符编码
    String UTF_8 = "UTF-8";
    String US_ASCII = "US-ASCII";
    String ISO_8859_1 = "ISO-8859-1";
    String y = "y";
    String Y = "Y";
    String YES = "yes";
    String ONE = "1";
    String ZERO = "0";
    String DOLLAR_LEFT_BRACE = "${";
    String HASH_LEFT_BRACE = "#{";
    String CRLF = "\r\n";

    // HTML 实体与脚本片段
    String HTML_NBSP = "&nbsp;";
    String HTML_AMP = "&amp";
    String HTML_QUOTE = "&quot;";
    String HTML_LT = "&lt;";
    String HTML_GT = "&gt;";
    String SCRIPT_OPEN = "<script>";
    String SCRIPT_CLOSE = "</script>";
}
