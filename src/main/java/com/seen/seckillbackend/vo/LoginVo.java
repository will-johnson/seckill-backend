package com.seen.seckillbackend.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginVo {
    @NotNull
    private String username;

    @NotNull
    private String password;

}
