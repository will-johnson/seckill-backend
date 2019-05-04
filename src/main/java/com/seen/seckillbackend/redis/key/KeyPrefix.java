package com.seen.seckillbackend.redis.key;

public interface KeyPrefix {
    int expireSeconds();
    String getPrefix();
}
