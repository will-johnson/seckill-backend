package com.seen.seckillbackend.redis.key;

public class AccessKeyPrefix extends BaseKeyPrefix {
    private AccessKeyPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKeyPrefix accessPrefix = new AccessKeyPrefix(5, "access");

    public static AccessKeyPrefix withExpireAccess(int expireSeconds) {
        return new AccessKeyPrefix(expireSeconds,"access");
    }
}
