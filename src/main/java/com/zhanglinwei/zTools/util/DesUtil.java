package com.zhanglinwei.zTools.util;

import com.google.common.base.Strings;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import com.zhanglinwei.zTools.common.constants.CharacterPool;
import com.zhanglinwei.zTools.doc.apidoc.constant.SwaggerAnnotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.zhanglinwei.zTools.common.constants.SpringPool.*;

/**
 * 注释工具类
 */
public class DesUtil {

    private DesUtil(){}

    /**
     * 获得方法注释中的字段描述
     */
    public static Map<String, String> paramDescMapForDocComment(PsiDocComment docComment) {
        Map<String, String> paramDescMap = new HashMap<>();
        if (docComment == null) {
            return paramDescMap;
        }
        for (PsiDocTag docTag : docComment.getTags()) {
            String tagValue = docTag.getValueElement() == null ? EMPTY : docTag.getValueElement().getText();
            if ("param".equals(docTag.getName()) && AssertUtils.isNotBlank(tagValue)) {
                paramDescMap.put(tagValue, getParamDesc(docTag.getText()));
            }
        }
        return paramDescMap;
    }

    private static String getParamDesc(String tagText) {
        String[] strings = tagText.replace(STAR, EMPTY).replaceAll(" {2,}", SPACE).trim().split(SPACE);
        if (strings.length >= 3) {
            String desc = strings[2];
            return desc.replace(NEWLINE, EMPTY);
        }
        return EMPTY;
    }

    /**
     * 去除字符串首尾出现的某个字符.
     *
     * @param source  源字符串.
     * @param element 需要去除的字符.
     * @return String.
     */
    private static String trimFirstAndLastChar(String source, char element) {
        boolean beginIndexFlag;
        boolean endIndexFlag;
        do {
            if (Strings.isNullOrEmpty(source.trim()) || source.equals(String.valueOf(element))) {
                source = EMPTY;
                break;
            }
            int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
            int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source.lastIndexOf(element) : source.length();
            source = source.substring(beginIndex, endIndex);
            beginIndexFlag = (source.indexOf(element) == 0);
            endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
        } while (beginIndexFlag || endIndexFlag);
        return source;
    }

    public static String obtainDescription(PsiClass psiClass) {
        String description = getDescription(psiClass.getDocComment(), psiClass.getAnnotations());
        return AssertUtils.isBlank(description) ? psiClass.getName() : description;
    }

    public static String obtainDescription(PsiMethod psiMethod) {
        String description = getDescription(psiMethod.getDocComment(), psiMethod.getAnnotations());
        return AssertUtils.isBlank(description) ? psiMethod.getName() : description;
    }

    public static String getDescription(PsiField psiField) {
        String description = getDescription(psiField.getDocComment(), psiField.getAnnotations());
        return AssertUtils.isBlank(description) ? psiField.getName() : description;
    }

    public static String getDescription(PsiDocComment docComment, PsiAnnotation[] annotations) {
        String desc = getDescription(docComment);
        if(AssertUtils.isBlank(desc)) {
            for (PsiAnnotation annotation : annotations) {
                String annotationText = annotation.getText();
                if(
                        annotationText.contains(SwaggerAnnotation.ApiOperation) ||
                        annotationText.contains(SwaggerAnnotation.ApiModelProperty)
                ) {
                    PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
                    if(attributes.length == 1 && attributes[0].getName() == null) {
                        return attributes[0].getLiteralValue();
                    }
                    if(attributes.length >= 1) {
                        for (PsiNameValuePair attribute : attributes) {
                            if("value".equals(attribute.getName())) {
                                return attribute.getLiteralValue();
                            }
                        }
                    }
                }
            }
        }
        return desc;
    }

    public static String getDescription(PsiDocComment psiDocComment) {
        if (psiDocComment != null) {
            PsiDocTag[] psiDocTags = psiDocComment.getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                String docTag = psiDocTag.getText().toLowerCase();
                if (docTag.contains("description")) {
                    return trimFirstAndLastChar(docTag
                            .replace("@description", EMPTY)
                            .replace("@Description", EMPTY)
                            .replace("Description", EMPTY)
                            .replace("<br>", EMPTY)
                            .replace(COLON, EMPTY)
                            .replace(STAR, EMPTY)
                            .replace(NEWLINE, SPACE), CharacterPool.SPACE);
                }
            }
            // 匹配 "todo"（忽略大小写）及其后面的所有内容，直到换行符或字符串结尾
            String text = psiDocComment.getText().replaceAll("(?i)TODO.*", EMPTY);
            return trimFirstAndLastChar(
                    text.split(AT)[0]
                            .replace("@description", EMPTY)
                            .replace("@Description", EMPTY)
                            .replace("Description", EMPTY)
                            .replace("<br>", NEWLINE)
                            .replace(":", EMPTY)
                            .replace("*", EMPTY)
                            .replace("/", EMPTY)
                            .replace("\n", SPACE)
                            .replace("<p>", NEWLINE)
                            .replace("</p>", NEWLINE)
                            .replace("<li>", NEWLINE)
                            .replace("</li>", NEWLINE)
                            .replace("{", EMPTY), CharacterPool.SPACE
            );
        }

