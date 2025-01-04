package com.zhanglinwei.zTools.jsontoclass.model;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaClass
 */
public class JavaClass {

    private String suffix = ".java";

    /**
     * 包名
     */
    private String packageName;

    /**
     * 引入的包
     */
    private final List<String> importPackageList = Lists.newArrayList("import lombok.Data;", "import java.math.BigDecimal;", "import java.time.LocalDateTime;", "import java.util.List;", "import java.util.Map;");

    /**
     * 类名
     */
    private String className;

    /**
     * 类中字段属性
     */
    private List<Property> properties = new ArrayList<>();

    private List<JavaClass> innerClassList = new ArrayList<>();

    /**
     * 类注解
     */
    private List<String> annotations = Lists.newArrayList("@Data");


    public JavaClass() {
    }

    public JavaClass(String className, String packageName) {
        this.className = className;
        this.packageName = packageName;
    }

    public String toJavaFileStr() {
        StringBuilder builder = new StringBuilder();

        builder.append("package ").append(this.packageName).append(";\n\n");

        importPackageList.forEach(item -> {
            builder.append(item).append("\n");
        });
        builder.append("import " + this.packageName + ".*;\n\n");

        annotations.forEach(item -> {
            builder.append(item).append("\n");
        });

        builder.append("public class ").append(this.className).append(" {\n\n");
        properties.forEach(property -> {
            builder.append("    private ").append(property.toJavaStr());
        });
        builder.append("}");

        return builder.toString();
    }

    public String toJavaInnerStr() {
        StringBuilder builder = new StringBuilder();

        builder.append("package ").append(this.packageName).append(";\n\n");

        importPackageList.forEach(item -> {
            builder.append(item).append("\n");
        });
        builder.append("\n");

        annotations.forEach(item -> {
            builder.append(item).append("\n");
        });

        builder.append("public class ").append(this.className).append(" {\n\n");
        properties.forEach(property -> {
            builder.append("    private ").append(property.toJavaStr());
        });
        builder.append("\n");

        innerClassList.forEach(innerClass -> {
            innerClass.annotations.forEach(item -> {
                builder.append("    ").append(item).append("\n");
            });

            builder.append("    public static class ").append(innerClass.getClassName()).append(" {\n\n");
            innerClass.properties.forEach(property -> {
                builder.append("        private ").append(property.toJavaStr());
            });
            builder.append("    }\n\n");
        });

        builder.append("}");

        return builder.toString();
    }

    public void addProperty(Property property) {
        this.properties.add(property);
    }

    public String getClassName() {
        return className;
    }

    public String getFullClassName() {
        return className + suffix;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<JavaClass> getInnerClassList() {
        return innerClassList;
    }

    public void setInnerClassList(List<JavaClass> innerClassList) {
        this.innerClassList = innerClassList;
    }

}
