package com.amst.ai.common.result;

import com.amst.ai.common.enums.ErrorCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;

    private T data;

    private String msg;

    /**
     * 描述
     */
    private String description;

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public Result(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }
    public Result(Integer code, T data, String msg, String description) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.description = description;
    }

    public Result(Integer code, String msg, String description) {
        this.code = code;
        this.msg = msg;
        this.description = description;
    }
    public static <T> Result<T> ok() {
        return new Result<>(1, "ok");
    }
    public static <T> Result<T> ok(T  data) {
        return new Result<>(1, data, "ok");
    }
    public static  <T> Result<T> fail(String msg) {
        return new Result<> (0, msg);
    }

    public static <T> Result<T> fail(ErrorCode code) {
        return new Result<>(code.getCode(), code.getMessage(), code.getDescription());
    }

    public static <T> Result<T> fail(ErrorCode code, String description) {
        return new Result<>(code.getCode(), code.getMessage(), description);
    }

    public static <T> Result<T> fail(Integer code, String description) {
        return new Result<>(code, description);
    }

    public static <T> Result<T> fail(Integer code, String message, String description) {
        return new Result<>(code, message, description);
    }
}