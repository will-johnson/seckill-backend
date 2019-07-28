package com.seen.seckillbackend.common.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ControllerAdvice 注解，可以用于定义@ExceptionHandler、@InitBinder、@ModelAttribute，并应用到所有@RequestMapping中。
 */
@ControllerAdvice
public class GlobalExceptionHandler  {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e) {
        if (e instanceof GlobalException) {
            GlobalException exception = (GlobalException)e;
            return Result.err(exception.getCm());
        }else{
            return Result.err(CodeMsg.UNKNOW_ERR.getCode(), e.getMessage());
        }
    }
}
