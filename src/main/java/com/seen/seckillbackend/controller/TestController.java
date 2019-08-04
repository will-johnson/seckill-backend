package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.common.response.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {


    @GetMapping("/exception")
    public Result<String> excep() {
        try {
            int i = 0;
            int j = 10 / i;
        } catch (Exception e) {
            throw e;
        }
        return Result.success("123");
    }
}
