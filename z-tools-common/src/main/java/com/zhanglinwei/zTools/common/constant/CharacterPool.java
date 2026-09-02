package com.zhanglinwei.zTools.common.constant;

/**
 * 常用字符常量池，避免魔法字符散落在各工具类中。
 * <p>
 * 仅按常量名引用即可，不要给每个常量写注释。分组大致为：标点符号、括号、
 * 布尔/开关字符（{@code y}/{@code n}/{@code 0}/{@code 1}）、树形展示前缀 {@link #FOLD}。
 */
public interface CharacterPool {

    // 标点与符号
    char AMPERSAND = '&';
    char AT = '@';
    char STAR = '*';
    char QUOTE = '"';
    char COLON = ':';
    char COMMA = ',';
    char DASH = '-';
    char SPACE = ' ';
    char DOLLAR = '$';
    char DUN = '、';
    char DOT = '.';
    char EQUALS = '=';
    char SLASH = '/';
    char HASH = '#';
    char HAT = '^';

    // 括号与尖括号
    char LEFT_BRACE = '{';
    char LEFT_BRACKET = '(';
    char LEFT_CHEV = '<';
    char n = 'n';
    char N = 'N';
    char PERCENT = '%';
    char PIPE = '|';
    char PLUS = '+';
    char QUESTION_MARK = '?';
    char EXCLAMATION_MARK = '!';
    // 树形文档折叠前缀
    char FOLD = '└';
    char RIGHT_BRACE = '}';
    char RIGHT_BRACKET = ')';
    char RIGHT_CHEV = '>';
    char SEMICOLON = ';';
    char BACKTICK = '`';
    char TILDA = '~';
    char LEFT_SQ_BRACKET = '[';
    char RIGHT_SQ_BRACKET = ']';
    char UNDERSCORE = '_';

    // 布尔 / 开关相关
    char y = 'y';
    char Y = 'Y';
    char ONE = '1';
    char ZERO = '0';
    
}
