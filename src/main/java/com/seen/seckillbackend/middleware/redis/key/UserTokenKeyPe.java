package com.seen.seckillbackend.middleware.redis.key;

public class UserTokenKeyPe extends BaseKeyPe {
    // token有效期7天
    public static final int TOKEN_EXPIRE = 3600 * 24 * 7 * 30 * 12;

    public UserTokenKeyPe(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserTokenKeyPe userTokenKeyPe = new UserTokenKeyPe(
            TOKEN_EXPIRE, "tk");

}
