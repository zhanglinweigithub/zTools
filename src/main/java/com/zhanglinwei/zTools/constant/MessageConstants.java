package com.zhanglinwei.zTools.constant;

/**
 * 错误信息
 */
public interface MessageConstants {

    /** 不是Controller */
    String NOT_CONTROLLER = "The file is not a Controller!";

    /** 不是RestApi */
    String NOT_REST_API = "The method is not a RestApi!";

    /** 仅支持Java类 */
    String ONLY_CLASS = "This operation only supports Java Class files!";

    /** 生成MarkDown文档成功 */
    String API_DOC_SUCCESS = "Generate Api document successfully!";

    /** 拷贝JSON成功 */
    String COPY_JSON_SUCCESS = "Copy Json successfully!";

    /** 拷贝DDL成功 */
    String COPY_DDL_SUCCESS = "Copy DDL successfully!";
    String COPY_INSERT_SUCCESS = "Copy Insert successfully!";
    String COPY_SELECT_SUCCESS = "Copy Select successfully!";
    String COPY_DELETE_SUCCESS = "Copy DELETE successfully!";
    String COPY_UPDATE_SUCCESS = "Copy UPDATE successfully!";

    /** 该文件无字段 */
    String NO_FIELDS = "This class file has no fields!";
    String READ_WORD_TEMPLATE_FAIL = "Failed to read word template!";
    String STYLE_NOT_EXIST = "Word style does not exist!";

}
