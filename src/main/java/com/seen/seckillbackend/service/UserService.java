package com.seen.seckillbackend.service;

import com.seen.seckillbackend.dao.UserDao;
import com.seen.seckillbackend.domain.User;
import com.seen.seckillbackend.exception.GlobalException;
import com.seen.seckillbackend.redis.RedisService;
import com.seen.seckillbackend.redis.key.UserKeyPrefix;
import com.seen.seckillbackend.redis.key.UserTokenKeyPrefix;
import com.seen.seckillbackend.util.CodeMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class UserService {
    public static final String COOKIE_TOKEN_NAME = "token";

    @Autowired
    UserDao userDao;

    @Autowired
    RedisService redisService;


    public String login(HttpServletResponse response, User loginVo) {
        if (null == loginVo) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String username = loginVo.getUsername()+"";
        User user = this.getByName(username);

        // 验证密码
        //TODO

        //生成token
        String token = UUID.randomUUID().toString();
        this.addCookie(response, token, user);
        return token;

    }
    /**
     * 把token写到cookie，传递给客户端
     * 标示token对应哪个用户
     * @param response
     * @param token
     * @param user
     */
    private void addCookie(HttpServletResponse response, String token, User user) {
        redisService.set(UserTokenKeyPrefix.userTokenPrefix, token, user);
        Cookie cookie = new Cookie(COOKIE_TOKEN_NAME, token);
        cookie.setMaxAge(UserTokenKeyPrefix.userTokenPrefix.expireSeconds());
        cookie.setPath("/"); // 设置到网站根目录 TODO
        response.addCookie(cookie);
    }


    /**
     * 获取用户并判断存在性
     * @param username
     * @return
     */
    public User getByName(String username) {
        // 查缓存
        User user;
        user = redisService.get(UserKeyPrefix.userIdPrefix, username, User.class);
        if (null != user) {
            return user;
        }
        // 查数据库
        user = userDao.getByName(username);
        redisService.set(UserKeyPrefix.userIdPrefix, username, user);
        if(user == null) {
            throw new GlobalException(CodeMsg.USER_NOT_EXIST);
        }
        return user;
    }

    public User getTest(String name) {
        return userDao.getByName(name);
    }

    public User getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisService.get(UserTokenKeyPrefix.userTokenPrefix, token, User.class);

        //延长有效期
        if (null != user) {
            addCookie(response, token, user);
        }
        return user;
    }
}
