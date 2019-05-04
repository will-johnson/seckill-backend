package com.seen.seckillbackend.util;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }
    private Result(T data) {
        this.data = data;
    }

    public static <T> Result<T> err(CodeMsg codeMsg){
        return new Result<>(codeMsg);
    }

    private Result(CodeMsg codeMsg) {
        if (null != codeMsg) {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }
}
