package com.zhanglinwei.zTools.common.constants;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class NormalType {

    private NormalType() {}

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
        NORMAL_TYPE_MAP.put("Boolean", false);
        NORMAL_TYPE_MAP.put("Byte", 0);
        NORMAL_TYPE_MAP.put("Short", (short) 0);
        NORMAL_TYPE_MAP.put("Integer", 0);
        NORMAL_TYPE_MAP.put("Long", 0L);
        NORMAL_TYPE_MAP.put("Float", 0.0F);
        NORMAL_TYPE_MAP.put("Double", 0.0D);
        NORMAL_TYPE_MAP.put("String", "stringValue");
        NORMAL_TYPE_MAP.put("BigDecimal", 0.111111);
        NORMAL_TYPE_MAP.put("Date", new Date().getTime());
        NORMAL_TYPE_MAP.put("LocalDateTime", LocalDateTime.now().toString());
        NORMAL_TYPE_MAP.put("LocalDate", LocalDate.now().toString());
        NORMAL_TYPE_MAP.put("LocalTime", LocalTime.now().toString());
        NORMAL_TYPE_MAP.put("Timestamp", Timestamp.valueOf(LocalDateTime.now()));
        NORMAL_TYPE_MAP.put("BigInteger", 0);
        NORMAL_TYPE_MAP.put("MultipartFile", "文件");
    }

    public static Object get(String key) {
        return NORMAL_TYPE_MAP.get(key);
    }

    public static boolean containsKey(String key) {
        return NORMAL_TYPE_MAP.containsKey(key);
    }
}
