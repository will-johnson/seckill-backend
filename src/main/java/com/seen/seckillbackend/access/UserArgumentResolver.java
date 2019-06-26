package com.seen.seckillbackend.access;

import com.seen.seckillbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 自定参数解析器
 * https://blog.csdn.net/leon_cx/article/details/81058509
 * SpringMvc中的HandlerAdapter会对Controller层方法的参数执行 HandlerMethodArgumentResolver(对参数的解析器)中的方法。
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    UserService userService;

    /**
     * supportsParameter：用于判定是否需要处理该参数分解，返回true为需要，并会去调用下面的方法resolveArgument。
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();
        return parameterType == Long.class;
    }


    /**
     * 真正用于处理参数分解的方法，返回的Object就是controller方法上的形参对象。
     * 在cookie或requestParam中获取到token，通过token去redis中获取详细的用户信息，再将用户信息放到页面进行展示
     */
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return UserContext.getUid();
    }
}
