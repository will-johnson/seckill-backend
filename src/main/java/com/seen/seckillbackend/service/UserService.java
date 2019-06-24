package com.seen.seckillbackend.service;

import com.seen.seckillbackend.dao.UserDao;
import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.domain.User;
import com.seen.seckillbackend.exception.GlobalException;
import com.seen.seckillbackend.redis.RedisService;
import com.seen.seckillbackend.redis.key.UserKeyPrefix;
import com.seen.seckillbackend.redis.key.UserTokenKeyPrefix;
import com.seen.seckillbackend.util.AesCryption;
import com.seen.seckillbackend.util.CodeMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    public static final String COOKIE_TOKEN_NAME = "token";
    private static final String TOKEN_SALT = "salt";


    @Autowired
    UserDao userDao;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    public String login(HttpServletResponse response, User user) {
        if (null == user) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        //TODO 验证密码

        //生成token, token = (userid +"," + 加密信息）
        String tokenSrc = user.getUid()  + "," + TOKEN_SALT;
        String token = AesCryption.encrypt(tokenSrc);

        this.addCookie(response, token);
        return token;
    }


    public Long getUidByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        boolean exists = redisService.exists(UserTokenKeyPrefix.userTokenPrefix, token);

        //延长有效期
        if (exists) {
            addCookie(response, token);
        }

        Long uid = Long.valueOf(AesCryption.decrypt(token).split(",")[0]);
        return uid;
    }

    /**
     * 获取用户并判断存在性
     * 查缓存
     * 查数据库
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
        if (user == null) {
            throw new GlobalException(CodeMsg.USER_NOT_EXIST);
        }
        return user;
    }



    public void loginAll(HttpServletResponse response) {
        //reset
        redisService.deleteAll();
        goodsService.reset();
        orderService.reset();

        List<User> allUser = userDao.getAllUser();
        File file = new File("src/main/resources/static/token.txt");
        file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter out = new BufferedWriter(fw);
            for (User user : allUser) {
                String token = this.login(response, user);
                String line = user.getUid() + "," + token + "\n";
                out.write(line);
            }
            out.close();
        } catch (IOException e) {

        }
    }


    /**
     * 把token写到cookie，传递给客户端
     * 标示token对应哪个用户
     */
    private void addCookie(HttpServletResponse response, String token) {
        redisService.set(UserTokenKeyPrefix.userTokenPrefix, token, null);
        Cookie cookie = new Cookie(COOKIE_TOKEN_NAME, token);
        cookie.setMaxAge(UserTokenKeyPrefix.userTokenPrefix.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
