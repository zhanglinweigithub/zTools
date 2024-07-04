package com.zhanglinwei.zTools.model;

/**
 * 成员
 */
public class Property {

    /**
     * 成员名称
     */
    private String name;

    /**
     * 成员类型
     */
    private String type;

    public Property(){}

    public Property(String name, String type){
        this.name = name;
        this.type = type;
    }

    public String toJavaStr() {
        return this.type + " " + this.name + ";\n\n";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
