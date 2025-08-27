package com.amst.ai.exception;

import com.amst.ai.common.ErrorCode;

import java.io.Serial;

/**
 * 自定义业务异常
 *
 * @author amst
 */
public class BusinessException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String description;

    public BusinessException(Integer code, String message, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }
    public Integer getCode(){
        return code;
    }
    public String getDescription(){
        return description;
    }
}
