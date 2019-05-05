package com.seen.seckillbackend.access;

import com.seen.seckillbackend.domain.User;
import com.seen.seckillbackend.redis.RedisService;
import com.seen.seckillbackend.redis.key.AccessKeyPrefix;
import com.seen.seckillbackend.service.UserService;
import com.seen.seckillbackend.util.Logg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    /**
     * AccessLimit 注解拦截器
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            // 在拦截器中取出User 对象
            User user = this.getUser(request, response);
            UserContext.setUser(user);

            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);

            if (null == accessLimit) {
                return true;
            }

            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin && null == user) {
                Logg.logger.error("需要登录");
                return false;
            }
            if (user != null) {
                key += "_" + user.getUsername();
            }

            AccessKeyPrefix prefix = AccessKeyPrefix.withExpireAccess(seconds);
            Integer count = redisService.get(prefix, key, Integer.class);

            if (count == null) {
                redisService.set(prefix, key, 1);
            } else if (count < maxCount) {
                redisService.incr(prefix, key);
            } else {
                Logg.logger.error("请求频繁");
                return false;
            }
        }

        return true;
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

    public User getUser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(UserService.COOKIE_TOKEN_NAME);
        String cookieToken = getCookieValue(request, UserService.COOKIE_TOKEN_NAME);

        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return userService.getByToken(response, token);
    }
}
