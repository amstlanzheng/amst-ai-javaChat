package com.amst.ai.common.enums;

/**
 * 错误码
 */

public enum ErrorCode {

    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求数据为空", ""),

    NOT_LOGIN(40100, "未登录", ""),
    NO_AUTH(40101, "无权限", ""),
    NOT_FOUND(40400, "请求数据不存在", ""),



    SYSTEM_ERROR(50000, "系统内部异常", ""),
    //新增用户失败
    ADD_SYSTEM_USER_ERROR(50001, "新增用户失败", ""),

    ;





    ;

    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误信息
     */
    private final String message;
    /**
     * 错误描述
     */
    private final String description;


    ErrorCode(Integer code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }





    public Integer getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    public String getDescription() {
        return description;
    }
}
