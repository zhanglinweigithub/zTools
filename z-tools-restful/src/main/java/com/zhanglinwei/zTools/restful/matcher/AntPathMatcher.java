package com.zhanglinwei.zTools.restful.matcher;

import com.zhanglinwei.zTools.common.constant.CharacterPool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zhanglinwei.zTools.common.constant.StringPool.*;

/**
 * Ant 风格路径匹配器（对齐 Spring {@code AntPathMatcher}）。
 * <p>
 * 用于 GoTo 窗口：接口路径作为 pattern，用户输入作为 path。
 * <ul>
 *     <li>{@code /user/{id}} 匹配 {@code /user/123}</li>
 *     <li>{@code /api/**} 匹配 {@code /api/user/list}</li>
 *     <li>{@code /user/?} 匹配 {@code /user/a}</li>
 * </ul>
 * 路径拼接本身由 {@code RequestPathUtils} 完成，例如 {@code /api} + {@code /user/{id}} → {@code /api/user/{id}}。
 */
public class AntPathMatcher {

    /** 默认路径分隔符 {@code /} */
    public static final String DEFAULT_PATH_SEPARATOR = SLASH;
    private static final String[] EMPTY_STRING_ARRAY = {};
    /** 通配字符：{@code *} {@code ?} {@code {} */
    private static final char[] WILDCARD_CHARS = {CharacterPool.STAR, CharacterPool.QUESTION_MARK, CharacterPool.LEFT_BRACE};

    /** 是否区分大小写 */
    private boolean caseSensitive = true;
    /** 分词后是否 trim */
    private boolean trimTokens = false;
    /** 路径分隔符 */
    private String pathSeparator;
    /** 是否缓存已编译的 pattern；过大时自动关闭 */
    private volatile Boolean cachePatterns;
    private final Map<String, String[]> tokenizedPatternCache = new ConcurrentHashMap<>(256);
    final Map<String, AntPathStringMatcher> stringMatcherCache = new ConcurrentHashMap<>(256);
    private static final int CACHE_TURNOFF_THRESHOLD = 65536;

    /**
     * 使用默认分隔符 {@code /} 构造。
     */
    public AntPathMatcher() {
        this.pathSeparator = DEFAULT_PATH_SEPARATOR;
    }

    /**
     * 判断 path 是否完整匹配 pattern。
     *
     * @param pattern Ant 路径模板，如 {@code /user/{id}}
     * @param path    实际路径，如 {@code /user/123}
     * @return 匹配则为 {@code true}
     */
    public boolean match(String pattern, String path) {
        return doMatch(pattern, path, true, null);
    }

    /**
     * 按段匹配 pattern 与 path，支持 {@code *}、{@code **}、{@code {var}}。
     *
     * @param pattern              Ant 路径模板
     * @param path                 实际路径
     * @param fullMatch            是否要求整段匹配
     * @param uriTemplateVariables 用于收集 {@code {id}} 捕获值，可为 {@code null}
     * @return 匹配则为 {@code true}
     */
    protected boolean doMatch(String pattern, String path, boolean fullMatch, Map<String, String> uriTemplateVariables) {
        if (path == null || path.startsWith(this.pathSeparator) != pattern.startsWith(this.pathSeparator)) {
            return false;
        }

        String[] pattDirs = tokenizePattern(pattern);
        if (fullMatch && this.caseSensitive && !isPotentialMatch(path, pattDirs)) {
            return false;
        }

        String[] pathDirs = tokenizePath(path);
        int pattIdxStart = 0;
        int pattIdxEnd = pattDirs.length - 1;
        int pathIdxStart = 0;
        int pathIdxEnd = pathDirs.length - 1;

        // 从左向右匹配，直到遇到 **
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String pattDir = pattDirs[pattIdxStart];
            if (STAR_STAR.equals(pattDir)) {
                break;
            }
            if (!matchStrings(pattDir, pathDirs[pathIdxStart], uriTemplateVariables)) {
                return false;
            }
            pattIdxStart++;
            pathIdxStart++;
        }

