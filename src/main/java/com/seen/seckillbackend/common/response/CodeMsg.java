package com.seen.seckillbackend.common.response;

public enum CodeMsg {

    // success
    PAY_SUCCESS(200, "Payment success"),

    // err
    USER_NULL(500100, "User is null"),
    USER_NEEDS_LOGIN(500101, "User needs login"),
    SECKILL_OVER(500200, "Seckill is over"),
    ACCESS_LIMIT_REACHED(500300, "Request is too frequent"),
    REPEAT_BUY(500301, "repeat buy"),
    PAY_FAIL(500400, "Payment fail"),
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
