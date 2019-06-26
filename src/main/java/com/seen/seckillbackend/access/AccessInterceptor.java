package com.seen.seckillbackend.access;

import com.seen.seckillbackend.middleware.redis.single.RedisService;
import com.seen.seckillbackend.middleware.redis.key.AccessKeyPrefix;
import com.seen.seckillbackend.service.UserService;
import com.seen.seckillbackend.util.CodeMsg;
import com.seen.seckillbackend.util.Result;
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
            Long uid = this.getUid(request, response);
            UserContext.setUid(uid);

            return limitAccess(request, response, handler, uid);
        }
        return true;
    }

    /**
     * 限流防刷
     */
    private boolean limitAccess(HttpServletRequest request, HttpServletResponse response, Object handler, Long uid) throws IOException {
        HandlerMethod hm = (HandlerMethod) handler;
        AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);

        if (null == accessLimit) {
            return true;
        }

        int seconds = accessLimit.seconds();
        int maxCount = accessLimit.maxCount();
        boolean needLogin = accessLimit.needLogin();
        String key = request.getRequestURI();

        if (needLogin && null == uid) {
            log.error("用户需要登录");
            response.setStatus(400);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().print(Result.err(CodeMsg.USER_NOT_EXIST));
            return false;
        }
        if (null != uid) {
            key += "_" + uid;
        }

        AccessKeyPrefix prefix = AccessKeyPrefix.withExpireAccess(seconds);
        Integer count = redisService.get(prefix, key, Integer.class);

        if (count == null) {
            redisService.set(prefix, key, 1);
        } else if (count < maxCount) {
            redisService.incr(prefix, key);
        } else {
            log.error("请求频繁");
            return false;
        }
        return true;
    }

    /**
     * 在拦截器中取出User.Uid
     */
    public Long getUid(HttpServletRequest request, HttpServletResponse response) {
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
