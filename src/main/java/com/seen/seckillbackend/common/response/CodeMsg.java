package com.seen.seckillbackend.common.response;

public enum CodeMsg {

    SERVER_ERROR(500100, "User is null"),
    USER_NOT_EXIST(500101, "User needs login"),
    SECKILL_OVER(500200, "Seckill is over"),
    ACCESS_LIMIT_REACHED(500300, "Request is too frequent"),
    UNKNOW_ERR(500500, "UNKNOW_ERR");


    private int code;
    private String msg;

    CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
