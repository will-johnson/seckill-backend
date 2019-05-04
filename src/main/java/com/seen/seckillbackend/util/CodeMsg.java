package com.seen.seckillbackend.util;

import lombok.Data;

@Data
public class CodeMsg {

    private int code;
    private String msg;


    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "LoginVo is null");
    public static CodeMsg USER_NOT_EXIST = new CodeMsg(500101, "User not exists");

    public static final CodeMsg SECKILL_OVER = new CodeMsg(500200, "Seckill is over");


    private CodeMsg( int code,String msg ) {
        this.code = code;
        this.msg = msg;
    }
}
