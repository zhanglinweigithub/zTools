package com.zhanglinwei.zTools.common.constant;

import com.zhanglinwei.zTools.common.util.TypeUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 文档 / 示例 JSON 生成时使用的「普通类型」默认值表。
 * <p>
 * 覆盖 Java 基本类型、包装类、常用时间 / 数值 / 标识类型、以及 {@code MultipartFile}。
 * 这里的值是便于阅读的占位，不是语言层面的零值语义。
 * {@code Object} 在表里占位，{@link #exampleOf} 每次新建空 Map，避免共享可变实例。
 *
 * <pre>
 *   NormalType.get("String") → "stringValue"
 *   NormalType.get("UUID") → "00000000-0000-0000-0000-000000000000"
 *   NormalType.exampleOf("Map&lt;String, Object&gt;") → {}
 *   NormalType.exampleOf("Object") → {}
 *   NormalType.containsKey("User") → false
 * </pre>
 */
public final class NormalType {

    /** 工具类，禁止实例化 */
    private NormalType() {}

    /** 简单类型名 → 示例值，例如 {@code String} → {@code stringValue} */
    public static final Map<String, Object> NORMAL_TYPE_MAP = new HashMap<>();

    static {
        NORMAL_TYPE_MAP.put("int", 1);
        NORMAL_TYPE_MAP.put("boolean", false);
        NORMAL_TYPE_MAP.put("byte", 1);
        NORMAL_TYPE_MAP.put("short", 1);
        NORMAL_TYPE_MAP.put("long", 1L);
        NORMAL_TYPE_MAP.put("float", 1.0F);
        NORMAL_TYPE_MAP.put("double", 1.0D);
        NORMAL_TYPE_MAP.put("char", 'a');
        NORMAL_TYPE_MAP.put("Character", 'a');
        NORMAL_TYPE_MAP.put("Boolean", false);
        NORMAL_TYPE_MAP.put("Byte", 0);
        NORMAL_TYPE_MAP.put("Short", (short) 0);
        NORMAL_TYPE_MAP.put("Integer", 0);
        NORMAL_TYPE_MAP.put("Long", 0L);
        NORMAL_TYPE_MAP.put("Float", 0.0F);
        NORMAL_TYPE_MAP.put("Double", 0.0D);
        NORMAL_TYPE_MAP.put("Number", 0);
        NORMAL_TYPE_MAP.put("AtomicInteger", 0);
        NORMAL_TYPE_MAP.put("AtomicLong", 0L);
        NORMAL_TYPE_MAP.put("String", "stringValue");
        NORMAL_TYPE_MAP.put("CharSequence", "stringValue");
        NORMAL_TYPE_MAP.put("BigDecimal", 0.111111);
        NORMAL_TYPE_MAP.put("BigInteger", 0);
        NORMAL_TYPE_MAP.put("UUID", "00000000-0000-0000-0000-000000000000");
        NORMAL_TYPE_MAP.put("URI", "https://example.com");
        NORMAL_TYPE_MAP.put("URL", "https://example.com");
        NORMAL_TYPE_MAP.put("Date", "yyyy-MM-dd HH:mm:ss");
        NORMAL_TYPE_MAP.put("Time", "HH:mm:ss");
        NORMAL_TYPE_MAP.put("Timestamp", Timestamp.valueOf(LocalDateTime.now()));
        NORMAL_TYPE_MAP.put("LocalDateTime", "yyyy-MM-dd HH:mm:ss");
        NORMAL_TYPE_MAP.put("LocalDate", "yyyy-MM-dd");
        NORMAL_TYPE_MAP.put("LocalTime", "HH:mm:ss");
        NORMAL_TYPE_MAP.put("Instant", "2026-01-01T00:00:00Z");
        NORMAL_TYPE_MAP.put("OffsetDateTime", "2026-01-01T00:00:00+08:00");
        NORMAL_TYPE_MAP.put("ZonedDateTime", "2026-01-01T00:00:00+08:00[Asia/Shanghai]");
        NORMAL_TYPE_MAP.put("OffsetTime", "00:00:00+08:00");
        NORMAL_TYPE_MAP.put("Duration", "PT1S");
        NORMAL_TYPE_MAP.put("Year", "2026");
        NORMAL_TYPE_MAP.put("YearMonth", "2026-01");
        NORMAL_TYPE_MAP.put("ZoneId", "Asia/Shanghai");
        NORMAL_TYPE_MAP.put("Path", "/tmp/file");
        NORMAL_TYPE_MAP.put("File", "/tmp/file");
        NORMAL_TYPE_MAP.put("Locale", "zh_CN");
        NORMAL_TYPE_MAP.put("Charset", "UTF-8");
        NORMAL_TYPE_MAP.put("Object", Collections.emptyMap());
        NORMAL_TYPE_MAP.put("MultipartFile", "文件");
    }

    /**
     * 按简单类型名取示例值。
     *
     * <pre>
     *   get("String") → "stringValue"
     *   get("int") → 1
     *   get("User") → null
     * </pre>
     *
     * @param key 简单类型名，如 {@code String}、{@code int}
     * @return 示例值；未知类型返回 {@code null}
     */
    public static Object get(String key) {
        return NORMAL_TYPE_MAP.get(key);
    }

    /**
     * 按声明类型给出 JSON 示例值。Map（含 {@code List<Map<...>>} 剥开后的 Map）给空对象；
     * 其余查表。每次调用都新建 Map，避免调用方改到共享实例。
     *
     * <pre>
     *   exampleOf("Number") → 0
     *   exampleOf("Object") → {}
     *   exampleOf("Map&lt;String, Object&gt;") → {}
     *   exampleOf("HashMap") → {}
     *   exampleOf("List&lt;Map&lt;String, Object&gt;&gt;") → {}（再由调用方按 nestDepth 包成 [{}]）
     *   exampleOf("User") → null
     * </pre>
     *
     * @param type presentable 类型文本
     * @return 示例值；未知类型返回 {@code null}
     */
    public static Object exampleOf(String type) {
        if (type == null) {
            return null;
        }
        String raw = TypeUtils.rawType(type);
        if (TypeUtils.isMap(type) || TypeUtils.isMap(raw) || "Object".equals(raw)) {
            return new LinkedHashMap<String, Object>();
        }
        return get(raw);
    }

    /**
     * 是否属于可直接给出示例值的普通类型。
     *
     * <pre>
     *   containsKey("Long") → true
     *   containsKey("User") → false
     * </pre>
     *
     * @param key 简单类型名
     * @return 表中存在则为 {@code true}
     */
    public static boolean containsKey(String key) {
        return NORMAL_TYPE_MAP.containsKey(key);
    }
}
