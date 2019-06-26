package com.seen.seckillbackend.common.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ControllerAdvice 注解，可以用于定义@ExceptionHandler、@InitBinder、@ModelAttribute，并应用到所有@RequestMapping中。
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler  {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e) {
        if (e instanceof GlobalException) {
            log.error("自定义异常",e);
            GlobalException exception = (GlobalException)e;
            return Result.err(exception.getCm());
        }else{
            log.error("系统异常",e);
            return Result.err(CodeMsg.UNKNOW_ERR.getCode(), e.getMessage());
        }
    }

}
