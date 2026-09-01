package com.zhanglinwei.zTools.yapi.model;


import java.util.List;

/**
 * YApi 添加接口请求
 */
public class YApiInterfaceAddRequest {

    /** 项目ID（必填） */
    private Number project_id;

    /** 接口标题（必填） */
    private String title;

    /** 接口请求路径（必填） */
    private String path;

    /** 请求方式（必填） */
    private String method;

    /** 分类ID（必填） */
    private Number catid;

    /** 接口描述 */
    private String desc;

    /** 接口状态 */
    private String status;

    /** Query 参数列表 */
    private List<YApiQueryParam> req_query;

    /** 请求头列表 */
    private List<YApiHeader> req_headers;

    /** 请求体类型：form / json / text / xml */
    private String req_body_type;

    /** 路径参数列表 */
    private List<YApiPathParam> req_params;

    /** Form 表单参数列表 */
    private List<YApiFormParam> req_body_form;

    /** 非 form 类型的请求体 */
    private String req_body_other;

    /** 响应体类型：json / text / xml */
    private String res_body_type;

    /** 响应体内容 */
    private String res_body;

    /** 自定义字段值 */
    private String custom_field_value;

    /** 接口是否公开 */
    private boolean api_opened;

    /** 请求体是否使用 JSON Schema */
    private boolean req_body_is_json_schema;

    /** 响应体是否使用 JSON Schema */
    private boolean res_body_is_json_schema;

    /** Markdown 备注 */
    private String markdown;

    /** 标签列表 */
    private List<String> tag;

    public Number getProject_id() {
        return project_id;
    }

    public void setProject_id(Number project_id) {
        this.project_id = project_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Number getCatid() {
        return catid;
    }

    public void setCatid(Number catid) {
        this.catid = catid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<YApiQueryParam> getReq_query() {
        return req_query;
    }

    public void setReq_query(List<YApiQueryParam> req_query) {
        this.req_query = req_query;
    }

    public List<YApiHeader> getReq_headers() {
        return req_headers;
    }

    public void setReq_headers(List<YApiHeader> req_headers) {
        this.req_headers = req_headers;
    }

    public String getReq_body_type() {
        return req_body_type;
    }

    public void setReq_body_type(String req_body_type) {
        this.req_body_type = req_body_type;
    }

    public List<YApiPathParam> getReq_params() {
        return req_params;
    }

    public void setReq_params(List<YApiPathParam> req_params) {
        this.req_params = req_params;
    }

    public List<YApiFormParam> getReq_body_form() {
        return req_body_form;
    }

    public void setReq_body_form(List<YApiFormParam> req_body_form) {
        this.req_body_form = req_body_form;
    }

    public String getReq_body_other() {
        return req_body_other;
    }

    public void setReq_body_other(String req_body_other) {
        this.req_body_other = req_body_other;
    }

    public String getRes_body_type() {
        return res_body_type;
    }

    public void setRes_body_type(String res_body_type) {
        this.res_body_type = res_body_type;
    }

    public String getRes_body() {
        return res_body;
    }

    public void setRes_body(String res_body) {
        this.res_body = res_body;
    }

    public String getCustom_field_value() {
        return custom_field_value;
    }

    public void setCustom_field_value(String custom_field_value) {
        this.custom_field_value = custom_field_value;
    }

    public boolean isApi_opened() {
        return api_opened;
    }

    public void setApi_opened(boolean api_opened) {
        this.api_opened = api_opened;
    }

    public boolean isReq_body_is_json_schema() {
        return req_body_is_json_schema;
    }

    public void setReq_body_is_json_schema(boolean req_body_is_json_schema) {
        this.req_body_is_json_schema = req_body_is_json_schema;
    }

    public boolean isRes_body_is_json_schema() {
        return res_body_is_json_schema;
    }

    public void setRes_body_is_json_schema(boolean res_body_is_json_schema) {
        this.res_body_is_json_schema = res_body_is_json_schema;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }
}
