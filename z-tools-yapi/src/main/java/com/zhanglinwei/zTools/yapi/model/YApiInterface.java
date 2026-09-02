package com.zhanglinwei.zTools.yapi.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * YApi 接口定义
 */
public class YApiInterface {

    /** 接口ID */
    @SerializedName("_id")
    private Number _id;

    /** 所属项目ID */
    private Number project_id;

    /** 所属分类ID */
    private Number catid;

    /** 接口标题 */
    private String title;

    /** 接口请求路径 */
    private String path;

    /** 请求方式：GET / POST / PUT / DELETE / HEAD / OPTIONS / PATCH */
    private String method;

    /** 接口类型：static */
    private String type;

    /** 接口描述 */
    private String desc;

    /** 接口状态：undone / done */
    private String status;

    /** 最后编辑者UID */
    private Number edit_uid;

    /** 所属分类名称 */
    private String cat_name;

    /** 查询路径信息 */
    private YApiQueryPath query_path;

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

    /** 非 form 类型的请求体（JSON / XML / 文本） */
    private String req_body_other;

    /** 请求体是否使用 JSON Schema */
    private boolean req_body_is_json_schema;

    /** 响应体类型：json / text / xml */
    private String res_body_type;

    /** 响应体内容 */
    private String res_body;

    /** 响应体是否使用 JSON Schema */
    private boolean res_body_is_json_schema;

    /** 自定义字段值 */
    private String custom_field_value;

    /** 接口是否公开 */
    private boolean api_opened;

    /** Markdown 备注 */
    private String markdown;

    /** 标签列表 */
    private List<String> tag;

    /** 创建者UID */
    private Number uid;

    /** 创建者用户名 */
    private String username;

    /** 排序索引 */
    private Number index;

    /** 创建时间 */
    private long add_time;

    /** 更新时间 */
    private long up_time;

    /** MongoDB 版本号 */
    @SerializedName("__v")
    private Number __v;

    /** 是否无创建者UID */
    private boolean noUid;

    /** 接口 ID。 */
    public Number get_id() {
        return _id;
    }

    /** 设置接口 ID。 */
    public void set_id(Number _id) {
        this._id = _id;
    }

    /** 所属项目 ID。 */
    public Number getProject_id() {
        return project_id;
    }

    /** 设置所属项目 ID。 */
    public void setProject_id(Number project_id) {
        this.project_id = project_id;
    }

    /** 所属分类 ID。 */
    public Number getCatid() {
        return catid;
    }

    /** 设置所属分类 ID。 */
    public void setCatid(Number catid) {
        this.catid = catid;
    }

    /** 接口标题。 */
    public String getTitle() {
        return title;
    }

    /** 设置接口标题。 */
    public void setTitle(String title) {
        this.title = title;
    }

    /** 接口请求路径。 */
    public String getPath() {
        return path;
    }

    /** 设置接口请求路径。 */
    public void setPath(String path) {
        this.path = path;
    }

    /** 请求方式。 */
    public String getMethod() {
        return method;
    }

    /** 设置请求方式。 */
    public void setMethod(String method) {
        this.method = method;
    }

    /** 接口类型。 */
    public String getType() {
        return type;
    }

    /** 设置接口类型。 */
    public void setType(String type) {
        this.type = type;
    }

    /** 接口描述。 */
    public String getDesc() {
        return desc;
    }

    /** 设置接口描述。 */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /** 接口状态。 */
    public String getStatus() {
        return status;
    }

    /** 设置接口状态。 */
    public void setStatus(String status) {
        this.status = status;
    }

    /** 最后编辑者 UID。 */
    public Number getEdit_uid() {
        return edit_uid;
    }

    /** 设置最后编辑者 UID。 */
    public void setEdit_uid(Number edit_uid) {
        this.edit_uid = edit_uid;
    }

    /** 所属分类名称。 */
    public String getCat_name() {
        return cat_name;
    }

    /** 设置所属分类名称。 */
    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    /** 查询路径信息。 */
    public YApiQueryPath getQuery_path() {
        return query_path;
    }

    /** 设置查询路径信息。 */
    public void setQuery_path(YApiQueryPath query_path) {
        this.query_path = query_path;
    }

    /** Query 参数列表。 */
    public List<YApiQueryParam> getReq_query() {
        return req_query;
    }

    /** 设置 Query 参数列表。 */
    public void setReq_query(List<YApiQueryParam> req_query) {
        this.req_query = req_query;
    }

