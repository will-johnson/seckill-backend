package com.seen.seckillbackend.middleware.redis.key;

public interface KeyPrefix {
    int expireSeconds();
    String getPrefix();
}
