package com.zhanglinwei.zTools.util;

import com.intellij.psi.*;
import com.zhanglinwei.zTools.constant.RequestMethodEnum;
import com.zhanglinwei.zTools.constant.WebAnnotation;
import com.zhanglinwei.zTools.model.FieldInfo;
import com.zhanglinwei.zTools.model.RequestHeader;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 注解工具类
 */
public class AnnotationUtil {

    private static Map<String, String> mediaType = new HashMap<>();

    static {
        mediaType.put("ALL_VALUE", "*/*");
        mediaType.put("APPLICATION_ATOM_XML_VALUE", "application/atom+xml");
        mediaType.put("APPLICATION_CBOR_VALUE", "application/cbor");
        mediaType.put("APPLICATION_FORM_URLENCODED_VALUE", "application/x-www-form-urlencoded");
        mediaType.put("APPLICATION_JSON_VALUE", "application/json");
        mediaType.put("APPLICATION_JSON_UTF8_VALUE", "application/json;charset=UTF-8");
        mediaType.put("APPLICATION_OCTET_STREAM_VALUE", "application/octet-stream");
        mediaType.put("APPLICATION_PDF_VALUE", "application/pdf");
        mediaType.put("APPLICATION_PROBLEM_JSON_VALUE", "application/problem+json");
        mediaType.put("APPLICATION_PROBLEM_JSON_UTF8_VALUE", "application/problem+json;charset=UTF-8");
        mediaType.put("APPLICATION_PROBLEM_XML_VALUE", "application/problem+xml");
        mediaType.put("APPLICATION_RSS_XML_VALUE", "application/rss+xml");
        mediaType.put("APPLICATION_STREAM_JSON_VALUE", "application/stream+json");
        mediaType.put("APPLICATION_XHTML_XML_VALUE", "application/xhtml+xml");
        mediaType.put("APPLICATION_XML_VALUE", "application/xml");
        mediaType.put("IMAGE_GIF_VALUE", "image/gif");
        mediaType.put("IMAGE_JPEG_VALUE", "image/jpeg");
        mediaType.put("IMAGE_PNG_VALUE", "image/png");
        mediaType.put("MULTIPART_FORM_DATA_VALUE", "multipart/form-data");
        mediaType.put("MULTIPART_MIXED_VALUE", "multipart/mixed");
        mediaType.put("TEXT_EVENT_STREAM_VALUE", "text/event-stream");
        mediaType.put("TEXT_HTML_VALUE", "text/html");
        mediaType.put("TEXT_MARKDOWN_VALUE", "text/markdown");
        mediaType.put("TEXT_PLAIN_VALUE", "text/plain");
        mediaType.put("TEXT_XML_VALUE", "text/xml");
        mediaType.put("PARAM_QUALITY_FACTOR", "q");
    }

    private AnnotationUtil(){}

