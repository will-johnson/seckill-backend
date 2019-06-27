package com.seen.seckillbackend.middleware.redis.key;

public class AccessKeyPe extends BaseKeyPe {
    private AccessKeyPe(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessKeyPe accessKeyPe(int expireSeconds) {
        return new AccessKeyPe(expireSeconds,"access");
    }
}
