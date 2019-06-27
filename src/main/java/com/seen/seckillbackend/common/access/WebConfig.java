package com.seen.seckillbackend.common.access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * xxxConfigurer，这种接口就是让你扩展功能的，比如你想添加一个自定义的视图解析器
 *
 *
 * 类似于配置Bean的XML
 * 框架中的Controller会带很多参数，response, request, model...
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    UserArgumentResolver userArgumentResolver;

    @Autowired
    AccessInterceptor accessInterceptor;

    /**
     * 注册参数解析器
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }

    /**
     * 注册拦截器
     * 排除或增加需要拦截的请求
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptor = registry.addInterceptor(accessInterceptor);

//        interceptor.excludePathPatterns("/which_needs_exclude");
//
//        interceptor.addPathPatterns("/which_needs_include");


    }
}
