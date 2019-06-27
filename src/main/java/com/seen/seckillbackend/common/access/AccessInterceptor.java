package com.seen.seckillbackend.common.access;

import com.seen.seckillbackend.common.response.GlobalException;
import com.seen.seckillbackend.middleware.redis.single.RedisService;
import com.seen.seckillbackend.middleware.redis.key.AccessKeyPe;
import com.seen.seckillbackend.service.UserService;
import com.seen.seckillbackend.common.response.CodeMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截请求
 * HandlerInterceptorAdapter，相当于一个Filter拦截器，但是颗粒度更细
 * request请求处理顺序，Filter -> HandlerInterceptor 拦截器 -> AOP
 * Filter 过滤器属于 Servlet 范畴的API, 与Spring 没什么关系
 * HandlerInterceptor 拦截器 属于Spring 的范畴
 *
 * 只有经过 dispatchservlet 的请求，才会走拦截器chain，我们自定义的的servlet 请求是不会被拦截的
 * 但过滤器会拦截所有请求
 *
 * 我们不能通过修改拦截器修改request内容，但可以通过抛出异常（或者返回false）暂停request执行
 *
 * HandlerInterceptorAdapter implements HandlerInterceptor
 *
 *
 * HandlerInterceptorAdapter 是 适配器 模式的一种
 * 实现适配器模式有两种方式：继承 和 组合
 * 实现接口时，可以将某些接口方法写死，然后让继承类覆盖
 * AsyncHandlerInterceptor 接口只增添了一个接口，这种设计模式可以留意下
 *
 * 在 WebConfig中注册
 *
 * 面向接口和面向抽象类编程的区别了。面向接口变成就是你要实现接口中【所有所有】的方法，管你用不用得上；但是，假如有个抽象类去实现了这个接口（抽象类里面都是空方法，还可以自己添加新的方法），然后我们只需要去继承这个抽象类，重写其中我们需要的方法就可以了，用多少，就重写多少。
 * 基于这种思想，springboot提供了一个抽象类WebMvcConfigurerAdapter去实现WebMvcConfigurer接口，所以我们只需要继承WebMvcConfigurerAdapter就可以了。
 */
@Component
@Slf4j
public class AccessInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    /**
     * false表示流程中断（如登录检查失败），不会继续调用其他的拦截器或处理器，此时我们需要通过response来产生响应；
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            Long userId = this.getUserId(request, response);
            UserContext.setUserId(userId);

            return limitAccess(request, response, handler, userId);
        }
        return true;
    }

    /**
     * 限流防刷
     */
    private boolean limitAccess(HttpServletRequest request, HttpServletResponse response, Object handler, Long userId) throws IOException {
        HandlerMethod hm = (HandlerMethod) handler;
        AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);

        if (null == accessLimit) {
            return true;
        }

        int seconds = accessLimit.seconds();
        int maxCount = accessLimit.maxCount();
        boolean needLogin = accessLimit.needLogin();
        String key = request.getRequestURI();

        if (needLogin && null == userId) {
            log.error("用户需要登录");
            throw new GlobalException(CodeMsg.USER_NEEDS_LOGIN);
        }
        if (null != userId) {
            key += "_" + userId;
        }

        AccessKeyPe prefix = AccessKeyPe.accessKeyPe(seconds);
        Integer count = redisService.get(prefix, key, Integer.class);

        if (count == null) {
            redisService.set(prefix, key, 1);
        } else if (count < maxCount) {
            redisService.incr(prefix, key);
        } else {
            log.error("请求频繁");
            throw new GlobalException(CodeMsg.ACCESS_LIMIT_REACHED);
        }
        return true;
    }

    /**
     * 在拦截器中取出User.userId
     */
    public Long getUserId(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(UserService.COOKIE_TOKEN_NAME);
        String cookieToken = getCookieValue(request, UserService.COOKIE_TOKEN_NAME);

        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return userService.getUidByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
