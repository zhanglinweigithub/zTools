package com.zhanglinwei.zTools.convert.components;

import java.util.ArrayList;
import java.util.List;

/**
 * 把匹配到的属性拼成方法体文本。不依赖 PSI。
 * <p>
 * 入参整体判空后直接赋值；未匹配的目标 setter 单独成组，生成无参调用占位。
 */
public final class ConvertMethodBodyGenerator {

    private ConvertMethodBodyGenerator() {}

    /**
     * 目标变量名：类型简单名首字母小写；与入参同名时改用 {@code target}。
     *
     * <pre>
     *   destVarName("UserDO", "userDTO") → "userDO"
     *   destVarName("User", "user") → "target"
     * </pre>
     *
     * @param targetClassName 目标类型简单名
     * @param sourceVar     入参名
     * @return 局部变量名
     */
    public static String targetVarName(String targetClassName, String sourceVar) {
        if (targetClassName == null || targetClassName.isEmpty()) {
            return "target";
        }
        String destVar = Character.toLowerCase(targetClassName.charAt(0)) + targetClassName.substring(1);
        if (destVar.equals(sourceVar)) {
            return "target";
        }
        return destVar;
    }

    /**
     * 生成完整代码块（含花括号）。
     *
     * @param destType 目标简单类名
     * @param destVar  目标变量名
     * @param sourceVar 入参名
     * @param fields   属性列表，可为空
     * @return 可交给 {@code PsiElementFactory#createCodeBlockFromText} 的代码块
     */
    public static String generate(String destType, String destVar, String sourceVar, List<ConvertField> fields) {
        List<ConvertField> matched = new ArrayList<ConvertField>();
        List<ConvertField> unmatched = new ArrayList<ConvertField>();
        if (fields != null) {
            for (ConvertField field : fields) {
                if (field == null || field.getKind() == null) {
                    continue;
                }
                if (field.getKind() == ConvertField.Kind.UNMATCHED) {
                    unmatched.add(field);
                } else {
                    matched.add(field);
                }
            }
        }

        StringBuilder body = new StringBuilder();
        body.append("{\n");
        body.append("    if (").append(sourceVar).append(" == null) {\n");
        body.append("        return null;\n");
        body.append("    }\n\n");
        body.append("    ").append(destType).append(' ').append(destVar)
                .append(" = new ").append(destType).append("();\n");
        appendGroup(body, null, matched, destVar, sourceVar);
        appendGroup(body, "unmatched", unmatched, destVar, sourceVar);
        body.append('\n');
        body.append("    return ").append(destVar).append(";\n");
        body.append("}");
        return body.toString();
    }

    private static void appendGroup(StringBuilder body, String comment, List<ConvertField> group,
                                    String destVar, String sourceVar) {
        if (group.isEmpty()) {
            return;
        }
        body.append('\n');
        if (comment != null) {
            body.append("    // ").append(comment).append('\n');
        }
        for (ConvertField field : group) {
            appendField(body, field, destVar, sourceVar);
        }
    }

    private static void appendField(StringBuilder body, ConvertField field, String destVar, String sourceVar) {
        if (field.getKind() == ConvertField.Kind.UNMATCHED) {
            body.append("    ").append(destVar).append('.').append(field.getSetterName()).append("();\n");
            return;
        }
        String getterCall = sourceVar + '.' + field.getGetterName() + "()";
        body.append("    ").append(destVar).append('.').append(field.getSetterName())
                .append('(').append(getterCall).append(");\n");
    }
}