        if (pathIdxStart > pathIdxEnd) {
            if (pattIdxStart > pattIdxEnd) {
                return (pattern.endsWith(this.pathSeparator) == path.endsWith(this.pathSeparator));
            }
            if (!fullMatch) {
                return true;
            }
            if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals(STAR) && path.endsWith(this.pathSeparator)) {
                return true;
            }
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals(STAR_STAR)) {
                    return false;
                }
            }
            return true;
        }
        else if (pattIdxStart > pattIdxEnd) {
            return false;
        }
        else if (!fullMatch && STAR_STAR.equals(pattDirs[pattIdxStart])) {
            return true;
        }

        // 从右向左匹配，直到遇到 **
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String pattDir = pattDirs[pattIdxEnd];
            if (pattDir.equals(STAR_STAR)) {
                break;
            }
            if (!matchStrings(pattDir, pathDirs[pathIdxEnd], uriTemplateVariables)) {
                return false;
            }
            pattIdxEnd--;
            pathIdxEnd--;
        }
        if (pathIdxStart > pathIdxEnd) {
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals(STAR_STAR)) {
                    return false;
                }
            }
            return true;
        }

        // 两段 ** 之间的中间片段
        while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            int patIdxTmp = -1;
            for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
                if (pattDirs[i].equals(STAR_STAR)) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == pattIdxStart + 1) {
                pattIdxStart++;
                continue;
            }

            int patLength = (patIdxTmp - pattIdxStart - 1);
            int strLength = (pathIdxEnd - pathIdxStart + 1);
            int foundIdx = -1;

            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = pattDirs[pattIdxStart + j + 1];
                    String subStr = pathDirs[pathIdxStart + i + j];
                    if (!matchStrings(subPat, subStr, uriTemplateVariables)) {
                        continue strLoop;
                    }
                }
                foundIdx = pathIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            pattIdxStart = patIdxTmp;
            pathIdxStart = foundIdx + patLength;
        }

        for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
            if (!pattDirs[i].equals(STAR_STAR)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 单段字符串匹配，委托给 {@link AntPathStringMatcher}。
     *
     * @param pattern              单段模板，如 {@code {id}}、{@code *}
     * @param str                  实际段
     * @param uriTemplateVariables URI 变量收集表，可为 {@code null}
     * @return 匹配则为 {@code true}
     */
    private boolean matchStrings(String pattern, String str, Map<String, String> uriTemplateVariables) {

        return getStringMatcher(pattern).matchStrings(str, uriTemplateVariables);
    }

    /**
     * 获取（或创建并缓存）单段匹配器。
     *
     * @param pattern 单段模板
     * @return 对应的 {@link AntPathStringMatcher}
     */
    protected AntPathStringMatcher getStringMatcher(String pattern) {
        AntPathStringMatcher matcher = null;
        Boolean cachePatterns = this.cachePatterns;
        if (cachePatterns == null || cachePatterns.booleanValue()) {
            matcher = this.stringMatcherCache.get(pattern);
        }
        if (matcher == null) {
            matcher = new AntPathStringMatcher(pattern, this.caseSensitive);
            if (cachePatterns == null && this.stringMatcherCache.size() >= CACHE_TURNOFF_THRESHOLD) {
                deactivatePatternCache();
                return matcher;
            }
            if (cachePatterns == null || cachePatterns.booleanValue()) {
                this.stringMatcherCache.put(pattern, matcher);
            }
        }
        return matcher;
    }

    /**
     * 快速判断 path 是否可能匹配，避免无谓的完整匹配。
     *
     * @param path     实际路径
     * @param pattDirs 已分词的模板段
     * @return 有可能匹配则为 {@code true}
     */
    private boolean isPotentialMatch(String path, String[] pattDirs) {
        if (!this.trimTokens) {
            int pos = 0;
            for (String pattDir : pattDirs) {
                int skipped = skipSeparator(path, pos, this.pathSeparator);
                pos += skipped;
                skipped = skipSegment(path, pos, pattDir);
                if (skipped < pattDir.length()) {
                    return (skipped > 0 || (pattDir.length() > 0 && isWildcardChar(pattDir.charAt(0))));
                }
                pos += skipped;
            }
        }
        return true;
    }

    /**
     * 跳过连续分隔符。
     *
     * @param path      路径
     * @param pos       起始下标
     * @param separator 分隔符
     * @return 跳过的字符数
     */
    private int skipSeparator(String path, int pos, String separator) {
        int skipped = 0;
        while (path.startsWith(separator, pos + skipped)) {
            skipped += separator.length();
        }
        return skipped;
    }

    /**
     * 按字面前缀跳过一段，遇到通配符即停。
     *
     * @param path   路径
     * @param pos    起始下标
     * @param prefix 模板段
     * @return 跳过的字符数
     */
    private int skipSegment(String path, int pos, String prefix) {
        int skipped = 0;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (isWildcardChar(c)) {
                return skipped;
            }
            int currPos = pos + skipped;
            if (currPos >= path.length()) {
                return 0;
            }
            if (c == path.charAt(currPos)) {
                skipped++;
            }
        }
        return skipped;
    }

    /**
     * 是否为通配字符 {@code *}、{@code ?}、{@code {}。
     *
     * @param c 待判断字符
     * @return 是通配符则为 {@code true}
     */
    private boolean isWildcardChar(char c) {
        for (char candidate : WILDCARD_CHARS) {
            if (c == candidate) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将 pattern 按分隔符分词，结果可缓存。
     *
     * @param pattern 路径模板
     * @return 分段数组
     */
    protected String[] tokenizePattern(String pattern) {
        String[] tokenized = null;
        Boolean cachePatterns = this.cachePatterns;
        if (cachePatterns == null || cachePatterns.booleanValue()) {
            tokenized = this.tokenizedPatternCache.get(pattern);
        }
        if (tokenized == null) {
            tokenized = tokenizePath(pattern);
            if (cachePatterns == null && this.tokenizedPatternCache.size() >= CACHE_TURNOFF_THRESHOLD) {
                deactivatePatternCache();
                return tokenized;
            }
            if (cachePatterns == null || cachePatterns.booleanValue()) {
                this.tokenizedPatternCache.put(pattern, tokenized);
            }
        }
        return tokenized;
    }

    /**
     * 将路径按分隔符分词。
     *
     * @param path 路径
     * @return 分段数组
     */
    protected String[] tokenizePath(String path) {
        return tokenizeToStringArray(path, this.pathSeparator, this.trimTokens, true);
    }

    /**
     * 按分隔符把字符串拆成数组。
     *
     * @param str               源字符串
     * @param delimiters        分隔符
     * @param trimTokens        是否 trim
     * @param ignoreEmptyTokens 是否忽略空段
     * @return 分段数组，源为 {@code null} 时为空数组
     */
    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        if (str == null) {
            return EMPTY_STRING_ARRAY;
        }

        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    /**
     * 集合转数组。
     *
     * @param collection 源集合
     * @return 字符串数组，空集合时为空数组
     */
    public static String[] toStringArray(Collection<String> collection) {
        return (collection != null && !collection.isEmpty() ? collection.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY);
    }

    /**
     * 缓存过大时关闭并清空 pattern 缓存。
     */
    private void deactivatePatternCache() {
        this.cachePatterns = false;
        this.tokenizedPatternCache.clear();
        this.stringMatcherCache.clear();
    }

    /**
     * 单段 Ant 模板匹配器：把 {@code ?}、{@code *}、{@code {var}} 转成正则。
     */
    protected static class AntPathStringMatcher {

        private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");

        private static final String DEFAULT_VARIABLE_PATTERN = "((?s).*)";

        /** 原始模板 */
        private final String rawPattern;

        /** 是否区分大小写 */
        private final boolean caseSensitive;

        /** 不含通配符时走精确相等 */
        private final boolean exactMatch;

        /** 编译后的正则，精确匹配时为 {@code null} */
        private final Pattern pattern;

        /** URI 模板变量名，与捕获组一一对应 */
        private final List<String> variableNames = new ArrayList<>();

        /**
         * 默认区分大小写。
         *
         * @param pattern 单段模板
         */
        public AntPathStringMatcher(String pattern) {
            this(pattern, true);
        }

        /**
         * 把 glob 转成正则。
         * <p>
         * {@code ?} → {@code .}，{@code *} → {@code .*}，{@code {id}} → 捕获组。
         *
         * @param pattern       单段模板
         * @param caseSensitive 是否区分大小写
         */
        public AntPathStringMatcher(String pattern, boolean caseSensitive) {
            this.rawPattern = pattern;
            this.caseSensitive = caseSensitive;
            StringBuilder patternBuilder = new StringBuilder();
            Matcher matcher = GLOB_PATTERN.matcher(pattern);
            int end = 0;
            while (matcher.find()) {
                patternBuilder.append(quote(pattern, end, matcher.start()));
                String match = matcher.group();
                if (QUESTION_MARK.equals(match)) {
                    patternBuilder.append(CharacterPool.DOT);
                }
                else if (STAR.equals(match)) {
                    patternBuilder.append(DOT_STAR);
                }
                else if (match.startsWith(LEFT_BRACE) && match.endsWith(RIGHT_BRACE)) {
                    int colonIdx = match.indexOf(CharacterPool.COLON);
                    if (colonIdx == -1) {
                        patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
                        this.variableNames.add(matcher.group(1));
                    }
                    else {
                        String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
                        patternBuilder.append(CharacterPool.LEFT_BRACKET);
                        patternBuilder.append(variablePattern);
                        patternBuilder.append(CharacterPool.RIGHT_BRACKET);
                        String variableName = match.substring(1, colonIdx);
                        this.variableNames.add(variableName);
                    }
                }
                end = matcher.end();
            }

            if (end == 0) {
                this.exactMatch = true;
                this.pattern = null;
            }
            else {
                this.exactMatch = false;
                patternBuilder.append(quote(pattern, end, pattern.length()));
                this.pattern = Pattern.compile(patternBuilder.toString(),
                        Pattern.DOTALL | (this.caseSensitive ? 0 : Pattern.CASE_INSENSITIVE));
            }
        }

        /**
         * 把字面量子串做正则转义。
         *
         * @param s     源字符串
         * @param start 起始下标（含）
         * @param end   结束下标（不含）
         * @return 转义后的片段，区间为空时返回空串
         */
        private String quote(String s, int start, int end) {
            if (start == end) {
                return EMPTY;
            }
            return Pattern.quote(s.substring(start, end));
        }

        /**
         * 用编译后的正则匹配单段。
         *
         * @param str                  实际段
         * @param uriTemplateVariables 变量收集表，可为 {@code null}
         * @return 匹配则为 {@code true}
         */
        public boolean matchStrings(String str, Map<String, String> uriTemplateVariables) {
            if (this.exactMatch) {
                return this.caseSensitive ? this.rawPattern.equals(str) : this.rawPattern.equalsIgnoreCase(str);
            }
            else if (this.pattern != null) {
                Matcher matcher = this.pattern.matcher(str);
                if (matcher.matches()) {
                    if (uriTemplateVariables != null) {
                        if (this.variableNames.size() != matcher.groupCount()) {
                            throw new IllegalArgumentException("The number of capturing groups in the pattern segment " +
                                    this.pattern + " does not match the number of URI template variables it defines, " +
                                    "which can occur if capturing groups are used in a URI template regex. " +
                                    "Use non-capturing groups instead.");
                        }
                        for (int i = 1; i <= matcher.groupCount(); i++) {
                            String name = this.variableNames.get(i - 1);
                            if (name.startsWith(STAR)) {
                                throw new IllegalArgumentException("Capturing patterns (" + name + ") are not " +
                                        "supported by the AntPathMatcher. Use the PathPatternParser instead.");
                            }
                            String value = matcher.group(i);
                            uriTemplateVariables.put(name, value);
                        }
                    }
                    return true;
                }
            }
            return false;
        }

    }
}