    /** 请求头列表。 */
    public List<YApiHeader> getReq_headers() {
        return req_headers;
    }

    /** 设置请求头列表。 */
    public void setReq_headers(List<YApiHeader> req_headers) {
        this.req_headers = req_headers;
    }

    /** 请求体类型。 */
    public String getReq_body_type() {
        return req_body_type;
    }

    /** 设置请求体类型。 */
    public void setReq_body_type(String req_body_type) {
        this.req_body_type = req_body_type;
    }

    /** 路径参数列表。 */
    public List<YApiPathParam> getReq_params() {
        return req_params;
    }

    /** 设置路径参数列表。 */
    public void setReq_params(List<YApiPathParam> req_params) {
        this.req_params = req_params;
    }

    /** Form 表单参数列表。 */
    public List<YApiFormParam> getReq_body_form() {
        return req_body_form;
    }

    /** 设置 Form 表单参数列表。 */
    public void setReq_body_form(List<YApiFormParam> req_body_form) {
        this.req_body_form = req_body_form;
    }

    /** 非 form 类型的请求体。 */
    public String getReq_body_other() {
        return req_body_other;
    }

    /** 设置非 form 类型的请求体。 */
    public void setReq_body_other(String req_body_other) {
        this.req_body_other = req_body_other;
    }

    /** 请求体是否使用 JSON Schema。 */
    public boolean isReq_body_is_json_schema() {
        return req_body_is_json_schema;
    }

    /** 设置请求体是否使用 JSON Schema。 */
    public void setReq_body_is_json_schema(boolean req_body_is_json_schema) {
        this.req_body_is_json_schema = req_body_is_json_schema;
    }

    /** 响应体类型。 */
    public String getRes_body_type() {
        return res_body_type;
    }

    /** 设置响应体类型。 */
    public void setRes_body_type(String res_body_type) {
        this.res_body_type = res_body_type;
    }

    /** 响应体内容。 */
    public String getRes_body() {
        return res_body;
    }

    /** 设置响应体内容。 */
    public void setRes_body(String res_body) {
        this.res_body = res_body;
    }

    /** 响应体是否使用 JSON Schema。 */
    public boolean isRes_body_is_json_schema() {
        return res_body_is_json_schema;
    }

    /** 设置响应体是否使用 JSON Schema。 */
    public void setRes_body_is_json_schema(boolean res_body_is_json_schema) {
        this.res_body_is_json_schema = res_body_is_json_schema;
    }

    /** 自定义字段值。 */
    public String getCustom_field_value() {
        return custom_field_value;
    }

    /** 设置自定义字段值。 */
    public void setCustom_field_value(String custom_field_value) {
        this.custom_field_value = custom_field_value;
    }

    /** 接口是否公开。 */
    public boolean isApi_opened() {
        return api_opened;
    }

    /** 设置接口是否公开。 */
    public void setApi_opened(boolean api_opened) {
        this.api_opened = api_opened;
    }

    /** Markdown 备注。 */
    public String getMarkdown() {
        return markdown;
    }

    /** 设置 Markdown 备注。 */
    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    /** 标签列表。 */
    public List<String> getTag() {
        return tag;
    }

    /** 设置标签列表。 */
    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    /** 创建者 UID。 */
    public Number getUid() {
        return uid;
    }

    /** 设置创建者 UID。 */
    public void setUid(Number uid) {
        this.uid = uid;
    }

    /** 创建者用户名。 */
    public String getUsername() {
        return username;
    }

    /** 设置创建者用户名。 */
    public void setUsername(String username) {
        this.username = username;
    }

    /** 排序索引。 */
    public Number getIndex() {
        return index;
    }

    /** 设置排序索引。 */
    public void setIndex(Number index) {
        this.index = index;
    }

    /** 创建时间。 */
    public long getAdd_time() {
        return add_time;
    }

    /** 设置创建时间。 */
    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    /** 更新时间。 */
    public long getUp_time() {
        return up_time;
    }

    /** 设置更新时间。 */
    public void setUp_time(long up_time) {
        this.up_time = up_time;
    }

    /** MongoDB 版本号。 */
    public Number get__v() {
        return __v;
    }

    /** 设置 MongoDB 版本号。 */
    public void set__v(Number __v) {
        this.__v = __v;
    }

    /** 是否无创建者 UID。 */
    public boolean isNoUid() {
        return noUid;
    }

    /** 设置是否无创建者 UID。 */
    public void setNoUid(boolean noUid) {
        this.noUid = noUid;
    }
}
