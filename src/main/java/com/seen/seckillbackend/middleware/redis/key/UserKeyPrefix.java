package com.seen.seckillbackend.middleware.redis.key;

public class UserKeyPrefix extends BaseKeyPrefix {
    private UserKeyPrefix(String prefix) {
        super(prefix);
    }

    public static UserKeyPrefix userIdPrefix = new UserKeyPrefix("id");
    public static UserKeyPrefix userNamePrefix = new UserKeyPrefix("name");
}