        return null;
    }

    /**
     * 通过paramName 获得描述.
     *
     * @param psiMethodTarget the psi method target
     * @param paramName       the param name
     * @return the param desc
     */
    public static String getParamDesc(PsiMethod psiMethodTarget, String paramName) {
        if (psiMethodTarget.getDocComment() != null) {
            PsiDocTag[] psiDocTags = psiMethodTarget.getDocComment().getTags();
            for (PsiDocTag psiDocTag : psiDocTags) {
                if ((psiDocTag.getText().contains("@param") || psiDocTag.getText().contains("@Param")) && (!psiDocTag.getText().contains(LEFT_SQ_BRACKET)) && psiDocTag.getText().contains(paramName)) {
                    return trimFirstAndLastChar(psiDocTag.getText().replace("@param", EMPTY).replace("@Param", EMPTY).replace(paramName, EMPTY).replace(COLON, EMPTY).replace(STAR, EMPTY).replace(NEWLINE, SPACE), CharacterPool.SPACE);
                }
            }
        }
        return EMPTY;
    }

    /**
     * 获得属性注释
     *
     * @param psiDocComment the psi doc comment
     * @return the filed desc
     */
    private static String getFiledDesc(PsiDocComment psiDocComment) {
        if (Objects.nonNull(psiDocComment)) {
            String fileText = psiDocComment.getText();
            if (!Strings.isNullOrEmpty(fileText)) {
                return trimFirstAndLastChar(fileText.replace(STAR, EMPTY).replace(SLASH, EMPTY).replace(SPACE, EMPTY).replace(NEWLINE, COMMA).replace(TAB, EMPTY), CharacterPool.COMMA).split("\\{@link")[0];
            }
        }
        return EMPTY;
    }

    /**
     * 获得link 备注
     *
     * @param remark  the remark
     * @param project the project
     * @param field   the field
     * @return the link remark
     */
    public static String getLinkRemark(String remark, Project project, PsiField field) {
        // 尝试获得@link 的常量定义
        if (Objects.isNull(field.getDocComment())) {
            return remark;
        }
        String[] linkString = field.getDocComment().getText().split("@link");
        if (linkString.length > 1) {
            //说明有link
            String linkAddress = linkString[1].split(RIGHT_BRACE)[0].trim();
            PsiClass psiClassLink = JavaPsiFacade.getInstance(project).findClass(linkAddress, GlobalSearchScope.allScope(project));
            if (Objects.isNull(psiClassLink)) {
                //可能没有获得全路径，尝试获得全路径
                String[] importPaths = field.getParent().getContext() != null ? field.getParent().getContext().getText().split("import") : new String[0];
                if (importPaths.length > 1) {
                    for (String importPath : importPaths) {
                        if (importPath.contains(linkAddress.split("\\.")[0])) {
                            linkAddress = importPath.split(linkAddress.split("\\.")[0])[0] + linkAddress;
                            psiClassLink = JavaPsiFacade.getInstance(project).findClass(linkAddress.trim(), GlobalSearchScope.allScope(project));
                            break;
                        }
                    }
                }
                //如果小于等于一为不存在import，不做处理
            }
            if (Objects.nonNull(psiClassLink)) {
                //说明获得了link 的class
                PsiField[] linkFields = psiClassLink.getFields();
                if (linkFields.length > 0) {
                    remark += COMMA + psiClassLink.getName() + LEFT_SQ_BRACKET;
                    StringBuilder remarkBuilder = new StringBuilder(remark);
                    for (int i = 0; i < linkFields.length; i++) {
                        PsiField psiField = linkFields[i];
                        if (i > 0) {
                            remarkBuilder.append(COMMA);
                        }
                        // 先获得名称
                        remarkBuilder.append(psiField.getName());
                        // 后获得value,通过= 来截取获得，第二个值，再截取;
                        String[] splitValue = psiField.getText().split(EQUALS);
                        if (splitValue.length > 1) {
                            String value = splitValue[1].split(SEMICOLON)[0];
                            remarkBuilder.append(COLON).append(value);
                        }
                        String filedValue = DesUtil.getFiledDesc(psiField.getDocComment());
                        if (!Strings.isNullOrEmpty(filedValue)) {
                            remarkBuilder.append(LEFT_BRACKET).append(filedValue).append(RIGHT_BRACKET);
                        }
                    }
                    remark = remarkBuilder.toString();
                    remark += RIGHT_SQ_BRACKET;
                }
            }
        }
        return remark;
    }
}