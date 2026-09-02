package com.zhanglinwei.zTools.yapi.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * YApi 项目
 */
public class YApiProject {

    /** 项目ID */
    @SerializedName("_id")
    private Number _id;

    /** 项目名称 */
    private String name;

    /** 项目基础路径 */
    private String basepath;

    /** 项目描述 */
    private String desc;

    /** 所属分组ID */
    private Number group_id;

    /** 所属分组名称 */
    private String group_name;

    /** 项目类型：private / public */
    private String project_type;

    /** 项目图标 */
    private String icon;

    /** 项目颜色 */
    private String color;

    /** 前置脚本 */
    private String pre_script;

    /** 后置脚本 */
    private String after_script;

    /** 项目Mock脚本 */
    private String project_mock_script;

    /** 接口分类列表 */
    private List<YApiInterfaceCat> cat;

    /** 环境配置列表 */
    private List<YApiProjectEnv> env;

    /** 标签列表 */
    private List<YApiProjectTag> tag;

    /** 当前用户角色 */
    private boolean role;

    /** 是否关注 */
    private boolean follow;

    /** 是否开启消息通知 */
    private boolean switch_notice;

    /** 是否开启 Mock */
    private boolean is_mock_open;

    /** 是否严格模式 */
    private boolean strice;

    /** 是否使用 JSON5 */
    private boolean is_json5;

    /** 创建者UID */
    private Number uid;

    /** 创建时间 */
    private long add_time;

    /** 更新时间 */
    private long up_time;

    /** 项目 ID。 */
    public Number get_id() {
        return _id;
    }

    /** 设置项目 ID。 */
    public void set_id(Number _id) {
        this._id = _id;
    }

    /** 项目名称。 */
    public String getName() {
        return name;
    }

    /** 设置项目名称。 */
    public void setName(String name) {
        this.name = name;
    }

    /** 项目基础路径。 */
    public String getBasepath() {
        return basepath;
    }

    /** 设置项目基础路径。 */
    public void setBasepath(String basepath) {
        this.basepath = basepath;
    }

    /** 项目描述。 */
    public String getDesc() {
        return desc;
    }

    /** 设置项目描述。 */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /** 所属分组 ID。 */
    public Number getGroup_id() {
        return group_id;
    }

    /** 设置所属分组 ID。 */
    public void setGroup_id(Number group_id) {
        this.group_id = group_id;
    }

    /** 所属分组名称。 */
    public String getGroup_name() {
        return group_name;
    }

    /** 设置所属分组名称。 */
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    /** 项目类型。 */
    public String getProject_type() {
        return project_type;
    }

    /** 设置项目类型。 */
    public void setProject_type(String project_type) {
        this.project_type = project_type;
    }

    /** 项目图标。 */
    public String getIcon() {
        return icon;
    }

    /** 设置项目图标。 */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /** 项目颜色。 */
    public String getColor() {
        return color;
    }

    /** 设置项目颜色。 */
    public void setColor(String color) {
        this.color = color;
    }

    /** 前置脚本。 */
    public String getPre_script() {
        return pre_script;
    }

    /** 设置前置脚本。 */
    public void setPre_script(String pre_script) {
        this.pre_script = pre_script;
    }

    /** 后置脚本。 */
    public String getAfter_script() {
        return after_script;
    }

    /** 设置后置脚本。 */
    public void setAfter_script(String after_script) {
        this.after_script = after_script;
    }

    /** 项目 Mock 脚本。 */
    public String getProject_mock_script() {
        return project_mock_script;
    }

    /** 设置项目 Mock 脚本。 */
    public void setProject_mock_script(String project_mock_script) {
        this.project_mock_script = project_mock_script;
    }

    /** 接口分类列表。 */
    public List<YApiInterfaceCat> getCat() {
        return cat;
    }

    /** 设置接口分类列表。 */
    public void setCat(List<YApiInterfaceCat> cat) {
        this.cat = cat;
    }

    /** 环境配置列表。 */
    public List<YApiProjectEnv> getEnv() {
        return env;
    }

    /** 设置环境配置列表。 */
    public void setEnv(List<YApiProjectEnv> env) {
        this.env = env;
    }

    /** 标签列表。 */
    public List<YApiProjectTag> getTag() {
        return tag;
    }

    /** 设置标签列表。 */
    public void setTag(List<YApiProjectTag> tag) {
        this.tag = tag;
    }

    /** 当前用户角色。 */
    public boolean isRole() {
        return role;
    }

    /** 设置当前用户角色。 */
    public void setRole(boolean role) {
        this.role = role;
    }

    /** 是否关注。 */
    public boolean isFollow() {
        return follow;
    }

    /** 设置是否关注。 */
    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    /** 是否开启消息通知。 */
    public boolean isSwitch_notice() {
        return switch_notice;
    }

    /** 设置是否开启消息通知。 */
    public void setSwitch_notice(boolean switch_notice) {
        this.switch_notice = switch_notice;
    }

    /** 是否开启 Mock。 */
    public boolean isIs_mock_open() {
        return is_mock_open;
    }

    /** 设置是否开启 Mock。 */
    public void setIs_mock_open(boolean is_mock_open) {
        this.is_mock_open = is_mock_open;
    }

    /** 是否严格模式。 */
    public boolean isStrice() {
        return strice;
    }

    /** 设置是否严格模式。 */
    public void setStrice(boolean strice) {
        this.strice = strice;
    }

    /** 是否使用 JSON5。 */
    public boolean isIs_json5() {
        return is_json5;
    }

    /** 设置是否使用 JSON5。 */
    public void setIs_json5(boolean is_json5) {
        this.is_json5 = is_json5;
    }

    /** 创建者 UID。 */
    public Number getUid() {
        return uid;
    }

    /** 设置创建者 UID。 */
    public void setUid(Number uid) {
        this.uid = uid;
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
}
