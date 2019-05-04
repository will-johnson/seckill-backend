package com.seen.seckillbackend.redis.key;

public class UserTokenKeyPrefix extends BaseKeyPrefix{
    public static final int TOKEN_EXPIRE = 3600*24 * 2;

    public UserTokenKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserTokenKeyPrefix userTokenPrefix = new UserTokenKeyPrefix(TOKEN_EXPIRE, "tk");

}
