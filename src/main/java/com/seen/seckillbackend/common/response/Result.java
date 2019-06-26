package com.seen.seckillbackend.common.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    public static <T> Result<T> err(CodeMsg codeMsg) {
        return new Result<>(codeMsg);
    }

    public static <T> Result<T> err(int code, String msg) {
        Result<T> tResult = new Result<>();
        tResult.setCode(code);
        tResult.setMsg(msg);
        return tResult;
    }

    private Result(CodeMsg codeMsg) {
        if (null != codeMsg) {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }

    private Result(T data) {
        this.data = data;
        this.code = 200;
    }
}