    /**
     * 方法是否存在xxxMapping注解
     */
    public static boolean hasMappingAnnotation(PsiMethod psiMethod) {
        PsiAnnotation[] annotations = psiMethod.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (annotation.getText().contains("Mapping")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得请求类型
     */
    public static String getRequestTypeFromAnnotation(PsiAnnotation classMapping, PsiAnnotation methodMapping) {
        Set<String> requestTypeList = new HashSet<>();
        requestTypeList.addAll(getRequestTypeListByMappingAnnotation(classMapping));
        requestTypeList.addAll(getRequestTypeListByMappingAnnotation(methodMapping));

        return String.join(", ", requestTypeList);
    }

    /**
     * 根据mapping注解，获得请求类型集合
     */
    public static List<String> getRequestTypeListByMappingAnnotation(PsiAnnotation mappingAnnotation) {
        List<String> requestTypeList = new ArrayList<>();
        if (isRequestMapping(mappingAnnotation)) {
            PsiNameValuePair[] attributes = mappingAnnotation.getParameterList().getAttributes();
            for (PsiNameValuePair pair : attributes) {
                if ("method".equals(pair.getName())) {
                    String text = pair.getValue().getText();
                    if (AssertUtils.isNotBlank(text)) {
                        if (text.contains(RequestMethodEnum.GET.name())) {
                            requestTypeList.add(RequestMethodEnum.GET.name());
                        }
                        if (text.contains(RequestMethodEnum.POST.name())) {
                            requestTypeList.add(RequestMethodEnum.POST.name());
                        }
                        if (text.contains(RequestMethodEnum.PUT.name())) {
                            requestTypeList.add(RequestMethodEnum.PUT.name());
                        }
                        if (text.contains(RequestMethodEnum.PATCH.name())) {
                            requestTypeList.add(RequestMethodEnum.PATCH.name());
                        }
                        if (text.contains(RequestMethodEnum.DELETE.name())) {
                            requestTypeList.add(RequestMethodEnum.DELETE.name());
                        }
                    }
                }
            }
        } else {
            requestTypeList.add(extractRequestTypeStringByMappingText(mappingAnnotation.getText()));
        }

        return requestTypeList;
    }

    /**
     * 根据mapping注解，获得请求类型字符串
     */
    private static String extractRequestTypeStringByMappingText(String text) {
        if (text.contains(WebAnnotation.GetMapping)) {
            return RequestMethodEnum.GET.name();
        }
        if (text.contains(WebAnnotation.PutMapping)) {
            return RequestMethodEnum.PUT.name();
        }
        if (text.contains(WebAnnotation.DeleteMapping)) {
            return RequestMethodEnum.DELETE.name();
        }
        if (text.contains(WebAnnotation.PatchMapping)) {
            return RequestMethodEnum.PATCH.name();
        }

        return RequestMethodEnum.POST.name();
    }

    /**
     * 是否@RequestMapping注解
     */
    private static boolean isRequestMapping(PsiAnnotation mappingAnnotation) {
        return mappingAnnotation.getText().contains(WebAnnotation.RequestMapping);
    }

    /**
     * 获取xxxMapping注解请求路径
     */
    public static String getPathFromAnnotation(PsiAnnotation mappingAnnotation) {
        if (mappingAnnotation == null) {
            return "";
        }
        PsiNameValuePair[] psiNameValuePairs = mappingAnnotation.getParameterList().getAttributes();
        if (psiNameValuePairs.length == 1 && psiNameValuePairs[0].getName() == null) {
            return CommonUtils.appendSlash(psiNameValuePairs[0].getLiteralValue());
        }
        if (psiNameValuePairs.length >= 1) {
            for (PsiNameValuePair psiNameValuePair : psiNameValuePairs) {
                if (psiNameValuePair.getName().equals("value") || psiNameValuePair.getName().equals("path")) {
                    String text = psiNameValuePair.getValue().getText();
                    if (StringUtils.isEmpty(text)) {
                        return "";
                    }
                    text = text.replace("{\"", "").replace("\"}", "").replace("\"", "");
                    if (text.contains(",")) {
                        return CommonUtils.appendSlash(text.split(",")[0]);
                    }
                    return CommonUtils.appendSlash(text);
                }
            }
        }
        return "";
    }

    public static List<RequestHeader> buildRequestHeaderByMappingAnnotation(PsiAnnotation methodRequestMappingAnnotation, PsiAnnotation classRequestMappingAnnotation) {
        List<RequestHeader> headerList = new ArrayList<>();

        headerList.addAll(resolveConsumesAndProducesByMappingAnnotation(methodRequestMappingAnnotation));
        headerList.addAll(resolveConsumesAndProducesByMappingAnnotation(classRequestMappingAnnotation));

        return headerList;
    }

    private static RequestHeader buildRequestHeader(String headerName, HashSet<String> headerValue, String required, String desc) {
        if (AssertUtils.isEmpty(headerValue) || AssertUtils.isBlank(headerName)) {
            return null;
        }
        return new RequestHeader(headerName, String.join(", ", headerValue), required, desc);
    }

    public static List<RequestHeader> resolveConsumesAndProducesByMappingAnnotation(PsiAnnotation mappingAnnotation) {
        List<RequestHeader> headers = new ArrayList<>();
        if (mappingAnnotation != null) {
            PsiNameValuePair[] attributes = mappingAnnotation.getParameterList().getAttributes();
            for (PsiNameValuePair pair : attributes) {
                if ("consumes".equals(pair.getName())) {
                    headers.addAll(resolveConsumes(pair));
                } else if ("produces".equals(pair.getName())) {
                    headers.addAll(resolveProduces(pair));
                }
            }
        }
        return headers;
    }

    private static List<RequestHeader> resolveConsumes(PsiNameValuePair pair) {
        if (pair == null) {
            return new ArrayList<>();
        }
        String text = pair.getValue().getText();
        if (AssertUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        List<RequestHeader> consumesList = new ArrayList<>();
        text = text.replace("{", "").replace("}", "").replace("\"", "");
        if (text.contains(",")) {
            String[] split = text.split(",");
            for (String item : split) {
                consumesList.add(buildRequestHeader("Content-Type", buildRequestHeaderValue(item)));
            }
        } else {
            consumesList.add(buildRequestHeader("Content-Type", buildRequestHeaderValue(text)));
        }
        return consumesList;
    }

    private static List<RequestHeader> resolveProduces(PsiNameValuePair pair) {
        if (pair == null) {
            return new ArrayList<>();
        }
        String text = pair.getValue().getText();
        if (AssertUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        List<RequestHeader> producesList = new ArrayList<>();
        text = text.replace("{\"", "").replace("\"}", "").replace("\"", "");
        if (text.contains(",")) {
            String[] split = text.split(",");
            for (String item : split) {
                producesList.add(buildRequestHeader("Accept", item));
            }
        } else {
            producesList.add(buildRequestHeader("Accept", text));
        }
        return producesList;
    }

    private static String buildRequestHeaderValue(String item) {
        String key = item;
        if (item.contains(".")) {
            String[] split = item.split("\\.");
            key = split[1];
        }
        String value = mediaType.get(key);
        return AssertUtils.isBlank(value) ? item : value;
    }

    public static RequestHeader buildRequestHeaderByFieldInfo(FieldInfo field) {
        return new RequestHeader(field.getName(), field.getRange(), field.getRequired(), field.getDesc());
    }

    public static RequestHeader buildRequestHeader(String headerName, String value) {
        return buildRequestHeader(headerName, value, "Y", "");
    }

    public static RequestHeader buildRequestHeader(String headerName, String value, String required, String desc) {
        return new RequestHeader(headerName, value, required, desc);
    }

    /**
     * 根据注解名称查找注解
     */
    public static PsiAnnotation getAnnotationByName(PsiAnnotation[] annotations, String annotationName) {
        for (PsiAnnotation annotation : annotations) {
            if (annotation.getText().contains(annotationName)) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * 获取xxxMapping注解
     */
    public static PsiAnnotation getXxxMappingAnnotation(PsiAnnotation[] annotations) {
        return getAnnotationByName(annotations, "Mapping");
    }

    /**
     * 获得PathVariable注解
     */
    public static PsiAnnotation getPathVariableAnnotation(PsiAnnotation[] annotations) {
        return getAnnotationByName(annotations, WebAnnotation.PathVariable);
    }
    /**
     * 获得RequestParam注解
     */
    public static PsiAnnotation getRequestParamAnnotation(PsiAnnotation[] annotations) {
        return getAnnotationByName(annotations, WebAnnotation.RequestParam);
    }

    /**
     * 获得RequestHeader注解
     */
    public static PsiAnnotation getRequestHeaderAnnotation(PsiAnnotation[] annotations) {
        return getAnnotationByName(annotations, WebAnnotation.RequestHeader);
    }

    /**
     * 获得RequestPart注解
     */
    public static PsiAnnotation getRequestPartAnnotation(PsiAnnotation[] annotations) {
        return getAnnotationByName(annotations, WebAnnotation.RequestPart);
    }
}
